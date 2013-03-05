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

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.view.subgraph.GLSubGraph.PathwayMultiFormInfo;

/**
 * @author Christian
 *
 */
public class GLPathwayColumn extends AnimatedGLElementContainer implements Comparable<GLPathwayColumn> {

	protected List<PathwayMultiFormInfo> rendererInfos = new ArrayList<>();
	protected final GLSubGraph view;

	/**
	 *
	 */
	public GLPathwayColumn(GLSubGraph view) {
		this.view = view;
	}

	public void addRenderer(PathwayMultiFormInfo renderer) {
		rendererInfos.add(renderer);
	}

	public void removeRenderer(PathwayMultiFormInfo renderer) {
		rendererInfos.remove(renderer);
	}

	public int getMinHeight() {
		int minHeight = 0;
		for (PathwayMultiFormInfo info : rendererInfos) {
			minHeight += info.multiFormRenderer.getMinHeightPixels();
		}
		return minHeight;
	}

	public int getMinWidth() {
		int maxMinWidth = 0;
		for (PathwayMultiFormInfo info : rendererInfos) {
			int minWidth = info.multiFormRenderer.getMinWidthPixels();
			if (minWidth > maxMinWidth)
				maxMinWidth = minWidth;
		}
		return maxMinWidth;
	}

	protected int getLevelScore() {
		int score = 0;
		for (PathwayMultiFormInfo info : rendererInfos) {
			int rendererID = info.multiFormRenderer.getActiveRendererID();
			EEmbeddingID embdeddingID = info.getEmbeddingIDFromRendererID(rendererID);
			if (embdeddingID.renderPriority() > score)
				score = embdeddingID.renderPriority();
		}
		return score;
	}

	@Override
	public int compareTo(GLPathwayColumn o) {
		return getLevelScore() - o.getLevelScore();
	}

	public void merge(GLPathwayColumn column) {
		rendererInfos.addAll(column.rendererInfos);
		for (PathwayMultiFormInfo info : column.rendererInfos) {
			add(info.window);
			column.remove(info.window);
		}
	}

}
