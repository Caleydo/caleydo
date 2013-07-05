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
public class GLPathwayGridLayout3 implements IGLLayout {

	protected final GLSubGraph view;
	protected List<PathwayColumn> columns = new ArrayList<>();
	protected GLPadding padding;
	protected float gap;

	/**
	 *
	 */
	public GLPathwayGridLayout3(GLSubGraph view, GLPadding padding, float gap) {
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
		float freeSpaceVertical = h - padding.vert();

		if (!view.wasContextChanged()) {
			columns.clear();

			for (PathwayMultiFormInfo info : view.pathwayInfos) {
				addColumn((GLPathwayWindow) info.window);
			}

			Collections.sort(columns, new ColumnPriorityComparator());
			Collections.reverse(columns);

			float level1FreeSpaceHorizontal = w - padding.hor()
					- getTotalColumnWidth(getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2));
			optimizeLevel1Space(level1FreeSpaceHorizontal, freeSpaceVertical);

			List<PathwayColumn> level1Columns = getColumnsWithLevel(EEmbeddingID.PATHWAY_LEVEL1);
			squeezeColumns(level1Columns, freeSpaceVertical);

			List<PathwayMultiFormInfo> level1Infos = getInfosWithLevel(EEmbeddingID.PATHWAY_LEVEL1);
			Set<PathwayMultiFormInfo> undemotableInfos = new HashSet<>();

			while (undemotableInfos.size() < level1Infos.size()
					&& !isSufficientHorizontalSpace(getFreeHorizontalSpace(w))) {
				Collections.sort(level1Infos, new WindowDemotabilityComparator(view));
				Collections.reverse(level1Infos);
				for (PathwayMultiFormInfo info : level1Infos) {
					if (demote(info)) {
						level1Infos.remove(info);
						break;
					} else {
						undemotableInfos.add(info);
					}
				}
			}
			level1FreeSpaceHorizontal = w - padding.hor()
					- getTotalColumnWidth(getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2));
			optimizeLevel1Space(level1FreeSpaceHorizontal, freeSpaceVertical);

