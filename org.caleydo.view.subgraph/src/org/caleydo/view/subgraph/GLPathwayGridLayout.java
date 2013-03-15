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
import org.caleydo.view.subgraph.GLSubGraph.MultiFormInfo;
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
		column.windows.add(window);
		columns.add(column);
	}

	public void setLevel1(GLPathwayWindow window) {
		PathwayColumn parentColumn = null;
		for (PathwayColumn column : columns) {
			for (GLPathwayWindow w : column.windows) {
				if (w == window) {
					parentColumn = column;
					break;
				}
			}
		}
		if (parentColumn != null) {
			parentColumn.windows.remove(window);
			if (parentColumn.windows.size() == 0)
				columns.remove(parentColumn);
		}
		addColumn(window);
	}

	public void removeWindow(GLPathwayWindow window) {
		PathwayColumn columnToRemove = null;
		for (PathwayColumn column : columns) {
			if (column.windows.contains(window)) {
				column.windows.remove(window);
				if (column.windows.size() == 0) {
					columnToRemove = column;
				}
				break;
			}
		}
		if (columnToRemove != null) {
			columns.remove(columnToRemove);
		}
	}

	public int getColumnIndex(GLPathwayWindow window) {
		for (PathwayColumn column : columns) {
			if (column.windows.contains(window))
				return columns.indexOf(column);
		}
		return -1;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		Map<GLPathwayWindow, IGLLayoutElement> windowToElement = new HashMap<>();
		for (IGLLayoutElement child : children) {
			windowToElement.put((GLPathwayWindow) child.asElement(), child);
		}
		List<LayoutSnapshot> snapshots = new ArrayList<>();
		snapshots.add(new LayoutSnapshot(columns));
		float freeSpaceVertical = h - padding.vert();
		// Vertical space
		List<PathwayColumn> spaceExceedingColumns = getSpaceExceedingColumns(freeSpaceVertical);
		if (spaceExceedingColumns.size() > 0) {
			for (PathwayColumn column : spaceExceedingColumns) {
				squeezeColumn(column, snapshots, freeSpaceVertical);
			}
		}

		Collections.reverse(snapshots);
		LayoutSnapshot previousSnapshot = null;
		for (LayoutSnapshot snapshot : snapshots) {
			if (previousSnapshot != null) {
				if (previousSnapshot.maxColumnHeight < snapshot.maxColumnHeight) {
					if (previousSnapshot.maxColumnHeight < snapshot.maxColumnHeight) {
						previousSnapshot.apply();
						break;
					} else if (Float.compare(previousSnapshot.maxColumnHeight, snapshot.maxColumnHeight) == 0) {
						snapshot.apply();
					}
				}
			}
			previousSnapshot = snapshot;
		}

		snapshots.clear();
		snapshots.add(previousSnapshot);

		// Horizontal space

		if (!isSufficientHorizontalSpace(getFreeHorizontalSpace(w))) {
			if (!mergeColumns(snapshots, w, freeSpaceVertical)) {
				Set<PathwayMultiFormInfo> undemotableInfos = new HashSet<>();
				List<PathwayMultiFormInfo> infos = new ArrayList<>(view.pathwayInfos);
				while (undemotableInfos.size() < infos.size()) {

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
					Collections.reverse(infos);

					for (PathwayMultiFormInfo info : infos) {
						if (demote(info)) {
							snapshots.add(new LayoutSnapshot(columns));
							if (isSufficientHorizontalSpace(getFreeHorizontalSpace(w))) {
								break;
							}
							if (mergeColumns(snapshots, w, freeSpaceVertical)) {
								break;
							}
						} else {
							undemotableInfos.add(info);
						}
					}
				}
			}

		}

		Collections.reverse(snapshots);
		previousSnapshot = null;
		for (int i = 0; i < snapshots.size(); i++) {
			LayoutSnapshot snapshot = snapshots.get(i);
			if (previousSnapshot != null) {
				if (previousSnapshot.minTotalWidth < snapshot.minTotalWidth) {
					previousSnapshot.apply();
					break;
				} else if (Float.compare(previousSnapshot.minTotalWidth, snapshot.minTotalWidth) == 0) {
					snapshot.apply();
				}
			}
			previousSnapshot = snapshot;
		}

		Set<PathwayColumn> level1Columns = new HashSet<>();
		int minTotalLevel1Size = 0;
		int totalFixedSize = 0;
		List<PathwayColumn> copyColumns = new ArrayList<>(columns);
		Collections.reverse(copyColumns);
		for (PathwayColumn column : copyColumns) {
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
		for (PathwayColumn column : copyColumns) {
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

		this.view.setLayoutDirty();
	}

	private void squeezeColumn(PathwayColumn column, List<LayoutSnapshot> snapshots, float freeSpaceVertical) {
		Set<GLPathwayWindow> undemotableWindows = new HashSet<>();
		for (GLPathwayWindow window : column.windows) {
			while (window.getMinHeight() > freeSpaceVertical) {
				if (demote(window.info)) {
					snapshots.add(new LayoutSnapshot(columns));
				} else {
					undemotableWindows.add(window);
					break;
				}
			}
			if (!getSpaceExceedingColumns(freeSpaceVertical).contains(column))
				return;
		}

		while (undemotableWindows.size() < column.windows.size()) {
			PathwayColumn maxFreeSpaceColumn = null;
			float maxFreeSpace = 0;
			for (PathwayColumn c : columns) {
				float currentFreeSpace = freeSpaceVertical - c.getMinHeight();
				if (maxFreeSpace < currentFreeSpace) {
					maxFreeSpace = currentFreeSpace;
					maxFreeSpaceColumn = c;
				}
			}
			if (maxFreeSpaceColumn == null)
				return;

			GLPathwayWindow largestWindowFittingFreeSpace = null;
			GLPathwayWindow largestWindow = null;
			for (GLPathwayWindow window : column.windows) {
				if (window.getMinHeight() < maxFreeSpace
						&& (largestWindowFittingFreeSpace == null || window.getMinHeight() > largestWindowFittingFreeSpace
								.getMinHeight())) {
					largestWindowFittingFreeSpace = window;
				}
				if (!undemotableWindows.contains(window)
						&& (largestWindow == null || window.getMinHeight() > largestWindow.getMinHeight())) {
					largestWindow = window;
				}
			}

			if (largestWindowFittingFreeSpace != null) {
				maxFreeSpaceColumn.windows.add(largestWindowFittingFreeSpace);
				column.windows.remove(largestWindowFittingFreeSpace);
				snapshots.add(new LayoutSnapshot(columns));
			} else {
				if (largestWindow != null) {
					if (demote(largestWindow.info)) {
						snapshots.add(new LayoutSnapshot(columns));
					} else {
						undemotableWindows.add(largestWindow);
					}
				}
			}
			if (!getSpaceExceedingColumns(freeSpaceVertical).contains(column)) {
				return;
			}
		}
	}

	private boolean demote(MultiFormInfo info) {
		if (info.multiFormRenderer != view.lastUsedRenderer && info.multiFormRenderer != view.lastUsedLevel1Renderer) {
			EEmbeddingID level = info.getEmbeddingIDFromRendererID(info.multiFormRenderer.getActiveRendererID());
			EEmbeddingID levelDown = EEmbeddingID.levelDown(level);
			if (levelDown != level) {
				int rendererID = info.embeddingIDToRendererIDs.get(levelDown).get(0);
				info.multiFormRenderer.setActive(rendererID);
				PathwayColumn columnOfInfo = null;
				for (PathwayColumn column : columns) {
					for (GLPathwayWindow window : column.windows) {
						if (window.info == info) {
							columnOfInfo = column;
							break;
						}
					}
					if (columnOfInfo != null)
						break;
				}
				if (columnOfInfo != null) {
					boolean level1Present = false;
					boolean level2OrHigherPresent = false;
					List<GLPathwayWindow> lowerLevelWindows = new ArrayList<>();
					for (GLPathwayWindow window : columnOfInfo.windows) {
						if (window.info.getEmbeddingIDFromRendererID(window.info.multiFormRenderer
								.getActiveRendererID()) == EEmbeddingID.PATHWAY_LEVEL1) {
							level1Present = true;
						} else {
							level2OrHigherPresent = true;
							lowerLevelWindows.add(window);
						}
					}
					if (level1Present && level2OrHigherPresent) {
						PathwayColumn newColumn = new PathwayColumn();
						newColumn.windows.addAll(lowerLevelWindows);
						columns.add(columns.indexOf(columnOfInfo), newColumn);
						columnOfInfo.windows.removeAll(lowerLevelWindows);
					}
				}

				return true;
			}
		}
		return false;
	}

	private boolean isSufficientHorizontalSpace(float freeSpace) {
		int minWidth = 0;
		for (PathwayColumn column : columns) {
			minWidth += column.getMinWidth();
		}
		return freeSpace >= minWidth;
	}

	private List<PathwayColumn> getSpaceExceedingColumns(float freeSpace) {
		List<PathwayColumn> spaceExceedingColumns = new ArrayList<>();
		for (PathwayColumn column : columns) {
			if (column.getMinHeight() > freeSpace)
				spaceExceedingColumns.add(column);
		}
		return spaceExceedingColumns;
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
	private boolean mergeColumns(List<LayoutSnapshot> snapshots, float w, float h) {

		boolean columnsMerged = false;
		do {
			columnsMerged = false;
			PathwayColumn columnToRemove = null;
			Collections.sort(columns);
			for (PathwayColumn sourceColumn : columns) {
				for (PathwayColumn destColumn : columns) {
					if (sourceColumn != destColumn) {
						// Only merge columns whose height is smaller than the total height, and that either both
						// contain only level1 views or both contain lower level views
						if ((sourceColumn.getMinHeight() + destColumn.getMinHeight() <= h)
								&& ((sourceColumn.getLevelScore() == EEmbeddingID.PATHWAY_LEVEL1.renderPriority() && destColumn
										.getLevelScore() == EEmbeddingID.PATHWAY_LEVEL1.renderPriority()) || ((sourceColumn
										.getLevelScore() < EEmbeddingID.PATHWAY_LEVEL1.renderPriority() && destColumn
										.getLevelScore() < EEmbeddingID.PATHWAY_LEVEL1.renderPriority())))) {
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
				columns.remove(columnToRemove);
				snapshots.add(new LayoutSnapshot(columns));
			}
			if (isSufficientHorizontalSpace(getFreeHorizontalSpace(w)))
				return true;

		} while (columnsMerged);
		return false;
	}

	private class LayoutSnapshot {
		protected List<PathwayColumn> cols = new ArrayList<>();
		protected Map<GLPathwayWindow, Integer> windowToRendererID = new HashMap<>();
		protected float minTotalWidth = 0;
		protected float maxColumnHeight = 0;

		public LayoutSnapshot(List<PathwayColumn> columns) {
			for (PathwayColumn column : columns) {
				PathwayColumn newColumn = new PathwayColumn();
				for (GLPathwayWindow window : column.windows) {
					newColumn.windows.add(window);
					windowToRendererID.put(window, window.info.multiFormRenderer.getActiveRendererID());
				}
				cols.add(newColumn);
				minTotalWidth += column.getMinWidth();
				if (newColumn.getMinHeight() > maxColumnHeight) {
					maxColumnHeight = newColumn.getMinHeight();
				}
			}
		}

		protected void apply() {
			columns = cols;
			for (PathwayColumn column : columns) {
				for (GLPathwayWindow window : column.windows) {
					window.info.multiFormRenderer.setActive(windowToRendererID.get(window));
				}
			}
		}

	}

	private class PathwayColumn implements Comparable<PathwayColumn> {
		protected List<GLPathwayWindow> windows = new ArrayList<>();

		public int getMinHeight() {
			int minHeight = 0;
			for (GLPathwayWindow window : windows) {
				minHeight += window.getMinHeight();
			}
			minHeight += (windows.size() - 1) * gap;

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

			Set<GLPathwayWindow> level4Windows = new HashSet<>();
			int totalLevel4Size = 0;
			int totalOtherLevelMinSize = 0;
			for (GLPathwayWindow window : windows) {
				EEmbeddingID level = window.info.getEmbeddingIDFromRendererID(window.info.multiFormRenderer
						.getActiveRendererID());
				if (level == EEmbeddingID.PATHWAY_LEVEL4) {
					level4Windows.add(window);
					totalLevel4Size += window.getMinHeight();
				} else {
					totalOtherLevelMinSize += window.getMinHeight();
				}
			}

			float currentPositionY = y;
			for (GLPathwayWindow window : windows) {
				float windowHeight = 0;
				if (level4Windows.contains(window)) {
					windowHeight = window.getMinHeight();
				} else {
					int minHeight = window.getMinHeight();
					float factor = (float) minHeight / (float) totalOtherLevelMinSize;
					windowHeight = factor * (freeSpaceVertical - totalLevel4Size);
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
