package org.caleydo.view.heatmap.uncertainty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.OverviewDetailConnectorRenderer;
import org.caleydo.view.heatmap.heatmap.template.UncertaintyDetailHeatMapTemplate;
import org.eclipse.swt.widgets.Composite;

/**
 * Uncertainty heat map view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Clemens Holzhüter
 */

public class GLUncertaintyHeatMap extends ATableBasedView implements
		IGLRemoteRenderingView {

	public final static String VIEW_TYPE = "org.caleydo.view.heatmap.uncertainty";

	public final static float[][] DATA_VALID = { { 0.90f, 0.90f, 0.90f, 1f },
			{ 0.80f, 0.80f, 0.80f, 1f }, { 0.0f, 0.70f, 0.70f, 1f } };

	private final static float[][] DATA_UNCERTAIN = {
			{ 179 / 255f, 88 / 255f, 6 / 255f, 1f },
			{ 224 / 255f, 130 / 255f, 20 / 255f, 1f },
			{ 253 / 255f, 184 / 255f, 99 / 255f, 1f },
			{ 254 / 255f, 224 / 255f, 182 / 255f, 1f }, };

	/*
	 * 179 88 6 2 E 241 163 64 3 G 254 224 182 4 I 216 218 235 5 K 153 142 195 6
	 * N 84 39 136
	 */

	public final static float[] BACKGROUND = { 0.7f, 0.7f, 0.7f, 1f };
	public final static float[] VIS_UNC = { 84 / 255f, 39 / 255f, 136 / 255f, 1f };

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

	private ColorMapper colorMapper;

	private boolean updateVisualUncertainty = true;

	private ArrayList<double[]> multiLevelUncertainty = new ArrayList<double[]>();
	private double[] aggregatedUncertainty;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLUncertaintyHeatMap(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);
		viewType = GLUncertaintyHeatMap.VIEW_TYPE;
	}

	@Override
	public void init(GL2 gl) {

		displayListIndex = gl.glGenLists(1);
		colorMapper = dataDomain.getColorMapper();
		templateRenderer = new LayoutManager(this.viewFrustum);
		if (template == null)
			template = new LayoutTemplate();

		templateRenderer.setTemplate(template);

		baseRow = new Row("baseRow");
		template.setBaseElementLayout(baseRow);

		ElementLayout sideSpacer = new ElementLayout("sideSpacer");
		sideSpacer.setPixelGLConverter(pixelGLConverter);
		sideSpacer.setPixelSizeX(15);

		ElementLayout topSpacer = new ElementLayout("topSpacer");
		topSpacer.setPixelGLConverter(pixelGLConverter);
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
		overviewLayout.setPixelGLConverter(pixelGLConverter);
		overviewLayout.setPixelSizeX(220);

		overviewDetailConnectorLayout = new Column("overviewDetailConnectorLayout");
		overviewDetailConnectorLayout.setDebug(false);
		overviewDetailConnectorLayout.setPixelGLConverter(pixelGLConverter);
		overviewDetailConnectorLayout.setPixelSizeX(60);

		detailLayout = new ElementLayout("detailLayout");
		detailLayout.setDebug(false);

		contentRow.append(overviewLayout);
		contentRow.append(overviewDetailConnectorLayout);
		contentRow.append(detailLayout);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

		initMultiLevelUncertainty();
		createOverviewHeatMap(gl);
		createDetailHeatMap(gl);

		OverviewDetailConnectorRenderer overviewDetailConnectorRenderer = new OverviewDetailConnectorRenderer(
				overviewHeatMap, detailHeatMap);
		overviewDetailConnectorLayout.setRenderer(overviewDetailConnectorRenderer);

		templateRenderer.updateLayout();
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
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
				.getViewManager()
				.createGLView(
						GLHeatMap.class,
						parentGLCanvas,
						parentComposite,
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
								-1, 1));

		detailHeatMap.setDataDomain(dataDomain);

		RecordPerspective detailHMRecordPerspective = new RecordPerspective(dataDomain);
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(dataContainer.getRecordPerspective().getVirtualArray()
				.getIndexList());
		detailHMRecordPerspective.init(data);

		DataContainer detailHeatMapContainer = new DataContainer(dataDomain,
				detailHMRecordPerspective, dataContainer.getDimensionPerspective());
		detailHeatMap.setDataContainer(detailHeatMapContainer);
		detailHeatMap.setRemoteRenderingGLView(this);
		detailHeatMap.setRenderTemplate(new UncertaintyDetailHeatMapTemplate(
				detailHeatMap, this));
		detailHeatMap.initialize();
		detailHeatMap.initRemote(gl, this, glMouseListener);

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
		throw new IllegalStateException("This view cannot be rendered remotely!");
	}

	@Override
	public void display(GL2 gl) {
		templateRenderer.render(gl);
		if (updateVisualUncertainty) {

			// very dirty
			for (ClusterRenderer clusterRenderer : overviewHeatMap
					.getClusterRendererList()) {

				clusterRenderer.updateVisualUncertainty(gl, pixelGLConverter);
			}

			updateVisualUncertainty = false;
		}

	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {

		switch (pickingType) {

		// handling the groups/clusters of genes
		case HEAT_MAP_RECORD_GROUP:
			switch (pickingMode) {
			case CLICKED:

				overviewHeatMap.setSelectedGroup(externalID);
				RecordGroupList groupList = dataContainer.getRecordPerspective()
						.getVirtualArray().getGroupList();

				// null if data set is not clustered
				if (groupList == null)
					break;

				ArrayList<Integer> clusterElements = dataContainer.getRecordPerspective().getVirtualArray().getIDsOfGroup(groupList
						.get(externalID).getGroupID());
				detailHeatMap.
				RecordVirtualArray clusterVA = new RecordVirtualArray(
						recordPerspectiveID, clusterElements);
				detailHeatMap.setRecordVA(clusterVA);

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
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		super.handleSelectionUpdate(selectionDelta, scrollToSelection, info);

	}



	

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// ArrayList<AGLView> remoteRenderedViews = new ArrayList<AGLView>();
		// remoteRenderedViews.add(overviewHeatMap);
		// return remoteRenderedViews;
		return null;
	}

	@Override
	protected void reactOnRecordVAChanges() {

		super.reactOnRecordVAChanges();

		setDisplayListDirty();
		initMultiLevelUncertainty();
		updateVisualUncertainty = true;
		overviewHeatMap.init();

	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(IDType idType, int id)
			throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	public ColorMapper getColorMapper() {
		return colorMapper;
	}

	public HeatMapRenderStyle getRenderStyle() {
		return renderStyle;
	}

	public RecordSelectionManager getRecordSelectionManager() {
		return recordSelectionManager;

	}

	public void initMultiLevelUncertainty() {
		DataTable table = dataDomain.getTable();
		if (table.getUncertainty() == null)
			return;

		float[] SNR = table.getUncertainty().getNormalizedUncertainty();
		aggregatedUncertainty = new double[SNR.length];
		multiLevelUncertainty.clear();

		double[] convertedSNR = new double[SNR.length];
		for (int index = 0; index < SNR.length; index++) {
			convertedSNR[index] = (double) (SNR[index]);
		}

		// Initialize with 1 in order to calculate uncertainty max
		for (int index = 0; index < aggregatedUncertainty.length; index++) {
			aggregatedUncertainty[index] = 1f;
		}

		multiLevelUncertainty.add(convertedSNR);

		Collection<double[]> statisticsUncertainties = table.getStatisticsResult()
				.getAllFoldChangeUncertainties();
		multiLevelUncertainty.addAll(statisticsUncertainties);

		for (Integer recordID : dataContainer.getRecordPerspective().getVirtualArray()) {
			for (double[] uncertaintyLevel : multiLevelUncertainty) {

				double uncertainty = uncertaintyLevel[recordID];
				if (uncertainty < aggregatedUncertainty[recordID])
					aggregatedUncertainty[recordID] = uncertainty;
			}
		}

		table.getStatisticsResult().setAggregatedUncertainty(aggregatedUncertainty);
	}

	public float getMaxUncertainty(int recordID) {
		return (float) aggregatedUncertainty[recordID];

	}

	public boolean isMaxUncertaintyCalculated() {
		if (aggregatedUncertainty != null)
			return true;

		return false;
	}

	public ArrayList<double[]> getMultiLevelUncertainty() {
		return multiLevelUncertainty;
	}

	public static float[] getUncertaintyColor(int level) {
		int l = level % DATA_UNCERTAIN.length;
		return DATA_UNCERTAIN[l];
	}
}
