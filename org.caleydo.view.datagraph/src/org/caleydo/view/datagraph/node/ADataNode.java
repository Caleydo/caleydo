package org.caleydo.view.datagraph.node;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.datagraph.FakeDimensionGroupData;
import org.caleydo.view.datagraph.ForceDirectedGraphLayout;
import org.caleydo.view.datagraph.GLDataGraph;

public abstract class ADataNode extends ADefaultTemplateNode {

	// private final static String TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE =
	// "org.caleydo.view.datagraph.toggledatacontainerbutton";

	protected IDataDomain dataDomain;

	private List<ADimensionGroupData> dimensionGroups;

	// protected ADataContainerRenderer dataContainerRenderer;
	// private ANodeLayout layout;

	public ADataNode(ForceDirectedGraphLayout graphLayout, GLDataGraph view,
			final DragAndDropController dragAndDropController, Integer id,
			IDataDomain dataDomain) {
		super(graphLayout, view, dragAndDropController, id);
		dimensionGroups = new ArrayList<ADimensionGroupData>();
		dimensionGroups.add(new FakeDimensionGroupData(0));
		dimensionGroups.add(new FakeDimensionGroupData(1));
		dimensionGroups.add(new FakeDimensionGroupData(2));
		dimensionGroups.add(new FakeDimensionGroupData(3));
		dimensionGroups.add(new FakeDimensionGroupData(4));

		this.dataDomain = dataDomain;

		// setupLayout();
		// init();

	}

	//
	// private void setupLayout() {
	// layoutManager = new LayoutManager(new ViewFrustum());
	// LayoutTemplate layoutTemplate = new LayoutTemplate();
	//
	// Row baseRow = new Row("baseRow");
	//
	// baseRow.setFrameColor(0, 0, 1, 0);
	//
	// BorderedAreaRenderer borderedAreaRenderer = new BorderedAreaRenderer(
	// view, PickingType.DATA_GRAPH_NODE, id);
	// // borderedAreaRenderer.setColor(new float[] { 0.25f + (251f / 255f) /
	// // 2f,
	// // 0.25f + (128f / 255f) / 2f, 0.25f + (114f / 255f) / 2f, 1f });
	// Color color = dataDomain.getColor();
	// if (color == null)
	// color = new Color(0.5f, 0.5f, 0.5f, 1f);
	// borderedAreaRenderer.setColor(color.getRGBA());
	//
	// baseRow.setRenderer(borderedAreaRenderer);
	//
	// ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
	// spacingLayoutX.setPixelGLConverter(pixelGLConverter);
	// spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
	// spacingLayoutX.setRatioSizeY(0);
	//
	// Column baseColumn = new Column();
	// baseColumn.setPixelGLConverter(pixelGLConverter);
	//
	// baseRow.append(spacingLayoutX);
	// baseRow.append(baseColumn);
	// baseRow.append(spacingLayoutX);
	//
	// Row titleRow = new Row("titleRow");
	//
	// ElementLayout captionLayout = new ElementLayout("caption");
	// captionLayout.setPixelGLConverter(pixelGLConverter);
	// captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
	// captionLayout.setRatioSizeX(1);
	// captionLayout.setRenderer(new LabelRenderer(view, dataDomain
	// .getDataDomainID(), PickingType.DATA_GRAPH_NODE, id));
	//
	// titleRow.append(captionLayout);
	// titleRow.setYDynamic(true);
	//
	// ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
	// lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
	// lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
	// lineSeparatorLayout.setRatioSizeX(1);
	// lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));
	//
	// // FIXME: Only show if there are multiple perspectives
	// if (dataDomain instanceof ATableBasedDataDomain) {
	// ElementLayout toggleDataContainerDetailLayout = new ElementLayout(
	// "toggleDataContainerLayout");
	// toggleDataContainerDetailLayout
	// .setPixelGLConverter(pixelGLConverter);
	// toggleDataContainerDetailLayout
	// .setPixelSizeY(CAPTION_HEIGHT_PIXELS);
	// toggleDataContainerDetailLayout
	// .setPixelSizeX(CAPTION_HEIGHT_PIXELS);
	// toggleDataContainerButtonRenderer = new ButtonRenderer(
	// new Button(TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE
	// + getID(), 0,
	// EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK), view,
	// view.getTextureManager(),
	// ButtonRenderer.TEXTURE_ROTATION_90);
	// toggleDataContainerDetailLayout.setRenderer(toggleDataContainerButtonRenderer);
	// titleRow.append(toggleDataContainerDetailLayout);
	// }
	// // lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
	// // lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
	// // lineSeparatorLayout.setRatioSizeX(1);
	// // lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));
	//
	// Row bodyRow = new Row("bodyRow");
	//
	// if (getDimensionGroups().size() > 0) {
	// bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1,
	// 1, 1 }));
	// }
	//
	// Column bodyColumn = new Column("bodyColumn");
	//
	// ElementLayout compGroupLayout = new ElementLayout("compGroupOverview");
	//
	// if (dataDomain instanceof ATableBasedDataDomain) {
	// dataContainerRenderer = new DetailDataContainerRenderer(
	// (ATableBasedDataDomain) dataDomain, view, this,
	// dragAndDropController);
	// } else {
	// dataContainerRenderer = new OverviewDataContainerRenderer(this,
	// view, dragAndDropController, getDimensionGroups());
	// }
	// // compGroupLayout.setPixelGLConverter(pixelGLConverter);
	// // compGroupLayout.setPixelSizeY(OVERVIEW_COMP_GROUP_HEIGHT_PIXELS);
	// compGroupLayout.setRatioSizeY(1);
	// //
	// compGroupLayout.setPixelSizeX(compGroupOverviewRenderer.getMinWidthPixels());
	// compGroupLayout.setRenderer(dataContainerRenderer);
	//
	// ElementLayout spacingLayoutY = new ElementLayout("spacingY");
	// spacingLayoutY.setPixelGLConverter(pixelGLConverter);
	// spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
	// spacingLayoutY.setRatioSizeX(0);
	//
	// bodyColumn.append(compGroupLayout);
	// bodyColumn.append(spacingLayoutY);
	//
	// bodyRow.append(bodyColumn);
	//
	// baseColumn.append(spacingLayoutY);
	// baseColumn.append(bodyRow);
	// baseColumn.append(lineSeparatorLayout);
	// baseColumn.append(titleRow);
	// baseColumn.append(spacingLayoutY);
	// layoutTemplate.setBaseElementLayout(baseRow);
	// layoutManager.setTemplate(layoutTemplate);
	// }

