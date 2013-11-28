/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path;

import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.path.node.BranchSummaryNode;

/**
 * Class that determines the size of several elements within a path in pixels.
 *
 * @author Christian Partl
 *
 */
public class PathSizeConfiguration {

	public static final PathSizeConfiguration DEFAULT = new Builder().rectangleNodeWidth(70).rectangleNodeHeight(20)
			.circleNodeRadius(10).minNodeSpacing(50).nodeTextHeight(16).pathStartSpacing(30).pathEndSpacing(16)
			.rowHeight(60).branchNodeToPathNodeVerticalSpacing(20).collapsedBranchAreaWidth(100)
			.expandedBranchAreaWidth(100).pathAreaWidth(150).branchNodeLeftSideSpacing(8).pathwayTextHeight(16)
			.pathwayTitleAreaWidth(20).dataPreviewRowHeight(30).branchSummaryNodeWidthCollapsed(90)
			.branchSummaryNodeWidthExpanded(90).branchSummaryNodeTextHeight(14).branchSummaryNodeBranchAreaSpacing(8)
			.branchSummaryNodeBranchNodeSpacing(20).pathwayBorderWidth(10).edgeArrowSize(15).edgeArrwoBaseLineSize(10)
			.edgeTextHeight(13).branchNodesShowDataPreview(true).build();

	public static final PathSizeConfiguration COMPACT = new Builder().rectangleNodeWidth(40).rectangleNodeHeight(14)
			.circleNodeRadius(7).minNodeSpacing(40).nodeTextHeight(12).pathStartSpacing(30).pathEndSpacing(20)
			.rowHeight(40).branchNodeToPathNodeVerticalSpacing(10).collapsedBranchAreaWidth(40)
			.expandedBranchAreaWidth(65).pathAreaWidth(50).branchNodeLeftSideSpacing(6).pathwayTextHeight(12)
			.pathwayTitleAreaWidth(14).dataPreviewRowHeight(20).branchSummaryNodeWidthCollapsed(25)
			.branchSummaryNodeWidthExpanded(50).branchSummaryNodeTextHeight(12).branchSummaryNodeBranchAreaSpacing(4)
			.branchSummaryNodeBranchNodeSpacing(10).pathwayBorderWidth(6).edgeArrowSize(10).edgeArrwoBaseLineSize(6)
			.edgeTextHeight(9).branchNodesShowDataPreview(false).build();

	public static final PathSizeConfiguration ENROUTE_DEFAULT = new Builder(DEFAULT).pathStartSpacing(35)
			.pathEndSpacing(35).minNodeSpacing(40).build();

	public static final PathSizeConfiguration ENROUTE_COMPACT = new Builder(COMPACT).pathStartSpacing(35)
			.pathEndSpacing(35).minNodeSpacing(26).pathAreaWidth(80).build();

