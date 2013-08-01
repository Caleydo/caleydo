/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.tableperspective.matrix;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.node.IDVINode;

abstract class ATablePerspectiveMatrixRenderingStrategy {

	protected static final int MAX_TEXT_WIDTH_PIXELS = 90;
	protected static final int TEXT_HEIGHT_PIXELS = 12;
	protected static final int COLUMN_WIDTH_PIXELS = 22;
	protected static final int ROW_HEIGHT_PIXELS = 22;
	protected static final int CAPTION_SPACING_PIXELS = 5;
	protected static final int CELL_SPACING_PIXELS = 3;
	protected static final int CELL_SIZE_PIXELS = 16;

	protected TablePerspectiveMatrixRenderer matrixRenderer;

	public ATablePerspectiveMatrixRenderingStrategy(
			TablePerspectiveMatrixRenderer matrixRenderer) {
		this.matrixRenderer = matrixRenderer;
	}

	public abstract void render(GL2 gl,
 Map<Object, Pair<Point2D, Point2D>> bottomObjectPositions,
			Map<Object, Pair<Point2D, Point2D>> topObjectPositions, float x,
			float y, IDVINode node, GLDataViewIntegrator view,
			List<Pair<String, Integer>> pickingIDsToBePushed, String rowsCaption,
			String columnsCaption);

	protected float calcMaxTextWidth(List<CellContainer> containers, AGLView view) {

		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float maxTextWidth = Float.MIN_VALUE;

		for (CellContainer container : containers) {
			float textWidth = textRenderer.getRequiredTextWidthWithMax(container.id,
					pixelGLConverter.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS),
					pixelGLConverter.getGLWidthForPixelWidth(MAX_TEXT_WIDTH_PIXELS));
			if (textWidth > maxTextWidth)
				maxTextWidth = textWidth;
		}

		return maxTextWidth;
	}

	public int getMinWidthPixels(List<CellContainer> rows, List<CellContainer> columns,
			AGLView view) {

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		int captionWidth = pixelGLConverter.getPixelWidthForGLWidth(calcMaxTextWidth(
				rows, view));

		int sumColumnWidth = 0;

		for (CellContainer column : columns) {
			if (column.isVisible) {
				sumColumnWidth += column.numSubdivisions * COLUMN_WIDTH_PIXELS;
			}
		}

		return captionWidth + sumColumnWidth + CAPTION_SPACING_PIXELS;

	}

	public int getMinHeightPixels(List<CellContainer> rows, List<CellContainer> columns,
			AGLView view) {

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		int captionWidth = pixelGLConverter.getPixelHeightForGLHeight(calcMaxTextWidth(
				columns, view));

		int sumRowHeight = 0;

		for (CellContainer row : rows) {
			if (row.isVisible) {
				sumRowHeight += row.numSubdivisions * COLUMN_WIDTH_PIXELS;
			}
		}

		return captionWidth + sumRowHeight + CAPTION_SPACING_PIXELS;

	}

	public float[] getPerspectiveColor() {
		return new float[] { 0.7f, 0.7f, 0.7f, 1f };
		// return matrixRenderer.dataDomain.getColor().getRGBA();
	}

	protected void pushPickingIDs(GL2 gl, AGLView view,
			List<Pair<String, Integer>> pickingIDsToBePushed) {
		for (Pair<String, Integer> pickingIDPair : pickingIDsToBePushed) {
			gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
					pickingIDPair.getFirst(), pickingIDPair.getSecond()));
		}
	}

	protected void popPickingIDs(GL2 gl, List<Pair<String, Integer>> pickingIDsToBePushed) {
		for (int k = 0; k < pickingIDsToBePushed.size(); k++) {
			gl.glPopName();
		}
	}
}
