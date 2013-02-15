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
package org.caleydo.core.view.opengl.layout2.util;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.util.button.Button;

/**
 * Element that renders a {@link Button} using a texture.
 *
 * @author Christian Partl
 *
 */
public class GLImageButton extends PickableGLElement {

	protected final Button button;

	/**
	 *
	 */
	public GLImageButton(Button button) {
		this.button = button;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.fillImage(button.getIconPath(), 0, 0, w, h);
		if (button.isSelected()) {

			g.color(new float[] { 0.55f, 0.55f, 0.55f, 0.5f });
			g.fillRect(0, 0, w, h);

			g.color(new float[] { 0.3f, 0.3f, 0.3f, 1f });
			g.drawPath(true, new Vec2f(0, 0), new Vec2f(w, 0), new Vec2f(w, h), new Vec2f(0, h));

		}
	}

}