	/**
	 * Height of labels for edges.
	 */
	protected final int edgeTextHeight;
	/**
	 * Size of the base line of an edge arrow.
	 */
	protected final int edgeArrwoBaseLineSize;
	/**
	 * The size from the top of the arrow to the base line of an edge.
	 */
	protected final int edgeArrowSize;
	/**
	 * Width of the pathway border.
	 */
	protected final int pathwayBorderWidth;
	/**
	 * Spacing between branch nodes within a {@link BranchSummaryNode}.
	 */
	protected final int branchSummaryNodeBranchNodeSpacing;
	/**
	 * The spacing before and after all branch nodes within a {@link BranchSummaryNode}.
	 */
	protected final int branchSummaryNodeBranchAreaSpacing;
	/**
	 * Height of the text displayed in branch summary nodes.
	 */
	protected final int branchSummaryNodeTextHeight;
	/**
	 * Width of collapsed {@link BranchSummaryNode}s.
	 */
	protected final int branchSummaryNodeWidthCollapsed;
	/**
	 * Width of collapsed {@link BranchSummaryNode}s.
	 */
	protected final int branchSummaryNodeWidthExpanded;
	/**
	 * The height of the row that shows the preview of data that maps to the node.
	 */
	protected final int dataPreviewRowHeight;
	/**
	 * Height of the pathway title.
	 */
	protected final int pathwayTextHeight;
	/**
	 * Width of the area where the pathway title is displayed.
	 */
	protected final int pathwayTitleAreaWidth;
	/**
	 * Width of rectangular path nodes.
	 */
	protected final int rectangleNodeWidth;
	/**
	 * Height of rectangular path nodes.
	 */
	protected final int rectangleNodeHeight;
	/**
	 * Radius of circular nodes.
	 */
	protected final int circleNodeRadius;
	/**
	 * Minimum space between two successive nodes of a path.
	 */
	protected final int minNodeSpacing;
	/**
	 * Height of text displayed in nodes.
	 */
	protected final int nodeTextHeight;
	/**
	 * Spacing before the first node of the path.
	 */
	protected final int pathStartSpacing;
	/**
	 * Spacing after the last node of the path.
	 */
	protected final int pathEndSpacing;
	/**
	 * Height of data rows that map to nodes in {@link GLEnRoutePathway}.
	 */
	protected final int rowHeight;
	/**
	 * Spacing between branch and path nodes.
	 */
	protected final int branchNodeToPathNodeVerticalSpacing;
	/**
	 * Width of the area for branch nodes when there are only collapsed branch nodes.
	 */
	protected final int collapsedBranchAreaWidth;
	/**
	 * Width of the area for branch nodes when there is at least one expanded branch nodes.
	 */
	protected final int expandedBranchAreaWidth;
	/**
	 * Width of the path area.
	 */
	protected final int pathAreaWidth;
	/**
	 * Side spacing of branch nodes.
	 */
	protected final int branchNodeLeftSideSpacing;
	/**
	 * Determines, whether the branch nodes show a more detailed data preview.
	 */
	protected final boolean branchNodesShowDataPreview;

	public static class Builder {
		protected int rectangleNodeWidth;
		protected int rectangleNodeHeight;
		protected int circleNodeRadius;
		protected int minNodeSpacing;
		protected int nodeTextHeight;
		protected int pathStartSpacing;
		protected int pathEndSpacing;
		protected int rowHeight;
		protected int branchNodeToPathNodeVerticalSpacing;
		protected int collapsedBranchAreaWidth;
		protected int expandedBranchAreaWidth;
		protected int pathAreaWidth;
		protected int branchNodeLeftSideSpacing;
		protected int pathwayTextHeight;
		protected int pathwayTitleAreaWidth;
		protected int dataPreviewRowHeight;
		protected int branchSummaryNodeBranchNodeSpacing;
		protected int branchSummaryNodeBranchAreaSpacing;
		protected int branchSummaryNodeTextHeight;
		protected int branchSummaryNodeWidthCollapsed;
		protected int branchSummaryNodeWidthExpanded;
		protected int pathwayBorderWidth;
		protected int edgeTextHeight;
		protected int edgeArrwoBaseLineSize;
		protected int edgeArrowSize;
		protected boolean branchNodesShowDataPreview;

		public Builder branchNodesShowDataPreview(boolean value) {
			this.branchNodesShowDataPreview = value;
			return this;
		}

		public Builder edgeArrwoBaseLineSize(int value) {
			this.edgeArrwoBaseLineSize = value;
			return this;
		}

		public Builder edgeTextHeight(int value) {
			this.edgeTextHeight = value;
			return this;
		}

		public Builder edgeArrowSize(int value) {
			this.edgeArrowSize = value;
			return this;
		}

		public Builder pathwayBorderWidth(int value) {
			this.pathwayBorderWidth = value;
			return this;
		}

		public Builder branchSummaryNodeBranchNodeSpacing(int value) {
			this.branchSummaryNodeBranchNodeSpacing = value;
			return this;
		}

		public Builder branchSummaryNodeBranchAreaSpacing(int value) {
			this.branchSummaryNodeBranchAreaSpacing = value;
			return this;
		}

