package cerberus.data.view.rep.pathway.renderstyle;

import java.awt.Color;

import cerberus.data.ARenderStyle;
import cerberus.util.exception.CerberusRuntimeException;

public class PathwayRenderStyle 
extends ARenderStyle {
	
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
	
	// Colors for pathway elements.
	// Separate colors are provided if gene mapping is turned on.
	protected Color enzymeNodeColor;
	protected Color enzymeNodeColorGeneMapped;
	protected Color compoundNodeColor;
	protected Color compoundNodeColorGeneMapped;
	protected Color pathwayNodeColor;
	protected Color pathwayNodeColorGeneMapped;
	
	protected Color highlightedNodeColor;
	
	/**
	 * The color of the neighborhood node with the distance 
	 * to the clicked node of [1..neighborhoodNodeColorArraysize]
	 */
	protected Color [] neighborhoodNodeColorArray;
	
	public static final int neighborhoodNodeColorArraysize = 4;
	
//	/**
//	 * The color of the neighborhood node with the distance 
//	 * to the clicked node of 1
//	 */
//	protected Color neighborhoodNodeColor_1;
//	
//	/**
//	 * The color of the neighborhood node with the distance 
//	 * to the clicked node of 2
//	 */
//	protected Color neighborhoodNodeColor_2;
//
//	/**
//	 * The color of the neighborhood node with the distance 
//	 * to the clicked node of 3
//	 */
//	protected Color neighborhoodNodeColor_3;
	
	protected EdgeLineStyle relationEdgeLineStyle;
	protected EdgeLineStyle reactionEdgeLineStyle;
	protected EdgeLineStyle maplinkEdgeLineStyle;
	
	protected Color relationEdgeColor;
	protected Color reactionEdgeColor;
	protected Color maplinkEdgeColor;
	
	protected EdgeArrowHeadStyle relationEdgeArrowHeadStyle;
	protected EdgeArrowHeadStyle reactionEdgeArrowHeadStyle;
	protected EdgeArrowHeadStyle mapEdgeArrowHeadStyle;
	
	protected float fEnzymeNodeWidth;
	protected float fEnzymeNodeHeight;
	protected float fCompoundNodeWidth;
	protected float fCompoundNodeHeight;
	protected float fPathwayNodeWidth;
	protected float fPathwayNodeHeight;
	
	private static final float SCALING_FACTOR_X = 0.0025f;
	private static final float SCALING_FACTOR_Y = 0.0025f;

	/**
	 * Constructor.
	 * Initializes the pathway render style.
	 * 
	 * TODO: load pathway style from XML file.
	 */
	public PathwayRenderStyle() {
		
		enzymeNodeShape 	= NodeShape.RECTANGULAR;
		compoundNodeShape 	= NodeShape.ROUND;
		pathwayNodeShape 	= NodeShape.ROUNDRECTANGULAR;
		
		enzymeNodeColor 			= new Color(0.53f, 0.81f, 1.0f);
		enzymeNodeColorGeneMapped 	= Color.GRAY;
		compoundNodeColor 			= Color.GREEN;
		compoundNodeColorGeneMapped = Color.DARK_GRAY;
		pathwayNodeColor 			= new Color(0.51f, 0.44f, 1.0f);
		pathwayNodeColorGeneMapped 	= Color.LIGHT_GRAY;
		highlightedNodeColor 		= new Color(0.0f, 0.0f, 1.0f);
		//highlightedNodeColor 		= new Color(1.0f, 0.0f, 0.0f);
		neighborhoodNodeColorArray 	= new Color [neighborhoodNodeColorArraysize];
		
		/* current highlighted */
		neighborhoodNodeColorArray[0] = new Color(1.0f, 0.0f, 0.0f);  //highlightedNodeColor;
		
		neighborhoodNodeColorArray[1] = new Color(1.0f, 0.39f, 0.0f);
		neighborhoodNodeColorArray[2] = new Color(1.0f, 0.67f, 0.0f);
		neighborhoodNodeColorArray[3] = new Color(0.95f, 1.0f, 0.0f);
		
//		neighborhoodNodeColorArray[1] = new Color(0.2f, 0.2f, 1.0f);
//		neighborhoodNodeColorArray[2] = new Color(0.5f, 0.5f, 1.0f);
//		neighborhoodNodeColorArray[3] = new Color(0.8f, 0.8f, 1.0f);
		
//		neighborhoodNodeColor_1 = new Color(1.0f, 0.5f, 0.0f);
//		neighborhoodNodeColor_2 = new Color(1.0f, 1.0f, 0.0f);
//		neighborhoodNodeColor_3 = new Color(1.0f, 1.0f, 0.5f);
		
		relationEdgeLineStyle 	= EdgeLineStyle.NORMAL;
		reactionEdgeLineStyle 	= EdgeLineStyle.NORMAL;
		maplinkEdgeLineStyle 	= EdgeLineStyle.DASHED;
		
		relationEdgeArrowHeadStyle 	= EdgeArrowHeadStyle.FILLED;
		reactionEdgeArrowHeadStyle 	= EdgeArrowHeadStyle.FILLED;
		mapEdgeArrowHeadStyle 		= EdgeArrowHeadStyle.EMPTY;
		
		relationEdgeColor 	= Color.GREEN;
		reactionEdgeColor 	= Color.BLUE;
		maplinkEdgeColor 	= Color.MAGENTA;
		
		fEnzymeNodeWidth 	= 45 * SCALING_FACTOR_X / 2.0f;
		fEnzymeNodeHeight 	= 17 * SCALING_FACTOR_Y / 2.0f;
		fCompoundNodeWidth 	= 8 * SCALING_FACTOR_X / 2.0f;
		fCompoundNodeHeight = 8 * SCALING_FACTOR_Y / 2.0f;
		fPathwayNodeWidth 	= 70 * SCALING_FACTOR_X / 2.0f;
		fPathwayNodeHeight 	= 27 * SCALING_FACTOR_Y / 2.0f;
	}

	public Color getCompoundNodeColor(boolean bGeneMappingEnabled) {
	
		if (bGeneMappingEnabled)
			return compoundNodeColorGeneMapped;
		
		return compoundNodeColor;
	}

	public void setCompoundNodeColor(Color compoundNodeColor) {
	
		this.compoundNodeColor = compoundNodeColor;
	}

	public NodeShape getCompoundNodeShape() {
	
		return compoundNodeShape;
	}

	public void setCompoundNodeShape(NodeShape compoundNodeShape) {
	
		this.compoundNodeShape = compoundNodeShape;
	}

	public Color getEnzymeNodeColor(boolean bGeneMappingEnabled) {
	
		if (bGeneMappingEnabled)
			return enzymeNodeColorGeneMapped;
		
		return enzymeNodeColor;
	}

	public void setEnzymeNodeColor(Color enzymeNodeColor) {
	
		this.enzymeNodeColor = enzymeNodeColor;
	}

	public NodeShape getEnzymeNodeShape() {
	
		return enzymeNodeShape;
	}

	public void setEnzymeNodeShape(NodeShape enzymeNodeShape) {
	
		this.enzymeNodeShape = enzymeNodeShape;
	}

	public EdgeArrowHeadStyle getMaplinkEdgeArrowHeadStyle() {
	
		return mapEdgeArrowHeadStyle;
	}

	public void setMaplinkEdgeArrowHeadStyle(
			EdgeArrowHeadStyle maplinkEdgeArrowHeadStyle) {
	
		this.mapEdgeArrowHeadStyle = maplinkEdgeArrowHeadStyle;
	}

	public Color getMaplinkEdgeColor() {
	
		return maplinkEdgeColor;
	}

	public void setMaplinkEdgeColor(Color maplinkEdgeColor) {
	
		this.maplinkEdgeColor = maplinkEdgeColor;
	}

	public EdgeLineStyle getMaplinkEdgeLineStyle() {
	
		return maplinkEdgeLineStyle;
	}

	public void setMaplinkEdgeLineStyle(EdgeLineStyle maplinkEdgeLineStyle) {
	
		this.maplinkEdgeLineStyle = maplinkEdgeLineStyle;
	}

	public Color getPathwayNodeColor(boolean bGeneMappingEnabled) {
	
		if (bGeneMappingEnabled)
			return pathwayNodeColorGeneMapped;
		
		return pathwayNodeColor;
	}

	public void setPathwayNodeColor(Color pathwayNodeColor) {
	
		this.pathwayNodeColor = pathwayNodeColor;
	}

	public NodeShape getPathwayNodeShape() {
	
		return pathwayNodeShape;
	}

	public void setPathwayNodeShape(NodeShape pathwayNodeShape) {
	
		this.pathwayNodeShape = pathwayNodeShape;
	}

	public EdgeArrowHeadStyle getReactionEdgeArrowHeadStyle() {
	
		return reactionEdgeArrowHeadStyle;
	}

	public void setReactionEdgeArrowHeadStyle(
			EdgeArrowHeadStyle reactionEdgeArrowHeadStyle) {
	
		this.reactionEdgeArrowHeadStyle = reactionEdgeArrowHeadStyle;
	}

	public Color getReactionEdgeColor() {
	
		return reactionEdgeColor;
	}

	public void setReactionEdgeColor(Color reactionEdgeColor) {
	
		this.reactionEdgeColor = reactionEdgeColor;
	}

	public EdgeLineStyle getReactionEdgeLineStyle() {
	
		return reactionEdgeLineStyle;
	}

	public void setReactionEdgeLineStyle(EdgeLineStyle reactionEdgeLineStyle) {
	
		this.reactionEdgeLineStyle = reactionEdgeLineStyle;
	}

	public EdgeArrowHeadStyle getRelationEdgeArrowHeadStyle() {
	
		return relationEdgeArrowHeadStyle;
	}

	public void setRelationEdgeArrowHeadStyle(
			EdgeArrowHeadStyle relationEdgeArrowHeadStyle) {
	
		this.relationEdgeArrowHeadStyle = relationEdgeArrowHeadStyle;
	}

	public Color getRelationEdgeColor() {
	
		return relationEdgeColor;
	}

	public void setRelationEdgeColor(Color relationEdgeColor) {
	
		this.relationEdgeColor = relationEdgeColor;
	}

	public EdgeLineStyle getRelationEdgeLineStyle() {
	
		return relationEdgeLineStyle;
	}

	public void setRelationEdgeLineStyle(EdgeLineStyle relationEdgeLineStyle) {
	
		this.relationEdgeLineStyle = relationEdgeLineStyle;
	}

	
	public float getCompoundNodeHeight() {
	
		return fCompoundNodeHeight;
	}

	
	public void setCompoundNodeHeight(float compoundNodeHeight) {
	
		fCompoundNodeHeight = compoundNodeHeight;
	}

	
	public float getCompoundNodeWidth() {
	
		return fCompoundNodeWidth;
	}

	
	public void setCompoundNodeWidth(float compoundNodeWidth) {
	
		fCompoundNodeWidth = compoundNodeWidth;
	}

	
	public float getEnzymeNodeWidth() {
	
		return fEnzymeNodeWidth;
	}

	
	public void setEnzymeNodeWidth(float enzymeNodeWidth) {
	
		fEnzymeNodeWidth = enzymeNodeWidth;
	}

	
	public float getEnzymeNodeHeight() {
	
		return fEnzymeNodeHeight;
	}

	
	public void setEnzymeNodeHeight(float enzymeNodeHeight) {
	
		fEnzymeNodeHeight = enzymeNodeHeight;
	}

	
	public float getPathwayNodeHeight() {
	
		return fPathwayNodeHeight;
	}

	
	public void setPathwayNodeHeight(float pathwayNodeHeight) {
	
		fPathwayNodeHeight = pathwayNodeHeight;
	}

	
	public float getPathwayNodeWidth() {
	
		return fPathwayNodeWidth;
	}

	
	public void setPathwayNodeWidth(float pathwayNodeWidth) {
	
		fPathwayNodeWidth = pathwayNodeWidth;
	}

	public Color getHighlightedNodeColor() {
		
		return highlightedNodeColor;
	}
	
	/**
	 * 
	 * @see cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle#neighborhoodNodeColorArraysize
	 * @see cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle#highlightedNodeColor
	 * 
	 * @param depth [0..1.. (neighborhoodNodeColorArraysize-1) ] ; if depth ==0 highlightedNodeColor is returened
	 * @return
	 */
	public Color getNeighborhoodNodeColorByDepth( final int depth ) {
		if ( depth >= neighborhoodNodeColorArraysize) {
			throw new CerberusRuntimeException("getNeighborhoodNodeColorByDepth(" + depth + ") exceed range!");
		}
		return this.neighborhoodNodeColorArray[depth];
	}
	
//	public Color getNeighborhoodNodeColor_1() {
//	
//		return neighborhoodNodeColor_1;
//	}
//
//	
//	public Color getNeighborhoodNodeColor_2() {
//	
//		return neighborhoodNodeColor_2;
//	}
//
//	
//	public Color getNeighborhoodNodeColor_3() {
//	
//		return neighborhoodNodeColor_3;
//	}
}
