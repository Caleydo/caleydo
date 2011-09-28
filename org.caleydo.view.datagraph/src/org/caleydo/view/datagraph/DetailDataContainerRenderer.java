package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

import com.jogamp.opengl.util.awt.TextRenderer;

public class DetailDataContainerRenderer extends ADataContainerRenderer {

	private ATableBasedDataDomain dataDomain;
	private AGLView view;

	private static final int MAX_TEXT_WIDTH_PIXELS = 60;
	private static final int TEXT_HEIGHT_PIXELS = 10;
	private static final int COLUMN_WIDTH_PIXELS = 10;
	private static final int ROW_HEIGHT_PIXELS = 10;

	private class CellContainer {
		private String caption;
		private int numSubdivisions;
	}

	private List<CellContainer> rows = new ArrayList<CellContainer>();
	private List<CellContainer> columns = new ArrayList<CellContainer>();

	public DetailDataContainerRenderer(ATableBasedDataDomain dataDomain,
			AGLView view) {
		this.dataDomain = dataDomain;
		this.view = view;
		createRowsAndColumns();
	}

	private void createRowsAndColumns() {
		Set<String> rowIDs = dataDomain.isColumnDimension() ? dataDomain
				.getRecordPerspectiveIDs() : dataDomain
				.getDimensionPerspectiveIDs();
		Set<String> columnIDs = dataDomain.isColumnDimension() ? dataDomain
				.getDimensionPerspectiveIDs() : dataDomain
				.getRecordPerspectiveIDs();

		rows.clear();
		columns.clear();

		for (String id : rowIDs) {
			CellContainer row = new CellContainer();
			row.caption = id;
			row.numSubdivisions = 1;
			rows.add(row);
		}
		for (String id : columnIDs) {
			CellContainer column = new CellContainer();
			column.caption = id;
			column.numSubdivisions = 1;
			columns.add(column);
		}
	}

	@Override
	public void render(GL2 gl) {
		float captionColumnWidth = calcMaxTextWidth(rows);
		float captionRowWidth = calcMaxTextWidth(columns);
		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float currentPositionY = y
				- pixelGLConverter.getGLHeightForPixelHeight(ROW_HEIGHT_PIXELS);
		float textHeight = pixelGLConverter
				.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS);

		for (CellContainer row : rows) {
			textRenderer.renderTextInBounds(gl, row.caption, 0,
					currentPositionY, 0, captionColumnWidth, textHeight);
		}
	}

	private float calcMaxTextWidth(List<CellContainer> containers) {

		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		float maxTextWidth = Float.MIN_VALUE;

		for (CellContainer container : containers) {
			float textWidth = textRenderer.getRequiredTextWidthWithMax(
					container.caption, TEXT_HEIGHT_PIXELS,
					MAX_TEXT_WIDTH_PIXELS);
			if (textWidth > maxTextWidth)
				maxTextWidth = textWidth;
		}

		return maxTextWidth;
	}

	@Override
	public int getMinWidthPixels() {
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		int captionWidth = pixelGLConverter
				.getPixelWidthForGLWidth(calcMaxTextWidth(rows));

		return captionWidth + columns.size() * COLUMN_WIDTH_PIXELS;

	}

	@Override
	public int getMinHeightPixels() {
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		int captionWidth = pixelGLConverter
				.getPixelHeightForGLHeight(calcMaxTextWidth(columns));

		return captionWidth + rows.size() * ROW_HEIGHT_PIXELS;

	}

	@Override
	public void setDimensionGroups(List<ADimensionGroupData> dimensionGroupDatas) {
		createRowsAndColumns();
	}

	@Override
	public Pair<Point2D, Point2D> getAnchorPointsOfDimensionGroup(
			ADimensionGroupData dimensionGroupData) {
		// TODO Auto-generated method stub
		return null;
	}

}
