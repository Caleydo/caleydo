package org.caleydo.core.view;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.CreateGUIViewEvent;
import org.caleydo.core.event.view.NewViewEvent;
import org.caleydo.core.event.view.ViewClosedEvent;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.execution.DisplayLoopExecution;
import org.caleydo.core.view.listener.CreateGUIViewListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
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

	private HashMap<GLCanvas, ArrayList<AGLView>> hashGLCanvas2GLView =
		new HashMap<GLCanvas, ArrayList<AGLView>>();

	private HashMap<Integer, AGLView> hashGLViewID2GLView = new HashMap<Integer, AGLView>();

	private HashMap<ARcpGLViewPart, IView> hashRCP2View = new HashMap<ARcpGLViewPart, IView>();

	private HashMap<IView, ARcpGLViewPart> hashView2RCP = new HashMap<IView, ARcpGLViewPart>();

	private FPSAnimator fpsAnimator;

	private PickingManager pickingManager = new PickingManager();

	private ConnectedElementRepresentationManager connectedElementRepManager =
		ConnectedElementRepresentationManager.get();

	private GLInfoAreaManager infoAreaManager = new GLInfoAreaManager();

	private Set<Object> busyRequests = new HashSet<Object>();

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
		registerEventListeners();
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

		NewViewEvent event = new NewViewEvent(glView);
		event.setSender(this);
		generalManager.getEventPublisher().triggerEvent(event);
	}

	public void registerGLEventListenerByGLCanvas(final GLCanvas glCanvas, final AGLView glView) {

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
	 * Associate an RCP view with the Caleydo view contained inside the RCP view.
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
	 * Remove association between an RCP view and the Caleydo view contained inside the RCP view.
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

	public void unregisterGLView(final AGLView glView) {

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
			initAnimator();

		fpsAnimator.start();
		fpsAnimator.setIgnoreExceptions(true);
		fpsAnimator.setPrintExceptions(true);
	}

	public void stopAnimator() {
		if (fpsAnimator != null && fpsAnimator.isAnimating())
			fpsAnimator.stop();
	}

	public void registerGLCanvasToAnimator(final GLCanvas glCaleydoCanvas) {

		// Lazy creation of animator
		if (fpsAnimator == null) {
			initAnimator();
		}

		fpsAnimator.add(glCaleydoCanvas);
	}

	private void initAnimator() {
		fpsAnimator = new FPSAnimator(60);

		displayLoopExecution = DisplayLoopExecution.get();
		fpsAnimator.add((GLAutoDrawable) displayLoopExecution.getDisplayLoopCanvas());
		displayLoopExecution.executeMultiple(connectedElementRepManager);
	}

	public void unregisterGLCanvasFromAnimator(final GLCanvas glCanvas) {
		fpsAnimator.remove(glCanvas);
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
	public AGLView createGLView(Class<? extends AGLView> viewClass, GLCanvas glCanvas,
		Composite parentComposite, ViewFrustum viewFrustum) {

		AGLView view;
		try {
			Class[] argTypes = { GLCanvas.class, Composite.class, ViewFrustum.class };
			Constructor aConstructor = viewClass.getConstructor(argTypes);
			view = (AGLView) aConstructor.newInstance(glCanvas, parentComposite, viewFrustum);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Cannot create GL view " + viewClass);
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

		// lazy creation of animator and display loop
		if (displayLoopExecution == null)
			initAnimator();

		return displayLoopExecution;
	}
}
