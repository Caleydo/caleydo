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

/**
 * Class that determines the size of several elements within a path in pixels.
 *
 * @author Christian Partl
 *
 */
public class PathSizeConfiguration {

	public static final PathSizeConfiguration DEFAULT = new Builder().rectangleNodeWidth(70).rectangleNodeHeight(20)
			.circleNodeRadius(10).minNodeSpacing(50).nodeTextHeight(16).pathStartSpacing(20).pathEndSpacing(20).build();

	public static final PathSizeConfiguration COMPACT = new Builder().rectangleNodeWidth(40).rectangleNodeHeight(14)
			.circleNodeRadius(7).minNodeSpacing(40).nodeTextHeight(12).pathStartSpacing(14).pathEndSpacing(14).build();

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

	public static class Builder {
		protected int rectangleNodeWidth;
		protected int rectangleNodeHeight;
		protected int circleNodeRadius;
		protected int minNodeSpacing;
		protected int nodeTextHeight;
		protected int pathStartSpacing;
		protected int pathEndSpacing;

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

		public Builder(Builder builder) {
			rectangleNodeWidth = builder.rectangleNodeWidth;
			rectangleNodeHeight = builder.rectangleNodeHeight;
			circleNodeRadius = builder.circleNodeRadius;
			minNodeSpacing = builder.minNodeSpacing;
			nodeTextHeight = builder.nodeTextHeight;
			pathStartSpacing = builder.pathStartSpacing;
			pathEndSpacing = builder.pathEndSpacing;
		}

		public Builder(PathSizeConfiguration config) {
			rectangleNodeWidth = config.rectangleNodeWidth;
			rectangleNodeHeight = config.rectangleNodeHeight;
			circleNodeRadius = config.circleNodeRadius;
			minNodeSpacing = config.minNodeSpacing;
			nodeTextHeight = config.nodeTextHeight;
			pathStartSpacing = config.pathStartSpacing;
			pathEndSpacing = config.pathEndSpacing;
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
}
