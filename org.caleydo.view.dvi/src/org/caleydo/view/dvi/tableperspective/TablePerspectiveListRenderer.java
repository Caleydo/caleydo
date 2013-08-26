/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.tableperspective;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.PickingType;
import org.caleydo.view.dvi.node.ADataNode;
import org.caleydo.view.dvi.node.IDVINode;

public class TablePerspectiveListRenderer extends AMultiTablePerspectiveRenderer {

	private final static int SPACING_PIXELS = 4;
	private final static int MIN_COMP_GROUP_WIDTH_PIXELS = 16;
	private final static int MAX_TEXT_WIDTH_PIXELS = 80;
	private final static int TEXT_HEIGHT_PIXELS = 13;
	private final static int SIDE_SPACING_PIXELS = 20;

	private List<TablePerspectiveRenderer> tablePerspectiveRenderers;

	public TablePerspectiveListRenderer(IDVINode node, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, List<TablePerspective> tablePerspectives) {
		super(node, view, dragAndDropController);

		tablePerspectiveRenderers = new ArrayList<TablePerspectiveRenderer>();
		setTablePerspectives(tablePerspectives);
		registerPickingListeners();
	}

	@Override
	public void createPickingListeners() {
		view.addTypePickingListener(new TablePerspectivePickingListener(view, dragAndDropController, this),
				PickingType.DATA_CONTAINER.name() + node.getID());
	}

	@Override
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		tablePerspectiveRenderers.clear();
		for (TablePerspective tablePerspective : tablePerspectives) {
			if (tablePerspective.isPrivate() || !view.isTablePerspectiveShownByView(tablePerspective))
				continue;

			TablePerspectiveRenderer tablePerspectiveRenderer = new TablePerspectiveRenderer(tablePerspective, view,
					node);
			tablePerspectiveRenderer.setTextHeightPixels(TEXT_HEIGHT_PIXELS);
			tablePerspectiveRenderer.setTextRotation(isUpsideDown ? TablePerspectiveRenderer.TEXT_ROTATION_90
					: TablePerspectiveRenderer.TEXT_ROTATION_270);
			tablePerspectiveRenderer.setActive(true);
			tablePerspectiveRenderers.add(tablePerspectiveRenderer);
		}
	}

	@Override
	public void renderContent(GL2 gl) {

		if (tablePerspectiveRenderers == null || tablePerspectiveRenderers.isEmpty())
			return;

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float dimensionGroupWidth = pixelGLConverter.getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS);

		float currentPosX = (x / 2.0f)
				- pixelGLConverter.getGLWidthForPixelWidth(getDimensionGroupsWidthPixels() / 2 - SIDE_SPACING_PIXELS);
		float step = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS) + dimensionGroupWidth;

		bottomObjectPositions.clear();
		topObjectPositions.clear();

		for (TablePerspectiveRenderer tablePerspectiveRenderer : tablePerspectiveRenderers) {

			int pickingID = view.getPickingManager().getPickingID(view.getID(),
					PickingType.DATA_CONTAINER.name() + node.getID(), tablePerspectiveRenderer.hashCode());

			gl.glPushName(pickingID);
			if (pickingIDsToBePushed != null) {
				for (Pair<String, Integer> pickingPair : pickingIDsToBePushed) {
					gl.glPushName(view.getPickingManager().getPickingID(view.getID(), pickingPair.getFirst(),
							pickingPair.getSecond()));
				}
			}

			tablePerspectiveRenderer.setLimits(dimensionGroupWidth, y);
			gl.glPushMatrix();
			gl.glTranslatef(currentPosX, 0, 0);

			tablePerspectiveRenderer.render(gl);
			gl.glPopMatrix();

			gl.glPopName();
			if (pickingIDsToBePushed != null) {
				for (int i = 0; i < pickingIDsToBePushed.size(); i++) {
					gl.glPopName();
				}
			}

			Point2D bottomPosition1 = new Point2D.Float(currentPosX, 0);
			Point2D bottomPosition2 = new Point2D.Float(currentPosX + dimensionGroupWidth, 0);
			Point2D topPosition1 = new Point2D.Float(currentPosX, y);
			Point2D topPosition2 = new Point2D.Float(currentPosX + dimensionGroupWidth, y);
			bottomObjectPositions.put(tablePerspectiveRenderer.getTablePerspective(), new Pair<Point2D, Point2D>(
					bottomPosition1, bottomPosition2));
			topObjectPositions.put(tablePerspectiveRenderer.getTablePerspective(),

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
		List<TablePerspective> tablePerspectives = node instanceof ADataNode ? ((ADataNode) node)
				.getVisibleTablePerspectives() : node.getTablePerspectives();

		return tablePerspectives.size() > 0 ? MAX_TEXT_WIDTH_PIXELS : 0;
		// getMaxDimensionGroupLabelHeight();
	}

	private int getDimensionGroupsWidthPixels() {
		List<TablePerspective> tablePerspectives = node instanceof ADataNode ? ((ADataNode) node)
				.getVisibleTablePerspectives() : node.getTablePerspectives();

		return (tablePerspectives.size() * MIN_COMP_GROUP_WIDTH_PIXELS)
				+ ((tablePerspectives.size() - 1) * SPACING_PIXELS) + 2 * SIDE_SPACING_PIXELS;
	}

	private int getMaxDimensionGroupLabelHeight() {

		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float maxTextWidth = Float.MIN_VALUE;
		List<TablePerspective> tablePerspectives = node instanceof ADataNode ? ((ADataNode) node)
				.getVisibleTablePerspectives() : node.getTablePerspectives();

		for (TablePerspective tablePerspective : tablePerspectives) {
			float textWidth = textRenderer.getRequiredTextWidthWithMax(tablePerspective.getLabel(),
					pixelGLConverter.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS),
					pixelGLConverter.getGLWidthForPixelWidth(MAX_TEXT_WIDTH_PIXELS));
			if (textWidth > maxTextWidth)
				maxTextWidth = textWidth;
		}

		return pixelGLConverter.getPixelHeightForGLHeight(maxTextWidth);

	}

	@Override
	public void setUpsideDown(boolean isUpsideDown) {
		this.isUpsideDown = isUpsideDown;

		for (TablePerspectiveRenderer renderer : tablePerspectiveRenderers) {
			renderer.setTextRotation(isUpsideDown ? TablePerspectiveRenderer.TEXT_ROTATION_90
					: TablePerspectiveRenderer.TEXT_ROTATION_270);
		}

	}

	@Override
	public void removePickingListeners() {
		view.removeAllTypePickingListeners(PickingType.DATA_CONTAINER.name() + node.getID());

	}

	@Override
	protected Collection<TablePerspectiveRenderer> getDimensionGroupRenderers() {
		return tablePerspectiveRenderers;
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
