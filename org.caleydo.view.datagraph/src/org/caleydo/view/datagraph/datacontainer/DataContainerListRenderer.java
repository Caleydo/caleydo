package org.caleydo.view.datagraph.datacontainer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class DataContainerListRenderer extends ADataContainerRenderer {

	private final static int SPACING_PIXELS = 4;
	private final static int MIN_COMP_GROUP_WIDTH_PIXELS = 16;
	private final static int MAX_TEXT_WIDTH_PIXELS = 80;
	private final static int TEXT_HEIGHT_PIXELS = 13;
	private final static int SIDE_SPACING_PIXELS = 20;

	private List<DimensionGroupRenderer> dimensionGroupRenderers;

	public DataContainerListRenderer(IDataGraphNode node, AGLView view,
			DragAndDropController dragAndDropController,
			List<DataContainer> dataContainers) {
		super(node, view, dragAndDropController);

		dimensionGroupRenderers = new ArrayList<DimensionGroupRenderer>();
		setDataContainers(dataContainers);
		createPickingListener();
	}

	private void createPickingListener() {
		view.addMultiIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				DimensionGroupRenderer draggedComparisonGroupRepresentation = null;
				int dimensionGroupID = pick.getID();

				for (DimensionGroupRenderer comparisonGroupRepresentation : dimensionGroupRenderers) {
					if (comparisonGroupRepresentation.getDataContainer()
							.getID() == dimensionGroupID) {
						draggedComparisonGroupRepresentation = comparisonGroupRepresentation;
						break;
					}
				}
				if (draggedComparisonGroupRepresentation == null)
					return;

				draggedComparisonGroupRepresentation
						.setSelectionType(SelectionType.SELECTION);

				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingStartPosition(pick
						.getPickedPoint());
				dragAndDropController
						.addDraggable(draggedComparisonGroupRepresentation);
				view.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {
			}

			@Override
			public void dragged(Pick pick) {
				if (!dragAndDropController.isDragging()) {
					dragAndDropController.startDragging("DimensionGroupDrag");
				}
			}

		}, DIMENSION_GROUP_PICKING_TYPE + node.getID());
	}

	@Override
	public void setDataContainers(List<DataContainer> dataContainers) {
		dimensionGroupRenderers.clear();
		for (DataContainer dataContainer : dataContainers) {
			float[] color = dataContainer.getDataDomain().getColor()
					.getRGBA();

			DimensionGroupRenderer dimensionGroupRenderer = new DimensionGroupRenderer(
					dataContainer, view, dragAndDropController, node,
					color);
			dimensionGroupRenderer.setTextHeightPixels(TEXT_HEIGHT_PIXELS);
			dimensionGroupRenderer.setUpsideDown(isUpsideDown);
			dimensionGroupRenderers.add(dimensionGroupRenderer);
		}
	}

	@Override
	public void render(GL2 gl) {

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		// CaleydoTextRenderer textRenderer = view.getTextRenderer();

		float currentPosX = (x / 2.0f)
				- pixelGLConverter
						.getGLWidthForPixelWidth(getDimensionGroupsWidthPixels()
								/ 2 - SIDE_SPACING_PIXELS);
		float step = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS
				+ MIN_COMP_GROUP_WIDTH_PIXELS);

		bottomDimensionGroupPositions.clear();
		topDimensionGroupPositions.clear();

		for (DimensionGroupRenderer dimensionGroupRenderer : dimensionGroupRenderers) {
			float currentDimGroupWidth = pixelGLConverter
					.getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS);

			int pickingID = view.getPickingManager().getPickingID(view.getID(),
					DIMENSION_GROUP_PICKING_TYPE + node.getID(),
					dimensionGroupRenderer.getDataContainer().getID());

			gl.glPushName(pickingID);

			dimensionGroupRenderer.setLimits(currentDimGroupWidth, y);
			gl.glPushMatrix();
			gl.glTranslatef(currentPosX, 0, 0);

			dimensionGroupRenderer.render(gl);
			gl.glPopMatrix();

			gl.glPopName();

			Point2D bottomPosition1 = new Point2D.Float(currentPosX, 0);
			Point2D bottomPosition2 = new Point2D.Float(currentPosX
					+ currentDimGroupWidth, 0);
			Point2D topPosition1 = new Point2D.Float(currentPosX, y);
			Point2D topPosition2 = new Point2D.Float(currentPosX
					+ currentDimGroupWidth, y);
			bottomDimensionGroupPositions
					.put(dimensionGroupRenderer.getDataContainer().getID(),
							new Pair<Point2D, Point2D>(bottomPosition1,
									bottomPosition2));
			topDimensionGroupPositions.put(dimensionGroupRenderer
					.getDataContainer().getID(),
					new Pair<Point2D, Point2D>(topPosition1, topPosition2));

			currentPosX += step;
		}

	}

	@Override
	public int getMinWidthPixels() {
		return getDimensionGroupsWidthPixels();
	}

	@Override
	public int getMinHeightPixels() {
		return getMaxDimensionGroupLabelHeight();
	}

	private int getDimensionGroupsWidthPixels() {
		return (node.getDataContainers().size() * MIN_COMP_GROUP_WIDTH_PIXELS)
				+ ((node.getDataContainers().size() - 1) * SPACING_PIXELS) + 2
				* SIDE_SPACING_PIXELS;
	}

	private int getMaxDimensionGroupLabelHeight() {

		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float maxTextWidth = Float.MIN_VALUE;

		for (DataContainer dataContainer : node.getDataContainers()) {
			float textWidth = textRenderer.getRequiredTextWidthWithMax(
					dataContainer.getLabel(), pixelGLConverter
							.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS),
					pixelGLConverter
							.getGLWidthForPixelWidth(MAX_TEXT_WIDTH_PIXELS));
			if (textWidth > maxTextWidth)
				maxTextWidth = textWidth;
		}

		return pixelGLConverter.getPixelHeightForGLHeight(maxTextWidth);

	}

	@Override
	public void destroy() {
		view.removeMultiIDPickingListeners(DIMENSION_GROUP_PICKING_TYPE
				+ node.getID());
	}

	@Override
	public void setUpsideDown(boolean isUpsideDown) {
		this.isUpsideDown = isUpsideDown;

		for (DimensionGroupRenderer renderer : dimensionGroupRenderers) {
			renderer.setUpsideDown(isUpsideDown);
		}

	}

}
