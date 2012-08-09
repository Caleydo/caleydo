/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.event.view.NewViewEvent;
import org.caleydo.core.event.view.ViewClosedEvent;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.execution.DisplayLoopExecution;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * Manage all canvas, view and canvas objects.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ViewManager extends AManager<IView> {

	private HashMap<GLCanvas, ArrayList<AGLView>> hashGLCanvas2GLView = new HashMap<GLCanvas, ArrayList<AGLView>>();

	private HashMap<Integer, AGLView> hashGLViewID2GLView = new HashMap<Integer, AGLView>();

	private HashMap<ARcpGLViewPart, IView> hashRCP2View = new HashMap<ARcpGLViewPart, IView>();

	private HashMap<IView, ARcpGLViewPart> hashView2RCP = new HashMap<IView, ARcpGLViewPart>();

	/**
	 * Map that maps from a remote rendering view to a list of its remote
	 * rendered views.
	 */
	private Map<AGLView, Set<AGLView>> hashRemoteRenderingView2RemoteRenderedViews = new HashMap<AGLView, Set<AGLView>>();

	/**
	 * Map that maps from a top level remote rendering view to remote rendering
	 * views that need to be destroyed. These views shall be destroyed via
	 * {@link #executePendingRemoteViewDestruction(GL2, AGLView)} in a display
	 * cycle.
	 */
	private Map<AGLView, Set<AGLView>> hashTopLevelView2ViewsToBeDestroyed = new HashMap<AGLView, Set<AGLView>>();

	private FPSAnimator fpsAnimator;

	private PickingManager pickingManager = new PickingManager();

	private ConnectedElementRepresentationManager connectedElementRepManager = ConnectedElementRepresentationManager
			.get();

	private GLInfoAreaManager infoAreaManager = new GLInfoAreaManager();

	private Set<Object> busyRequests = new HashSet<Object>();

	/**
	 * Determines whether the views that were deserialized have already been
	 * initialized. Views do not get initialized by default when they are not
	 * visible.
	 */
	private boolean areSerializedViewsInitialized = false;

	/**
	 * Utility object to execute code within the display loop, e.g. used by
	 * managers to avoid access conflicts with views.
	 */
	private DisplayLoopExecution displayLoopExecution;

	private volatile static ViewManager instance;

	public static ViewManager get() {
		if (instance == null) {
			synchronized (ViewManager.class) {
				if (instance == null) {
					instance = new ViewManager();
				}
			}
		}
		return instance;
	}

	@Override
	public boolean hasItem(int iItemId) {
		if (hashItems.containsKey(iItemId))
			return true;

		if (hashGLViewID2GLView.containsKey(iItemId))
			return true;

		return false;
	}

	public AGLView getGLView(int iItemID) {
		return hashGLViewID2GLView.get(iItemID);
	}

	public boolean unregisterGLCanvas(final GLCanvas glCanvas) {

		if (glCanvas == null)
			return false;

		fpsAnimator.remove(glCanvas);
		hashGLCanvas2GLView.remove(glCanvas);

		return true;
	}

	public void registerGLView(AGLView glView) {
		hashGLViewID2GLView.put(glView.getID(), glView);
		Logger.log(new Status(Status.INFO, this.toString(), "Registering view: " + glView));
		NewViewEvent event = new NewViewEvent(glView);
		event.setSender(this);
		generalManager.getEventPublisher().triggerEvent(event);
	}

	/**
	 * Registers the dependency between a remote rendering and a remote rendered
	 * view.
	 * 
	 * @param remoteRenderedView
	 *            The remote rendered view.
	 * @param remoteRenderingView
	 *            The view that renders the remoteRenderedView.
	 */
	public void registerRemoteRenderedView(AGLView remoteRenderedView,
			AGLView remoteRenderingView) {

		Set<AGLView> remoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews
				.get(remoteRenderingView);

		if (remoteRenderedViews == null) {
			remoteRenderedViews = new HashSet<AGLView>();
			hashRemoteRenderingView2RemoteRenderedViews.put(remoteRenderingView,
					remoteRenderedViews);
		}
		remoteRenderedViews.add(remoteRenderedView);
	}

	/**
	 * Destroys all remote views of topLevelRemoteRenderingView that have
	 * previously been unregistered via {@link #unregisterGLView(AGLView)}.
	 * 
	 * @param gl
	 * @param topLevelRemoteRenderingView
	 */
	public void executePendingRemoteViewDestruction(GL2 gl,
			AGLView topLevelRemoteRenderingView) {

		Set<AGLView> viewsToBeDestroyed = hashTopLevelView2ViewsToBeDestroyed
				.get(topLevelRemoteRenderingView);

		if (viewsToBeDestroyed != null) {
			for (AGLView view : viewsToBeDestroyed) {
				view.destroy(gl);
			}
			viewsToBeDestroyed.clear();
		}
	}

	// /**
	// * Destroys the specified view and all of its remote rendered views.
	// *
	// * @param gl
	// * @param remoteRenderingView
	// */
	// public void destroyTopLevelGLView(GL2 gl, AGLView remoteRenderingView) {
	//
	// destroyRemoteViews(gl, remoteRenderingView);
	//
	// remoteRenderingView.destroy(gl);
	// }

	/**
	 * Recursively destroys all remote views of the specified view.
	 * 
	 * @param gl
	 * @param remoteRenderingView
	 */
	public void destroyRemoteViews(GL2 gl, AGLView remoteRenderingView) {
		Set<AGLView> remoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews
				.get(remoteRenderingView);

		if (remoteRenderedViews != null) {
			Set<AGLView> tempRemoteRenderedViews = new HashSet<AGLView>(
					remoteRenderedViews);
			for (AGLView remoteRenderedView : tempRemoteRenderedViews) {
				destroyRemoteViews(gl, remoteRenderedView);
				unregisterGLView(remoteRenderedView, false);
				remoteRenderedView.destroy(gl);
			}
		}
	}

	public void registerGLEventListenerByGLCanvas(final GLCanvas glCanvas,
			final AGLView glView) {

		// This is the case when a view is rendered remote
		if (glCanvas == null)
			return;

		if (!hashGLCanvas2GLView.containsKey(glCanvas)) {
			hashGLCanvas2GLView.put(glCanvas, new ArrayList<AGLView>());
		}

		hashGLCanvas2GLView.get(glCanvas).add(glView);
		glCanvas.addGLEventListener(glView);
	}

	/**
	 * Associate an RCP view with the Caleydo view contained inside the RCP
	 * view.
	 * 
	 * @param rcpView
	 * @param view
	 */
	public void registerRCPView(final ARcpGLViewPart rcpView, final IView view) {
		if (!hashRCP2View.containsKey(rcpView))
			hashRCP2View.put(rcpView, view);

		if (!hashView2RCP.containsKey(view))
			hashView2RCP.put(view, rcpView);
	}

	/**
	 * Remove association between an RCP view and the Caleydo view contained
	 * inside the RCP view.
	 * 
	 * @param rcpView
	 * @param view
	 */
	public void unregisterRCPView(final ARcpGLViewPart rcpView, final IView view) {

		if (hashRCP2View.containsKey(rcpView))
			hashRCP2View.remove(rcpView);

		if (hashView2RCP.containsKey(view))
			hashView2RCP.remove(view);
	}

	public ARcpGLViewPart getViewPartFromView(IView view) {
		return hashView2RCP.get(view);
	}

	public IView getViewFromViewPart(ARcpGLViewPart viewPart) {
		return hashRCP2View.get(viewPart);
	}

	/**
	 * Removes all views, canvas and GL2 event listeners
	 */
	public void cleanup() {

		hashGLCanvas2GLView.clear();
		hashGLViewID2GLView.clear();
		hashItems.clear();
	}

	/**
	 * This method unregisters the specified view and also triggers the
	 * destruction of that view and all remote rendered child views if it is
	 * remotely rendered.
	 * 
	 * @param glView
	 */
	public void unregisterGLView(final AGLView glView) {
		unregisterGLView(glView, glView.getTopLevelGLView() != glView);
	}

	/**
	 * Unregisters the specified view from this manager.
	 * 
	 * @param glView
	 * @param registerAtTopLevelViewForDestruction
	 *            Specifies whether the view (if remote rendered) shall be
	 *            destroyed in the next display cycle of its top level remote
	 *            rendering view.
	 */
	private void unregisterGLView(final AGLView glView,
			boolean registerAtTopLevelViewForDestruction) {
		if (glView == null)
			return;

		GLCanvas parentGLCanvas = (glView).getParentGLCanvas();

		if (parentGLCanvas != null) {
			parentGLCanvas.removeGLEventListener(glView);

			if (hashGLCanvas2GLView.containsKey(parentGLCanvas)) {
				hashGLCanvas2GLView.get(parentGLCanvas).remove(glView);
			}
		}

		hashGLViewID2GLView.remove(glView.getID());

		AGLView parentView = (AGLView) glView.getRemoteRenderingGLView();

		// Remove this view from the parent's remote rendering list
		if (parentView != null) {
			Set<AGLView> parentRemoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews
					.get(parentView);
			if (parentRemoteRenderedViews != null)
				parentRemoteRenderedViews.remove(glView);
		}

		AGLView topLevelGLView = glView.getTopLevelGLView();
		if (topLevelGLView != glView) {
			Set<AGLView> viewsToBeDestroyed = hashTopLevelView2ViewsToBeDestroyed
					.get(topLevelGLView);

			if (registerAtTopLevelViewForDestruction) {
				if (viewsToBeDestroyed == null) {
					viewsToBeDestroyed = new HashSet<AGLView>();
					hashTopLevelView2ViewsToBeDestroyed.put(topLevelGLView,
							viewsToBeDestroyed);
				}
				viewsToBeDestroyed.add(glView);
			}
			Set<AGLView> remoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews
					.get(glView);

			if (remoteRenderedViews != null) {
				Set<AGLView> tempRemoteRenderedViews = new HashSet<AGLView>(
						remoteRenderedViews);
				for (AGLView remoteRenderedView : tempRemoteRenderedViews) {
					// Unregister remote rendered views of glView
					unregisterGLView(remoteRenderedView,
							registerAtTopLevelViewForDestruction);
					// Register them to be destroyed in the next display cycle
					// of the top level remote rendering view
					if (registerAtTopLevelViewForDestruction)
						viewsToBeDestroyed.add(remoteRenderedView);
				}
				remoteRenderedViews.clear();
			}
		} else {
			hashTopLevelView2ViewsToBeDestroyed.remove(glView);
			unregisterGLCanvas(glView.getParentGLCanvas());
		}

		hashRemoteRenderingView2RemoteRenderedViews.remove(glView);

		ViewClosedEvent event = new ViewClosedEvent(glView);
		event.setSender(this);
		generalManager.getEventPublisher().triggerEvent(event);
	}

	public Collection<AGLView> getAllGLViews() {
		return hashGLViewID2GLView.values();
	}

	public PickingManager getPickingManager() {
		return pickingManager;
	}

	public ConnectedElementRepresentationManager getConnectedElementRepresentationManager() {
		return connectedElementRepManager;
	}

	public GLInfoAreaManager getInfoAreaManager() {
		return infoAreaManager;
	}

	public void startAnimator() {

		if (fpsAnimator == null)
			fpsAnimator = new FPSAnimator(30);

		if (!fpsAnimator.isAnimating())
			fpsAnimator.start();

		fpsAnimator.setIgnoreExceptions(true);
		fpsAnimator.setPrintExceptions(true);

		Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Start animator"));
	}

	public void stopAnimator() {
		if (fpsAnimator != null && fpsAnimator.isAnimating())
			fpsAnimator.stop();

		Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Stop animator"));
	}

	public void registerGLCanvasToAnimator(final GLCanvas glCanvas) {

		// Lazy creation of animator
		if (fpsAnimator == null) {
			startAnimator();
		}

		fpsAnimator.add(glCanvas);

		Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID,
				"Add canvas to animator" + glCanvas.getName()));
	}

	public void unregisterGLCanvasFromAnimator(final GLCanvas glCanvas) {
		fpsAnimator.remove(glCanvas);
	}

	/**
	 * Requests busy mode for the application. This method should be called
	 * whenever a process needs to stop any user interaction with the
	 * application, e.g. when starting up or when loading multiple pathways.
	 * Usually this should result disabling user events and showing a loading
	 * screen animation.
	 * 
	 * @param requestInstance
	 *            object that wants to request busy mode
	 */
	public void requestBusyMode(Object requestInstance) {
		if (requestInstance == null) {
			throw new IllegalArgumentException("requestInstance must not be null");
		}
		synchronized (busyRequests) {
			if (busyRequests.isEmpty()) {
				for (AGLView tmpGLEventListener : getAllGLViews()) {
					if (!tmpGLEventListener.isRenderedRemote()) {
						tmpGLEventListener.enableBusyMode(true);
					}
				}
			}
			if (!busyRequests.contains(requestInstance)) {
				busyRequests.add(requestInstance);
			}
		}
	}

	/**
	 * Releases a previously requested busy mode. Releases are only performed by
	 * passing the originally requesting object to this method.
	 * 
	 * @param requestInstance
	 *            the object that requested the busy mode
	 */
	public void releaseBusyMode(Object requestInstance) {
		if (requestInstance == null) {
			throw new IllegalArgumentException("requestInstance must not be null");
		}
		synchronized (busyRequests) {
			if (busyRequests.contains(requestInstance)) {
				busyRequests.remove(requestInstance);
			}
			if (busyRequests.isEmpty()) {
				for (AGLView tmpGLEventListener : getAllGLViews()) {
					if (!tmpGLEventListener.isRenderedRemote()) {
						tmpGLEventListener.enableBusyMode(false);
					}
				}
			}
		}
	}

	public void createSWTView(final ASerializedView serializedView) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					ARcpGLViewPart viewPart = (ARcpGLViewPart) page
							.showView(serializedView.getViewType());
					AGLView view = viewPart.getGLView();
					view.initFromSerializableRepresentation(serializedView);
					// TODO re-init view with its serializedView

				} catch (PartInitException ex) {
					throw new RuntimeException("could not create view with gui-id="
							+ serializedView.getViewType(), ex);
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public AGLView createGLView(Class<? extends AGLView> viewClass, GLCanvas glCanvas,
			Composite parentComposite, ViewFrustum viewFrustum) {

		AGLView view;
		try {
			Class[] argTypes = { GLCanvas.class, Composite.class, ViewFrustum.class };
			Constructor aConstructor = viewClass.getConstructor(argTypes);
			view = (AGLView) aConstructor.newInstance(glCanvas, parentComposite,
					viewFrustum);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Cannot create GL view " + viewClass);
		}

		return view;
	}

	/**
	 * Retrieves the {@link DisplayLoopExecution} related to the
	 * {@link ViewManager}'s display loop.
	 * 
	 * @return {@link DisplayLoopExecution} for executing code in the display
	 *         loop
	 */
	public DisplayLoopExecution getDisplayLoopExecution() {

		// lazy creation of animator and display loop
		if (displayLoopExecution == null) {
			startAnimator();
			displayLoopExecution = DisplayLoopExecution.get();
			fpsAnimator.add((GLAutoDrawable) displayLoopExecution.getDisplayLoopCanvas());
			displayLoopExecution.executeMultiple(connectedElementRepManager);
		}
		return displayLoopExecution;
	}

	public synchronized void initializeUnserializedViews() {
		if (!areSerializedViewsInitialized) {
			areSerializedViewsInitialized = true;
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					System.out.println("unserialized view initialization");
					IViewReference[] views = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.getViewReferences();

					try {
						for (IViewReference view : views) {

							PlatformUI
									.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.showView(view.getId(), view.getSecondaryId(),
											IWorkbenchPage.VIEW_VISIBLE);
						}
					} catch (PartInitException e) {
						throw new IllegalStateException();
					}

					// Make DVI visible
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage().showView("org.caleydo.view.dvi");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
