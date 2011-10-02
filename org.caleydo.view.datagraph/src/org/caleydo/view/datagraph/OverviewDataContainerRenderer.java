package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;

public class OverviewDataContainerRenderer extends ADataContainerRenderer {

	private final static int SPACING_PIXELS = 2;
	private final static int MIN_COMP_GROUP_WIDTH_PIXELS = 16;

	
	private List<DimensionGroupRenderer> comparisonGroupRepresentations;

	public OverviewDataContainerRenderer(IDataGraphNode node, AGLView view,
			DragAndDropController dragAndDropController,
			List<ADimensionGroupData> dimensionGroupDatas) {
		super(node, view, dragAndDropController);

		
		comparisonGroupRepresentations = new ArrayList<DimensionGroupRenderer>();
		setDimensionGroups(dimensionGroupDatas);
		createPickingListener();
	}

	private void createPickingListener() {
		view.addMultiIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				DimensionGroupRenderer draggedComparisonGroupRepresentation = null;
				int dimensionGroupID = pick.getID();

				for (DimensionGroupRenderer comparisonGroupRepresentation : comparisonGroupRepresentations) {
					if (comparisonGroupRepresentation.getDimensionGroupData()
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
	public void setDimensionGroups(List<ADimensionGroupData> dimensionGroupDatas) {
		// this.dimensionGroupDatas = dimensionGroupDatas;
		comparisonGroupRepresentations.clear();
		for (ADimensionGroupData dimensionGroupData : dimensionGroupDatas) {
			DimensionGroupRenderer comparisonGroupRepresentation = new DimensionGroupRenderer(
					dimensionGroupData, view, dragAndDropController, node);
			comparisonGroupRepresentations.add(comparisonGroupRepresentation);
		}
	}

	@Override
	public void render(GL2 gl) {

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		// CaleydoTextRenderer textRenderer = view.getTextRenderer();

		float currentPosX = (x / 2.0f)
				- pixelGLConverter
						.getGLWidthForPixelWidth(getDimensionGroupsWidthPixels() / 2);
		float step = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS
				+ MIN_COMP_GROUP_WIDTH_PIXELS);

		dimensionGroupPositions.clear();

		for (DimensionGroupRenderer comparisonGroupRepresentation : comparisonGroupRepresentations) {
			float currentDimGroupWidth = pixelGLConverter
					.getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS);

			int pickingID = view.getPickingManager().getPickingID(
					view.getID(),
					DIMENSION_GROUP_PICKING_TYPE + node.getID(),
					comparisonGroupRepresentation.getDimensionGroupData()
							.getID());

			gl.glPushName(pickingID);

			comparisonGroupRepresentation.setX(currentDimGroupWidth);
			comparisonGroupRepresentation.setY(y);
			gl.glPushMatrix();
			gl.glTranslatef(currentPosX, 0, 0);
			comparisonGroupRepresentation.render(gl);
			gl.glPopMatrix();
			//
			// gl.glColor3f(0.6f, 0.6f, 0.6f);
			// gl.glBegin(GL2.GL_QUADS);
			// gl.glVertex3f(currentPosX, 0, 0.1f);
			// gl.glVertex3f(currentPosX + currentDimGroupWidth, 0, 0.1f);
			// gl.glVertex3f(currentPosX + currentDimGroupWidth, y, 0.1f);
			// gl.glVertex3f(currentPosX, y, 0.1f);
			// gl.glEnd();
			// gl.glPushMatrix();
			// gl.glTranslatef(currentPosX, y, 0.1f);
			// gl.glRotatef(-90, 0, 0, 1);
			//
			// textRenderer.renderTextInBounds(gl, data.getLabel(), 0, 0, 0, y,
			// currentDimGroupWidth);
			// gl.glPopMatrix();

			gl.glPopName();

			Point2D position1 = new Point2D.Float(currentPosX, 0);
			Point2D position2 = new Point2D.Float(currentPosX
					+ currentDimGroupWidth, 0);
			dimensionGroupPositions.put(
					comparisonGroupRepresentation.getDimensionGroupData().getID(),
					new Pair<Point2D, Point2D>(position1, position2));

			currentPosX += step;
		}

	}

	@Override
	public int getMinWidthPixels() {
		return getDimensionGroupsWidthPixels();
	}

	private int getDimensionGroupsWidthPixels() {
		return (node.getDimensionGroups().size() * MIN_COMP_GROUP_WIDTH_PIXELS)
				+ ((node.getDimensionGroups().size() - 1) * SPACING_PIXELS);
	}

}
