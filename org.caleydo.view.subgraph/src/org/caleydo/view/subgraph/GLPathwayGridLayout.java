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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.view.subgraph.GLSubGraph.PathwayMultiFormInfo;

/**
 * @author Christian
 *
 */
public class GLPathwayGridLayout implements IGLLayout {

	protected final GLSubGraph view;
	protected List<PathwayColumn> columns = new ArrayList<>();
	protected GLPadding padding;
	protected float gap;

	/**
	 *
	 */
	public GLPathwayGridLayout(GLSubGraph view, GLPadding padding, float gap) {
		this.view = view;
		this.padding = padding;
		this.gap = gap;
	}

	public void addColumn(GLPathwayWindow window) {
		PathwayColumn column = new PathwayColumn();
		column.addPathwayWindow(window);
		columns.add(column);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {

		Map<GLPathwayWindow, IGLLayoutElement> windowToElement = new HashMap<>();
		for (IGLLayoutElement child : children) {
			windowToElement.put((GLPathwayWindow) child.asElement(), child);
		}

		// float freeSpaceHorizontal =
		float freeSpaceVertical = h - padding.vert();

		if (!isSufficientSpace(columns, getFreeHorizontalSpace(w))) {
			if (!mergeColumns(w, columns, freeSpaceVertical)) {

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
							if (isSufficientSpace(columns, getFreeHorizontalSpace(w))) {
								break;
							}
							if (mergeColumns(w, columns, freeSpaceVertical)) {
								break;
							}
						}
					}
				}
			}
		}

		Set<PathwayColumn> level1Columns = new HashSet<>();
		int minTotalLevel1Size = 0;
		int totalFixedSize = 0;
		for (PathwayColumn column : columns) {
			if (column.getLevelScore() == EEmbeddingID.PATHWAY_LEVEL1.renderPriority()) {
				level1Columns.add(column);
				minTotalLevel1Size += column.getMinWidth();
			} else {
				totalFixedSize += column.getMinWidth();
				// column.setSize(column.getMinWidth(), freeSpaceVertical);
				// column.setSize(column.getMinWidth(), Float.NaN);
			}
		}

		float currentPositionX = padding.left;
		for (PathwayColumn column : columns) {
			float columnWidth = 0;
			if (minTotalLevel1Size == 0) {
				columnWidth = ((float) column.getMinWidth() / (float) totalFixedSize) * getFreeHorizontalSpace(w);
			} else {
				if (level1Columns.contains(column)) {
					columnWidth = ((float) column.getMinWidth() / (float) minTotalLevel1Size)
							* (getFreeHorizontalSpace(w) - totalFixedSize);
				} else {
					columnWidth = column.getMinWidth();
				}
			}
			column.layout(windowToElement, currentPositionX, padding.top, columnWidth, freeSpaceVertical);
			currentPositionX += columnWidth + gap;
		}

	}

	private boolean isSufficientSpace(List<PathwayColumn> pathwayColumns, float freeSpace) {
		int minWidth = 0;
		for (PathwayColumn column : pathwayColumns) {
			minWidth += column.getMinWidth();
		}
		return freeSpace >= minWidth;
	}

	private float getFreeHorizontalSpace(float w) {
		return w - padding.hor() - (columns.size() - 1) * gap;
	}

	/**
	 *
	 *
	 * @param w
	 * @param pathwayColumns
	 * @param h
	 * @return True, if there is enough space after merging columns
	 */
	private boolean mergeColumns(float w, List<PathwayColumn> pathwayColumns, float h) {

		boolean columnsMerged = false;
		do {
			columnsMerged = false;
			PathwayColumn columnToRemove = null;
			Collections.sort(pathwayColumns);
			for (PathwayColumn sourceColumn : pathwayColumns) {
				for (PathwayColumn destColumn : pathwayColumns) {
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
			}
			if (isSufficientSpace(pathwayColumns, getFreeHorizontalSpace(w)))
				return true;

		} while (columnsMerged);
		return false;
	}

	private class PathwayColumn implements Comparable<PathwayColumn> {
		protected List<GLPathwayWindow> windows = new ArrayList<>();

		protected void addPathwayWindow(GLPathwayWindow window) {
			windows.add(window);
		}

		protected void removePathwayWindow(GLPathwayWindow window) {
			windows.remove(window);
		}

		public int getMinHeight() {
			int minHeight = 0;
			for (GLPathwayWindow window : windows) {
				minHeight += window.getMinHeight();
			}
			return minHeight;
		}

		public int getMinWidth() {
			int maxMinWidth = 0;
			for (GLPathwayWindow window : windows) {
				int minWidth = window.getMinWidth();
				if (minWidth > maxMinWidth)
					maxMinWidth = minWidth;
			}
			return maxMinWidth;
		}

		protected void layout(Map<GLPathwayWindow, IGLLayoutElement> windowToElement, float x, float y, float w, float h) {

			float freeSpaceVertical = h - gap * (windows.size() - 1);

			Set<GLPathwayWindow> level1Windows = new HashSet<>();
			int minTotalLevel1Size = 0;
			int totalFixedSize = 0;
			for (GLPathwayWindow window : windows) {
				if (window.info.getEmbeddingIDFromRendererID(window.info.multiFormRenderer.getActiveRendererID()) == EEmbeddingID.PATHWAY_LEVEL1) {
					level1Windows.add(window);
					minTotalLevel1Size += window.getMinHeight();
				} else {
					totalFixedSize += window.getMinHeight();
				}
			}

			float currentPositionY = y;
			for (GLPathwayWindow window : windows) {
				float windowHeight = 0;
				if (minTotalLevel1Size == 0) {
					int minHeight = window.getMinHeight();
					float factor = (float) minHeight / (float) totalFixedSize;
					windowHeight = factor * freeSpaceVertical;
				} else {
					if (level1Windows.contains(window)) {
						windowHeight = ((float) window.getMinHeight() / (float) minTotalLevel1Size)
								* (freeSpaceVertical - totalFixedSize);
					} else {
						windowHeight = window.getMinHeight();
					}
				}
				windowToElement.get(window).setSize(w, windowHeight);
				windowToElement.get(window).setLocation(x, currentPositionY);
				currentPositionY += windowHeight + gap;
			}

		}

		protected void merge(PathwayColumn column) {
			windows.addAll(column.windows);
		}

		protected int getLevelScore() {
			int score = 0;
			for (GLPathwayWindow window : windows) {
				int rendererID = window.info.multiFormRenderer.getActiveRendererID();
				EEmbeddingID embdeddingID = window.info.getEmbeddingIDFromRendererID(rendererID);
				if (embdeddingID.renderPriority() > score)
					score = embdeddingID.renderPriority();
			}
			return score;
		}

		@Override
		public int compareTo(PathwayColumn o) {
			return getLevelScore() - o.getLevelScore();
		}

	}

}
