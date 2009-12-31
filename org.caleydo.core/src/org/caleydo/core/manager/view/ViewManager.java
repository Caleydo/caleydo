package org.caleydo.core.manager.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GLCanvas;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.execution.DisplayLoopExecution;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.bookmarking.GLBookmarkManager;
import org.caleydo.core.view.opengl.canvas.cell.GLCell;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.glyph.sliderview.GLGlyphSliderView;
import org.caleydo.core.view.opengl.canvas.grouper.GLGrouper;
import org.caleydo.core.view.opengl.canvas.histogram.GLHistogram;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.remote.dataflipper.GLDataFlipper;
import org.caleydo.core.view.opengl.canvas.remote.viewbrowser.GLPathwayViewBrowser;
import org.caleydo.core.view.opengl.canvas.remote.viewbrowser.GLTissueViewBrowser;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLDendrogram;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.GLParallelCoordinates;
import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.GLScatterplot;
import org.caleydo.core.view.opengl.canvas.tissue.GLTissue;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.swt.browser.GenomeHTMLBrowserViewRep;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;
import org.caleydo.core.view.swt.collab.CollabViewRep;
import org.caleydo.core.view.swt.glyph.GlyphMappingConfigurationViewRep;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;
import org.caleydo.core.view.swt.tabular.TabularDataViewRep;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