			List<PathwayColumn> level2Columns = getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2);
			while (level2Columns.size() >= 2 && !isSufficientHorizontalSpace(getFreeHorizontalSpace(w))) {
				Collections.sort(level2Columns, new ColumnPriorityComparator());
				Collections.reverse(level2Columns);
				boolean columnRemoved = false;
				while (!columnRemoved) {
					for (PathwayColumn column : level2Columns) {
						int currentColumnIndex = level2Columns.indexOf(column);
						PathwayColumn firstColumn = level2Columns.get(0);
						if (currentColumnIndex == level2Columns.size() - 1) {
							if (firstColumn != column && firstColumn.windows.isEmpty()) {
								level2Columns.remove(firstColumn);
								columns.remove(firstColumn);
								columnRemoved = true;
								break;
							}
						} else {
							PathwayColumn nextColumn = level2Columns.get(currentColumnIndex + 1);
							if (nextColumn.windows.size() < column.windows.size() || column == firstColumn) {
								nextColumn.windows.add(0, column.windows.get(column.windows.size() - 1));
								column.windows.remove(column.windows.size() - 1);
							} else if (firstColumn.windows.isEmpty()) {
								level2Columns.remove(firstColumn);
								columns.remove(firstColumn);
								columnRemoved = true;
								break;
							}

						}
					}
				}
			}

			level1FreeSpaceHorizontal = w - padding.hor()
					- getTotalColumnWidth(getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2));
			optimizeLevel1Space(level1FreeSpaceHorizontal, freeSpaceVertical);
		}
		List<PathwayMultiFormInfo> level2Infos = getInfosWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2);
		Set<PathwayMultiFormInfo> undemotableInfos = new HashSet<>();

		List<LayoutSnapshot> snapshots = new ArrayList<>();
		snapshots.add(new LayoutSnapshot());

		while (undemotableInfos.size() < level2Infos.size() && !isSufficientHorizontalSpace(getFreeHorizontalSpace(w))) {
			Collections.sort(level2Infos, new WindowDemotabilityComparator(view));
			Collections.reverse(level2Infos);
			for (PathwayMultiFormInfo info : level2Infos) {
				if (demote(info)) {
					snapshots.add(new LayoutSnapshot());
					break;
				} else {
					undemotableInfos.add(info);
				}
			}
		}

		Collections.reverse(snapshots);
		LayoutSnapshot previousSnapshot = null;
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

		if (!view.wasContextChanged()) {
			float level1FreeSpaceHorizontal = w - padding.hor()
					- getTotalColumnWidth(getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2));
			optimizeLevel1Space(level1FreeSpaceHorizontal, freeSpaceVertical);
		}

		List<PathwayColumn> level2Columns = getColumnsWithMaxLevel(EEmbeddingID.PATHWAY_LEVEL2);
		squeezeColumns(level2Columns, freeSpaceVertical);

		Set<PathwayMultiFormInfo> unpromotableInfos = new HashSet<>(level2Infos.size());
		while (unpromotableInfos.size() < level2Infos.size()) {

			Collections.sort(level2Infos, new WindowDemotabilityComparator(view));

			for (PathwayMultiFormInfo info : level2Infos) {
				LayoutSnapshot snapshotPriorPromotion = new LayoutSnapshot();
				if (info.getCurrentEmbeddingID() != EEmbeddingID.PATHWAY_LEVEL2
						&& info.multiFormRenderer != view.lastUsedRenderer && promote(info, freeSpaceVertical)) {
					LayoutSnapshot snapshotAfterPromotion = new LayoutSnapshot();
					if ((snapshotAfterPromotion.maxL2ColumnHeight > freeSpaceVertical && snapshotAfterPromotion.maxL2ColumnHeight > snapshotPriorPromotion.maxL2ColumnHeight)
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
		view.setWasContextChanged(false);
	}

	private List<PathwayMultiFormInfo> getInfosWithLevel(EEmbeddingID level) {
		List<PathwayMultiFormInfo> infos = new ArrayList<>();
		for (PathwayMultiFormInfo info : view.pathwayInfos) {
			if (info.getCurrentEmbeddingID() == level) {
				infos.add(info);
			}
		}
		return infos;
	}

	private List<PathwayMultiFormInfo> getInfosWithMaxLevel(EEmbeddingID level) {
		List<PathwayMultiFormInfo> infos = new ArrayList<>();
		for (PathwayMultiFormInfo info : view.pathwayInfos) {
			if (info.getCurrentEmbeddingID().renderPriority() <= level.renderPriority()) {
				infos.add(info);
			}
		}
		return infos;
	}

	private boolean promote(PathwayMultiFormInfo info, float freeSpaceVertical, EEmbeddingID level) {

		if (isPromotable(info)) {
			int rendererID = info.embeddingIDToRendererIDs.get(level).get(0);
			info.multiFormRenderer.setActive(rendererID);
			return true;
		}
		return false;
	}

	private boolean promote(PathwayMultiFormInfo info, float freeSpaceVertical) {
		EEmbeddingID level = info.getCurrentEmbeddingID();
		EEmbeddingID levelUp = EEmbeddingID.levelUp(level);
		return promote(info, freeSpaceVertical, levelUp);
	}

	private void squeezeColumns(List<PathwayColumn> columns, float freeSpaceVertical) {

		for (PathwayColumn column : columns) {
			Set<PathwayMultiFormInfo> undemotableInfos = new HashSet<>();
			List<PathwayMultiFormInfo> infos = column.getInfos();
			while (undemotableInfos.size() < infos.size() && column.getMinHeight() > freeSpaceVertical) {
				Collections.sort(infos, new WindowDemotabilityComparator(view));
				Collections.reverse(infos);
				for (PathwayMultiFormInfo info : infos) {
					if (demote(info)) {
						break;
					} else {
						undemotableInfos.add(info);
					}
				}
			}
		}
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

	private float getFreeHorizontalSpace(float w) {
		return w - padding.hor() - (columns.size() - 1) * gap;
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
		protected float maxL2ColumnHeight = 0;

		public LayoutSnapshot() {
			for (PathwayColumn column : columns) {
				PathwayColumn newColumn = new PathwayColumn();
				for (GLPathwayWindow window : column.windows) {
					newColumn.windows.add(window);
					windowToRendererID.put(window, window.info.multiFormRenderer.getActiveRendererID());
				}
				cols.add(newColumn);
				minTotalWidth += column.getMinWidth();
				if (newColumn.getMinHeight() > maxL2ColumnHeight && !newColumn.hasLevel(EEmbeddingID.PATHWAY_LEVEL1)) {
					maxL2ColumnHeight = newColumn.getMinHeight();
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

	private static class ColumnPriorityComparator implements Comparator<PathwayColumn> {

		@Override
		public int compare(PathwayColumn o1, PathwayColumn o2) {
			boolean has1Level1 = o1.hasLevel(EEmbeddingID.PATHWAY_LEVEL1);
			boolean has2Level1 = o2.hasLevel(EEmbeddingID.PATHWAY_LEVEL1);
			if (has1Level1 && !has2Level1)
				return 1;
			if (has2Level1 && !has1Level1)
				return -1;
			int age1 = o1.getMinAge();
			int age2 = o2.getMinAge();
			if (age1 < age2) {
				return 1;
			} else if (age2 < age1)
				return -1;
			return 0;
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
			boolean hasContext1 = view.hasPathwayCurrentContext(o1.pathway);
			boolean hasContext2 = view.hasPathwayCurrentContext(o1.pathway);
			if (hasContext1 && !hasContext2)
				return -1;
			if (hasContext2 && !hasContext1)
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

	private class PathwayColumn {
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

		protected int getMinAge() {
			int minimumAge = Integer.MAX_VALUE;
			for (GLPathwayWindow window : windows) {

				PathwayMultiFormInfo info = (PathwayMultiFormInfo) window.info;
				if (minimumAge > info.age)
					minimumAge = info.age;
			}

			return minimumAge;
		}

		protected List<PathwayMultiFormInfo> getInfos() {
			List<PathwayMultiFormInfo> infos = new ArrayList<>(windows.size());
			for (GLPathwayWindow window : windows) {
				infos.add((PathwayMultiFormInfo) window.info);
			}
			return infos;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (GLPathwayWindow window : windows) {
				builder.append(window.toString()).append(", ");
			}
			builder.append("]");
			return builder.toString();
		}
	}
}
