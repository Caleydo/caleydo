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
package org.caleydo.view.datagraph.datacontainer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.media.opengl.GL2;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.view.datagraph.GLDataViewIntegrator;
import org.caleydo.view.datagraph.PickingType;
import org.caleydo.view.datagraph.node.IDVINode;

public class DataContainerListRenderer
	extends ADataContainerRenderer
{

	private final static int SPACING_PIXELS = 4;
	private final static int MIN_COMP_GROUP_WIDTH_PIXELS = 16;
	private final static int MAX_TEXT_WIDTH_PIXELS = 80;
	private final static int TEXT_HEIGHT_PIXELS = 13;
	private final static int SIDE_SPACING_PIXELS = 20;

	private List<DimensionGroupRenderer> dimensionGroupRenderers;

	public DataContainerListRenderer(IDVINode node, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, List<DataContainer> dataContainers)
	{
		super(node, view, dragAndDropController);

		dimensionGroupRenderers = new ArrayList<DimensionGroupRenderer>();
		setDataContainers(dataContainers);
		registerPickingListeners();
	}

	@Override
	public void createPickingListeners()
	{
		view.addTypePickingListener(new DataContainerPickingListener(view,
				dragAndDropController, this), PickingType.DATA_CONTAINER.name() + node.getID());
	}

	@Override
	public void setDataContainers(List<DataContainer> dataContainers)
	{
		dimensionGroupRenderers.clear();
		for (DataContainer dataContainer : dataContainers)
		{
			if(dataContainer.isPrivate())
				continue;
			float[] color = dataContainer.getDataDomain().getColor().getRGBA();

			if (dataContainer instanceof PathwayDataContainer)
			{
				color = ((PathwayDataContainer) dataContainer).getPathwayDataDomain()
						.getColor().getRGBA();
			}

			DimensionGroupRenderer dimensionGroupRenderer = new DimensionGroupRenderer(
					dataContainer, view, node, color);
			dimensionGroupRenderer.setTextHeightPixels(TEXT_HEIGHT_PIXELS);
			dimensionGroupRenderer
					.setTextRotation(isUpsideDown ? DimensionGroupRenderer.TEXT_ROTATION_90
							: DimensionGroupRenderer.TEXT_ROTATION_270);
			dimensionGroupRenderers.add(dimensionGroupRenderer);
		}
	}

	@Override
	public void render(GL2 gl)
	{

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		// CaleydoTextRenderer textRenderer = view.getTextRenderer();
		// float dimensionGroupWidth = (x -
		// pixelGLConverter.getGLWidthForPixelWidth(2
		// * SIDE_SPACING_PIXELS + (node.getDataContainers().size() - 1)
		// * SPACING_PIXELS))
		// / (float) node.getDataContainers().size();

		float dimensionGroupWidth = pixelGLConverter
				.getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS);

		// float currentPosX =
		// pixelGLConverter.getGLWidthForPixelWidth(SIDE_SPACING_PIXELS);
		float currentPosX = (x / 2.0f)
				- pixelGLConverter.getGLWidthForPixelWidth(getDimensionGroupsWidthPixels() / 2
						- SIDE_SPACING_PIXELS);
		float step = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS)
				+ dimensionGroupWidth;

		bottomDimensionGroupPositions.clear();
		topDimensionGroupPositions.clear();

		for (DimensionGroupRenderer dimensionGroupRenderer : dimensionGroupRenderers)
		{
			// float currentDimGroupWidth = pixelGLConverter
			// .getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS);

			int pickingID = view.getPickingManager().getPickingID(view.getID(),
					PickingType.DATA_CONTAINER.name() + node.getID(),
					dimensionGroupRenderer.getDataContainer().getID());

			gl.glPushName(pickingID);
			if (pickingIDsToBePushed != null)
			{
				for (Pair<String, Integer> pickingPair : pickingIDsToBePushed)
				{
					gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
							pickingPair.getFirst(), pickingPair.getSecond()));
				}
			}

			dimensionGroupRenderer.setLimits(dimensionGroupWidth, y);
			gl.glPushMatrix();
			gl.glTranslatef(currentPosX, 0, 0);

			dimensionGroupRenderer.render(gl);
			gl.glPopMatrix();

			gl.glPopName();
			if (pickingIDsToBePushed != null)
			{
				for (int i = 0; i < pickingIDsToBePushed.size(); i++)
				{
					gl.glPopName();
				}
			}

			Point2D bottomPosition1 = new Point2D.Float(currentPosX, 0);
			Point2D bottomPosition2 = new Point2D.Float(currentPosX + dimensionGroupWidth, 0);
			Point2D topPosition1 = new Point2D.Float(currentPosX, y);
			Point2D topPosition2 = new Point2D.Float(currentPosX + dimensionGroupWidth, y);
			bottomDimensionGroupPositions.put(dimensionGroupRenderer.getDataContainer()
					.getID(), new Pair<Point2D, Point2D>(bottomPosition1, bottomPosition2));
			topDimensionGroupPositions.put(dimensionGroupRenderer.getDataContainer().getID(),
					new Pair<Point2D, Point2D>(topPosition1, topPosition2));

			currentPosX += step;
		}

	}

	@Override
	public int getMinWidthPixels()
	{
		return getDimensionGroupsWidthPixels();
	}

	@Override
	public int getMinHeightPixels()
	{
		return getMaxDimensionGroupLabelHeight();
	}

	private int getDimensionGroupsWidthPixels()
	{
		return (node.getDataContainers().size() * MIN_COMP_GROUP_WIDTH_PIXELS)
				+ ((node.getDataContainers().size() - 1) * SPACING_PIXELS) + 2
				* SIDE_SPACING_PIXELS;
	}

	private int getMaxDimensionGroupLabelHeight()
	{

		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float maxTextWidth = Float.MIN_VALUE;

		for (DataContainer dataContainer : node.getDataContainers())
		{
			float textWidth = textRenderer.getRequiredTextWidthWithMax(
					dataContainer.getLabel(),
					pixelGLConverter.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS),
					pixelGLConverter.getGLWidthForPixelWidth(MAX_TEXT_WIDTH_PIXELS));
			if (textWidth > maxTextWidth)
				maxTextWidth = textWidth;
		}

		return pixelGLConverter.getPixelHeightForGLHeight(maxTextWidth);

	}

	@Override
	public void setUpsideDown(boolean isUpsideDown)
	{
		this.isUpsideDown = isUpsideDown;

		for (DimensionGroupRenderer renderer : dimensionGroupRenderers)
		{
			renderer.setTextRotation(isUpsideDown ? DimensionGroupRenderer.TEXT_ROTATION_90
					: DimensionGroupRenderer.TEXT_ROTATION_270);
		}

	}

	@Override
	public void removePickingListeners()
	{
		view.removeAllTypePickingListeners(PickingType.DATA_CONTAINER.name() + node.getID());

	}

	@Override
	protected Collection<DimensionGroupRenderer> getDimensionGroupRenderers()
	{
		return dimensionGroupRenderers;
	}

}