/**
 * Manage all canvas, view, ViewReps and GLCanvas objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ViewManager
	extends AManager<IView>
	implements IViewManager, IListenerOwner {
	protected HashMap<Integer, GLCaleydoCanvas> hashGLCanvasID2GLCanvas;

	protected HashMap<GLCaleydoCanvas, ArrayList<AGLEventListener>> hashGLCanvas2GLEventListeners;

	protected HashMap<Integer, AGLEventListener> hashGLEventListenerID2GLEventListener;

	private Animator fpsAnimator;

	private PickingManager pickingManager;

	private ConnectedElementRepresentationManager selectionManager;

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
		selectionManager = new ConnectedElementRepresentationManager();
		infoAreaManager = new GLInfoAreaManager();

		hashGLCanvasID2GLCanvas = new HashMap<Integer, GLCaleydoCanvas>();
		hashGLCanvas2GLEventListeners = new HashMap<GLCaleydoCanvas, ArrayList<AGLEventListener>>();
		hashGLEventListenerID2GLEventListener = new HashMap<Integer, AGLEventListener>();

		fpsAnimator = new FPSAnimator(null, 60);

		busyRequests = new HashSet<Object>();

		registerEventListeners();

		displayLoopExecution = DisplayLoopExecution.get();
		fpsAnimator.add(displayLoopExecution.getDisplayLoopCanvas());

		displayLoopExecution.executeMultiple(selectionManager);
	}

	@Override
	public boolean hasItem(int iItemId) {
		if (hashItems.containsKey(iItemId))
			return true;

		if (hashGLCanvasID2GLCanvas.containsKey(iItemId))
			return true;

		if (hashGLEventListenerID2GLEventListener.containsKey(iItemId))
			return true;

		return false;
	}

	public GLCaleydoCanvas getCanvas(int iItemID) {
		return hashGLCanvasID2GLCanvas.get(iItemID);
	}

	public AGLEventListener getGLEventListener(int iItemID) {
		return hashGLEventListenerID2GLEventListener.get(iItemID);
	}

	@Override
	public IView createView(final EManagedObjectType type, final int iParentContainerID, final String sLabel) {
		IView view = null;

		switch (type) {
			case VIEW:
				break;
			case VIEW_SWT_TABULAR_DATA_VIEWER:
				view = new TabularDataViewRep(iParentContainerID, sLabel);
				break;
			case VIEW_SWT_BROWSER_GENERAL:
				view = new HTMLBrowserViewRep(iParentContainerID, sLabel);
				break;
			case VIEW_SWT_BROWSER_GENOME:
				view = new GenomeHTMLBrowserViewRep(iParentContainerID, sLabel);
				break;
			case VIEW_SWT_GLYPH_MAPPINGCONFIGURATION:
				view = new GlyphMappingConfigurationViewRep(iParentContainerID, sLabel);
				break;
			case VIEW_SWT_COLLAB:
				view = new CollabViewRep(iParentContainerID, sLabel);
				break;
			default:
				throw new IllegalStateException("ViewManager.createView() failed due to unhandled type ["
					+ type.toString() + "]");
		}

		registerItem(view);

		return view;
	}

	@Override
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

	@Override
	public AGLEventListener createGLEventListener(ECommandType type, GLCaleydoCanvas glCanvas,
		final String sLabel, final IViewFrustum viewFrustum) {
		GeneralManager.get().getLogger().log(
			new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Creating GL canvas view from type: [" + type
				+ "] and label: [" + sLabel + "]"));

		AGLEventListener glEventListener = null;

		switch (type) {
			case CREATE_GL_HEAT_MAP_3D:

				glEventListener = new GLHeatMap(glCanvas, sLabel, viewFrustum);
				break;
			case CREATE_GL_SCATTERPLOT:
				glEventListener = new GLScatterplot(glCanvas, sLabel, viewFrustum);
				break;
			case CREATE_GL_PROPAGATION_HEAT_MAP_3D:
				glEventListener = new GLBookmarkManager(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_TEXTURE_HEAT_MAP_3D:
				glEventListener = new GLHierarchicalHeatMap(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_PATHWAY_3D:
				glEventListener = new GLPathway(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_PARALLEL_COORDINATES:
				glEventListener = new GLParallelCoordinates(glCanvas, sLabel, viewFrustum);
				break;
			case CREATE_GL_GLYPH:
				glEventListener = new GLGlyph(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_GLYPH_SLIDER:
				glEventListener = new GLGlyphSliderView(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_CELL:
				glEventListener = new GLCell(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_TISSUE:
				glEventListener = new GLTissue(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_BUCKET_3D:
				glEventListener =
					new GLRemoteRendering(glCanvas, sLabel, viewFrustum,
						ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET);
				break;

			case CREATE_GL_JUKEBOX_3D:
				glEventListener =
					new GLRemoteRendering(glCanvas, sLabel, viewFrustum,
						ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX);
				break;

			case CREATE_GL_DATA_FLIPPER:
				glEventListener = new GLDataFlipper(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_TISSUE_VIEW_BROWSER:
				glEventListener = new GLTissueViewBrowser(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_PATHWAY_VIEW_BROWSER:
				glEventListener = new GLPathwayViewBrowser(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_RADIAL_HIERARCHY:
				glEventListener = new GLRadialHierarchy(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_HISTOGRAM:
				glEventListener = new GLHistogram(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_GROUPER:
				glEventListener = new GLGrouper(glCanvas, sLabel, viewFrustum);
				break;

			case CREATE_GL_DENDROGRAM_HORIZONTAL:
				glEventListener = new GLDendrogram(glCanvas, sLabel, viewFrustum, true);
				break;

			case CREATE_GL_DENDROGRAM_VERTICAL:
				glEventListener = new GLDendrogram(glCanvas, sLabel, viewFrustum, false);
				break;

			default:
				throw new RuntimeException(
					"ViewJoglManager.createGLCanvasUser() failed due to unhandled type [" + type.toString()
						+ "]");
		}

		registerGLEventListenerByGLCanvas(glCanvas, glEventListener);

		return glEventListener;
	}

	@Override
	public boolean registerGLCanvas(final GLCaleydoCanvas glCanvas) {
		int iGLCanvasID = glCanvas.getID();

		if (hashGLCanvasID2GLCanvas.containsKey(iGLCanvasID)) {
			generalManager.getLogger().log(
				new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID, "GL Canvas with ID " + iGLCanvasID
					+ " is already registered! Do nothing."));

			return false;
		}

		hashGLCanvasID2GLCanvas.put(iGLCanvasID, glCanvas);
		// fpsAnimator.add(glCanvas);

		return true;
	}

	@Override
	public boolean unregisterGLCanvas(final GLCaleydoCanvas glCanvas) {

		if (glCanvas == null)
			return false;

		fpsAnimator.remove(glCanvas);
		hashGLCanvasID2GLCanvas.remove(glCanvas.getID());
		hashGLCanvas2GLEventListeners.remove(glCanvas);

		return true;
	}

	@Override
	public void registerGLEventListenerByGLCanvas(final GLCaleydoCanvas glCanvas,
		final AGLEventListener gLEventListener) {
		hashGLEventListenerID2GLEventListener.put(gLEventListener.getID(), gLEventListener);

		// This is the case when a view is rendered remote
		if (glCanvas == null)
			return;

		if (!hashGLCanvas2GLEventListeners.containsKey(glCanvas)) {
			hashGLCanvas2GLEventListeners.put(glCanvas, new ArrayList<AGLEventListener>());
		}

		hashGLCanvas2GLEventListeners.get(glCanvas).add(gLEventListener);
		glCanvas.addGLEventListener(gLEventListener);
	}

	@Override
	public void cleanup() {

		hashGLCanvasID2GLCanvas.clear();
		hashGLCanvas2GLEventListeners.clear();
		hashGLEventListenerID2GLEventListener.clear();
		hashItems.clear();
	}

	@Override
	public void unregisterGLEventListener(final AGLEventListener glEventListener) {

		if (glEventListener == null)
			return;

		GLCaleydoCanvas parentGLCanvas = (glEventListener).getParentGLCanvas();

		if (parentGLCanvas != null) {
			parentGLCanvas.removeGLEventListener(glEventListener);

			if (hashGLCanvas2GLEventListeners.containsKey(parentGLCanvas)) {
				hashGLCanvas2GLEventListeners.get(parentGLCanvas).remove(glEventListener);
			}
		}

		hashGLEventListenerID2GLEventListener.remove(glEventListener.getID());
	}

	@Override
	public Collection<GLCaleydoCanvas> getAllGLCanvasUsers() {
		return hashGLCanvasID2GLCanvas.values();
	}

	@Override
	public Collection<AGLEventListener> getAllGLEventListeners() {
		return hashGLEventListenerID2GLEventListener.values();
	}

	public PickingManager getPickingManager() {
		return pickingManager;
	}

	public ConnectedElementRepresentationManager getConnectedElementRepresentationManager() {
		return selectionManager;
	}

	public GLInfoAreaManager getInfoAreaManager() {
		return infoAreaManager;
	}

	@Override
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

	@Override
	public void stopAnimator() {
		if (fpsAnimator.isAnimating())
			fpsAnimator.stop();
	}

	@Override
	public void registerGLCanvasToAnimator(final GLCanvas glCanvas) {
		fpsAnimator.add(glCanvas);
	}

	@Override
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

	@Override
	public void requestBusyMode(Object requestInstance) {
		if (requestInstance == null) {
			throw new IllegalArgumentException("requestInstance must not be null");
		}
		synchronized (busyRequests) {
			if (busyRequests.isEmpty()) {
				for (AGLEventListener tmpGLEventListener : getAllGLEventListeners()) {
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

	@Override
	public void releaseBusyMode(Object requestInstance) {
		if (requestInstance == null) {
			throw new IllegalArgumentException("requestInstance must not be null");
		}
		synchronized (busyRequests) {
			if (busyRequests.contains(requestInstance)) {
				busyRequests.remove(requestInstance);
			}
			if (busyRequests.isEmpty()) {
				for (AGLEventListener tmpGLEventListener : getAllGLEventListeners()) {
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

	private void registerEventListeners() {
		IGeneralManager generalManager = GeneralManager.get();
		IEventPublisher eventPublisher = generalManager.getEventPublisher();

		createGUIViewListener = new CreateGUIViewListener();
		createGUIViewListener.setHandler(this);
		eventPublisher.addListener(CreateGUIViewEvent.class, createGUIViewListener);
	}

	@SuppressWarnings("unused")
	private void unregisterEventListeners() {
		IGeneralManager generalManager = GeneralManager.get();
		IEventPublisher eventPublisher = generalManager.getEventPublisher();

		if (createGUIViewListener != null) {
			eventPublisher.removeListener(createGUIViewListener);
			createGUIViewListener = null;
		}
	}

	@Override
	public DisplayLoopExecution getDisplayLoopExecution() {
		return displayLoopExecution;
	}

}
