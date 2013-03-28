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
package org.caleydo.view.subgraph;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * Element for a {@link GLWindow} that lets it slide in or out.
 *
 * @author Christian Partl
 *
 */
public class SlideInElement extends GLElementContainer {

	protected GLWindow window;
	protected ESlideInElementPosition elementPosition;
	protected GLButton slideInButton;

	/**
	 *
	 */
	SlideInElement() {
		window = null;
		elementPosition = ESlideInElementPosition.NONE;
		slideInButton = null;
	}

	/**
	 *
	 */
	public SlideInElement(GLWindow window, ESlideInElementPosition elementPosition) {
		this.window = window;
		this.elementPosition = elementPosition;
		slideInButton = new GLButton(EButtonMode.CHECKBOX);
		SlideInButtonRenderer buttonRenderer = new SlideInButtonRenderer();
		slideInButton.setRenderer(buttonRenderer);
		slideInButton.setPicker(buttonRenderer);
		slideInButton.setHoverEffect(null);
		slideInButton.setSelected(true);
		slideInButton.setCallback(new SlideInButtonCallBack());
		setLayout(GLLayouts.LAYERS);
		add(slideInButton);
	}

	public void setCallBack(ISelectionCallback callback) {
		slideInButton.setCallback(callback);
	}

	public enum ESlideInElementPosition {
		NONE, LEFT, RIGHT, TOP, BOTTOM;
	}

	// /**
	// * Applies a certain window state. The {@link #apply()} method is called automatically when the state should be
	// * applied. The state changes when pressing slider buttons.
	// *
	// * @author Christian
	// *
	// */
	// public interface IWindowState {
	// public void apply();
	// }
	//
	// public static IWindowState fixedWindowSize(float w, float h) {
	// return new IWindowState() {
	//
	// @Override
	// public void apply() {
	//
	// }
	// };
	// }

	protected class SlideInButtonRenderer implements IGLRenderer {

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			switch (elementPosition) {
			case NONE:
				return;
			case LEFT:
				g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(-10, h / 2.0f - 20, 10, 40, 2);
				g.fillImage(!slideInButton.isSelected() ? "resources/icons/bullet_arrow_left.png"
						: "resources/icons/bullet_arrow_right.png", -15, h / 2.0f - 10, 20, 20);
				break;
			case BOTTOM:
				g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(w / 2.0f - 20, h, 40, 10, 2);
				g.fillImage(!slideInButton.isSelected() ? "resources/icons/bullet_arrow_bottom.png"
						: "resources/icons/bullet_arrow_top.png", w / 2.0f - 10, h, 20, 20);
				break;
			case RIGHT:
				g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(w, h / 2.0f - 20, 10, 40, 2);
				g.fillImage(!slideInButton.isSelected() ? "resources/icons/bullet_arrow_right.png"
						: "resources/icons/bullet_arrow_left.png", w - 5, h / 2.0f - 10, 20, 20);
				break;
			case TOP:
				g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(w / 2.0f - 20, -10, 40, 10, 2);
				g.fillImage(!slideInButton.isSelected() ? "resources/icons/bullet_arrow_top.png"
						: "resources/icons/bullet_arrow_bottom.png", w / 2.0f - 10, -15, 20, 20);
				break;
			default:
				break;
			}

		}
	}

	protected class SlideInButtonCallBack implements ISelectionCallback {

		private Vec2f previousWindowSize;

		@Override
		public void onSelectionChanged(GLButton button, boolean selected) {
			AnimatedGLElementContainer anim = (AnimatedGLElementContainer) window.getParent();
			if (selected) {
				if (elementPosition == ESlideInElementPosition.LEFT || elementPosition == ESlideInElementPosition.RIGHT) {
					anim.resizeChild(window, previousWindowSize.x(), Float.NaN);
				} else {
					anim.resizeChild(window, Float.NaN, previousWindowSize.y());
				}

			} else {
				previousWindowSize = new Vec2f(getSize());
				if (elementPosition == ESlideInElementPosition.LEFT || elementPosition == ESlideInElementPosition.RIGHT) {
					anim.resizeChild(window, 1, Float.NaN);
				} else {
					anim.resizeChild(window, Float.NaN, 1);
				}
			}
		}
	}
}
