package org.caleydo.view.visbricks.dimensiongroup;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;

public class DimensionGroupGlowRenderer
	extends ColorRenderer
{

	private DimensionGroup dimensionGroup;
	private boolean renderWhenDimensionGroupIsCollapsed;

	public DimensionGroupGlowRenderer(float[] color, DimensionGroup dimensionGroup,
			boolean renderWhenDimensionGroupIsCollapsed)
	{
		super(color);
		this.dimensionGroup = dimensionGroup;
		this.renderWhenDimensionGroupIsCollapsed = renderWhenDimensionGroupIsCollapsed;
	}

	@Override
	public void render(GL2 gl)
	{

		if (dimensionGroup.isCollapsed() != renderWhenDimensionGroupIsCollapsed)
			return;

		if (renderWhenDimensionGroupIsCollapsed)
		{
			PixelGLConverter pixelGLConverter = dimensionGroup.getPixelGLConverter();
			float overlapX = pixelGLConverter.getGLWidthForPixelWidth(10);
			float overlapY = pixelGLConverter.getGLWidthForPixelWidth(10);

			gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(0, y, 0);
			gl.glVertex3f(x, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(x + overlapX, y + overlapY, 0);
			gl.glVertex3f(-overlapX, y + overlapY, 0);

			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(-overlapX, y + overlapY, 0);
			gl.glVertex3f(-overlapX, -overlapY, 0);

			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(x, 0, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(x + overlapX, -overlapY, 0);
			gl.glVertex3f(-overlapX, -overlapY, 0);

			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(x, 0, 0);
			gl.glVertex3f(x, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(x + overlapX, y + overlapY, 0);
			gl.glVertex3f(x + overlapX, -overlapY, 0);

			gl.glEnd();
		}
		else
		{
			gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(-x / 3.0f, 0, 0);
			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(x / 3.0f, 0, 0);
			gl.glVertex3f(x / 3.0f, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(-x / 3.0f, y, 0);

			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(x / 3.0f, 0, 0);
			gl.glVertex3f(2.0f * x / 3.0f, 0, 0);
			gl.glVertex3f(2.0f * x / 3.0f, y, 0);
			gl.glVertex3f(x / 3.0f, y, 0);

			gl.glVertex3f(2.0f * x / 3.0f, 0, 0);
			gl.glColor4f(color[0], color[1], color[2], 0);
			gl.glVertex3f(x + x / 3.0f, 0, 0);
			gl.glVertex3f(x + x / 3.0f, y, 0);
			gl.glColor4f(color[0], color[1], color[2], 1);
			gl.glVertex3f(2.0f * x / 3.0f, y, 0);

			gl.glEnd();
		}

	}

}
