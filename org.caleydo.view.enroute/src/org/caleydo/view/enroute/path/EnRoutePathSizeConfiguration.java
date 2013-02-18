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
package org.caleydo.view.enroute.path;

import org.caleydo.view.enroute.GLEnRoutePathway;

/**
 * Configuration for {@link EnRoutePathRenderer}.
 *
 * @author Christian Partl
 *
 */
public class EnRoutePathSizeConfiguration extends PathSizeConfiguration {

	public static final EnRoutePathSizeConfiguration ENROUTE_DEFAULT = new Builder(PathSizeConfiguration.DEFAULT)
			.rowHeight(60).branchNodeToPathNodeVerticalSpacing(20).branchAreaWidth(100).pathAreaWidth(150)
			.branchNodeLeftSideSpacing(8).build();
	public static final EnRoutePathSizeConfiguration ENROUTE_COMPACT = new Builder(PathSizeConfiguration.DEFAULT)
			.rowHeight(40).branchNodeToPathNodeVerticalSpacing(15).branchAreaWidth(65).pathAreaWidth(100)
			.branchNodeLeftSideSpacing(6).build();

	/**
	 * Height of data rows that map to nodes in {@link GLEnRoutePathway}.
	 */
	protected final int rowHeight;
	/**
	 * Spacing between branch and path nodes.
	 */
	protected final int branchNodeToPathNodeVerticalSpacing;
	/**
	 * Width of the area for branch nodes.
	 */
	protected final int branchAreaWidth;
	/**
	 * Width of the path area.
	 */
	protected final int pathAreaWidth;
	/**
	 * Side spacing of branch nodes.
	 */
	protected final int branchNodeLeftSideSpacing;

	public static class Builder extends PathSizeConfiguration.Builder {
		protected int rowHeight;
		protected int branchNodeToPathNodeVerticalSpacing;
		protected int branchAreaWidth;
		protected int pathAreaWidth;
		protected int branchNodeLeftSideSpacing;

		public Builder rowHeight(int value) {
			this.rowHeight = value;
			return this;
		}

		public Builder branchAreaWidth(int value) {
			this.branchAreaWidth = value;
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

		public Builder() {

		}

		public Builder(PathSizeConfiguration config) {
			super(config);
		}

		@Override
		public EnRoutePathSizeConfiguration build() {
			return new EnRoutePathSizeConfiguration(this);
		}
	}

	private EnRoutePathSizeConfiguration(Builder builder) {
		super(builder);
		this.rowHeight = builder.rowHeight;
		this.branchNodeToPathNodeVerticalSpacing = builder.branchNodeToPathNodeVerticalSpacing;
		this.branchAreaWidth = builder.branchAreaWidth;
		this.pathAreaWidth = builder.pathAreaWidth;
		this.branchNodeLeftSideSpacing = builder.branchNodeLeftSideSpacing;
	}

	/**
	 * @return the branchAreaWidth, see {@link #branchAreaWidth}
	 */
	public int getBranchAreaWidth() {
		return branchAreaWidth;
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

}
