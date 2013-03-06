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
import org.caleydo.core.view.opengl.layout2.GLElement;
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

	public GLPathwayWindow(PathwayGraph pathway, GLSubGraph view, final MultiFormInfo info) {
		setLayout(GLLayouts.LAYERS);
		this.info = info;

		GLElementContainer multiFormContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 1,
				GLPadding.ZERO));
		multiFormContainer.add(new GLTitleBar(pathway == null ? "" : pathway.getTitle()));
		GLElementAdapter container = new GLElementAdapter(view, info.multiFormRenderer, true);
		multiFormContainer.add(container);
		info.container = container;
		// multiFormRendererAdapter.onPick(pl);

		GLElementViewSwitchingBar viewSwitchingBar = new GLElementViewSwitchingBar(info.multiFormRenderer);
		GLPathwayBackground bg = new GLPathwayBackground(view, viewSwitchingBar);
		GLElementContainer barRow = new GLElementContainer(new GLSizeRestrictiveFlowLayout(true, 0, new GLPadding(0, 2,
				5, 0)));

		barRow.add(new GLElement());
		barRow.add(viewSwitchingBar);

		add(bg);
		add(multiFormContainer);
		add(barRow);
	}

	public int getMinWidth() {
		return info.multiFormRenderer.getMinWidthPixels();
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

}
