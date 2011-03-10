package org.caleydo.view.visbricks.brick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;

/**
 * Individual Brick for VisBricks
 * 
 * @author Alexander Lex
 * 
 */
public class GLBrick extends AGLView implements IDataDomainSetBasedView,
		IGLRemoteRenderingView {

	public final static String VIEW_ID = "org.caleydo.view.brick";
	
	public static final int HEATMAP_VIEW = 0;
	public static final int PARCOORDS_VIEW = 1;
	public static final int HISTOGRAM_VIEW = 2;

	private LayoutManager templateRenderer;
	private BrickLayoutTemplate brickLayout;

	private AGLView currentRemoteView;

	private ElementLayout wrappingLayout;
	
	private Map<Integer, AGLView> views;
	private Map<Integer, LayoutRenderer> viewLayoutRenderers;

	private int baseDisplayListIndex;
	private boolean isBaseDisplayListDirty = true;
	private ISet set;
	// private GLHeatMap heatMap;
	private ASetBasedDataDomain dataDomain;

	private HashMap<EPickingType, HashMap<Integer, IPickingListener>> pickingListeners;

	public GLBrick(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);
		viewType = GLBrick.VIEW_ID;
		
		views = new HashMap<Integer, AGLView>();
		viewLayoutRenderers = new HashMap<Integer, LayoutRenderer>();

		pickingListeners = new HashMap<EPickingType, HashMap<Integer, IPickingListener>>();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GL2 gl) {
		baseDisplayListIndex = gl.glGenLists(1);
		
		if(set == null)
			set = dataDomain.getSet();
		
		if(contentVA == null)
			contentVA = dataDomain.getContentVA(Set.CONTENT);

		templateRenderer = new LayoutManager(viewFrustum);
		brickLayout = new BrickLayoutTemplate(this);

		brickLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());

		HeatMapCreator heatMapCreator = new HeatMapCreator();
		AGLView heatMap = heatMapCreator.createRemoteView(this,
				gl, glMouseListener);
		LayoutRenderer heatMapLayoutRenderer = new ViewLayoutRenderer(heatMap);
		views.put(HEATMAP_VIEW, heatMap);
		viewLayoutRenderers.put(HEATMAP_VIEW, heatMapLayoutRenderer);
		
		ParCoordsCreator parCoordsCreator = new ParCoordsCreator();
		AGLView parCoords = parCoordsCreator.createRemoteView(this,
				gl, glMouseListener);
		LayoutRenderer parCoordsLayoutRenderer = new ViewLayoutRenderer(parCoords);
		views.put(PARCOORDS_VIEW, parCoords);
		viewLayoutRenderers.put(PARCOORDS_VIEW, parCoordsLayoutRenderer);
		
		HistogramCreator histogramCreator = new HistogramCreator();
		AGLView histogram = histogramCreator.createRemoteView(this,
				gl, glMouseListener);
		LayoutRenderer histogramLayoutRenderer = new ViewLayoutRenderer(histogram);
		views.put(HISTOGRAM_VIEW, histogram);
		viewLayoutRenderers.put(HISTOGRAM_VIEW, histogramLayoutRenderer);
		
		currentRemoteView = histogram;
		
		brickLayout.setViewRenderer(histogramLayoutRenderer);
		templateRenderer.setTemplate(brickLayout);
		templateRenderer.updateLayout();

		// if (heatMap == null) {
		// templateRenderer = new LayoutManager(viewFrustum);
		// brickLayout = new BrickLayoutTemplate(this);
		//
		// brickLayout.setPixelGLConverter(parentGLCanvas
		// .getPixelGLConverter());
		//
		// heatMap = (GLHeatMap) GeneralManager
		// .get()
		// .getViewGLCanvasManager()
		// .createGLView(
		// GLHeatMap.class,
		// getParentGLCanvas(),
		//
		// new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC,
		// 0, 1, 0, 1, -1, 1));
		//
		// heatMap.setRemoteRenderingGLView(this);
		// heatMap.setSet(set);
		// heatMap.setDataDomain(dataDomain);
		// heatMap.setRenderTemplate(new BrickHeatMapTemplate(heatMap));
		// heatMap.initialize();
		// heatMap.initRemote(gl, this, glMouseListener);
		// if (this.contentVA != null)
		// heatMap.setContentVA(contentVA);
		// brickLayout.setViewRenderer(new ViewLayoutRenderer(heatMap));
		// templateRenderer.setTemplate(brickLayout);
		// templateRenderer.updateLayout();
		// }

	}

	@Override
	protected void initLocal(GL2 gl) {
		init(gl);

	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView,
			GLMouseListener glMouseListener) {
		init(gl);

	}

	@Override
	public void display(GL2 gl) {
		currentRemoteView.processEvents();
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		if (isBaseDisplayListDirty)
			buildBaseDisplayList(gl);

		templateRenderer.render(gl);

		gl.glCallList(baseDisplayListIndex);
	}

	@Override
	protected void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
		checkForHits(gl);
	}

	private void buildBaseDisplayList(GL2 gl) {
		gl.glNewList(baseDisplayListIndex, GL2.GL_COMPILE);
		// templateRenderer.updateLayout();

		gl.glEndList();
		isBaseDisplayListDirty = false;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		super.reshape(drawable, x, y, width, height);
		if (templateRenderer != null)
			templateRenderer.updateLayout();
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int pickingID, Pick pick) {

		HashMap<Integer, IPickingListener> map = pickingListeners
				.get(pickingType);
		if (map == null)
			return;

		IPickingListener pickingListener = map.get(pickingID);

		if (pickingListener == null)
			return;

		switch (pickingMode) {
		case CLICKED:
			pickingListener.clicked(pick);
			break;
		case DOUBLE_CLICKED:
			pickingListener.doubleClicked(pick);
			break;
		case RIGHT_CLICKED:
			pickingListener.rightClicked(pick);
			break;
		case MOUSE_OVER:
			pickingListener.mouseOver(pick);
			break;
		case DRAGGED:
			pickingListener.dragged(pick);
			break;
		}

		// switch (pickingType) {
		// case BRICK_CLUSTER:
		// switch (pickingMode) {
		// case CLICKED:
		// // set.cluster(clusterState);
		// System.out.println("cluster");
		//
		// getParentGLCanvas().getParentComposite().getDisplay()
		// .asyncExec(new Runnable() {
		// @Override
		// public void run() {
		// StartClusteringDialog dialog = new StartClusteringDialog(
		// new Shell(), dataDomain);
		// dialog.open();
		// ClusterState clusterState = dialog
		// .getClusterState();
		//
		// StartClusteringEvent event = null;
		// // if (clusterState != null && set != null)
		//
		// event = new StartClusteringEvent(clusterState,
		// set.getID());
		// event.setDataDomainType(dataDomain
		// .getDataDomainType());
		// GeneralManager.get().getEventPublisher()
		// .triggerEvent(event);
		// }
		// });
		//
		// }
		// }

	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		// TODO Auto-generated method stub
		return dataDomain;
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		this.contentVA = contentVA;
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		if (templateRenderer != null)
			templateRenderer.updateLayout();
	}

	public void addPickingListener(IPickingListener pickingListener,
			EPickingType pickingType, int externalID) {
		HashMap<Integer, IPickingListener> map = pickingListeners
				.get(pickingType);
		if (map == null) {
			map = new HashMap<Integer, IPickingListener>();
			pickingListeners.put(pickingType, map);
		}

		map.put(externalID, pickingListener);

	}

	public ISet getSet() {
		return set;
	}
	
	public void setRemoteView(int viewType) {
		AGLView view = views.get(viewType);
		if(view == null)
			return;
		
		currentRemoteView = view;
		LayoutRenderer viewRenderer = viewLayoutRenderers.get(viewType);
		brickLayout.setViewRenderer(viewRenderer);
		templateRenderer.updateLayout();
	}
}
