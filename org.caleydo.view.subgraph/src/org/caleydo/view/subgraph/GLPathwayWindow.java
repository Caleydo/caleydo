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

import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElementAdapter;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.util.GLElementViewSwitchingBar;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.subgraph.GLSubGraph.MultiFormInfo;

/**
 * @author Christian
 *
 */
public class GLPathwayWindow extends AnimatedGLElementContainer {

	protected final MultiFormInfo info;
	protected final GLElementViewSwitchingBar viewSwitchingBar;
	protected final GLSubGraph view;
	protected final GLPathwayBackground background;
	protected boolean active = false;

	public GLPathwayWindow(PathwayGraph pathway, GLSubGraph view, final MultiFormInfo info) {
		setLayout(GLLayouts.LAYERS);
		this.info = info;
		this.view = view;

		GLElementContainer multiFormContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 1,
				GLPadding.ZERO));
		GLTitleBar titleBar = new GLTitleBar(pathway == null ? "" : pathway.getTitle());
		multiFormContainer.add(titleBar);
		GLElementAdapter container = new GLElementAdapter(view, info.multiFormRenderer, true);
		multiFormContainer.add(container);
		info.container = container;
		// multiFormRendererAdapter.onPick(pl);

		viewSwitchingBar = new GLElementViewSwitchingBar(info.multiFormRenderer);
		background = new GLPathwayBackground(this);
		titleBar.add(viewSwitchingBar);
		viewSwitchingBar.setVisibility(EVisibility.NONE);

		add(background);
		add(multiFormContainer);
	}

	public int getMinWidth() {
		return Math.max(info.multiFormRenderer.getMinWidthPixels(), 100);
	}

	public int getMinHeight() {
		return info.multiFormRenderer.getMinHeightPixels() + 20;
	}

	/**
	 * @return the info, see {@link #info}
	 */
	public MultiFormInfo getInfo() {
		return info;
	}

	public void setActive(boolean active) {
		if (active == this.active)
			return;

		GLPathwayWindow activeWindow = view.getActiveWindow();

		if (active) {
			if (activeWindow != null && activeWindow != this) {
				activeWindow.setActive(false);
			}
			view.setActiveWindow(this);
			viewSwitchingBar.setVisibility(EVisibility.VISIBLE);
			// currentActiveBackground.bar.setVisibility(EVisibility.NONE);
			repaint();
		} else {
			viewSwitchingBar.setVisibility(EVisibility.NONE);
		}
		background.setHovered(active);
		this.active = active;
	}

}
