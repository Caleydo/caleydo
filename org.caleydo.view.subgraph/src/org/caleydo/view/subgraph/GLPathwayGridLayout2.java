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
public class GLPathwayGridLayout2 implements IGLLayout {

	protected final GLSubGraph view;
	protected List<PathwayColumn> columns = new ArrayList<>();
	protected GLPadding padding;
	protected float gap;

	/**
	 *
	 */
	public GLPathwayGridLayout2(GLSubGraph view, GLPadding padding, float gap) {
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

		boolean level1Exists = false;
		for (PathwayColumn column : columns) {
			if (column.hasLevel(EEmbeddingID.PATHWAY_LEVEL1)) {
				level1Exists = true;
				break;
			}
		}
		float freeSpaceVertical = h - padding.vert();
		List<PathwayMultiFormInfo> infos = new ArrayList<>(view.pathwayInfos);

		if (!level1Exists) {
			Collections.sort(infos, new WindowLevel1PromotabolityComparator());
			Collections.reverse(infos);
			for (PathwayMultiFormInfo info : infos) {
				if (promote(info, freeSpaceVertical, EEmbeddingID.PATHWAY_LEVEL1)) {
					break;
				}
			}
		}

		ensureLayoutIntegrity();

		// float level1FreeSpaceHorizontal = w - padding.hor()
		// - getTotalColumnWidth(getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2));
		// optimizeLevel1Space(level1FreeSpaceHorizontal, freeSpaceVertical);

		List<LayoutSnapshot> snapshots = new ArrayList<>();

		LayoutSnapshot previousSnapshot = null;
		squeezeColumn(null, snapshots, freeSpaceVertical);

		snapshots.add(new LayoutSnapshot());

		// Horizontal space

		// remove <= level 2 columns by distributing windows among other columns

		reduceColumns(getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2), freeSpaceVertical, gap);

		// if (!mergeColumns(snapshots, w, freeSpaceVertical)) {
		Set<PathwayMultiFormInfo> undemotableInfos = new HashSet<>();

