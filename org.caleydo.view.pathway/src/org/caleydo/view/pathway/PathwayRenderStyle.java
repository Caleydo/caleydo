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
package org.caleydo.view.pathway;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class PathwayRenderStyle
	extends GeneralRenderStyle {

	public static final int neighborhoodNodeColorArraysize = 4;

	public static final int ENZYME_NODE_WIDTH = 45;
	public static final int ENZYME_NODE_HEIGHT = 17;
	public static final int COMPOUND_NODE_WIDTH = 8;
	public static final int COMPOUND_NODE_HEIGHT = 8;
	
	public static final float Z_OFFSET = 0.01f;

	public static final float[] ENZYME_NODE_COLOR = new float[] { 0.3f, 0.3f, 0.3f, 1 };
	public static final float[] COMPOUND_NODE_COLOR = new float[] { 0.3f, 0.3f, 0.3f, 1 };
	public static final float[] PATHWAY_NODE_COLOR = new float[] { 0.7f, 0.7f, 1f, 1 };

	public static final float[] RELATION_EDGE_COLOR = new float[] { 0, 0, 1, 1 };
	public static final float[] REACTION_EDGE_COLOR = new float[] { 0, 0, 1, 1 };
	public static final float[] MAPLINK_EDGE_COLOR = new float[] { 1, 0, 1, 1 };

	public static final float[] PATH_COLOR = new float[] { 1, 1, 1, 0.3f };

	public static final float[] STD_DEV_COLOR = new float[] { 49f / 255f, 163f / 255, 84f / 255, 1f };
	public static final float STD_DEV_BAR_WIDTH = 0.028f;

	/**
	 * The color of the neighborhood node with the distance to the clicked node
	 * of [1..neighborhoodNodeColorArraysize]
	 */
	private float[][] neighborhoodNodeColorArray;

	public enum NodeShape {
		RECTANGULAR,
		ROUND,
		ROUNDRECTANGULAR
	};

	public enum EdgeLineStyle {
		NORMAL,
		DASHED
	};

	public enum EdgeArrowHeadStyle {
		FILLED,
		EMPTY
	};

	protected NodeShape enzymeNodeShape;

	protected NodeShape compoundNodeShape;

	protected NodeShape pathwayNodeShape;

	protected EdgeLineStyle relationEdgeLineStyle;

	protected EdgeLineStyle reactionEdgeLineStyle;

	protected EdgeLineStyle maplinkEdgeLineStyle;

	protected EdgeArrowHeadStyle relationEdgeArrowHeadStyle;

	protected EdgeArrowHeadStyle reactionEdgeArrowHeadStyle;

	protected EdgeArrowHeadStyle mapEdgeArrowHeadStyle;

	public PathwayRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);
		init();
	}

	/**
	 * Constructor. Initializes the pathway render style. TODO: load pathway
	 * style from XML file.
	 */
	private void init() {
		// TODO: use float[] constants for colors

		enzymeNodeShape = NodeShape.RECTANGULAR;
		compoundNodeShape = NodeShape.ROUND;
		pathwayNodeShape = NodeShape.ROUNDRECTANGULAR;

		neighborhoodNodeColorArray = new float[neighborhoodNodeColorArraysize][4];
		neighborhoodNodeColorArray[0] = SelectionType.MOUSE_OVER.getColor();
		neighborhoodNodeColorArray[1] = new float[] { 0.2f, 0.2f, 1.0f };
		neighborhoodNodeColorArray[2] = new float[] { 0.5f, 0.5f, 1.0f };
		neighborhoodNodeColorArray[3] = new float[] { 0.8f, 0.8f, 1.0f };

		relationEdgeLineStyle = EdgeLineStyle.NORMAL;
		reactionEdgeLineStyle = EdgeLineStyle.NORMAL;
		maplinkEdgeLineStyle = EdgeLineStyle.DASHED;

		relationEdgeArrowHeadStyle = EdgeArrowHeadStyle.FILLED;
		reactionEdgeArrowHeadStyle = EdgeArrowHeadStyle.FILLED;
		mapEdgeArrowHeadStyle = EdgeArrowHeadStyle.EMPTY;
	}

	public NodeShape getCompoundNodeShape() {
		return compoundNodeShape;
	}

	public NodeShape getEnzymeNodeShape() {
		return enzymeNodeShape;
	}

	public EdgeArrowHeadStyle getMaplinkEdgeArrowHeadStyle() {
		return mapEdgeArrowHeadStyle;
	}

	public EdgeLineStyle getMaplinkEdgeLineStyle() {
		return maplinkEdgeLineStyle;
	}

	public void setMaplinkEdgeLineStyle(EdgeLineStyle maplinkEdgeLineStyle) {
		this.maplinkEdgeLineStyle = maplinkEdgeLineStyle;
	}

	public NodeShape getPathwayNodeShape() {
		return pathwayNodeShape;
	}

	public EdgeArrowHeadStyle getReactionEdgeArrowHeadStyle() {
		return reactionEdgeArrowHeadStyle;
	}

	public void setReactionEdgeArrowHeadStyle(EdgeArrowHeadStyle reactionEdgeArrowHeadStyle) {
		this.reactionEdgeArrowHeadStyle = reactionEdgeArrowHeadStyle;
	}

	public EdgeLineStyle getReactionEdgeLineStyle() {
		return reactionEdgeLineStyle;
	}

	public EdgeArrowHeadStyle getRelationEdgeArrowHeadStyle() {
		return relationEdgeArrowHeadStyle;
	}

	public EdgeLineStyle getRelationEdgeLineStyle() {
		return relationEdgeLineStyle;
	}

	/**
	 * @see org.caleydo.core.view.opengl.canvas.pathway.PathwayRenderStyle#neighborhoodNodeColorArraysize
	 * @see org.caleydo.core.view.opengl.canvas.pathway.PathwayRenderStyle#highlightedNodeColor
	 * @param depth [0..1.. (neighborhoodNodeColorArraysize-1) ] ; if depth ==0
	 *            highlightedNodeColor is returned
	 * @return
	 */
	public float[] getNeighborhoodNodeColorByDepth(final int depth) {
		if (depth >= neighborhoodNodeColorArraysize)
			throw new IllegalStateException("getNeighborhoodNodeColorByDepth(" + depth + ") exceed range!");
		return this.neighborhoodNodeColorArray[depth];
	}
}