		public Builder branchSummaryNodeTextHeight(int value) {
			this.branchSummaryNodeTextHeight = value;
			return this;
		}

		public Builder branchSummaryNodeWidthCollapsed(int value) {
			this.branchSummaryNodeWidthCollapsed = value;
			return this;
		}

		public Builder branchSummaryNodeWidthExpanded(int value) {
			this.branchSummaryNodeWidthExpanded = value;
			return this;
		}

		public Builder dataPreviewRowHeight(int value) {
			this.dataPreviewRowHeight = value;
			return this;
		}

		public Builder pathwayTitleAreaWidth(int value) {
			this.pathwayTitleAreaWidth = value;
			return this;
		}

		public Builder pathwayTextHeight(int value) {
			this.pathwayTextHeight = value;
			return this;
		}

		public Builder rowHeight(int value) {
			this.rowHeight = value;
			return this;
		}

		public Builder collapsedBranchAreaWidth(int value) {
			this.collapsedBranchAreaWidth = value;
			return this;
		}

		public Builder expandedBranchAreaWidth(int value) {
			this.expandedBranchAreaWidth = value;
			return this;
		}

		public Builder branchNodeToPathNodeVerticalSpacing(int value) {
			this.branchNodeToPathNodeVerticalSpacing = value;
			return this;
		}

		public Builder pathAreaWidth(int value) {
			this.pathAreaWidth = value;
			return this;
		}

		public Builder branchNodeLeftSideSpacing(int value) {
			this.branchNodeLeftSideSpacing = value;
			return this;
		}

		public Builder rectangleNodeWidth(int val) {
			this.rectangleNodeWidth = val;
			return this;
		}

		public Builder rectangleNodeHeight(int val) {
			this.rectangleNodeHeight = val;
			return this;
		}

		public Builder circleNodeRadius(int val) {
			this.circleNodeRadius = val;
			return this;
		}

		public Builder minNodeSpacing(int val) {
			this.minNodeSpacing = val;
			return this;
		}

		public Builder nodeTextHeight(int val) {
			this.nodeTextHeight = val;
			return this;
		}

		public Builder pathStartSpacing(int val) {
			this.pathStartSpacing = val;
			return this;
		}

		public Builder pathEndSpacing(int val) {
			this.pathEndSpacing = val;
			return this;
		}

		public Builder() {
		}

		public Builder(PathSizeConfiguration config) {
			rectangleNodeWidth = config.rectangleNodeWidth;
			rectangleNodeHeight = config.rectangleNodeHeight;
			circleNodeRadius = config.circleNodeRadius;
			minNodeSpacing = config.minNodeSpacing;
			nodeTextHeight = config.nodeTextHeight;
			pathStartSpacing = config.pathStartSpacing;
			pathEndSpacing = config.pathEndSpacing;
			rowHeight = config.rowHeight;
			branchNodeToPathNodeVerticalSpacing = config.branchNodeToPathNodeVerticalSpacing;
			collapsedBranchAreaWidth = config.collapsedBranchAreaWidth;
			expandedBranchAreaWidth = config.collapsedBranchAreaWidth;
			pathAreaWidth = config.pathAreaWidth;
			branchNodeLeftSideSpacing = config.branchNodeLeftSideSpacing;
			pathwayTextHeight = config.pathwayTextHeight;
			pathwayTitleAreaWidth = config.pathwayTitleAreaWidth;
			dataPreviewRowHeight = config.dataPreviewRowHeight;
			branchSummaryNodeBranchAreaSpacing = config.branchSummaryNodeBranchAreaSpacing;
			branchSummaryNodeBranchNodeSpacing = config.branchSummaryNodeBranchNodeSpacing;
			branchSummaryNodeTextHeight = config.branchSummaryNodeTextHeight;
			branchSummaryNodeWidthCollapsed = config.branchSummaryNodeWidthCollapsed;
			branchSummaryNodeWidthExpanded = config.branchSummaryNodeWidthExpanded;
			pathwayBorderWidth = config.pathwayBorderWidth;
			edgeTextHeight = config.edgeTextHeight;
			edgeArrowSize = config.edgeArrowSize;
			edgeArrwoBaseLineSize = config.edgeArrwoBaseLineSize;
			rectangleNodeWidth = config.rectangleNodeWidth;
			branchNodesShowDataPreview = config.branchNodesShowDataPreview;
		}

