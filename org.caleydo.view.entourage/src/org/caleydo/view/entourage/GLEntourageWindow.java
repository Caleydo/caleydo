/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage;

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow;

import com.google.common.collect.Iterables;

/**
 * @author Christian
 *
 */
public class GLEntourageWindow extends GLElementWindow {

	protected final GLEntourage view;

	/**
	 * @param titleLabelProvider
	 * @param view
	 */
	public GLEntourageWindow(ILabelProvider titleLabelProvider, GLEntourage view) {
		super(titleLabelProvider);
		this.view = view;
	}

	public GLEntourageWindow(String title, GLEntourage view) {
		this(new DefaultLabelProvider(title), view);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
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

			for (int i = 1; i < titleBar.size(); i++) {
				titleBar.get(i).setVisibility(EVisibility.PICKABLE);
			}
			titleBar.setHighlight(true);
			if (!showCloseButton) {
				setShowCloseButton(false);
				// titleBar.closeButton.setVisibility(EVisibility.NONE);
			}
		} else {
			// titleBar.closeButton.setVisibility(EVisibility.NONE);

			for (int i = 1; i < titleBar.size(); i++) {
				titleBar.get(i).setVisibility(EVisibility.NONE);
			}
			titleBar.setHighlight(false);
		}
		// background.setHovered(active);
		this.active = active;
	}

}
