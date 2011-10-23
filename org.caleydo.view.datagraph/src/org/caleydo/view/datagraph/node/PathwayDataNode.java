package org.caleydo.view.datagraph.node;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.datagraph.ForceDirectedGraphLayout;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;
import org.caleydo.view.datagraph.datacontainer.DataContainerListRenderer;

public class PathwayDataNode extends ADataNode {

	private ADataContainerRenderer dataContainerRenderer;
	private PathwayDataDomain dataDomain;

	public PathwayDataNode(ForceDirectedGraphLayout graphLayout,
			GLDataGraph view, DragAndDropController dragAndDropController,
			Integer id, IDataDomain dataDomain) {
		super(graphLayout, view, dragAndDropController, id, dataDomain);
		this.dataDomain = (PathwayDataDomain) dataDomain;
		dataContainerRenderer = new DataContainerListRenderer(this, view,
				dragAndDropController, getDimensionGroups());

	}

	@Override
	public ElementLayout setupLayout() {
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		Row baseRow = createDefaultBaseRow(dataDomain.getColor().getRGBA(),
				getID());

		ElementLayout spacingLayoutX = createDefaultSpacingX();

		baseColumn = new Column();
		baseColumn.setPixelGLConverter(pixelGLConverter);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout captionLayout = createDefaultCaptionLayout(
				dataDomain.getLabel(), getID());

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		Row bodyRow = new Row("bodyRow");

		if (getDimensionGroups().size() > 0) {
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1,
					1, 1 }));
		}

		bodyColumn = new Column("bodyColumn");

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
		baseColumn.append(spacingLayoutY);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(captionLayout);
		baseColumn.append(spacingLayoutY);
		
		setUpsideDown(isUpsideDown);

		return baseRow;
	}

	@Override
	public void update() {
		dataContainerRenderer.setDimensionGroups(getDimensionGroups());
	}

	@Override
	public void destroy() {
		dataContainerRenderer.destroy();
	}

	@Override
	protected ADataContainerRenderer getDataContainerRenderer() {
		return dataContainerRenderer;
	}

}
