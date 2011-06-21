package org.caleydo.view.datagraph;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.visbricks.brick.data.IDimensionGroupData;

public class ComparisonGroupOverviewRenderer extends LayoutRenderer {

	private final static int SPACING_PIXELS = 2;
	private final static int MIN_COMP_GROUP_WIDTH_PIXELS = 10;

	private List<IDimensionGroupData> dimensionGroupData;
	private AGLView view;

	public ComparisonGroupOverviewRenderer(
			List<IDimensionGroupData> dimensionGroupData, AGLView view) {
		this.dimensionGroupData = dimensionGroupData;
		this.view = view;
	}

	@Override
	public void render(GL2 gl) {

		PixelGLConverter pixelGLConverter = view.getParentGLCanvas()
				.getPixelGLConverter();

		gl.glBegin(GL2.GL_QUADS);

		float currentPosX = 0;
		float step = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS
				+ MIN_COMP_GROUP_WIDTH_PIXELS);

		for (int i = 0; i < dimensionGroupData.size(); i++) {

			gl.glVertex3f(currentPosX, 0, 0);
			gl.glVertex3f(
					currentPosX
							+ pixelGLConverter
									.getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS),
					0, 0);
			gl.glVertex3f(
					currentPosX
							+ pixelGLConverter
									.getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS),
					y, 0);
			gl.glVertex3f(currentPosX, y, 0);

			currentPosX += step;
		}

		gl.glEnd();
	}

	@Override
	public int getMinWidthPixels() {
		return (dimensionGroupData.size() * MIN_COMP_GROUP_WIDTH_PIXELS)
				+ ((dimensionGroupData.size() - 1) * SPACING_PIXELS);
	}

}
