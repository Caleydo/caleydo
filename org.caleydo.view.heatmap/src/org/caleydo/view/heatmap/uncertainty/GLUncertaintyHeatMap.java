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
package org.caleydo.view.heatmap.uncertainty;

import java.util.ArrayList;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
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

	public static String VIEW_TYPE = "org.caleydo.view.heatmap.uncertainty";

	public static String VIEW_NAME = "Uncertainty Heat Map";

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

	private LayoutManager layoutManager;

	private Row baseRow;
	private Row contentRow;
	private Column overviewLayout;
	private ElementLayout detailLayout;
	private ElementLayout overviewDetailConnectorLayout;

	private ColorMapper colorMapper;

	private boolean updateVisualUncertainty = true;

	private ArrayList<double[]> multiLevelUncertainty = new ArrayList<double[]>();
	private double[] aggregatedUncertainty;

	private boolean initOverviewRenderer = false;

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLUncertaintyHeatMap(IGLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public void init(GL2 gl) {

		displayListIndex = gl.glGenLists(1);
		colorMapper = dataDomain.getColorMapper();
		layoutManager = new LayoutManager(this.viewFrustum, pixelGLConverter);

		baseRow = new Row("baseRow");
		layoutManager.setBaseElementLayout(baseRow);

		ElementLayout sideSpacer = new ElementLayout("sideSpacer");
		sideSpacer.setPixelSizeX(15);

		ElementLayout topSpacer = new ElementLayout("topSpacer");
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
		overviewLayout.setPixelSizeX(220);

		overviewDetailConnectorLayout = new Column("overviewDetailConnectorLayout");
		overviewDetailConnectorLayout.setPixelSizeX(60);

		detailLayout = new ElementLayout("detailLayout");

		contentRow.append(overviewLayout);
		contentRow.append(overviewDetailConnectorLayout);
		contentRow.append(detailLayout);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		initMultiLevelUncertainty();
		createOverviewHeatMap(gl);
		createDetailHeatMap(gl);

		OverviewDetailConnectorRenderer overviewDetailConnectorRenderer = new OverviewDetailConnectorRenderer(
				overviewHeatMap, detailHeatMap);
		overviewDetailConnectorLayout.setRenderer(overviewDetailConnectorRenderer);

		layoutManager.updateLayout();
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
		layoutManager.updateLayout();
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
		overviewHeatMap.init(gl);
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
		data.setData(tablePerspective.getRecordPerspective().getVirtualArray().getIDs());
		detailHMRecordPerspective.init(data);

		TablePerspective detailHeatMapContainer = new TablePerspective(dataDomain,
				detailHMRecordPerspective, tablePerspective.getDimensionPerspective());
		detailHeatMap.setTablePerspective(detailHeatMapContainer);
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

		if (initOverviewRenderer) {
			overviewHeatMap.init(gl);
			initOverviewRenderer = false;
		}

		layoutManager.render(gl);
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
				RecordGroupList groupList = tablePerspective.getRecordPerspective()
						.getVirtualArray().getGroupList();

				// null if data set is not clustered
				if (groupList == null)
					break;

				List<Integer> embeddedRecords = tablePerspective.getRecordPerspective()
						.getVirtualArray().getIDsOfGroup(externalID);

				PerspectiveInitializationData data = new PerspectiveInitializationData();

				// RecordVirtualArray recordVA =
				// tablePerspective.getRecordPerspective()
				// .getVirtualArray();

				data.setData(embeddedRecords);
				detailHeatMap.getTablePerspective().getRecordPerspective().init(data);
				detailHeatMap.handleRecordVAUpdate(detailHeatMap.getTablePerspective()
						.getRecordPerspective().getPerspectiveID());

				// ArrayList<Integer> clusterElements =
				// tablePerspective.getRecordPerspective().getVirtualArray().getIDsOfGroup(groupList
				// .get(externalID);
				// detailHeatMap.setTablePerspective(tablePerspective)
				// RecordVirtualArray clusterVA = new RecordVirtualArray(
				// recordPerspectiveID, clusterElements);
				// detailHeatMap.setRecordVA(clusterVA);

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
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		super.handleSelectionUpdate(selectionDelta);

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
		initOverviewRenderer = true;

	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	public ColorMapper getColorMapper() {
		return colorMapper;
	}

	public HeatMapRenderStyle getRenderStyle() {
		return renderStyle;
	}

	@Override
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
			convertedSNR[index] = (SNR[index]);
		}

		// Initialize with 1 in order to calculate uncertainty max
		for (int index = 0; index < aggregatedUncertainty.length; index++) {
			aggregatedUncertainty[index] = 1f;
		}

		multiLevelUncertainty.add(convertedSNR);

		// Collection<double[]> statisticsUncertainties =
		// tablePerspective.getContainerStatistics().foldChange().getAllFoldChangeResults().values();
		// multiLevelUncertainty.addAll(statisticsUncertainties);

		for (Integer recordID : tablePerspective.getRecordPerspective().getVirtualArray()) {
			for (double[] uncertaintyLevel : multiLevelUncertainty) {

				double uncertainty = uncertaintyLevel[recordID];
				if (uncertainty < aggregatedUncertainty[recordID])
					aggregatedUncertainty[recordID] = uncertainty;
			}
		}

		// table.getStatisticsResult().setAggregatedUncertainty(aggregatedUncertainty);
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

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		layoutManager.destroy(gl);

	}
}
