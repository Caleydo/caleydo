package org.caleydo.view.datagraph.datacontainer.matrix;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;
import org.caleydo.view.datagraph.datacontainer.DimensionGroupRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class TopDownDataContainerMatrixRenderingStrategy extends
		ADataContainerMatrixRenderingStrategy {

	@Override
	public void render(GL2 gl, List<CellContainer> rows,
			List<CellContainer> columns, Map<String, ColorRenderer> cells,
			Map<Integer, Pair<Point2D, Point2D>> bottomDimensionGroupPositions,
			Map<Integer, Pair<Point2D, Point2D>> topDimensionGroupPositions,
			float x, float y, IDataGraphNode node, GLDataGraph view,
			List<Pair<String, Integer>> pickingIDsToBePushed) {
		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float captionColumnWidth = calcMaxTextWidth(rows, view);
		float captionRowHeight = calcMaxTextWidth(columns, view);

		float currentPositionX = (x / 2.0f)
				- pixelGLConverter.getGLWidthForPixelWidth(getMinWidthPixels(
						rows, columns, view) / 2);
		float rowHeight = pixelGLConverter
				.getGLHeightForPixelHeight(ROW_HEIGHT_PIXELS);
		float captionSpacingY = pixelGLConverter
				.getGLHeightForPixelHeight(CAPTION_SPACING_PIXELS);

		float captionSpacingX = pixelGLConverter
				.getGLWidthForPixelWidth(CAPTION_SPACING_PIXELS);

		float currentPositionY = y - captionRowHeight - captionSpacingY;
		float textHeight = pixelGLConverter
				.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS);

		for (int i = 0; i < rows.size(); i++) {

			CellContainer row = rows.get(i);
			if (!row.isVisible) {
				continue;
			}

			float textPositionY = currentPositionY - rowHeight
					+ (rowHeight - textHeight) / 2.0f
					+ pixelGLConverter.getGLHeightForPixelHeight(2);

			float childIndent = 0;
			float parentIndent = 0;

			if (row.parentContainer == null) {

				gl.glColor3f(0.7f, 0.7f, 0.7f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3f(currentPositionX, currentPositionY - rowHeight, 0);
				gl.glVertex3f(currentPositionX + captionColumnWidth
						+ captionSpacingX, currentPositionY - rowHeight, 0);
				gl.glVertex3f(currentPositionX + captionColumnWidth
						+ captionSpacingX, currentPositionY, 0);
				gl.glVertex3f(currentPositionX, currentPositionY, 0);
				gl.glEnd();

				if (row.childContainers != null
						&& row.childContainers.size() > 1) {
					Button collapsePerspectiveButton = new Button(
							DataContainerMatrixRenderer.COLLAPSE_BUTTON_PICKING_TYPE
									+ node.getID(),
							row.id.hashCode(),
							row.isCollapsed ? EIconTextures.GROUPER_COLLAPSE_PLUS
									: EIconTextures.GROUPER_COLLAPSE_MINUS);

					ButtonRenderer collapsePerspectiveButtonRenderer = new ButtonRenderer(
							collapsePerspectiveButton, view,
							view.getTextureManager());

					collapsePerspectiveButtonRenderer.setLimits(
							captionSpacingX * 2, captionSpacingX * 2);

					gl.glPushMatrix();
					gl.glTranslatef(
							currentPositionX
									+ pixelGLConverter
											.getGLWidthForPixelWidth(2),
							currentPositionY - rowHeight / 2.0f
									- captionSpacingX, 0);
					collapsePerspectiveButtonRenderer.render(gl);
					gl.glPopMatrix();

					parentIndent = captionSpacingX * 2
							+ pixelGLConverter.getGLHeightForPixelHeight(2);

				}
			} else {

				childIndent = captionSpacingY * 2;

				gl.glColor3f(0.8f, 0.8f, 0.8f);

				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3f(currentPositionX, currentPositionY, 0);
				gl.glVertex3f(currentPositionX + captionColumnWidth
						+ captionSpacingX, currentPositionY, 0);
				gl.glVertex3f(currentPositionX + captionColumnWidth
						+ captionSpacingX, currentPositionY - rowHeight, 0);
				gl.glVertex3f(currentPositionX, currentPositionY - rowHeight, 0);

				gl.glColor3f(0.7f, 0.7f, 0.7f);

				gl.glVertex3f(currentPositionX, currentPositionY, 0);
				gl.glVertex3f(currentPositionX + childIndent, currentPositionY,
						0);
				gl.glVertex3f(currentPositionX + childIndent, currentPositionY
						- rowHeight, 0);
				gl.glVertex3f(currentPositionX, currentPositionY - rowHeight, 0);

				gl.glEnd();
			}

			// gl.glColor3f(0, 0, 0);
			textRenderer.setColor(new float[] { 0, 0, 0 });
			textRenderer.renderTextInBounds(gl, row.caption, currentPositionX
					+ captionSpacingX + parentIndent + childIndent,
					textPositionY, 0, captionColumnWidth - childIndent
							- parentIndent - 2 * captionSpacingX, textHeight);

			gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
			gl.glColor3f(0, 0, 0);
			if ((row.parentContainer != null) && (i != 0)
					&& (rows.get(i - 1) != row.parentContainer)) {
				gl.glColor3f(0.5f, 0.5f, 0.5f);
			}
			gl.glLineWidth(1);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(currentPositionX + childIndent, currentPositionY,
					0.1f);
			gl.glVertex3f(x, currentPositionY, 0.1f);
			gl.glEnd();
			gl.glPopAttrib();

			row.position = currentPositionY;

			currentPositionY -= rowHeight;

		}

		float columnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(COLUMN_WIDTH_PIXELS);
		currentPositionX += captionColumnWidth
				+ pixelGLConverter
						.getGLWidthForPixelWidth(CAPTION_SPACING_PIXELS);

		for (int i = 0; i < columns.size(); i++) {
			CellContainer column = columns.get(i);
			if (!column.isVisible) {
				continue;
			}
			float currentColumnWidth = columnWidth * column.numSubdivisions;

			gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);

			float childIndent = 0;
			float parentIndent = 0;

			gl.glColor3f(0.7f, 0.7f, 0.7f);
			if (column.parentContainer == null) {

				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3f(currentPositionX, y - captionRowHeight
						- captionSpacingY, 0);
				gl.glVertex3f(currentPositionX + currentColumnWidth, y
						- captionRowHeight - captionSpacingY, 0);
				gl.glVertex3f(currentPositionX + currentColumnWidth, y, 0);
				gl.glVertex3f(currentPositionX, y, 0);
				gl.glEnd();

				if (column.childContainers != null
						&& column.childContainers.size() > 1) {
					Button collapsePerspectiveButton = new Button(
							DataContainerMatrixRenderer.COLLAPSE_BUTTON_PICKING_TYPE
									+ node.getID(),
							column.id.hashCode(),
							column.isCollapsed ? EIconTextures.GROUPER_COLLAPSE_PLUS
									: EIconTextures.GROUPER_COLLAPSE_MINUS);

					ButtonRenderer collapsePerspectiveButtonRenderer = new ButtonRenderer(
							collapsePerspectiveButton, view,
							view.getTextureManager());

					collapsePerspectiveButtonRenderer.setLimits(
							captionSpacingY * 2, captionSpacingY * 2);

					gl.glPushMatrix();
					gl.glTranslatef(currentPositionX + currentColumnWidth
							/ 2.0f - captionSpacingY,
							y - pixelGLConverter.getGLHeightForPixelHeight(2)
									- captionSpacingY * 2, 0);
					collapsePerspectiveButtonRenderer.render(gl);
					gl.glPopMatrix();

					parentIndent = captionSpacingY * 2
							+ pixelGLConverter.getGLHeightForPixelHeight(2);

				}

			} else {

				childIndent = captionSpacingY * 2;

				gl.glColor3f(0.8f, 0.8f, 0.8f);

				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3f(currentPositionX, y - captionRowHeight
						- captionSpacingY, 0);
				gl.glVertex3f(currentPositionX + currentColumnWidth, y
						- captionRowHeight - captionSpacingY, 0);
				gl.glVertex3f(currentPositionX + currentColumnWidth, y, 0);
				gl.glVertex3f(currentPositionX, y, 0);

				gl.glColor3f(0.7f, 0.7f, 0.7f);

				gl.glVertex3f(currentPositionX, y - childIndent, 0);
				gl.glVertex3f(currentPositionX + currentColumnWidth, y
						- childIndent, 0);
				gl.glVertex3f(currentPositionX + currentColumnWidth, y, 0);
				gl.glVertex3f(currentPositionX, y, 0);

				gl.glEnd();

				// gl.glColor3f(1,1,1);
				// gl.glBegin(GL2.GL_LINES);
				// gl.glVertex3f(currentPositionX, y - captionRowHeight
				// - captionSpacingY, 1);
				// gl.glVertex3f(currentPositionX, y - childIndent, 1);
				// gl.glEnd();
			}

			float textPositionX = currentPositionX
					+ (currentColumnWidth - textHeight) / 2.0f
					+ pixelGLConverter.getGLHeightForPixelHeight(2);

			gl.glPushMatrix();
			gl.glTranslatef(textPositionX, y - childIndent - parentIndent
					- captionSpacingY, 0);
			gl.glRotatef(-90, 0, 0, 1);
			// gl.glColor3f(0, 0, 0);
			textRenderer.setColor(new float[] { 0, 0, 0 });
			textRenderer.renderTextInBounds(gl, column.caption, 0, 0, 0,
					captionRowHeight - childIndent - parentIndent - 2
							* captionSpacingY, textHeight);
			gl.glPopMatrix();

			gl.glColor3f(0, 0, 0);
			if ((column.parentContainer != null) && (i != 0)
					&& (columns.get(i - 1) != column.parentContainer)) {
				gl.glColor3f(0.5f, 0.5f, 0.5f);
			}
			gl.glLineWidth(1);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(currentPositionX, 0, 0);
			gl.glVertex3f(currentPositionX, y - childIndent, 0);
			// for (int i = 1; i < column.numSubdivisions; i++) {
			// gl.glVertex3f(currentPositionX + i * columnWidth, 0, 0);
			// gl.glVertex3f(currentPositionX + i * columnWidth, y
			// - captionRowHeight - captionSpacingY, 0);
			// }
			gl.glEnd();

			float currentDimGroupPositionX = currentPositionX;

			for (CellContainer row : rows) {

				if (!row.isVisible) {
					continue;
				}

				float cellSpacingX = pixelGLConverter
						.getGLWidthForPixelWidth(CELL_SPACING_PIXELS);
				float cellSpacingY = pixelGLConverter
						.getGLHeightForPixelHeight(CELL_SPACING_PIXELS);

				float emptyCellPositionX = currentPositionX
						+ currentColumnWidth - columnWidth;

				// boolean dimensionGroupExists = false;

				ColorRenderer cell = cells.get(row.id + column.id);

				gl.glPushMatrix();
				int pickingID = 0;
				if (cell instanceof DimensionGroupRenderer) {

					pickingID = view.getPickingManager().getPickingID(
							view.getID(),
							ADataContainerRenderer.DIMENSION_GROUP_PICKING_TYPE
									+ node.getID(),
							((DimensionGroupRenderer) cell).getDataContainer()
									.getID());

					gl.glTranslatef(currentDimGroupPositionX + cellSpacingX,
							row.position - rowHeight + cellSpacingY, 0);

					Point2D bottomPosition1 = new Point2D.Float(
							currentDimGroupPositionX + cellSpacingX,
							row.position - rowHeight + cellSpacingY);
					Point2D bottomPosition2 = new Point2D.Float(
							(float) bottomPosition1.getX()
									+ pixelGLConverter
											.getGLWidthForPixelWidth(CELL_SIZE_PIXELS),
							(float) bottomPosition1.getY());
					Point2D topPosition1 = new Point2D.Float(
							(float) bottomPosition1.getX(), row.position
									- cellSpacingY);
					Point2D topPosition2 = new Point2D.Float(
							(float) bottomPosition2.getX(),
							(float) topPosition1.getY());

					bottomDimensionGroupPositions.put(
							((DimensionGroupRenderer) cell).getDataContainer()
									.getID(), new Pair<Point2D, Point2D>(
									bottomPosition1, bottomPosition2));
					topDimensionGroupPositions.put(
							((DimensionGroupRenderer) cell).getDataContainer()
									.getID(), new Pair<Point2D, Point2D>(
									topPosition1, topPosition2));

					currentDimGroupPositionX += columnWidth;
				} else {

					pickingID = view.getPickingManager().getPickingID(
							view.getID(),
							DataContainerMatrixRenderer.EMPTY_CELL_PICKING_TYPE
									+ node.getID(),
							((EmptyCellRenderer) cell).getID());

					gl.glTranslatef(emptyCellPositionX + cellSpacingX,
							row.position - rowHeight + cellSpacingY, 0);
				}
				cell.setLimits(pixelGLConverter
						.getGLWidthForPixelWidth(CELL_SIZE_PIXELS),
						pixelGLConverter
								.getGLHeightForPixelHeight(CELL_SIZE_PIXELS));
				gl.glPushName(pickingID);
				for (Pair<String, Integer> pickingIDPair : pickingIDsToBePushed) {
					gl.glPushName(view.getPickingManager().getPickingID(
							view.getID(), pickingIDPair.getFirst(),
							pickingIDPair.getSecond()));
				}
				cell.render(gl);
				for (int k = 0; k < pickingIDsToBePushed.size(); k++) {
					gl.glPopName();
				}
				gl.glPopName();
				gl.glPopMatrix();
			}

			gl.glPopAttrib();

			column.position = currentPositionX;

			currentPositionX += currentColumnWidth;
		}
	}

}
