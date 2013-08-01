/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.tableperspective;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.view.dvi.node.IDVINode;

public class TablePerspectiveRenderer extends ADraggableColorRenderer {

	private static final int TEXT_SPACING_PIXELS = 2;

	public final static int TEXT_ROTATION_0 = 0;
	public final static int TEXT_ROTATION_90 = 90;
	public final static int TEXT_ROTATION_180 = 180;
	public final static int TEXT_ROTATION_270 = 270;

	public static final float[] INACTIVE_COLOR = { 0.9f, 0.9f, 0.9f, 1f };
	public static final float[] INACTIVE_BORDER_COLOR = { 0.7f, 0.7f, 0.7f, 1f };

	private TablePerspective tablePerspective;
	private TablePerspectiveCreator creator;

	private IDVINode node;
	protected boolean showText = true;
	protected int textRotation = 0;
	protected int textHeightPixels;
	protected boolean isActive = false;

	public TablePerspectiveRenderer(TablePerspective tablePerspective, AGLView view, IDVINode node) {
		super(INACTIVE_COLOR, INACTIVE_BORDER_COLOR, 2, view);
		this.setTablePerspective(tablePerspective);
		this.view = view;
		this.node = node;
	}

	public TablePerspectiveRenderer(TablePerspectiveCreator creator, AGLView view, IDVINode node) {
		super(INACTIVE_COLOR, INACTIVE_BORDER_COLOR, 2, view);
		this.creator = creator;
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

		if (showText && tablePerspective != null) {
			float textPositionX = 0;
			switch (textRotation) {
			case TEXT_ROTATION_0:
				textRenderer.renderTextInBounds(gl, tablePerspective.getLabel(),
						pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
						pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS), 0.1f,
						x - 2 * pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
						pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
				break;

			case TEXT_ROTATION_90:

				gl.glPushMatrix();
				textPositionX = pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels - 2)
						+ (x - pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;

				gl.glTranslatef(textPositionX, pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS), 0.2f);
				gl.glRotatef(90, 0, 0, 1);
				textRenderer.renderTextInBounds(gl, tablePerspective.getLabel(), 0, 0, 0,
						y - pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
						pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
				gl.glPopMatrix();
				break;
			case TEXT_ROTATION_270:

				gl.glPushMatrix();
				textPositionX = (x - pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;
				gl.glTranslatef(textPositionX, y - pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
						0.2f);
				gl.glRotatef(-90, 0, 0, 1);
				textRenderer.renderTextInBounds(gl, tablePerspective.getLabel(), 0, 0, 0,
						y - pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
						pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
				gl.glPopMatrix();
				break;
			}
		}
	}

	public void setTablePerspective(TablePerspective dimensionGroupData) {
		this.tablePerspective = dimensionGroupData;
	}

	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	public TablePerspective createOrGetTablePerspective() {
		if (tablePerspective != null)
			return tablePerspective;
		return creator.create();
	}

	/**
	 * @return the creator, see {@link #creator}
	 */
	public TablePerspectiveCreator getCreator() {
		return creator;
	}

	public boolean hasTablePerspective() {
		return tablePerspective != null;
	}

	@Override
	protected Point2D getPosition() {
		return node.getBottomObjectAnchorPoints(this).getFirst();
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
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	/**
	 * @return the isActive, see {@link #isActive}
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            setter, see {@link isActive}
	 */
	public void setActive(boolean isActive) {
		if (isActive) {
			if (tablePerspective == null)
				return;
			float[] color = getActiveColor();
			float[] borderColor = new float[] { color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f, 1f };
			setColor(color);
			setBorderColor(borderColor);
		} else {
			setColor(INACTIVE_COLOR);
			setBorderColor(INACTIVE_BORDER_COLOR);
		}
		this.isActive = isActive;
	}

	public float[] getActiveColor() {

		if (tablePerspective instanceof PathwayTablePerspective) {
			return ((PathwayTablePerspective) tablePerspective).getPathwayDataDomain().getColor().getRGBA();
		} else {
			return tablePerspective.getDataDomain().getColor().getRGBA();
		}
	}

	public ATableBasedDataDomain getDataDomain() {
		if (tablePerspective != null)
			return tablePerspective.getDataDomain();
		return creator.getDataDomain();
	}
}
