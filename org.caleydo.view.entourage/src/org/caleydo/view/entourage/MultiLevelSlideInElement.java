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
package org.caleydo.view.entourage;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * @author Christian
 *
 */
public class MultiLevelSlideInElement extends SlideInElement {

	protected GLButton increaseSizeButton;
	protected GLButton reduceSizeButton;
	protected List<IWindowState> windowStates = new ArrayList<>();
	protected int currentWindowStateIndex = 0;

	/**
	 * @param window
	 */
	public MultiLevelSlideInElement(GLWindow window, ESlideInElementPosition elementPosition) {
		super();
		this.window = window;
		this.elementPosition = elementPosition;
		setRenderer(new BackgroundRenderer());
		AnimatedGLElementContainer buttonContainer = new AnimatedGLElementContainer();

		if (elementPosition == ESlideInElementPosition.NONE)
			return;
		if (elementPosition == ESlideInElementPosition.LEFT || elementPosition == ESlideInElementPosition.RIGHT) {
			setLayout(GLLayouts.flowVertical(0));
			buttonContainer.setLayout(GLLayouts.flowVertical(0));
			buttonContainer.setSize(Float.NaN, 40);
		} else {
			setLayout(GLLayouts.flowHorizontal(0));
			buttonContainer.setLayout(GLLayouts.flowHorizontal(0));
			buttonContainer.setSize(40, Float.NaN);
		}

		// buttonContainer.setRenderer(GLRenderers.RECT);

		add(new GLElement());
		increaseSizeButton = new GLButton();

		SlideInButtonRenderer increaseSizeRenderer = new SlideInButtonRenderer(true);
		increaseSizeButton.setRenderer(increaseSizeRenderer);
		increaseSizeButton.setPicker(increaseSizeRenderer);
		increaseSizeButton.setHoverEffect(null);
		increaseSizeButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				if (currentWindowStateIndex + 1 < windowStates.size()) {
					currentWindowStateIndex++;
					if (currentWindowStateIndex == windowStates.size() - 1) {
						increaseSizeButton.setVisibility(EVisibility.NONE);
					}
					reduceSizeButton.setVisibility(EVisibility.PICKABLE);
					windowStates.get(currentWindowStateIndex).apply();
				}
			}
		});

		// increaseSizeButton.setPicker(new SlideInButtonRenderer(true));
		reduceSizeButton = new GLButton();
		SlideInButtonRenderer reduceSizeRenderer = new SlideInButtonRenderer(false);
		reduceSizeButton.setRenderer(reduceSizeRenderer);
		reduceSizeButton.setPicker(reduceSizeRenderer);
		reduceSizeButton.setHoverEffect(null);
		reduceSizeButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				if (currentWindowStateIndex - 1 >= 0) {
					currentWindowStateIndex--;
					if (currentWindowStateIndex == 0) {
						reduceSizeButton.setVisibility(EVisibility.NONE);
					}
					increaseSizeButton.setVisibility(EVisibility.PICKABLE);
					windowStates.get(currentWindowStateIndex).apply();
				}
			}
		});
		// reduceSizeButton.setRenderer(new SlideInButtonRenderer(true));
		buttonContainer.add(increaseSizeButton);
		buttonContainer.add(reduceSizeButton);
		add(buttonContainer);
		add(new GLElement());
	}

	public void setCurrentWindowState(IWindowState windowState) {
		if (windowStates.contains(windowState)) {
			currentWindowStateIndex = windowStates.indexOf(windowState);
		}
	}

	public void addWindowState(IWindowState windowState) {
		windowStates.add(windowState);
	}

	public void removeWindowState(IWindowState windowState) {
		windowStates.remove(windowState);
	}

	/**
	 * Applies a certain window state. The {@link #apply()} method is called automatically when the state should be
	 * applied. The state changes when pressing slider buttons.
	 *
	 * @author Christian
	 *
	 */
	public interface IWindowState {
		public void apply();
	}

	public static IWindowState fixedWindowSize(float w, float h) {
		return new IWindowState() {

			@Override
			public void apply() {

			}
		};
	}

	protected class BackgroundRenderer implements IGLRenderer {

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			switch (elementPosition) {
			case NONE:
				return;
			case LEFT:
				g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(-10, h / 2.0f - 20, 10, 40, 2);
				break;
			case BOTTOM:
				g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(w / 2.0f - 20, h, 40, 10, 2);
				break;
			case RIGHT:
				g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(w, h / 2.0f - 20, 10, 40, 2);
				break;
			case TOP:
				g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(w / 2.0f - 20, -10, 40, 10, 2);
				break;
			default:
				break;
			}

		}
	}

	protected class SlideInButtonRenderer implements IGLRenderer {

		protected boolean isIncreasing;

		/**
		 *
		 */
		public SlideInButtonRenderer(boolean isIncreasing) {
			this.isIncreasing = isIncreasing;
			// TODO Auto-generated constructor stub
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			switch (elementPosition) {
			case NONE:
				return;
			case LEFT:
				g.color(0f, 0f, 0f, 0f).fillRect(-10, 0, 10, h);
				g.fillImage(isIncreasing ? "resources/icons/bullet_arrow_left.png"
						: "resources/icons/bullet_arrow_right.png", -15, (h - 20) / 2.0f, 20, 20);
				break;
			case BOTTOM:
				// TODO: implement
				// g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(w / 2.0f - 20, h, 40, 10, 2);
				// g.fillImage(!slideInButton.isSelected() ? "resources/icons/bullet_arrow_bottom.png"
				// : "resources/icons/bullet_arrow_top.png", w / 2.0f - 10, h, 20, 20);
				break;
			case RIGHT:
				g.color(0f, 0f, 0f, 0f).fillRect(w, 0, 10, h);
				g.fillImage(isIncreasing ? "resources/icons/bullet_arrow_right.png"
						: "resources/icons/bullet_arrow_left.png", w - 5, 0, 20, 20);
				break;
			case TOP:
				// TODO: implement
				// g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(w / 2.0f - 20, -10, 40, 10, 2);
				// g.fillImage(!slideInButton.isSelected() ? "resources/icons/bullet_arrow_top.png"
				// : "resources/icons/bullet_arrow_bottom.png", w / 2.0f - 10, -15, 20, 20);
				break;
			default:
				break;
			}

		}
	}

}