	@Override
	public List<ADimensionGroupData> getDimensionGroups() {
		// List<ADimensionGroupData> groups =
		// representedView.getDimensionGroups();
		// if (groups == null) {
		// groups = new ArrayList<ADimensionGroupData>();
		// }

		List<ADimensionGroupData> groups = new ArrayList<ADimensionGroupData>();
		FakeDimensionGroupData data = new FakeDimensionGroupData(0);
		data.setDimensionPerspectiveID("ColumnPerspec2");
		data.setRecordPerspectiveID("Row1");
		if (dataDomain instanceof ATableBasedDataDomain)
			data.setDataDomain((ATableBasedDataDomain) dataDomain);
		groups.add(data);

		data = new FakeDimensionGroupData(1);
		data.setDimensionPerspectiveID("ColumnPerspec2");
		data.setRecordPerspectiveID("AnotherRow");
		if (dataDomain instanceof ATableBasedDataDomain)
			data.setDataDomain((ATableBasedDataDomain) dataDomain);
		groups.add(data);

		data = new FakeDimensionGroupData(2);
		data.setDimensionPerspectiveID("ColumnPerspec2");
		data.setRecordPerspectiveID("YetAnotherRow");
		if (dataDomain instanceof ATableBasedDataDomain)
			data.setDataDomain((ATableBasedDataDomain) dataDomain);
		groups.add(data);

		data = new FakeDimensionGroupData(3);
		data.setDimensionPerspectiveID("ColumnPerspec2");
		data.setRecordPerspectiveID("RowPerspec2");
		if (dataDomain instanceof ATableBasedDataDomain)
			data.setDataDomain((ATableBasedDataDomain) dataDomain);
		groups.add(data);

		data = new FakeDimensionGroupData(4);
		data.setDimensionPerspectiveID("AnotherColumn2");
		data.setRecordPerspectiveID("Row1");
		if (dataDomain instanceof ATableBasedDataDomain)
			data.setDataDomain((ATableBasedDataDomain) dataDomain);
		groups.add(data);

		data = new FakeDimensionGroupData(5);
		data.setDimensionPerspectiveID("YetAnotherColumn2");
		data.setRecordPerspectiveID("YetAnotherRow");
		if (dataDomain instanceof ATableBasedDataDomain)
			data.setDataDomain((ATableBasedDataDomain) dataDomain);
		groups.add(data);

		return groups;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		return dataDomain;
	}

}
