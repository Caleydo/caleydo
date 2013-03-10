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

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * @author Christian
 *
 */
public class GLWindow extends AnimatedGLElementContainer {

	private GLElement content;
	protected final GLSubGraph view;
	protected final GLTitleBar titleBar;
	protected final GLPathwayBackground background;
	protected final GLElementContainer baseContainer;
	protected final GLButton slideInButton;
	protected boolean active = false;
	protected boolean showCloseButton = true;
	protected boolean showSlideInButton = false;

	public GLWindow(ILabelProvider titleLabelProvider, GLSubGraph view) {
		this.view = view;
		titleBar = new GLTitleBar(titleLabelProvider);
		background = new GLPathwayBackground(this);
		setLayout(GLLayouts.LAYERS);
		baseContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 1, GLPadding.ZERO));
		baseContainer.add(titleBar);
		GLElementContainer sliderButtonContainer = new GLElementContainer(GLLayouts.flowHorizontal(0));

		slideInButton = new GLButton(EButtonMode.BUTTON);
		slideInButton.setSize(20, 5);
		slideInButton.setRenderer(new IGLRenderer() {

			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.color(0.6f, 0.6f, 0.6f, 1f).fillRoundedRect(0, -10, 50, 10, 2);
				g.fillRect(-5, -5, 60, 5);
				g.color(1f, 1f, 1f, 1f).fillRoundedRect(0, -10, 50, 10, 2);

			}
		});
		sliderButtonContainer.add(new GLElement());
		sliderButtonContainer.add(slideInButton);
		sliderButtonContainer.add(new GLElement());

		add(background);
		add(baseContainer);
		add(sliderButtonContainer);
	}

	public GLWindow(String title, GLSubGraph view) {
		this(new DefaultLabelProvider(title), view);
	}

	public void setActive(boolean active) {
		if (active == this.active)
			return;

		GLWindow activeWindow = view.getActiveWindow();

		if (active) {
			if (activeWindow != null && activeWindow != this) {
				activeWindow.setActive(false);
			}
			view.setActiveWindow(this);
			repaint();
			if (showCloseButton)
				titleBar.closeButton.setVisibility(EVisibility.PICKABLE);
		} else {
			titleBar.closeButton.setVisibility(EVisibility.NONE);
		}
		background.setHovered(active);
		this.active = active;
	}

	/**
	 * @param showCloseButton
	 *            setter, see {@link showCloseButton}
	 */
	public void setShowCloseButton(boolean showCloseButton) {
		this.showCloseButton = showCloseButton;
		if (showCloseButton)
			titleBar.closeButton.setVisibility(EVisibility.PICKABLE);
		else
			titleBar.closeButton.setVisibility(EVisibility.NONE);
	}

	/**
	 * @param showSlideInButton
	 *            setter, see {@link showSlideInButton}
	 */
	public void setShowSlideInButton(boolean showSlideInButton) {
		this.showSlideInButton = showSlideInButton;
	}

	/**
	 * @param content
	 *            setter, see {@link content}
	 */
	public void setContent(GLElement content) {
		if (this.content != null)
			baseContainer.remove(this.content);
		baseContainer.add(content);
		this.content = content;
	}

	/**
	 * @return the content, see {@link #content}
	 */
	public GLElement getContent() {
		return content;
	}

	public void onClose(final ICloseWindowListener listener) {
		titleBar.closeButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				listener.onWindowClosed(GLWindow.this);
			}
		});
	}

	public static interface ICloseWindowListener {
		/**
		 * Called when the close button of the window was pressed.
		 */
		public void onWindowClosed(GLWindow window);
	}

}
