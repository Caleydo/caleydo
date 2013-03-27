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
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;

import com.google.common.collect.Iterables;

/**
 * @author Christian
 *
 */
public class GLWindow extends GLElementContainer {
	public static final Color DEFAULT_COLOR = new Color(0.95f, 0.95f, 0.95f, 1f);
	protected Color bgColor = DEFAULT_COLOR;

	private GLElement content;
	protected final GLSubGraph view;
	protected final GLTitleBar titleBar;
	protected final GLElementContainer contentContainer;
	protected boolean active = false;
	protected boolean showCloseButton = true;

	private int backgroundPickingId = -1;
	protected final PickingListenerComposite backgroundPicker = new PickingListenerComposite(2);

	public GLWindow(ILabelProvider titleLabelProvider, GLSubGraph view) {
		super(GLLayouts.LAYERS);
		this.view = view;
		contentContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 1, GLPadding.ZERO));
		titleBar = new GLTitleBar(titleLabelProvider);
		contentContainer.add(titleBar);
		titleBar.closeButton.setVisibility(EVisibility.NONE);
		// baseContainer.add(contentContainer);
		// slideInButton.setSize(20, 5);
		// add(slideInButton);
		this.add(contentContainer);
		setPicker(null);
	}

	public GLWindow(String title, GLSubGraph view) {
		this(new DefaultLabelProvider(title), view);
	}

	@Override
	protected void init(IGLElementContext context) {
		backgroundPickingId = context.registerPickingListener(backgroundPicker);
		super.init(context);
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(backgroundPickingId);
		super.takeDown();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(bgColor);
		g.incZ(-0.2f);
		g.fillRoundedRect(0, 0, w, h, 7);
		g.incZ(0.2f);

		if (w <= 1 || h <= 1) { // just render the SlideInElements
			g.incZ();
			for (SlideInElement child : Iterables.filter(this, SlideInElement.class))
				child.render(g);
			g.decZ();
		} else
			// render normally
			super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (w <= 1 || h <= 1) { // just render the SlideInElements
			g.incZ();
			for (SlideInElement child : Iterables.filter(this, SlideInElement.class))
				child.renderPick(g);
			g.decZ();
		} else {
			g.incZ(-0.2f);
			g.pushName(backgroundPickingId);
			g.fillRect(0, 0, w, h); // render a background rect
			g.popName();
			g.incZ(0.2f);
			// render normally
			super.renderPickImpl(g, w, h);
		}
	}

	public void addSlideInElement(SlideInElement element) {
		add(element);
	}

	public void setActive(boolean active) {
		if (active == this.active)
			return;

		if (active) {
			view.setActiveWindow(this);
			repaint();
			if (showCloseButton)
				titleBar.closeButton.setVisibility(EVisibility.PICKABLE);
			titleBar.setHighlight(true);
		} else {
			titleBar.closeButton.setVisibility(EVisibility.NONE);
			titleBar.setHighlight(false);
		}
		// background.setHovered(active);
		this.active = active;
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

	/**
	 * @param content
	 *            setter, see {@link content}
	 */
	public void setContent(GLElement content) {
		if (this.content != null)
			contentContainer.remove(this.content);
		contentContainer.add(content);
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

	public void setTitleBarColor(Color color) {
		titleBar.setBarColor(color);
	}

	public void setBackgroundColor(Color color) {
		this.bgColor = color;
		repaint();
	}

}
