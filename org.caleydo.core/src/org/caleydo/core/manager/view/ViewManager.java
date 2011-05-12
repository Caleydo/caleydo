package org.caleydo.core.manager.view;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.execution.DisplayLoopExecution;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
public class ViewManager
	extends AManager<IView>
	implements IListenerOwner {

	protected HashMap<Integer, GLCaleydoCanvas> hashGLCanvasID2GLCanvas;

	protected HashMap<GLCaleydoCanvas, ArrayList<AGLView>> hashGLCanvas2GLView;

	protected HashMap<Integer, AGLView> hashGLViewID2GLView;

	private FPSAnimator fpsAnimator;

	private PickingManager pickingManager;

	private ConnectedElementRepresentationManager connectedElementRepManager;

	private GLInfoAreaManager infoAreaManager;

	private Composite activeSWTView;

	private Set<Object> busyRequests;

	private CreateGUIViewListener createGUIViewListener;

	/**
	 * Utility object to execute code within the display loop, e.g. used by managers to avoid access conflicts
	 * with views.
	 */
	private DisplayLoopExecution displayLoopExecution;

	/**
	 * Constructor.
	 */
	public ViewManager() {
		pickingManager = new PickingManager();
		connectedElementRepManager = ConnectedElementRepresentationManager.get();
		infoAreaManager = new GLInfoAreaManager();

		hashGLCanvasID2GLCanvas = new HashMap<Integer, GLCaleydoCanvas>();
		hashGLCanvas2GLView = new HashMap<GLCaleydoCanvas, ArrayList<AGLView>>();
		hashGLViewID2GLView = new HashMap<Integer, AGLView>();

		busyRequests = new HashSet<Object>();

		registerEventListeners();
	}

	public void init() {
		fpsAnimator = new FPSAnimator(60);

//		displayLoopExecution = DisplayLoopExecution.get();
//		fpsAnimator.add(displayLoopExecution.getDisplayLoopCanvas());
//
//		displayLoopExecution.executeMultiple(connectedElementRepManager);
	}

	@Override
	public boolean hasItem(int iItemId) {
		if (hashItems.containsKey(iItemId))
			return true;

		if (hashGLCanvasID2GLCanvas.containsKey(iItemId))
			return true;

		if (hashGLViewID2GLView.containsKey(iItemId))
			return true;

		return false;
	}

	public GLCaleydoCanvas getCanvas(int iItemID) {
		return hashGLCanvasID2GLCanvas.get(iItemID);
	}

	public AGLView getGLView(int iItemID) {
		return hashGLViewID2GLView.get(iItemID);
	}

	public boolean registerGLCanvas(final GLCaleydoCanvas glCanvas) {
		int iGLCanvasID = glCanvas.getID();

		if (hashGLCanvasID2GLCanvas.containsKey(iGLCanvasID)) {
			Logger.log(new Status(IStatus.WARNING, this.toString(), "GL2 Canvas with ID " + iGLCanvasID
				+ " is already registered! Do nothing."));

			return false;
		}

		hashGLCanvasID2GLCanvas.put(iGLCanvasID, glCanvas);
		// fpsAnimator.add(glCanvas);

		return true;
	}

	public boolean unregisterGLCanvas(final GLCaleydoCanvas glCanvas) {

		if (glCanvas == null)
			return false;

		fpsAnimator.remove(glCanvas);
		hashGLCanvasID2GLCanvas.remove(glCanvas.getID());
		hashGLCanvas2GLView.remove(glCanvas);

		return true;
	}

	public void registerGLView(AGLView glView) {
		hashGLViewID2GLView.put(glView.getID(), glView);
	}

	public void registerGLEventListenerByGLCanvas(final GLCaleydoCanvas glCanvas, final AGLView glView) {

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
	 * Removes all views, canvas and GL2 event listeners
	 */
	public void cleanup() {

		hashGLCanvasID2GLCanvas.clear();
		hashGLCanvas2GLView.clear();
		hashGLViewID2GLView.clear();
		hashItems.clear();
	}

	public void unregisterGLView(final AGLView gViews) {

		if (gViews == null)
			return;

		GLCaleydoCanvas parentGLCanvas = (gViews).getParentGLCanvas();

		if (parentGLCanvas != null) {
			parentGLCanvas.removeGLEventListener(gViews);

			if (hashGLCanvas2GLView.containsKey(parentGLCanvas)) {
				hashGLCanvas2GLView.get(parentGLCanvas).remove(gViews);
			}
		}

		hashGLViewID2GLView.remove(gViews.getID());
	}

	public Collection<GLCaleydoCanvas> getAllGLCanvas() {
		return hashGLCanvasID2GLCanvas.values();
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

		// // add all canvas objects before starting animator
		// // this is needed because all the views are fully filled with needed
		// data at that time.
		// for (GLCaleydoCanvas glCanvas : hashGLCanvasID2GLCanvas.values())
		// {
		// fpsAnimator.add(glCanvas);
		// }

		fpsAnimator.start();
		fpsAnimator.setIgnoreExceptions(true);
		fpsAnimator.setPrintExceptions(true);
	}

	public void stopAnimator() {
		if (fpsAnimator.isAnimating())
			fpsAnimator.stop();
	}

	public void registerGLCanvasToAnimator(final GLCaleydoCanvas glCaleydoCanvas) {

		fpsAnimator.add((GLAutoDrawable) glCaleydoCanvas);
	}

	public void unregisterGLCanvasFromAnimator(final GLCaleydoCanvas glCanvas) {
		fpsAnimator.remove(glCanvas);
	}

	public void setActiveSWTView(Composite composite) {
		if (composite == null)
			throw new IllegalStateException("Tried to set a null object as active SWT view.");

		activeSWTView = composite;
	}

	public Composite getActiveSWTView() {
		return activeSWTView;
	}

	/**
	 * Requests busy mode for the application. This method should be called whenever a process needs to stop
	 * any user interaction with the application, e.g. when starting up or when loading multiple pathways.
	 * Usually this should result disabling user events and showing a loading screen animation.
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
	 * Releases a previously requested busy mode. Releases are only performed by passing the originally
	 * requesting object to this method.
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
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					ARcpGLViewPart viewPart = (ARcpGLViewPart) page.showView(serializedView.getViewType());
					AGLView view = viewPart.getGLView();
					view.initFromSerializableRepresentation(serializedView);
					// TODO re-init view with its serializedView

				}
				catch (PartInitException ex) {
					throw new RuntimeException("could not create view with gui-id="
						+ serializedView.getViewType(), ex);
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public AGLView createGLView(Class<? extends AGLView> viewClass, GLCaleydoCanvas glCanvas,
		ViewFrustum viewFrustum) {

		AGLView view;
		try {
			Class[] argTypes = {GLCaleydoCanvas.class, ViewFrustum.class };
			Constructor aConstructor = viewClass.getConstructor(argTypes);
			view = (AGLView) aConstructor.newInstance(glCanvas, viewFrustum);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Cannot create GL2 view " + viewClass);
		}

		return view;
	}

	@Override
	public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener,
		final AEvent event) {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getDisplay().asyncExec(new
		// Runnable() {
		// public void run() {
		listener.handleEvent(event);
		// }
		// });
	}

	@Override
	public void registerEventListeners() {
		GeneralManager generalManager = GeneralManager.get();
		EventPublisher eventPublisher = generalManager.getEventPublisher();

		createGUIViewListener = new CreateGUIViewListener();
		createGUIViewListener.setHandler(this);
		eventPublisher.addListener(CreateGUIViewEvent.class, createGUIViewListener);
	}

	@Override
	public void unregisterEventListeners() {
		GeneralManager generalManager = GeneralManager.get();
		EventPublisher eventPublisher = generalManager.getEventPublisher();

		if (createGUIViewListener != null) {
			eventPublisher.removeListener(createGUIViewListener);
			createGUIViewListener = null;
		}
	}

	/**
	 * Retrieves the {@link DisplayLoopExecution} related to the {@link ViewManager}'s display loop.
	 * 
	 * @return {@link DisplayLoopExecution} for executing code in the display loop
	 */
	public DisplayLoopExecution getDisplayLoopExecution() {
		return displayLoopExecution;
	}
}
