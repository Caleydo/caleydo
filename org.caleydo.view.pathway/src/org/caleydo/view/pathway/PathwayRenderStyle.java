package org.caleydo.view.pathway;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class PathwayRenderStyle extends GeneralRenderStyle {
	public static final int neighborhoodNodeColorArraysize = 4;

	public static final float SCALING_FACTOR_X = 0.0025f;
	public static final float SCALING_FACTOR_Y = 0.0025f;

	public static final float ENZYME_NODE_WIDTH = 45 * SCALING_FACTOR_X / 2.0f;
	public static final float ENZYME_NODE_HEIGHT = 17 * SCALING_FACTOR_X / 2.0f;
	public static final float COMPOUND_NODE_WIDTH = 8 * SCALING_FACTOR_X / 2.0f;
	public static final float COMPOUND_NODE_HEIGHT = 8 * SCALING_FACTOR_X / 2.0f;

	public static final float[] ENZYME_NODE_COLOR = new float[] { 0.3f, 0.3f,
			0.3f, 1 };
	public static final float[] COMPOUND_NODE_COLOR = new float[] { 0.3f, 0.3f,
			0.3f, 1 };
	public static final float[] PATHWAY_NODE_COLOR = new float[] { 0.7f, 0.7f,
			1f, 1 };

	public static final float[] RELATION_EDGE_COLOR = new float[] { 0, 0, 1, 1 };
	public static final float[] REACTION_EDGE_COLOR = new float[] { 0, 0, 1, 1 };
	public static final float[] MAPLINK_EDGE_COLOR = new float[] { 1, 0, 1, 1 };

	/**
	 * The color of the neighborhood node with the distance to the clicked node
	 * of [1..neighborhoodNodeColorArraysize]
	 */
	private float[][] neighborhoodNodeColorArray;

	public enum NodeShape {
		RECTANGULAR, ROUND, ROUNDRECTANGULAR
	};

	public enum EdgeLineStyle {
		NORMAL, DASHED
	};

	public enum EdgeArrowHeadStyle {
		FILLED, EMPTY
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

	public PathwayRenderStyle(IViewFrustum viewFrustum) {
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

	public void setReactionEdgeArrowHeadStyle(
			EdgeArrowHeadStyle reactionEdgeArrowHeadStyle) {
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
	 * @param depth
	 *            [0..1.. (neighborhoodNodeColorArraysize-1) ] ; if depth ==0
	 *            highlightedNodeColor is returned
	 * @return
	 */
	public float[] getNeighborhoodNodeColorByDepth(final int depth) {
		if (depth >= neighborhoodNodeColorArraysize)
			throw new IllegalStateException("getNeighborhoodNodeColorByDepth("
					+ depth + ") exceed range!");
		return this.neighborhoodNodeColorArray[depth];
	}
}