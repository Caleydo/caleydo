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
package org.caleydo.core.view.opengl.miniview.slider;

import gleem.linalg.Vec4f;

import java.awt.Font;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.miniview.AGLMiniView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.border.IBorderRenderStyle;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * OpenGL2 Slider Distribution Mini View
 * 
 * @author Stefan Sauer
 */
public class GLDistributionMiniView
	extends AGLMiniView {

	public enum ALIGN {
		LEFT,
		RIGHT,
		CENTER;
	}

	private ALIGN alignment = ALIGN.LEFT;

	private Vec4f vDistributionColor = new Vec4f(0.0f, 0.5f, 0.0f, 1.0f);
	private Vec4f vDistributionSelectedColor = new Vec4f(0.0f, 0.75f, 0.0f, 1.0f);

	private ArrayList<Float> alNormalicedDistribution = null;
	private ArrayList<Float> alNormalicedSelectedDistribution = null;

	TextRenderer textRenderer = null;

	private IBorderRenderStyle borderStyle;

	public GLDistributionMiniView(GLMouseListener glMouseListener, final int viewID, final int iDistributionID) {
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 16), false);
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.setSmoothing(false);

		fWidth = 1.0f;
		fHeight = 1.0f;
	}

	@Override
	public void setHeight(final float fHeight) {
		this.fHeight = fHeight;
	}

	public void setNormalicedDistribution(final ArrayList<Float> values) {
		alNormalicedDistribution = values;
	}

	public void setDistribution(final ArrayList<Float> values) {

		// alAxisScaleNominal = new ArrayList<String>();
		// float realinc = 1000 / (values.size());
		// float inccounter = 0;
		// int counter = 0;
		// for (int i = 0; i < 1000; ++i)
		// {
		// alAxisScaleNominal.add(values.get(counter));
		// if (inccounter >= realinc)
		// {
		// inccounter = 0;
		// counter++;
		// }
		// ++inccounter;
		// }

		throw new RuntimeException("not implemented yet");
	}

	public void setNormalicedSelectedDistribution(final ArrayList<Float> values) {
		alNormalicedSelectedDistribution = values;
	}

	public void setSelectedDistribution(final ArrayList<Float> values) {
		throw new RuntimeException("not implemented yet");
	}

	public void setBorderStyle(IBorderRenderStyle borderStyle) {
		this.borderStyle = borderStyle;
	}

	public void setDistributionAlign(ALIGN align) {
		alignment = align;
	}

	@Override
	public void render(GL2 gl, float fXOrigin, float fYOrigin, float fZOrigin) {
		gl.glPushMatrix();
		// draw helplines
		// GLHelperFunctions.drawAxis(gl);

		drawBorder(gl);
		drawDistribution(gl);

		gl.glPopMatrix();
	}

	private void drawBorder(GL2 gl) {
		if (borderStyle == null)
			return;
		gl.glScalef(fWidth, fHeight, 1.0f);
		borderStyle.display(gl);
		gl.glScalef(1.0f / fWidth, 1.0f / fHeight, 1.0f);

	}

	private void drawDistribution(GL2 gl) {
		if (alNormalicedDistribution != null) {

			float increment = fHeight / alNormalicedDistribution.size();

			gl.glPushMatrix();
			gl.glTranslatef(0, 0, -0.01f);

			gl.glColor4f(vDistributionColor.get(0), vDistributionColor.get(1), vDistributionColor.get(2),
				vDistributionColor.get(3));

			for (float value : alNormalicedDistribution) {
				float posx = 0;
				float width = fWidth * value;
				if (alignment == ALIGN.RIGHT) {
					posx = fWidth - width;
				}
				if (alignment == ALIGN.CENTER) {
					posx = fWidth / 2.0f - width / 2.0f;
				}

				gl.glBegin(GL2.GL_QUADS);
				gl.glNormal3i(0, 1, 0);
				gl.glVertex3f(posx, 0, 0);
				gl.glVertex3f(posx, increment, 0);
				gl.glVertex3f(posx + width, increment, 0);
				gl.glVertex3f(posx + width, 0, 0);
				gl.glEnd();

				gl.glTranslatef(0, increment, 0);
			}
			gl.glTranslatef(0, 0, 0.01f);
			gl.glPopMatrix();
		}

		if (alNormalicedSelectedDistribution != null) {
			float increment = fHeight / alNormalicedSelectedDistribution.size();

			gl.glPushMatrix();
			// gl.glTranslatef(0, 0, -0.75f);

			gl.glColor4f(vDistributionSelectedColor.get(0), vDistributionSelectedColor.get(1),
				vDistributionSelectedColor.get(2), vDistributionSelectedColor.get(3));

			for (float value : alNormalicedSelectedDistribution) {
				float posx = 0;
				float width = fWidth * value;
				if (alignment == ALIGN.RIGHT) {
					posx = fWidth - width;
				}
				if (alignment == ALIGN.CENTER) {
					posx = fWidth / 2.0f - width / 2.0f;
				}

				gl.glBegin(GL2.GL_QUADS);
				gl.glNormal3i(0, 1, 0);
				gl.glVertex3f(posx, 0, 0);
				gl.glVertex3f(posx, increment, 0);
				gl.glVertex3f(posx + width, increment, 0);
				gl.glVertex3f(posx + width, 0, 0);
				gl.glEnd();

				gl.glTranslatef(0, increment, 0);
			}

			gl.glPopMatrix();
		}
	}

}