		while (undemotableInfos.size() < infos.size() && !isSufficientHorizontalSpace(getFreeHorizontalSpace(w))) {

			Collections.sort(infos, new WindowDemotabilityComparator(view));
			Collections.reverse(infos);

			for (PathwayMultiFormInfo info : infos) {
				if (demote(info)) {
					snapshots.add(new LayoutSnapshot());
					reduceColumns(getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2), freeSpaceVertical, gap);
					snapshots.add(new LayoutSnapshot());
					if (!isSufficientHorizontalSpace(getFreeHorizontalSpace(w))) {
						reduceColumns(getColumnsWithLevel(EEmbeddingID.PATHWAY_LEVEL1), freeSpaceVertical, gap);
						snapshots.add(new LayoutSnapshot());
					}
					if (isSufficientHorizontalSpace(getFreeHorizontalSpace(w))) {
						break;
					}
				} else {
					undemotableInfos.add(info);
				}
			}
		}
		// }

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
		float level1FreeSpaceHorizontal = w - padding.hor()
				- getTotalColumnWidth(getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2));
		optimizeLevel1Space(level1FreeSpaceHorizontal, freeSpaceVertical);
		Set<PathwayMultiFormInfo> unpromotableInfos = new HashSet<>(infos.size());
		while (unpromotableInfos.size() < infos.size()) {

			Collections.sort(infos, new WindowDemotabilityComparator(view));

			for (PathwayMultiFormInfo info : infos) {
				LayoutSnapshot snapshotPriorPromotion = new LayoutSnapshot();
				if (info.getCurrentEmbeddingID() != EEmbeddingID.PATHWAY_LEVEL2 && promote(info, freeSpaceVertical)) {
					reduceColumns(getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2), freeSpaceVertical, gap);
					LayoutSnapshot snapshotAfterPromotion = new LayoutSnapshot();

					if ((snapshotAfterPromotion.maxColumnHeight > freeSpaceVertical && snapshotAfterPromotion.maxColumnHeight > snapshotPriorPromotion.maxColumnHeight)
							|| (snapshotAfterPromotion.minTotalWidth > getFreeHorizontalSpace(w) && snapshotAfterPromotion.minTotalWidth > snapshotPriorPromotion.minTotalWidth)) {
						snapshotPriorPromotion.apply();
						unpromotableInfos.add(info);
					}

				} else {
					unpromotableInfos.add(info);
				}
			}
		}

		Set<PathwayColumn> level1Columns = new HashSet<>();
		int minTotalLevel1Size = 0;
		int totalFixedSize = 0;
		List<PathwayColumn> copyColumns = new ArrayList<>(columns);
		Collections.sort(copyColumns, new ColumnPriorityComparator());
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

	private boolean promote(PathwayMultiFormInfo info, float freeSpaceVertical, EEmbeddingID level) {

		if (isPromotable(info)) {
			int rendererID = info.embeddingIDToRendererIDs.get(level).get(0);
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
			if (columnOfInfo != null && columnOfInfo.getMinHeight() > freeSpaceVertical) {
				addColumn((GLPathwayWindow) info.window);
				columnOfInfo.windows.remove(info.window);
			}

			return true;
		}
		return false;
	}

	private boolean promote(PathwayMultiFormInfo info, float freeSpaceVertical) {
		EEmbeddingID level = info.getCurrentEmbeddingID();
		EEmbeddingID levelUp = EEmbeddingID.levelUp(level);
		return promote(info, freeSpaceVertical, levelUp);
	}

	/**
	 * Adapts the layout so that it is guaranteed that lv1 views only share their column with other lv1 views.
	 */
	private void ensureLayoutIntegrity() {
		boolean columnsChanged = true;
		while (columnsChanged) {
			columnsChanged = false;
			for (PathwayColumn column : columns) {
				columnsChanged = ensureColumnIntegrity(column);
				if (columnsChanged)
					break;
			}
		}
	}

	private void reduceColumns(List<PathwayColumn> columns, float height, float gap) {

		// List<PathwayColumn> lv2Columns = getColumnsWithLevel(EEmbeddingID.PATHWAY_LEVEL2);

		if (columns.isEmpty())
			return;
		LayoutSnapshot workingSnapshot = new LayoutSnapshot();
		Set<PathwayColumn> columnsTried = new HashSet<>();

		while (columnsTried.size() < columns.size()) {

			// Sort by min height: try to remove smallest column first
			Collections.sort(columns, new MinColumnHeightComparator());
			PathwayColumn columnToRemove = null;
			for (PathwayColumn column : columns) {
				if (!columnsTried.contains(column)) {
					columnToRemove = column;
					break;
				}
			}
			if (columnToRemove == null)
				return;

			for (PathwayColumn column : columns) {
				if (column == columnToRemove)
					continue;
				List<GLPathwayWindow> windows = new ArrayList<>(columnToRemove.windows);

				// Try to fit largest window into other column first
				Collections.sort(windows, new MinWindowHeightComparator());
				Collections.reverse(windows);
				for (GLPathwayWindow window : windows) {
					if (column.getMinHeight() + window.getMinHeight() + gap <= height) {
						column.windows.add(window);
						columnToRemove.windows.remove(window);
					} else {
						break;
					}
				}
			}
			if (columnToRemove.windows.isEmpty()) {
				this.columns.remove(columnToRemove);
				columns.remove(columnToRemove);
				workingSnapshot = new LayoutSnapshot();
			} else {
				workingSnapshot.apply();
				columnsTried.add(columnToRemove);
			}
		}
	}

	private void squeezeColumn(PathwayColumn f, List<LayoutSnapshot> snapshots, float freeSpaceVertical) {
		// Set<GLPathwayWindow> undemotableWindows = new HashSet<>();
		boolean demoted = true;
		while (demoted) {
			demoted = false;
			List<PathwayColumn> columnsCopy = new ArrayList<>(columns);

			for (PathwayColumn column : columnsCopy) {
				while (column.getMinHeight() > freeSpaceVertical) {

					GLPathwayWindow largestWindow = Collections.max(column.windows, new MinWindowHeightComparator());
					while (largestWindow.getMinHeight() > freeSpaceVertical) {
						if (demote(largestWindow.info)) {
							// start all over again, as demote does ensure column integrity, thus unknown column changes
							// could have happened.
							demoted = true;
						} else {
							break;
						}
					}
					if (demoted) {
						break;
					}

					if (column.getMinHeight() > freeSpaceVertical) {
						if (column.windows.size() > 1) {
							column.windows.remove(largestWindow);
							addColumn(largestWindow);
						} else {
							break;
						}
					}
				}
				if (demoted) {
					break;
				}
			}

		}

		// for (GLPathwayWindow window : column.windows) {
		// while (window.getMinHeight() > freeSpaceVertical) {
		// if (demote(window.info)) {
		// snapshots.add(new LayoutSnapshot());
		// } else {
		// undemotableWindows.add(window);
		// break;
		// }
		// }
		// if (!getSpaceExceedingColumns(freeSpaceVertical).contains(column))
		// return;
		// }
		//
		// while (undemotableWindows.size() < column.windows.size()) {
		// PathwayColumn maxFreeSpaceColumn = null;
		// float maxFreeSpace = 0;
		// for (PathwayColumn c : columns) {
		// float currentFreeSpace = freeSpaceVertical - c.getMinHeight();
		// if (maxFreeSpace < currentFreeSpace && !c.hasLevel(EEmbeddingID.PATHWAY_LEVEL1)) {
		// maxFreeSpace = currentFreeSpace;
		// maxFreeSpaceColumn = c;
		// }
		// }
		// // if (maxFreeSpaceColumn == null)
		// // return;
		//
		// GLPathwayWindow largestWindowFittingFreeSpace = null;
		// GLPathwayWindow largestWindow = null;
		// for (GLPathwayWindow window : column.windows) {
		// if (window.getMinHeight() < maxFreeSpace
		// && (largestWindowFittingFreeSpace == null || window.getMinHeight() > largestWindowFittingFreeSpace
		// .getMinHeight())) {
		// largestWindowFittingFreeSpace = window;
		// }
		// if (!undemotableWindows.contains(window)
		// && (largestWindow == null || window.getMinHeight() > largestWindow.getMinHeight())) {
		// largestWindow = window;
		// }
		// }
		//
		// if (largestWindowFittingFreeSpace != null && maxFreeSpaceColumn != null) {
		// maxFreeSpaceColumn.windows.add(largestWindowFittingFreeSpace);
		// column.windows.remove(largestWindowFittingFreeSpace);
		// snapshots.add(new LayoutSnapshot());
		// } else {
		// if (largestWindow != null) {
		// if (demote(largestWindow.info)) {
		// snapshots.add(new LayoutSnapshot());
		// } else {
		// undemotableWindows.add(largestWindow);
		// }
		// }
		// }
		// if (!getSpaceExceedingColumns(freeSpaceVertical).contains(column)) {
		// return;
		// }
		// }
	}

	private boolean isDemotable(MultiFormInfo info) {
		if (info.multiFormRenderer != view.lastUsedRenderer && info.multiFormRenderer != view.lastUsedLevel1Renderer
				&& !view.pinnedWindows.contains(info.window)) {
			EEmbeddingID level = info.getEmbeddingIDFromRendererID(info.multiFormRenderer.getActiveRendererID());
			EEmbeddingID levelDown = EEmbeddingID.levelDown(level);
			if (levelDown != level)
				return true;
		}
		return false;
	}

	private boolean isPromotable(MultiFormInfo info) {
		return info.multiFormRenderer != view.lastUsedRenderer && !view.pinnedWindows.contains(info.window)
				&& info.getCurrentEmbeddingID() != EEmbeddingID.PATHWAY_LEVEL1;
	}

	private boolean demote(MultiFormInfo info) {
		if (isDemotable(info)) {
			EEmbeddingID level = info.getEmbeddingIDFromRendererID(info.multiFormRenderer.getActiveRendererID());
			EEmbeddingID levelDown = EEmbeddingID.levelDown(level);
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
				ensureColumnIntegrity(columnOfInfo);
			}

			return true;
		}
		return false;
	}

	/**
	 * Ensures the rule of lv1 views not being mixed with other lv views in the specified columns. If this rule is
	 * violated, a new column containing all other lv views is added to the layout.
	 *
	 * @param column
	 */
	private boolean ensureColumnIntegrity(PathwayColumn column) {
		boolean level1Present = false;
		boolean level2OrHigherPresent = false;
		List<GLPathwayWindow> lowerLevelWindows = new ArrayList<>();
		for (GLPathwayWindow window : column.windows) {
			if (window.info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL1) {
				level1Present = true;
			} else {
				level2OrHigherPresent = true;
				lowerLevelWindows.add(window);
			}
		}
		if (level1Present && level2OrHigherPresent) {
			PathwayColumn newColumn = new PathwayColumn();
			newColumn.windows.addAll(lowerLevelWindows);
			columns.add(columns.indexOf(column), newColumn);
			column.windows.removeAll(lowerLevelWindows);
			return true;
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

	// private List<PathwayColumn> getSpaceExceedingColumns(float freeSpace) {
	// List<PathwayColumn> spaceExceedingColumns = new ArrayList<>();
	// for (PathwayColumn column : columns) {
	// if (column.getMinHeight() > freeSpace)
	// spaceExceedingColumns.add(column);
	// }
	// return spaceExceedingColumns;
	// }

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
				snapshots.add(new LayoutSnapshot());
			}
			if (isSufficientHorizontalSpace(getFreeHorizontalSpace(w)))
				return true;

		} while (columnsMerged);
		return false;
	}

	/**
	 * Optimizes the distribution of level1 windows over columns. This method assumes the layout's integrity not to be
	 * violated.
	 *
	 * @param freeSpaceHorizontal
	 * @param freeSpaceVertical
	 */
	private void optimizeLevel1Space(float freeSpaceHorizontal, float freeSpaceVertical) {

		// Initialize columns with one window each
		List<PathwayColumn> newColumns = new ArrayList<>();
		List<PathwayColumn> level1Columns = new ArrayList<>();
		for (PathwayColumn column : columns) {
			if (column.hasLevel(EEmbeddingID.PATHWAY_LEVEL1)) {
				for (GLPathwayWindow window : column.windows) {
					PathwayColumn c = new PathwayColumn();
					c.windows.add(window);
					newColumns.add(c);
				}
				level1Columns.add(column);
			}
		}

		// Minimize aspect ratio difference between available space and minimum required space by columns
		// Note: this is not the optimal solution
		float freeSpaceAspectRatio = freeSpaceHorizontal / freeSpaceVertical;

		float currentColumnsAspectRatio = getTotalColumnAspectRatio(newColumns);
		float minAspectRatioDifference = Math.abs(freeSpaceAspectRatio - currentColumnsAspectRatio);

		List<PathwayColumn> improvedColumns = null;
		do {
			improvedColumns = null;
			for (PathwayColumn column : newColumns) {
				for (PathwayColumn c : newColumns) {
					if (column != c) {
						List<PathwayColumn> tempColumns = new ArrayList<>(newColumns.size());
						for (PathwayColumn col : newColumns) {
							if (col == column)
								continue;
							PathwayColumn newColumn = new PathwayColumn();
							newColumn.windows.addAll(col.windows);
							if (col == c) {
								newColumn.windows.addAll(column.windows);
							}
							tempColumns.add(newColumn);
						}
						float aspectRatioDifference = Math.abs(freeSpaceAspectRatio
								- getTotalColumnAspectRatio(tempColumns));
						if (aspectRatioDifference < minAspectRatioDifference) {
							minAspectRatioDifference = aspectRatioDifference;
							improvedColumns = tempColumns;
						}
					}
				}
			}
			if (improvedColumns != null)
				newColumns = improvedColumns;

		} while (improvedColumns != null);

		columns.removeAll(level1Columns);
		columns.addAll(newColumns);
	}

	private float getTotalColumnAspectRatio(List<PathwayColumn> columns) {
		if (columns.isEmpty())
			return 1;
		return getTotalColumnWidth(columns) / getMaxColumnHeight(columns);
	}

	private float getTotalColumnWidth(List<PathwayColumn> columns) {
		float width = 0;
		for (PathwayColumn column : columns) {
			width += column.getMinWidth();
		}

		if (columns.size() > 0)
			width += (columns.size() - 1) * gap;

		return width;
	}

	private float getMaxColumnHeight(List<PathwayColumn> columns) {
		float maxHeight = 0;
		for (PathwayColumn column : columns) {
			if (column.getMinHeight() > maxHeight)
				maxHeight = column.getMinHeight();
		}

		return maxHeight;
	}

	private List<PathwayColumn> getColumnsWithLevel(EEmbeddingID level) {
		List<PathwayColumn> columnsWithLevel = new ArrayList<>();
		for (PathwayColumn column : columns) {
			if (column.hasLevel(level))
				columnsWithLevel.add(column);
		}
		return columnsWithLevel;
	}

	private List<PathwayColumn> getColumnsWithMaxLevel(EEmbeddingID level) {
		List<PathwayColumn> columnsWithLevel = new ArrayList<>();
		for (PathwayColumn column : columns) {
			if (column.getLevelScore() <= level.renderPriority())
				columnsWithLevel.add(column);
		}
		return columnsWithLevel;
	}

	private class LayoutSnapshot {
		protected List<PathwayColumn> cols = new ArrayList<>();
		protected Map<GLPathwayWindow, Integer> windowToRendererID = new HashMap<>();
		protected float minTotalWidth = 0;
		protected float maxColumnHeight = 0;

		public LayoutSnapshot() {
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

	private static class MinColumnHeightComparator implements Comparator<PathwayColumn> {

		@Override
		public int compare(PathwayColumn arg0, PathwayColumn arg1) {
			return arg0.getMinHeight() - arg1.getMinHeight();
		}

	}

	private static class MinColumnWidthComparator implements Comparator<PathwayColumn> {

		@Override
		public int compare(PathwayColumn arg0, PathwayColumn arg1) {
			return arg0.getMinWidth() - arg1.getMinWidth();
		}

	}

	private static class MinWindowHeightComparator implements Comparator<GLPathwayWindow> {

		@Override
		public int compare(GLPathwayWindow arg0, GLPathwayWindow arg1) {
			return arg0.getMinHeight() - arg1.getMinHeight();
		}

	}

	private static class MinWindowWidthComparator implements Comparator<GLPathwayWindow> {

		@Override
		public int compare(GLPathwayWindow arg0, GLPathwayWindow arg1) {
			return arg0.getMinWidth() - arg1.getMinWidth();
		}

	}

	private static class ColumnPriorityComparator implements Comparator<PathwayColumn> {

		@Override
		public int compare(PathwayColumn o1, PathwayColumn o2) {
			int priority1 = o1.getLevelScore();
			int priority2 = o2.getLevelScore();
			if (priority1 == priority2) {
				float age1 = o1.getAverageAge();
				float age2 = o2.getAverageAge();
				if (age1 > age2) {
					return 1;
				} else if (age2 > age1)
					return -1;
				return 0;
			}
			return priority1 - priority2;
		}

	}

	private static class WindowDemotabilityComparator implements Comparator<PathwayMultiFormInfo> {

		private GLSubGraph view;

		/**
		 *
		 */
		public WindowDemotabilityComparator(GLSubGraph view) {
			this.view = view;
		}

		@Override
		public int compare(PathwayMultiFormInfo o1, PathwayMultiFormInfo o2) {
			boolean hasPath1 = view.hasPathPathway(o1.pathway);
			boolean hasPath2 = view.hasPathPathway(o2.pathway);
			if (hasPath1 && !hasPath2)
				return -1;
			if (hasPath2 && !hasPath1)
				return 1;
			int priority1 = o1.getCurrentEmbeddingID().renderPriority();
			int priority2 = o2.getCurrentEmbeddingID().renderPriority();
			// younger ones should be ranked lower
			if (priority1 == priority2) {
				return o1.age - o2.age;
			}
			return priority1 - priority2;
		}
	}

	private static class WindowLevel1PromotabolityComparator implements Comparator<PathwayMultiFormInfo> {

		@Override
		public int compare(PathwayMultiFormInfo o1, PathwayMultiFormInfo o2) {
			int priority1 = o1.getCurrentEmbeddingID().renderPriority();
			int priority2 = o2.getCurrentEmbeddingID().renderPriority();
			// younger ones should be ranked higher
			if (priority1 == priority2) {
				return o2.age - o1.age;
			}
			return priority1 - priority2;
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

			// Set<GLPathwayWindow> level4Windows = new HashSet<>();
			// int totalLevel4Size = 0;
			int totalOtherLevelMinSize = 0;
			int totalLevel1MinSize = 0;
			for (GLPathwayWindow window : windows) {
				EEmbeddingID level = window.info.getCurrentEmbeddingID();
				// if (level == EEmbeddingID.PATHWAY_LEVEL4) {
				// level4Windows.add(window);
				// totalLevel4Size += window.getMinHeight();
				// }
				if (level == EEmbeddingID.PATHWAY_LEVEL1) {
					totalLevel1MinSize += window.getMinHeight();
				} else {
					totalOtherLevelMinSize += window.getMinHeight();
				}
			}
			float windowSpacing = gap;
			if (windows.size() > 1 && totalLevel1MinSize == 0) {
				windowSpacing += (freeSpaceVertical - totalOtherLevelMinSize) / (windows.size() - 1);
			}

			float currentPositionY = y;
			for (GLPathwayWindow window : windows) {
				float windowHeight = 0;

				if (window.info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL1) {
					int minHeight = window.getMinHeight();
					float factor = (float) minHeight / (float) totalLevel1MinSize;
					windowHeight = factor * (freeSpaceVertical - totalOtherLevelMinSize);
				} else {
					windowHeight = window.getMinHeight();
				}
				windowToElement.get(window).setSize(w, windowHeight);
				windowToElement.get(window).setLocation(x, currentPositionY);
				currentPositionY += windowHeight + windowSpacing;
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

		protected boolean hasLevel(EEmbeddingID level) {
			for (GLPathwayWindow window : windows) {
				int rendererID = window.info.multiFormRenderer.getActiveRendererID();
				EEmbeddingID embdeddingID = window.info.getEmbeddingIDFromRendererID(rendererID);
				if (embdeddingID == level)
					return true;
			}
			return false;
		}

		@Override
		public int compareTo(PathwayColumn o) {
			return getLevelScore() - o.getLevelScore();
		}

		protected float getAverageAge() {
			float averageAge = 0;
			for (GLPathwayWindow window : windows) {
				PathwayMultiFormInfo info = (PathwayMultiFormInfo) window.info;
				averageAge += info.age;
			}

			return averageAge / windows.size();
		}

	}

}
