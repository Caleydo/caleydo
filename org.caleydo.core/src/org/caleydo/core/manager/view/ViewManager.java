package org.caleydo.core.manager.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GLCanvas;

import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.execution.DisplayLoopExecution;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.view.creator.AGLViewCreator;
import org.caleydo.core.manager.view.creator.ASWTViewCreator;
import org.caleydo.core.manager.view.creator.IViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.BundleException;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

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

	private Animator fpsAnimator;

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

	private ArrayList<IViewCreator> glViewCreators;

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

		glViewCreators = new ArrayList<IViewCreator>();
	}

	
	public void init() {
		fpsAnimator = new FPSAnimator(60);

		displayLoopExecution = DisplayLoopExecution.get();
		fpsAnimator.add(displayLoopExecution.getDisplayLoopCanvas());

		displayLoopExecution.executeMultiple(connectedElementRepManager);
	}

	
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

	
	public IView createView(String viewType, int parentContainerID, String label) {
		IView view = null;

		for (IViewCreator viewCreator : glViewCreators) {

			if (viewCreator instanceof ASWTViewCreator && viewCreator.getViewType().equals(viewType)) {

				view = ((ASWTViewCreator) viewCreator).createView(parentContainerID, label);
				break;
			}
		}

		registerItem(view);

		return view;
	}

	
	public IView createGLView(final EManagedObjectType useViewType, final int iParentContainerID,
		final String sLabel) {
		IView view = null;

		switch (useViewType) {
			case VIEW_GL_CANVAS:
				view = new SwtJoglGLCanvasViewRep(iParentContainerID, sLabel);
				break;

			default:
				throw new RuntimeException("Unhandled view type [" + useViewType.toString() + "]");
		}

		registerItem(view);

		return view;
	}

	
	public AGLView createGLView(String viewID, GLCaleydoCanvas glCanvas, final String label,
		final IViewFrustum viewFrustum) {
		GeneralManager
			.get()
			.getLogger()
			.log(
				new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Creating GL canvas view from type: ["
					+ viewID + "] and label: [" + label + "]"));

		AGLView glView = null;

		// Force plugins of start views to load
		try {
			if (viewID.contains("hierarchical") || viewID.contains("vertical")
				|| viewID.contains("horizontal"))
				Platform.getBundle("org.caleydo.view.heatmap").start();
			else
				Platform.getBundle(viewID).start();
		}
		catch (BundleException e) {
			// this is ok if the view is a subview contained in an already loaded plugin
		}
		catch (NullPointerException e) {
		}

		for (IViewCreator glViewCreator : glViewCreators) {

			if (glViewCreator instanceof AGLViewCreator && glViewCreator.getViewType().equals(viewID)) {

				glView = ((AGLViewCreator) glViewCreator).createGLView(glCanvas, label, viewFrustum);
				break;
			}
		}

		if (glView == null) {
			throw new RuntimeException(
				"Unable to create GL view because the requested view plugin is not available: " + viewID);
		}

		registerGLEventListenerByGLCanvas(glCanvas, glView);

		return glView;
	}

	
	public boolean registerGLCanvas(final GLCaleydoCanvas glCanvas) {
		int iGLCanvasID = glCanvas.getID();

		if (hashGLCanvasID2GLCanvas.containsKey(iGLCanvasID)) {
			generalManager.getLogger().log(
				new Status(IStatus.WARNING, GeneralManager.PLUGIN_ID, "GL Canvas with ID " + iGLCanvasID
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

	
	public void registerGLEventListenerByGLCanvas(final GLCaleydoCanvas glCanvas,
		final AGLView gLEventListener) {
		hashGLViewID2GLView.put(gLEventListener.getID(), gLEventListener);

		// This is the case when a view is rendered remote
		if (glCanvas == null)
			return;

		if (!hashGLCanvas2GLView.containsKey(glCanvas)) {
			hashGLCanvas2GLView.put(glCanvas, new ArrayList<AGLView>());
		}

		hashGLCanvas2GLView.get(glCanvas).add(gLEventListener);
		glCanvas.addGLEventListener(gLEventListener);
	}

	/**
	 * Removes all views, canvas and GL event listeners
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

	
	public void registerGLCanvasToAnimator(final GLCanvas glCanvas) {

		fpsAnimator.add(glCanvas);
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

	public void createSWTView(ASerializedView serializedView) {
		generalManager.getGUIBridge().createView(serializedView);
	}

	
	public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener,
		final AEvent event) {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getDisplay().asyncExec(new
		// Runnable() {
		// public void run() {
		listener.handleEvent(event);
		// }
		// });
	}

	
	public void registerEventListeners() {
		GeneralManager generalManager = GeneralManager.get();
		EventPublisher eventPublisher = generalManager.getEventPublisher();

		createGUIViewListener = new CreateGUIViewListener();
		createGUIViewListener.setHandler(this);
		eventPublisher.addListener(CreateGUIViewEvent.class, createGUIViewListener);
	}

	@SuppressWarnings("unused")
	
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

	
	public void addViewCreator(IViewCreator glViewCreator) {
		glViewCreators.add(glViewCreator);
	}

	
	public IViewCreator getViewCreator(String viewType) {

		for (IViewCreator glViewCreator : glViewCreators) {
			if (glViewCreator.getViewType().equals(viewType)) {
				return glViewCreator;
			}
		}

		throw new IllegalStateException("Cannot find view creator for " + viewType);
	}
}
