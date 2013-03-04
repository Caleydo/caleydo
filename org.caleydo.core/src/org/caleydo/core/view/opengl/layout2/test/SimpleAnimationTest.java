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
package org.caleydo.core.view.opengl.layout2.test;

import gleem.linalg.Vec4f;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.animation.InOutInitializers;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.StyleAnimations;
import org.caleydo.core.view.opengl.layout2.animation.StyleAnimations.IStyleAnimation;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleAnimationTest extends AnimatedGLElementContainer {
	public SimpleAnimationTest() {
		setLayout(GLLayouts.flowHorizontal(4));
		add(new GLElement(GLRenderers.fillRect(Color.GREEN)).setSize(Float.NaN, 200).setLayoutData(0.3f));
		final GLElement red = new GLElement(GLRenderers.fillRect(Color.RED)).setSize(100, -1);
		add(red);
		animate(red, 4000, 3000, new IStyleAnimation() {
			@Override
			public void render(GLElement elem, GLGraphics g, float alpha) {
				elem.render(g);
				Vec4f bounds = elem.getBounds();
				g.incZ(elem.getzDelta() + .5f);
				int v = (int) (alpha * 300);
				if (v % 2 == 0)
					g.color(Color.BLACK).fillRect(bounds.x(), bounds.y(), bounds.z(), bounds.w());
				g.incZ(-elem.getzDelta() - .5f);
			}
		});

		GLElementContainer l = new GLElementContainer();
		l.setLayout(GLLayouts.flowVertical(5));
		l.add(new GLElement(GLRenderers.fillRect(Color.ORANGE)).setSize(200, 120));
		l.add(new GLElement(GLRenderers.fillRect(Color.YELLOW)).setSize(Float.NaN, 50));
		l.pack(true, false);
		add(l, 5000);
		add(new ButtonElement().setSize(100, 20));

		l = new GLElementContainer();
		l.setLayout(GLLayouts.flowVertical(10));
		l.add(new GLElement(GLRenderers.fillRect(Color.BLACK)), 10);
		l.add(new GLElement(GLRenderers.fillRect(Color.WHITE)), 5);
		add(l);

	}

	class ButtonElement extends PickableGLElement {
		private boolean hovered;

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			if (this.hovered)
				g.color(Color.RED);
			else
				g.color(Color.BLUE);
			g.fillRect(0, 0, w, h);
		}

		@Override
		protected void onMouseOver(Pick pick) {
			this.hovered = true;
			((AnimatedGLElementContainer) getParent()).animate(this, 5000, StyleAnimations.glowOut(Color.YELLOW, 10));
			repaint();
		}

		@Override
		protected void onMouseOut(Pick pick) {
			this.hovered = false;
			repaint();
			((AnimatedGLElementContainer) getParent()).remove(this, 500, new InOutTransitions.InOutTransitionBase(
					InOutInitializers.LEFT, MoveTransitions.MOVE_LINEAR));
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(Color.CYAN).fillRect(0, 0, w, h);
		super.renderImpl(g, w, h);
	}

	public static void main(String[] args) {
		GLSandBox.main(args, new SimpleAnimationTest());
	}
}
