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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.view.subgraph.GLSubGraph.PathwayMultiFormInfo;

/**
 * @author Christian Partl
 *
 */
public class GLMultiFormPathwayLayout extends GLSizeRestrictiveFlowLayout {

	protected GLSubGraph view;
	protected GLElementContainer container;

	/**
	 * @param horizontal
	 * @param gap
	 * @param padding
	 */
	public GLMultiFormPathwayLayout(float gap, GLPadding padding, GLSubGraph view, GLElementContainer container) {
		super(true, gap, padding);
		this.view = view;
		this.container = container;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {

		float freeSpace = w - padding.hor() - (children.size() - 1) * gap;

		List<GLPathwayColumn> pathwayColumns = new ArrayList<>(children.size());

		Map<GLPathwayColumn, IGLLayoutElement> columnToElement = new HashMap<>();
		for (IGLLayoutElement child : children) {
			GLPathwayColumn column = (GLPathwayColumn) child.asElement();
			pathwayColumns.add(column);
			columnToElement.put(column, child);
		}
		if (!isSufficientSpace(pathwayColumns, freeSpace)) {
			if (!mergeColumns(freeSpace, pathwayColumns, h)) {

				List<PathwayMultiFormInfo> infos = new ArrayList<>(view.pathwayInfos.size());
				infos.addAll(view.pathwayInfos);
				Collections.sort(infos, new Comparator<PathwayMultiFormInfo>() {

					@Override
					public int compare(PathwayMultiFormInfo o1, PathwayMultiFormInfo o2) {
						int priority1 = o1.getEmbeddingIDFromRendererID(o1.multiFormRenderer.getActiveRendererID())
								.renderPriority();
						int priority2 = o2.getEmbeddingIDFromRendererID(o2.multiFormRenderer.getActiveRendererID())
								.renderPriority();
						if (priority1 == priority2) {
							return o1.age - o2.age;
						}
						return priority1 - priority2;
					}
				});

				for (PathwayMultiFormInfo info : infos) {
					EEmbeddingID level = info
							.getEmbeddingIDFromRendererID(info.multiFormRenderer.getActiveRendererID());
					if (info.multiFormRenderer != view.lastUsedRenderer) {
						EEmbeddingID levelDown = EEmbeddingID.levelDown(level);
						if (levelDown != EEmbeddingID.PATHWAY_LEVEL4) {
							int rendererID = info.embeddingIDToRendererIDs.get(levelDown).get(0);
							info.multiFormRenderer.setActive(rendererID);
							if (mergeColumns(freeSpace, pathwayColumns, h)) {
								break;
							}
						}
					}
				}
			}
		}

		List<GLPathwayColumn> level1Columns = new ArrayList<>();
		int minTotalLevel1ColumnWidth = 0;
		for (GLPathwayColumn column : pathwayColumns) {
			if (column.getLevelScore() == EEmbeddingID.PATHWAY_LEVEL1.renderPriority()) {
				level1Columns.add(column);
				minTotalLevel1ColumnWidth += column.getMinWidth();
			} else {
				column.setSize(column.getMinWidth(), Float.NaN);
			}
		}

		for (GLPathwayColumn column : level1Columns) {
			column.setSize(Float.NaN, Float.NaN);
			column.setLayoutData((float) column.getMinWidth() / (float) minTotalLevel1ColumnWidth);
		}

		super.doLayout(children, w, h);
	}

	private boolean isSufficientSpace(List<GLPathwayColumn> pathwayColumns, float freeSpace) {
		int minWidth = 0;
		for (GLPathwayColumn column : pathwayColumns) {
			minWidth += column.getMinWidth();
		}
		return freeSpace >= minWidth;
	}

	/**
	 *
	 *
	 * @param freeSpace
	 * @param pathwayColumns
	 * @param h
	 * @return True, if there is enough space after merging columns
	 */
	private boolean mergeColumns(float freeSpace, List<GLPathwayColumn> pathwayColumns, float h) {

		boolean columnsMerged = false;
		do {
			columnsMerged = false;
			GLPathwayColumn columnToRemove = null;
			Collections.sort(pathwayColumns);
			for (GLPathwayColumn sourceColumn : pathwayColumns) {
				for (GLPathwayColumn destColumn : pathwayColumns) {
					if (sourceColumn != destColumn) {
						if (sourceColumn.getMinHeight() + destColumn.getMinHeight() <= h) {
							destColumn.merge(sourceColumn);
							columnToRemove = sourceColumn;
							columnsMerged = true;
							break;
						}
					}
				}
				if (columnsMerged)
					break;
			}
			if (columnsMerged) {
				pathwayColumns.remove(columnToRemove);
				container.remove(columnToRemove);
			}
			if (isSufficientSpace(pathwayColumns, freeSpace))
				return true;

		} while (columnsMerged);
		return false;
	}
}
