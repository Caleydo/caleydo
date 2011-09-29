package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class DetailDataContainerRenderer extends ADataContainerRenderer {

	private static final int MAX_TEXT_WIDTH_PIXELS = 80;
	private static final int TEXT_HEIGHT_PIXELS = 12;
	private static final int COLUMN_WIDTH_PIXELS = 20;
	private static final int ROW_HEIGHT_PIXELS = 20;
	private static final int CAPTION_SPACING = 5;
	private static final int CELL_SPACING = 2;

	private ATableBasedDataDomain dataDomain;
	private AGLView view;
	List<ADimensionGroupData> dimensionGroupDatas;

	private class CellContainer {
		private String caption;
		private int numSubdivisions;
		private float position;
	}

	private List<CellContainer> rows = new ArrayList<CellContainer>();
	private List<CellContainer> columns = new ArrayList<CellContainer>();

	public DetailDataContainerRenderer(ATableBasedDataDomain dataDomain,
			AGLView view) {
		this.dataDomain = dataDomain;
		this.view = view;

		// FIXME: Use from datadomain
		dimensionGroupDatas = new ArrayList<ADimensionGroupData>();
		FakeDimensionGroupData data = new FakeDimensionGroupData(0);
		data.setDimensionPerspectiveID("ColumnPerspec2");
		data.setRecordPerspectiveID("Row1");
		dimensionGroupDatas.add(data);

		data = new FakeDimensionGroupData(0);
		data.setDimensionPerspectiveID("ColumnPerspec2");
		data.setRecordPerspectiveID("AnotherRow");
		dimensionGroupDatas.add(data);

		data = new FakeDimensionGroupData(0);
		data.setDimensionPerspectiveID("AnotherColumn2");
		data.setRecordPerspectiveID("Row1");
		dimensionGroupDatas.add(data);
		data = new FakeDimensionGroupData(0);

		data.setDimensionPerspectiveID("YetAnotherColumn2");
		data.setRecordPerspectiveID("YetAnotherRow");
		dimensionGroupDatas.add(data);
		createRowsAndColumns(dimensionGroupDatas);
	}

	private void createRowsAndColumns(
			List<ADimensionGroupData> dimensionGroupDatas) {
		// FIXME: Use real data
		// Set<String> rowIDs = dataDomain.isColumnDimension() ? dataDomain
		// .getRecordPerspectiveIDs() : dataDomain
		// .getDimensionPerspectiveIDs();
		// Set<String> columnIDs = dataDomain.isColumnDimension() ? dataDomain
		// .getDimensionPerspectiveIDs() : dataDomain
		// .getRecordPerspectiveIDs();

		String[] rowIDs = new String[] { "Row1", "RowPerspec2", "AnotherRow",
				"YetAnotherRow" };
		String[] columnIDs = new String[] { "Column1", "ColumnPerspec2",
				"AnotherColumn", "YetAnotherColumn", "Column2",
				"ColumnPerspec22", "AnotherColumn2", "YetAnotherColumn2" };

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
			int numSubdivisions = 1;
			for (ADimensionGroupData dimensionGroupData : dimensionGroupDatas) {
				// FIXME: do it properly
				if (((FakeDimensionGroupData) dimensionGroupData)
						.getDimensionPerspectiveID().equals(id)) {
					numSubdivisions++;
				}
			}

			column.numSubdivisions = numSubdivisions;
			columns.add(column);
		}
	}

	@Override
	public void render(GL2 gl) {
		float captionColumnWidth = calcMaxTextWidth(rows);
		float captionRowHeight = calcMaxTextWidth(columns);
		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float currentPositionX = (x / 2.0f)
				- pixelGLConverter
						.getGLWidthForPixelWidth(getMinWidthPixels() / 2);
		float rowHeight = pixelGLConverter
				.getGLHeightForPixelHeight(ROW_HEIGHT_PIXELS);
		float currentPositionY = y - captionRowHeight
				- pixelGLConverter.getGLHeightForPixelHeight(CAPTION_SPACING);
		float textHeight = pixelGLConverter
				.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS);

		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, currentPositionY, 0);
		gl.glVertex3f(0, currentPositionY, 0);

		gl.glVertex3f(
				currentPositionX
						+ captionColumnWidth
						+ pixelGLConverter
								.getGLWidthForPixelWidth(CAPTION_SPACING),
				currentPositionY, 0);
		gl.glVertex3f(x, currentPositionY, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(
				currentPositionX
						+ captionColumnWidth
						+ pixelGLConverter
								.getGLWidthForPixelWidth(CAPTION_SPACING), y, 0);
		gl.glEnd();
		gl.glPopAttrib();

		for (CellContainer row : rows) {
			float textPositionY = currentPositionY - rowHeight
					+ (rowHeight - textHeight) / 2.0f
					+ pixelGLConverter.getGLHeightForPixelHeight(2);
			textRenderer.renderTextInBounds(gl, row.caption, currentPositionX,
					textPositionY, 0, captionColumnWidth, textHeight);

			gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
			gl.glColor3f(0, 0, 0);
			gl.glLineWidth(1);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(0, currentPositionY, 0);
			gl.glVertex3f(x, currentPositionY, 0);
			gl.glEnd();
			gl.glPopAttrib();

			row.position = currentPositionY;

			currentPositionY -= rowHeight;

		}

		float columnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(COLUMN_WIDTH_PIXELS);
		currentPositionX += captionColumnWidth
				+ pixelGLConverter.getGLWidthForPixelWidth(CAPTION_SPACING);

		for (CellContainer column : columns) {
			float currentColumnWidth = columnWidth * column.numSubdivisions;

			float textPositionX = currentPositionX
					+ (currentColumnWidth - textHeight) / 2.0f
					+ pixelGLConverter.getGLHeightForPixelHeight(2);

			gl.glPushMatrix();
			gl.glTranslatef(textPositionX, y, 0);
			gl.glRotatef(-90, 0, 0, 1);

			textRenderer.renderTextInBounds(gl, column.caption, 0, 0, 0,
					captionRowHeight, textHeight);
			gl.glPopMatrix();

			gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
			gl.glColor3f(0, 0, 0);
			gl.glLineWidth(1);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(currentPositionX, 0, 0);
			gl.glVertex3f(currentPositionX, y, 0);
			for (int i = 1; i < column.numSubdivisions; i++) {
				gl.glVertex3f(currentPositionX + i * columnWidth, 0, 0);
				gl.glVertex3f(
						currentPositionX + i * columnWidth,
						y
								- captionRowHeight
								- pixelGLConverter
										.getGLHeightForPixelHeight(CAPTION_SPACING),
						0);
			}
			gl.glEnd();

			float currentDimGroupPositionX = currentPositionX;

			for (CellContainer row : rows) {
				float cellSpacingX = pixelGLConverter
						.getGLWidthForPixelWidth(CELL_SPACING);
				float cellSpacingY = pixelGLConverter
						.getGLHeightForPixelHeight(CELL_SPACING);

				float cellPositionX = currentPositionX + currentColumnWidth
						- columnWidth;

				boolean dimensionGroupExists = false;

				for (ADimensionGroupData dimensionGroupData : dimensionGroupDatas) {
					// FIXME: Do properly
					FakeDimensionGroupData fakeDimensionGroupData = (FakeDimensionGroupData) dimensionGroupData;
					if (fakeDimensionGroupData.getDimensionPerspectiveID()
							.equals(column.caption)
							&& fakeDimensionGroupData.getRecordPerspectiveID()
									.equals(row.caption)) {

						gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);
						gl.glColor3fv(dataDomain.getColor().getRGB(), 0);

						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3f(currentDimGroupPositionX + cellSpacingX,
								row.position - rowHeight + cellSpacingY, 0);
						gl.glVertex3f(currentDimGroupPositionX + columnWidth
								- cellSpacingX, row.position - rowHeight
								+ cellSpacingY, 0);
						gl.glVertex3f(currentDimGroupPositionX + columnWidth
								- cellSpacingX, row.position - cellSpacingY, 0);
						gl.glVertex3f(currentDimGroupPositionX + cellSpacingX,
								row.position - cellSpacingY, 0);
						gl.glEnd();
						gl.glPopAttrib();

						currentDimGroupPositionX += columnWidth;
						dimensionGroupExists = true;
						break;
					}
				}
				
				if(!dimensionGroupExists) {
					gl.glColor3f(0.7f, 0.7f, 0.7f);
					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex3f(cellPositionX + cellSpacingX, row.position
							- rowHeight + cellSpacingY, 0);
					gl.glVertex3f(cellPositionX + columnWidth - cellSpacingX,
							row.position - rowHeight + cellSpacingY, 0);
					gl.glVertex3f(cellPositionX + columnWidth - cellSpacingX,
							row.position - cellSpacingY, 0);
					gl.glVertex3f(cellPositionX + cellSpacingX, row.position
							- cellSpacingY, 0);
					gl.glEnd();
				}
				
			}

			gl.glPopAttrib();

			column.position = currentPositionX;

			currentPositionX += currentColumnWidth;
		}

	}

	private float calcMaxTextWidth(List<CellContainer> containers) {

		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float maxTextWidth = Float.MIN_VALUE;

		for (CellContainer container : containers) {
			float textWidth = textRenderer.getRequiredTextWidthWithMax(
					container.caption, pixelGLConverter
							.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS),
					pixelGLConverter
							.getGLWidthForPixelWidth(MAX_TEXT_WIDTH_PIXELS));
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

		int sumColumnWidth = 0;

		for (CellContainer column : columns) {
			sumColumnWidth += column.numSubdivisions * COLUMN_WIDTH_PIXELS;
		}

		return captionWidth + sumColumnWidth + CAPTION_SPACING;

	}

	@Override
	public int getMinHeightPixels() {
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		int captionWidth = pixelGLConverter
				.getPixelHeightForGLHeight(calcMaxTextWidth(columns));

		return captionWidth + rows.size() * ROW_HEIGHT_PIXELS + CAPTION_SPACING;

	}

	@Override
	public void setDimensionGroups(List<ADimensionGroupData> dimensionGroupDatas) {
		createRowsAndColumns(dimensionGroupDatas);
	}

	@Override
	public Pair<Point2D, Point2D> getAnchorPointsOfDimensionGroup(
			ADimensionGroupData dimensionGroupData) {
		// TODO Auto-generated method stub
		return null;
	}

}
