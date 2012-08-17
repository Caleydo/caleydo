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
package org.caleydo.view.dvi.tableperspective;

import java.awt.geom.Point2D;
import javax.media.opengl.GL2;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.dvi.node.IDVINode;

public class TablePerspectiveRenderer extends ADraggableColorRenderer {

	private static final int TEXT_SPACING_PIXELS = 2;

	public final static int TEXT_ROTATION_0 = 0;
	public final static int TEXT_ROTATION_90 = 90;
	public final static int TEXT_ROTATION_180 = 180;
	public final static int TEXT_ROTATION_270 = 270;

	private TablePerspective tablePerspective;

	private IDVINode node;
	protected boolean showText = true;
	protected int textRotation = 0;
	protected int textHeightPixels;

	public TablePerspectiveRenderer(TablePerspective tablePerspective, AGLView view,
			IDVINode node, float[] color) {
		super(color,
				new float[] { color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f, 1f }, 2,
				view);
		this.setTablePerspective(tablePerspective);
		this.view = view;
		this.node = node;
	}

	@Override
	public void renderContent(GL2 gl) {
		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0.1f);
		super.renderContent(gl);
		gl.glPopMatrix();

		if (showText) {
			float textPositionX = 0;
			switch (textRotation) {
			case TEXT_ROTATION_0:
				textRenderer.renderTextInBounds(
						gl,
						tablePerspective.getLabel(),
						pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
						pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
						0.1f,
						x
								- 2
								* pixelGLConverter
										.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
						pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
				break;

			case TEXT_ROTATION_90:

				gl.glPushMatrix();
				textPositionX = pixelGLConverter
						.getGLHeightForPixelHeight(textHeightPixels - 2)
						+ (x - pixelGLConverter
								.getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;

				gl.glTranslatef(textPositionX,
						pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
						0.2f);
				gl.glRotatef(90, 0, 0, 1);
				textRenderer.renderTextInBounds(
						gl,
						tablePerspective.getLabel(),
						0,
						0,
						0,
						y
								- pixelGLConverter
										.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
						pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
				gl.glPopMatrix();
				break;
			case TEXT_ROTATION_270:

				gl.glPushMatrix();
				textPositionX = (x - pixelGLConverter
						.getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;
				gl.glTranslatef(
						textPositionX,
						y
								- pixelGLConverter
										.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
						0.2f);
				gl.glRotatef(-90, 0, 0, 1);
				textRenderer.renderTextInBounds(
						gl,
						tablePerspective.getLabel(),
						0,
						0,
						0,
						y
								- pixelGLConverter
										.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
						pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
				gl.glPopMatrix();
				break;
			}
			;

		}

	}

	public void setTablePerspective(TablePerspective dimensionGroupData) {
		this.tablePerspective = dimensionGroupData;
	}

	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	@Override
	protected Point2D getPosition() {
		return node.getBottomTablePerspectiveAnchorPoints(tablePerspective).getFirst();
	}

	public boolean isShowText() {
		return showText;
	}

	public void setShowText(boolean showText) {
		this.showText = showText;
	}

	public int getTextRotation() {
		return textRotation;
	}

	public void setTextRotation(int textRotation) {
		this.textRotation = textRotation;
	}

	public int getTextHeightPixels() {
		return textHeightPixels;
	}

	public void setTextHeightPixels(int textHeightPixels) {
		this.textHeightPixels = textHeightPixels;
	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}

}