		public PathSizeConfiguration build() {
			return new PathSizeConfiguration(this);
		}
	}

	protected PathSizeConfiguration(Builder builder) {
		rectangleNodeWidth = builder.rectangleNodeWidth;
		rectangleNodeHeight = builder.rectangleNodeHeight;
		circleNodeRadius = builder.circleNodeRadius;
		minNodeSpacing = builder.minNodeSpacing;
		nodeTextHeight = builder.nodeTextHeight;
		pathStartSpacing = builder.pathStartSpacing;
		pathEndSpacing = builder.pathEndSpacing;
		rowHeight = builder.rowHeight;
		branchNodeToPathNodeVerticalSpacing = builder.branchNodeToPathNodeVerticalSpacing;
		collapsedBranchAreaWidth = builder.collapsedBranchAreaWidth;
		expandedBranchAreaWidth = builder.expandedBranchAreaWidth;
		pathAreaWidth = builder.pathAreaWidth;
		branchNodeLeftSideSpacing = builder.branchNodeLeftSideSpacing;
		pathwayTextHeight = builder.pathwayTextHeight;
		pathwayTitleAreaWidth = builder.pathwayTitleAreaWidth;
		dataPreviewRowHeight = builder.dataPreviewRowHeight;
		branchSummaryNodeBranchAreaSpacing = builder.branchSummaryNodeBranchAreaSpacing;
		branchSummaryNodeBranchNodeSpacing = builder.branchSummaryNodeBranchNodeSpacing;
		branchSummaryNodeTextHeight = builder.branchSummaryNodeTextHeight;
		branchSummaryNodeWidthCollapsed = builder.branchSummaryNodeWidthCollapsed;
		branchSummaryNodeWidthExpanded = builder.branchSummaryNodeWidthExpanded;
		pathwayBorderWidth = builder.pathwayBorderWidth;
		edgeTextHeight = builder.edgeTextHeight;
		edgeArrowSize = builder.edgeArrowSize;
		edgeArrwoBaseLineSize = builder.edgeArrwoBaseLineSize;
		branchNodesShowDataPreview = builder.branchNodesShowDataPreview;
	}

	/**
	 * @return the branchNodesShowDataPreview, see {@link #branchNodesShowDataPreview}
	 */
	public boolean isBranchNodesShowDataPreview() {
		return branchNodesShowDataPreview;
	}

	/**
	 * @return the circleNodeRadius, see {@link #circleNodeRadius}
	 */
	public int getCircleNodeRadius() {
		return circleNodeRadius;
	}

	/**
	 * @return the minNodeSpacing, see {@link #minNodeSpacing}
	 */
	public int getMinNodeSpacing() {
		return minNodeSpacing;
	}

	/**
	 * @return the nodeTextHeight, see {@link #nodeTextHeight}
	 */
	public int getNodeTextHeight() {
		return nodeTextHeight;
	}

	/**
	 * @return the pathEndSpacing, see {@link #pathEndSpacing}
	 */
	public int getPathEndSpacing() {
		return pathEndSpacing;
	}

	/**
	 * @return the pathStartSpacing, see {@link #pathStartSpacing}
	 */
	public int getPathStartSpacing() {
		return pathStartSpacing;
	}

	/**
	 * @return the rectangleNodeHeight, see {@link #rectangleNodeHeight}
	 */
	public int getRectangleNodeHeight() {
		return rectangleNodeHeight;
	}

	/**
	 * @return the rectangleNodeWidth, see {@link #rectangleNodeWidth}
	 */
	public int getRectangleNodeWidth() {
		return rectangleNodeWidth;
	}

