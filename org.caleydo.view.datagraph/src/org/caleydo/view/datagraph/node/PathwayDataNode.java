package org.caleydo.view.datagraph.node;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;
import org.caleydo.view.datagraph.datacontainer.DataContainerListRenderer;
import org.caleydo.view.datagraph.layout.AGraphLayout;

public class PathwayDataNode
	extends ADataNode
{

	private ADataContainerRenderer dataContainerRenderer;
	private PathwayDataDomain dataDomain;

	public PathwayDataNode(AGraphLayout graphLayout, GLDataGraph view,
			DragAndDropController dragAndDropController, Integer id, IDataDomain dataDomain)
	{
		super(graphLayout, view, dragAndDropController, id, dataDomain);
		this.dataDomain = (PathwayDataDomain) dataDomain;
		dataContainerRenderer = new DataContainerListRenderer(this, view,
				dragAndDropController, getDataContainers());

	}

	@Override
	public ElementLayout setupLayout()
	{

		Row baseRow = createDefaultBaseRow(dataDomain.getColor().getRGBA(), getID());

		ElementLayout spacingLayoutX = createDefaultSpacingX();

		baseColumn = new Column();

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout captionLayout = createDefaultCaptionLayout(dataDomain.getLabel(),
				getID());

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		Row bodyRow = new Row("bodyRow");

		if (getDataContainers().size() > 0)
		{
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
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
	public void update()
	{
		dataContainerRenderer.setDataContainers(getDataContainers());
	}

	@Override
	public void destroy()
	{
		dataContainerRenderer.destroy();
	}

	@Override
	protected ADataContainerRenderer getDataContainerRenderer()
	{
		return dataContainerRenderer;
	}

	@Override
	public List<DataContainer> getDataContainers()
	{

		// FIXME: not clear what we want here
		return new ArrayList<DataContainer>();

		// return new ArrayList<DataContainer>(dataDomain.get);
	}

	@Override
	protected int getMinTitleBarWidthPixels()
	{

		float textWidth = view.getTextRenderer().getRequiredTextWidthWithMax(dataDomain.getLabel(),
				pixelGLConverter.getGLHeightForPixelHeight(CAPTION_HEIGHT_PIXELS),
				MIN_TITLE_BAR_WIDTH_PIXELS);

		return pixelGLConverter.getPixelWidthForGLWidth(textWidth);
	}

}
