package org.caleydo.view.datagraph.nodelayout;

import java.awt.geom.Point2D;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.OverviewDataContainerRenderer;
import org.caleydo.view.datagraph.node.ADataNode;

public class PathwayNodeLayout extends ADataNodeLayout {

	private PathwayDataDomain dataDomain;

	public PathwayNodeLayout(ADataNode node, GLDataGraph view,
			DragAndDropController dragAndDropController) {
		super(node, view, dragAndDropController);
		this.dataDomain = (PathwayDataDomain) node.getDataDomain();
		dataContainerRenderer = new OverviewDataContainerRenderer(node, view,
				dragAndDropController, node.getDimensionGroups());
	}

	@Override
	public ElementLayout setupLayout() {
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		Row baseRow = createDefaultBaseRow(dataDomain.getColor().getRGBA(),
				node.getID());

		ElementLayout spacingLayoutX = createDefaultSpacingX();

		Column baseColumn = new Column();
		baseColumn.setPixelGLConverter(pixelGLConverter);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout captionLayout = createDefaultCaptionLayout(
				dataDomain.getDataDomainID(), node.getID());

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		Row bodyRow = new Row("bodyRow");

		if (node.getDimensionGroups().size() > 0) {
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1,
					1, 1 }));
		}

		Column bodyColumn = new Column("bodyColumn");

		ElementLayout compGroupLayout = new ElementLayout("compGroupOverview");
		compGroupLayout.setRatioSizeY(1);
		// compGroupLayout.setPixelSizeX(compGroupOverviewRenderer.getMinWidthPixels());
		compGroupLayout.setRenderer(dataContainerRenderer);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(compGroupLayout);
		bodyColumn.append(spacingLayoutY);

		bodyRow.append(bodyColumn);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(bodyRow);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(captionLayout);
		baseColumn.append(spacingLayoutY);

		return baseRow;
	}

	@Override
	public void update() {
		dataContainerRenderer.setDimensionGroups(node.getDimensionGroups());
	}

	@Override
	public void destroy() {
		dataContainerRenderer.destroy();
	}

	@Override
	public Class<? extends IDataDomain> getDataDomainType() {
		return PathwayDataDomain.class;
	}

}
