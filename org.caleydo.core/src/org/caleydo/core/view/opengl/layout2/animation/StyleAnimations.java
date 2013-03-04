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
package org.caleydo.core.view.opengl.layout2.animation;

import gleem.linalg.Vec4f;

import java.awt.Color;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Samuel Gratzl
 *
 */
public class StyleAnimations {
	public interface IStyleAnimation {
		void render(GLElement elem, GLGraphics g, float alpha);
	}

	public static IStyleAnimation glowOut(final Color color, final int width) {
		return new IStyleAnimation() {
			@Override
			public void render(GLElement elem, GLGraphics g, float alpha) {
				elem.render(g);
				Vec4f bounds = elem.getBounds();
				Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) ((1 - alpha) * 255));
				g.decZ();
				g.gl.glBegin(GL2.GL_QUAD_STRIP);
				float z = g.z();
				// first
				float x = bounds.x();
				float x2 = x + bounds.z();
				float y = bounds.y();
				float y2 = y + bounds.w();
				g.color(c).gl.glVertex3f(x, y, z);
				g.color(1, 1, 1, 1 - alpha).gl.glVertex3f(x - width, y - width, z);
				g.color(c).gl.glVertex3f(x2, y, z);
				g.color(1, 1, 1, 1 - alpha).gl.glVertex3f(x2 + width, y - width, z);
				g.color(c).gl.glVertex3f(x2, y2, z);
				g.color(1, 1, 1, 1 - alpha).gl.glVertex3f(x2 + width, y2 + width, z);
				g.color(c).gl.glVertex3f(x, y2, z);
				g.color(1, 1, 1, 1 - alpha).gl.glVertex3f(x - width, y2 + width, z);
				g.color(c).gl.glVertex3f(x, y, z);
				g.color(1, 1, 1, 1 - alpha).gl.glVertex3f(x - width, y - width, z);
				g.gl.glEnd();
				g.incZ();
			}
		};
	}
}