	/**
	 * @return the branchAreaWidth, see {@link #collapsedBranchAreaWidth}
	 */
	public int getCollapsedBranchAreaWidth() {
		return collapsedBranchAreaWidth;
	}

	/**
	 * @return the branchAreaWidth, see {@link #expandedBranchAreaWidth}
	 */
	public int getExpandedBranchAreaWidth() {
		return expandedBranchAreaWidth;
	}

	/**
	 * @return the branchNodeLeftSideSpacing, see {@link #branchNodeLeftSideSpacing}
	 */
	public int getBranchNodeLeftSideSpacing() {
		return branchNodeLeftSideSpacing;
	}

	/**
	 * @return the branchNodeToPathNodeVerticalSpacing, see {@link #branchNodeToPathNodeVerticalSpacing}
	 */
	public int getBranchNodeToPathNodeVerticalSpacing() {
		return branchNodeToPathNodeVerticalSpacing;
	}

	/**
	 * @return the pathAreaWidth, see {@link #pathAreaWidth}
	 */
	public int getPathAreaWidth() {
		return pathAreaWidth;
	}

	/**
	 * @return the rowHeight, see {@link #rowHeight}
	 */
	public int getRowHeight() {
		return rowHeight;
	}

	/**
	 * @return the pathwayTextHeight, see {@link #pathwayTextHeight}
	 */
	public int getPathwayTextHeight() {
		return pathwayTextHeight;
	}

	/**
	 * @return the pathwayTitleAreaWidth, see {@link #pathwayTitleAreaWidth}
	 */
	public int getPathwayTitleAreaWidth() {
		return pathwayTitleAreaWidth;
	}

	/**
	 * @return the dataPreviewRowHeight, see {@link #dataPreviewRowHeight}
	 */
	public int getDataPreviewRowHeight() {
		return dataPreviewRowHeight;
	}

	/**
	 * @return the branchSummaryNodeBranchAreaSpacing, see {@link #branchSummaryNodeBranchAreaSpacing}
	 */
	public int getBranchSummaryNodeBranchAreaSpacing() {
		return branchSummaryNodeBranchAreaSpacing;
	}

	/**
	 * @return the branchSummaryNodeBranchNodeSpacing, see {@link #branchSummaryNodeBranchNodeSpacing}
	 */
	public int getBranchSummaryNodeBranchNodeSpacing() {
		return branchSummaryNodeBranchNodeSpacing;
	}

	/**
	 * @return the branchSummaryNodeTextHeight, see {@link #branchSummaryNodeTextHeight}
	 */
	public int getBranchSummaryNodeTextHeight() {
		return branchSummaryNodeTextHeight;
	}

	/**
	 * @return the branchSummaryNodeWidth, see {@link #branchSummaryNodeWidthCollapsed}
	 */
	public int getBranchSummaryNodeWidthCollapsed() {
		return branchSummaryNodeWidthCollapsed;
	}

	/**
	 * @return the branchSummaryNodeWidth, see {@link #branchSummaryNodeWidthExpanded}
	 */
	public int getBranchSummaryNodeWidthExpanded() {
		return branchSummaryNodeWidthExpanded;
	}

	/**
	 * @return the pathwayBorderWidth, see {@link #pathwayBorderWidth}
	 */
	public int getPathwayBorderWidth() {
		return pathwayBorderWidth;
	}

	/**
	 * @return the edgeArrowSize, see {@link #edgeArrowSize}
	 */
	public int getEdgeArrowSize() {
		return edgeArrowSize;
	}

	/**
	 * @return the edgeArrwoBaseLineSize, see {@link #edgeArrwoBaseLineSize}
	 */
	public int getEdgeArrwoBaseLineSize() {
		return edgeArrwoBaseLineSize;
	}

	/**
	 * @return the edgeTextHeight, see {@link #edgeTextHeight}
	 */
	public int getEdgeTextHeight() {
		return edgeTextHeight;
	}
}
