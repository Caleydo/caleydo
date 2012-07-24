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
package org.caleydo.view.dvi.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.datacontainer.ADataContainerRenderer;
import org.caleydo.view.dvi.datacontainer.DataContainerListRenderer;
import org.caleydo.view.dvi.layout.AGraphLayout;

public class PathwayDataNode
	extends ADataNode
{

	private ADataContainerRenderer dataContainerRenderer;
	private PathwayDataDomain dataDomain;
	private Row bodyRow;

	public PathwayDataNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, Integer id, IDataDomain dataDomain)
	{
		super(graphLayout, view, dragAndDropController, id, dataDomain);
		this.dataDomain = (PathwayDataDomain) dataDomain;

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

		bodyRow = new Row("bodyRow");

		if (getDataContainers().size() > 0)
		{
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
		}

		bodyColumn = new Column("bodyColumn");

		dataContainerRenderer = new DataContainerListRenderer(this, view,
				dragAndDropController, getDataContainers());

		List<Pair<String, Integer>> pickingIDsToBePushed = new ArrayList<Pair<String, Integer>>();
		pickingIDsToBePushed.add(new Pair<String, Integer>(
				DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id));

		dataContainerRenderer.setPickingIDsToBePushed(pickingIDsToBePushed);

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
		recalculateNodeSize();
		if (getDataContainers().size() > 0)
		{
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
		}
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
		List<PathwayDataContainer> containers = dataDomain.getDataContainers();

		List<Pair<String, DataContainer>> sortedContainers = new ArrayList<Pair<String, DataContainer>>(
				containers.size());

		for (PathwayDataContainer container : containers)
		{
			sortedContainers.add(new Pair<String, DataContainer>(container.getLabel(),
					container));
		}

		Collections.sort(sortedContainers);

		List<DataContainer> dataContainers = new ArrayList<DataContainer>(containers.size());
		for (Pair<String, DataContainer> containerPair : sortedContainers)
		{
			dataContainers.add(containerPair.getSecond());
		}

		return dataContainers;

		// return new ArrayList<DataContainer>(dataDomain.get);
	}

	@Override
	protected int getMinTitleBarWidthPixels()
	{

		float textWidth = view.getTextRenderer().getRequiredTextWidthWithMax(
				dataDomain.getLabel(),
				pixelGLConverter.getGLHeightForPixelHeight(CAPTION_HEIGHT_PIXELS),
				MIN_TITLE_BAR_WIDTH_PIXELS);

		return pixelGLConverter.getPixelWidthForGLWidth(textWidth);
	}

	@Override
	public String getCaption() {
		return dataDomain.getLabel();
	}

}
