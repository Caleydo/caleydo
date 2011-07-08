package org.caleydo.view.heatmap.uncertainty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.OverviewDetailConnectorRenderer;
import org.caleydo.view.heatmap.heatmap.template.UncertaintyDetailHeatMapTemplate;
import org.eclipse.jface.layout.PixelConverter;

/**
 * Uncertainty heat map view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Clemens Holzhüter
 */

public class GLUncertaintyHeatMap extends AStorageBasedView implements
		IViewCommandHandler, ISelectionUpdateHandler, IGLRemoteRenderingView {

	public static enum UncertaintyColors {
		VISUAL_VALID, VISUAL_UNCERTAIN, DATA_VALID, DATA_UNCERTAIN, DATA2_VALID, DATA2_UNCERTAIN, DATA3_VALID, DATA3_UNCERTAIN, BACKGROUND
	}

	public final static String VIEW_ID = "org.caleydo.view.heatmap.uncertainty";

	public final static float[][] DATA_VALID = { { 0.90f, 0.90f, 0.90f, 1f },
			{ 0.80f, 0.80f, 0.80f, 1f }, { 0.0f, 0.70f, 0.70f, 1f } };
	public final static float[][] DATA_UNCERTAIN = {
			{ 0.90f, 0.90f, 0.20f, 1f }, { 0.80f, 0.80f, 0.20f, 1f },
			{ 0.7f, 0.70f, 0.20f, 1f } };

	public final static float[] BACKGROUND = { 0.5f, 0.5f, 0.5f, 1f };
	public final static float[] VIS_UNC = { 0.7f, 0.7f, 0.7f, 1f };

	private HeatMapRenderStyle renderStyle;

	private OverviewRenderer overviewHeatMap;
	private GLHeatMap detailHeatMap;

	private LayoutManager templateRenderer;
	private LayoutTemplate template;

	private Row baseRow;
	private Row contentRow;
	private Column overviewLayout;
	private ElementLayout detailLayout;
	private ElementLayout overviewDetailConnectorLayout;

	private ColorMapper colorMapper = ColorMappingManager.get()
			.getColorMapping(EColorMappingType.GENE_EXPRESSION);

	private boolean updateVisualUncertainty = true;

	HashMap<Integer, ArrayList<Double>> uncertaintyValues = new HashMap<Integer, ArrayList<Double>>();
	HashMap<Integer, Integer> maxUncertaintyValueIndex = new HashMap<Integer, Integer>();
	

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLUncertaintyHeatMap(GLCaleydoCanvas glCanvas,
			final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum);
		viewType = GLUncertaintyHeatMap.VIEW_ID;
	}

	@Override
	public void init(GL2 gl) {

		
		templateRenderer = new LayoutManager(this.viewFrustum);
		if (template == null)
			template = new LayoutTemplate();

		templateRenderer.setTemplate(template);

		baseRow = new Row("baseRow");
		template.setBaseElementLayout(baseRow);

		ElementLayout sideSpacer = new ElementLayout("sideSpacer");
		sideSpacer.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		sideSpacer.setPixelSizeX(15);

		ElementLayout topSpacer = new ElementLayout("topSpacer");
		topSpacer.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		topSpacer.setPixelSizeY(15);

		Column baseColumnn = new Column("baseColumn");

		baseRow.append(sideSpacer);
		baseRow.append(baseColumnn);
		baseRow.append(sideSpacer);

		contentRow = new Row("contentRow");
	
		baseColumnn.append(topSpacer);
		baseColumnn.append(contentRow);
		baseColumnn.append(topSpacer);

		overviewLayout = new Column("overviewLayout");
		overviewLayout.setDebug(false);
		overviewLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		overviewLayout.setPixelSizeX(90);

		overviewDetailConnectorLayout = new Column("overviewDetailConnectorLayout");
		overviewDetailConnectorLayout.setDebug(false);
		overviewDetailConnectorLayout.setPixelGLConverter(parentGLCanvas
				.getPixelGLConverter());
		overviewDetailConnectorLayout.setPixelSizeX(60);

		detailLayout = new ElementLayout("detailLayout");
		detailLayout.setDebug(false);

		contentRow.append(overviewLayout);
		contentRow.append(overviewDetailConnectorLayout);
		contentRow.append(detailLayout);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

		initUncertaintyArrays();
		createOverviewHeatMap(gl);
		createDetailHeatMap(gl);

		OverviewDetailConnectorRenderer overviewDetailConnectorRenderer = new OverviewDetailConnectorRenderer(
				overviewHeatMap, detailHeatMap);
		overviewDetailConnectorLayout.setRenderer(overviewDetailConnectorRenderer);
		
		templateRenderer.updateLayout();

//		templateRenderer = new LayoutManager(this.viewFrustum);
//		if (template == null)
//			template = new LayoutTemplate();
//
//		templateRenderer.setTemplate(template);
//
//		baseRow = new Row("baseRow");
//		template.setBaseElementLayout(baseRow);
//
//		overviewLayout = new Column("overviewLayout");
//		overviewLayout.setDebug(false);
//		overviewLayout
//				.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
//		overviewLayout.setPixelSizeX(90);
//
//		overviewDetailConnectorLayout = new Column(
//				"overviewDetailConnectorLayout");
//		overviewDetailConnectorLayout.setDebug(false);
//		overviewDetailConnectorLayout.setPixelGLConverter(parentGLCanvas
//				.getPixelGLConverter());
//		overviewDetailConnectorLayout.setPixelSizeX(60);
//
//		detailLayout = new ElementLayout("detailLayout");
//		detailLayout.setDebug(false);
//
//		baseRow.append(overviewLayout);
//		baseRow.append(overviewDetailConnectorLayout);
//		baseRow.append(detailLayout);
//
//		super.renderStyle = renderStyle;
//		detailLevel = DetailLevel.HIGH;
//
//		createOverviewHeatMap(gl);
//		createDetailHeatMap(gl);
//
//		OverviewDetailConnectorRenderer overviewDetailConnectorRenderer = new OverviewDetailConnectorRenderer(
//				overviewHeatMap, detailHeatMap);
//		overviewDetailConnectorLayout
//				.setRenderer(overviewDetailConnectorRenderer);
//
//		templateRenderer.updateLayout();

	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		super.reshape(drawable, x, y, width, height);
		templateRenderer.updateLayout();
		updateVisualUncertainty = true;
	}

	/**
	 * Create embedded heat map
	 * 
	 * @param overviewLayout2
	 * 
	 * @param
	 */
	private void createOverviewHeatMap(GL2 gl) {

		overviewHeatMap = new OverviewRenderer(this, overviewLayout);
		overviewLayout.setRenderer(overviewHeatMap);
		overviewHeatMap.init();
	}

	private void createDetailHeatMap(GL2 gl) {
		detailHeatMap = (GLHeatMap) GeneralManager
				.get()
				.getViewGLCanvasManager()
				.createGLView(
						GLHeatMap.class,
						this.getParentGLCanvas(),
						new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
								1, 0, 1, -1, 1));

		detailHeatMap.setDataDomain(dataDomain);
		detailHeatMap.setRemoteRenderingGLView(this);
		detailHeatMap.setSet(set);
		detailHeatMap.setRenderTemplate(new UncertaintyDetailHeatMapTemplate(
				detailHeatMap, this));
		detailHeatMap.initialize();
		detailHeatMap.initRemote(gl, this, glMouseListener);

		if (contentVA != null)
			detailHeatMap.setContentVA(contentVA);

		ViewLayoutRenderer detailHeatMapLayoutRenderer = new ViewLayoutRenderer(
				detailHeatMap);

		detailLayout.setRenderer(detailHeatMapLayoutRenderer);

		overviewHeatMap.setDetailHeatMap(detailHeatMap);
	}

	@Override
	public void displayLocal(GL2 gl) {

		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);

		// if (overviewHeatMap != null)
		// overviewHeatMap.processEvents();
	}

	@Override
	public void displayRemote(GL2 gl) {
		throw new IllegalStateException(
				"This view cannot be rendered remotely!");
	}

	@Override
	public void display(GL2 gl) {
		templateRenderer.render(gl);
		PixelGLConverter pc = this.getParentGLCanvas().getPixelGLConverter();
		if (updateVisualUncertainty) {

			// very dirty
			for (ClusterRenderer clusterRenderer : overviewHeatMap
					.getClusterRendererList()) {
				ArrayList<Float> uncertaintyVA = new ArrayList<Float>();
				if (clusterRenderer.textureRenderer != null
						&& clusterRenderer.textureRenderer.heatmapLayout != null
						&& clusterRenderer.visUncBarTextureRenderer != null) {
					VisualUncertaintyUtil.calcVisualUncertainty(gl, pc,
							clusterRenderer.textureRenderer.heatmapLayout,
							clusterRenderer.textureRenderer, uncertaintyVA);

					clusterRenderer.visUncBarTextureRenderer
							.initTextures(uncertaintyVA);
				}
			}

			updateVisualUncertainty = false;
		}

	}

	@Override
	public String getShortInfo() {

		return "LayoutTemplate Caleydo View";
	}

	@Override
	public String getDetailedInfo() {
		return "LayoutTemplate Caleydo View";

	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalID, Pick pick) {

		switch (pickingType) {

		// handling the groups/clusters of genes
		case HEAT_MAP_CLUSTER_GROUP:
			switch (pickingMode) {
			case CLICKED:

				overviewHeatMap.setSelectedGroup(externalID);
				ContentGroupList groupList = contentVA.getGroupList();

				// null if data set is not clustered
				if (groupList == null)
					break;

				ArrayList<Integer> clusterElements = contentVA
						.getIDsOfGroup(groupList.get(externalID).getID());
				ContentVirtualArray clusterVA = new ContentVirtualArray(
						Set.CONTENT, clusterElements);
				detailHeatMap.setContentVA(clusterVA);

				setDisplayListDirty();
				break;

			case DRAGGED:
				break;

			case MOUSE_OVER:
				break;
			}
			break;
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedUncertaintyHeatMapView serializedForm = new SerializedUncertaintyHeatMapView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		super.handleSelectionUpdate(selectionDelta, scrollToSelection, info);

	}

	@Override
	public void handleRedrawView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUpdateView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

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
	public List<AGLView> getRemoteRenderedViews() {
		// ArrayList<AGLView> remoteRenderedViews = new ArrayList<AGLView>();
		// remoteRenderedViews.add(overviewHeatMap);
		// return remoteRenderedViews;
		return null;
	}

	@Override
	public void renderContext(boolean bRenderContext) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void reactOnContentVAChanges(ContentVADelta delta) {

		super.reactOnContentVAChanges(delta);

		setDisplayListDirty();
		initUncertaintyArrays();
		overviewHeatMap.init();
		
	}

	@Override
	protected void initLists() {

		if (bRenderOnlyContext)
			contentVAType = ISet.CONTENT_CONTEXT;
		else
			contentVAType = ISet.CONTENT;

		contentVA = dataDomain.getContentVA(contentVAType);
		storageVA = dataDomain.getStorageVA(storageVAType);

		// In case of importing group info
		// if (set.isGeneClusterInfo())
		// contentVA.setGroupList(set.getContentGroupList());
		// if (set.isExperimentClusterInfo())
		// storageVA.setGroupList(set.getStorageGroupList());

		contentSelectionManager.setVA(contentVA);
		storageSelectionManager.setVA(storageVA);
		setDisplayListDirty();
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(IDType idType,
			int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	public ColorMapper getColorMapper() {
		return colorMapper;
	}

	@Override
	public void replaceContentVA(int setID, String dataDomainType, String vaType) {

		super.replaceContentVA(setID, dataDomainType, vaType);

		overviewHeatMap.init();
		updateVisualUncertainty = true;
	}

	public HeatMapRenderStyle getRenderStyle() {
		return renderStyle;
	}

	public ContentSelectionManager getContentSelectionManager() {
		return contentSelectionManager;

	}

	/*
	 * generating MAX uncertainties
	 */
	public void initUncertaintyArrays() {

		uncertaintyValues.clear();
		maxUncertaintyValueIndex.clear();

		float[] snr = set.getNormalizedUncertainty();

		// getFold
		Collection<double[]> fold = this.set.getStatisticsResult()
				.getAllFoldChangeUncertainties();

		// init first with snr values
		
		for (Integer contentID: contentVA) {

			// init 2 levels
			int uncertainties = 2;
			ArrayList<Double> geneUnc = new ArrayList<Double>(5);
			// getSNR
			double unc1 = 0;
			if (snr != null) {
				unc1 = snr[contentID];
			} else {
				unc1 = 1;
			}
			geneUnc.add(unc1);
			uncertaintyValues.put(contentID,geneUnc);
			maxUncertaintyValueIndex.put(contentID, 0);
		}
		
		
		
		// init now with Foldchange
		
		int 	contentID = 0;
		for (double[] val : fold) {

			double unc2 = 0f;
			// getMaxFoldChangeUnc
			for (int i = 0; i < val.length; i++) {
				double temp = val[i];
				if (unc2 < temp)
					unc2 = temp;
			}
			ArrayList<Double> geneUnc = uncertaintyValues.get(contentID);
			geneUnc.add(unc2);
			if (geneUnc.get(contentID) >= unc2) {
				//maxUncertaintyValueIndex.set(contentID, element);
			} else {
				maxUncertaintyValueIndex.put(contentID, 1);
			}
			contentID++;
		}
		
	}

	public float getMaxUncertainty(int contentIndex) {
		float ret = 0;
		Integer uncLevel = maxUncertaintyValueIndex.get(contentIndex);
		if (uncLevel != null) {
			ret = uncertaintyValues.get(contentIndex).get(uncLevel).floatValue();
		}
		return ret;
	}

	public ArrayList<Double> getUncertainty(int contentID) {

		ArrayList<Double> ret = uncertaintyValues.get(contentID);
		
		if (ret == null) ret = new ArrayList<Double>();
		
		return ret;
	}

}
