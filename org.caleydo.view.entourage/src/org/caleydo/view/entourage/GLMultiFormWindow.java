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

import java.util.List;
import java.util.Map.Entry;

import org.caleydo.core.view.opengl.layout2.GLElementAdapter;
import org.caleydo.core.view.opengl.layout2.util.GLElementViewSwitchingBar;
import org.caleydo.view.entourage.GLEntourage.MultiFormInfo;

/**
 * @author Christian
 *
 */
public class GLMultiFormWindow extends GLWindow {

	protected final MultiFormInfo info;
	protected final GLElementViewSwitchingBar viewSwitchingBar;
	protected boolean showViewSwitchingBar = true;

	public GLMultiFormWindow(String title, GLEntourage view, MultiFormInfo info, boolean isScrollable) {
		super(title, view);
		this.info = info;

		GLElementAdapter container = isScrollable ? new ScrollableGLElementAdapter(view, info.multiFormRenderer)
				: new GLElementAdapter(view, info.multiFormRenderer, true);
		info.container = container;
		setContent(container);

		viewSwitchingBar = new GLElementViewSwitchingBar(info.multiFormRenderer);
		titleBar.add(titleBar.size() - 1, viewSwitchingBar);
		viewSwitchingBar.setVisibility(EVisibility.NONE);
		for (Entry<EEmbeddingID, List<Integer>> entry : info.embeddingIDToRendererIDs.entrySet()) {
			for (Integer rendererID : entry.getValue()) {
				String toolTip = null;
				switch (entry.getKey()) {
				case PATHWAY_LEVEL1:
					toolTip = "Pathway";
					break;
				case PATHWAY_LEVEL2:
					toolTip = "Context Paths with Pathway Thumbnail";
					break;
				case PATHWAY_LEVEL3:
					toolTip = "ContextPaths";
					break;
				case PATHWAY_LEVEL4:
					toolTip = "Minimize";
					break;
				case PATH_LEVEL1:
					toolTip = "Selected Path with Detailed Experimental Data";
					break;
				case PATH_LEVEL2:
					toolTip = "Selected Path";
					break;
				default:
					break;
				}
				if (toolTip != null) {
					viewSwitchingBar.setButtonToolTip(toolTip, rendererID);
				}

			}
		}
	}

	public int getMinWidth() {
		return Math.max(info.multiFormRenderer.getMinWidthPixels(), 230);
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

	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (showViewSwitchingBar) {
			if (active) {
				viewSwitchingBar.setVisibility(EVisibility.VISIBLE);
			} else {
				viewSwitchingBar.setVisibility(EVisibility.NONE);
			}
		}
	}

	/**
	 * @param showViewSwitchingBar
	 *            setter, see {@link showViewSwitchingBar}
	 */
	public void setShowViewSwitchingBar(boolean showViewSwitchingBar) {
		this.showViewSwitchingBar = showViewSwitchingBar;
		if (showViewSwitchingBar) {
			viewSwitchingBar.setVisibility(EVisibility.VISIBLE);
		} else {
			viewSwitchingBar.setVisibility(EVisibility.NONE);
		}

	}
}
