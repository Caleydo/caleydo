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

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * @author Christian
 *
 */
public class GLElementWindow extends GLElementContainer {

	private GLElement content;
	protected final GLTitleBar titleBar;
	protected final BackgroundRenderer background;
	protected final GLElementContainer baseContainer;
	protected final GLElementContainer contentContainer;
	protected boolean active = false;
	protected boolean showCloseButton = true;

	protected static class BackgroundRenderer extends PickableGLElement {

		public static final Color DEFAULT_COLOR = new Color(0.95f, 0.95f, 0.95f, 1f);
		protected Color color = DEFAULT_COLOR;

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			g.color(color);
			g.incZ(-0.2f);
			g.fillRoundedRect(0, 0, w, h, 7);
			g.incZ(0.2f);

		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			g.incZ(-0.2f);
			super.renderPickImpl(g, w, h);
			g.incZ(0.2f);
		}

		/**
		 * @param color
		 *            setter, see {@link color}
		 */
		public void setColor(Color color) {
			this.color = color;
			repaint();
		}

	}

	public static class GLTitleBar extends GLElementContainer {

		public static final Color DEFAULT_COLOR = new Color(0.6f, 0.6f, 0.6f);
		public static final int TITLE_BAR_HEIGHT = 20;

		protected ILabeled labelProvider;
		protected final GLButton closeButton;
		protected Color barColor = DEFAULT_COLOR;
		protected boolean isHighlight = false;
		protected GLElement spacingElement;

		public GLTitleBar(String text) {
			this(new DefaultLabelProvider(text));
		}

		public GLTitleBar(ILabeled labelProvider) {
			super();
			this.labelProvider = labelProvider;
			setSize(Float.NaN, TITLE_BAR_HEIGHT);
			setLayout(new GLSizeRestrictiveFlowLayout(true, 2, new GLPadding(3, 2)));
			setRenderer(new IGLRenderer() {

				@Override
				public void render(GLGraphics g, float w, float h, GLElement parent) {
					g.drawText(GLTitleBar.this.labelProvider, 0, -2, w, h);

				}
			});
			spacingElement = new GLElement();
			add(spacingElement);
			closeButton = new GLButton(EButtonMode.BUTTON);
			closeButton.setSize(16, 16);
			closeButton.setTooltip("Close");
			closeButton.setRenderer(GLRenderers.fillImage("resources/icons/general/remove.png"));
			add(closeButton);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			Color color = isHighlight ? new Color(barColor.r + 0.15f, barColor.g + 0.15f, barColor.b + 0.15f)
					: barColor;
			g.color(color).fillRoundedRect(0, 0, w, h, 7);
			super.renderImpl(g, w, h);
		}

		/**
		 * @param barColor
		 *            setter, see {@link barColor}
		 */
		public void setBarColor(Color barColor) {
			this.barColor = barColor;
			repaint();
		}

		/**
		 * @param highlight
		 *            setter, see {@link highlight}
		 */
		public void setHighlight(boolean highlight) {
			this.isHighlight = highlight;
			repaint();
		}

		/**
		 * @param labelProvider
		 *            setter, see {@link labelProvider}
		 */
		public void setLabelProvider(ILabeled labelProvider) {
			this.labelProvider = labelProvider;
		}
	}

	public GLElementWindow(ILabeled titleLabelProvider) {

		titleBar = new GLTitleBar(titleLabelProvider);

		setLayout(GLLayouts.LAYERS);
		baseContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 1, GLPadding.ZERO));
		baseContainer.add(titleBar);
		contentContainer = new GLElementContainer(GLLayouts.LAYERS);
		baseContainer.add(contentContainer);
		titleBar.closeButton.setVisibility(EVisibility.NONE);
		background = new BackgroundRenderer();

		add(background);
		add(baseContainer);
	}

	public GLElementWindow(String title) {
		this(new DefaultLabelProvider(title));
	}

	/**
	 * @param showCloseButton
	 *            setter, see {@link showCloseButton}
	 */
	public void setShowCloseButton(boolean showCloseButton) {
		this.showCloseButton = showCloseButton;
		if (showCloseButton) {
			titleBar.closeButton.setVisibility(EVisibility.PICKABLE);
		} else {
			titleBar.closeButton.setVisibility(EVisibility.NONE);
		}
	}

	public void addTitleElement(GLElement element, boolean left) {
		int index = left ? titleBar.indexOf(titleBar.spacingElement) : titleBar.indexOf(titleBar.spacingElement) + 1;
		titleBar.add(index, element);
	}

	public void clearTitleElements() {
		for (GLElement element : titleBar) {
			if (element != titleBar.spacingElement && element != titleBar.closeButton) {
				titleBar.remove(element);
			}
		}
	}

	/**
	 * @param content
	 *            setter, see {@link content}
	 */
	public void setContent(GLElement content) {
		if (this.content != null)
			contentContainer.remove(this.content);
		contentContainer.add(0, content);
		this.content = content;
	}

	public void addContentLayer(GLElement element) {
		contentContainer.add(element);
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
				listener.onWindowClosed(GLElementWindow.this);
			}
		});
	}

	public static interface ICloseWindowListener {
		/**
		 * Called when the close button of the window was pressed.
		 */
		public void onWindowClosed(GLElementWindow window);
	}

	public void setTitleBarColor(Color color) {
		titleBar.setBarColor(color);
	}

	public void setBackgroundColor(Color color) {
		background.setColor(color);
	}

	/**
	 * @return the titleBar, see {@link #titleBar}
	 */
	public GLTitleBar getTitleBar() {
		return titleBar;
	}

	@Override
	public Vec2f getMinSize() {
		Vec2f contentMinSize = GLMinSizeProviders.getLayeredMinSize(contentContainer);
		return new Vec2f(contentMinSize.x(), contentMinSize.y() + GLTitleBar.TITLE_BAR_HEIGHT);
	}

}
