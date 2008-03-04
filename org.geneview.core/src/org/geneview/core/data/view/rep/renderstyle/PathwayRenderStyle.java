package org.geneview.core.data.view.rep.renderstyle;

import gleem.linalg.Vec3f;

import org.geneview.core.data.GeneralRenderStyle;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.util.exception.GeneViewRuntimeException;

public class PathwayRenderStyle 
extends GeneralRenderStyle {
	
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
	protected Vec3f enzymeNodeColor;
	protected Vec3f enzymeNodeColorGeneMapped;
	protected Vec3f compoundNodeColor;
	protected Vec3f compoundNodeColorGeneMapped;
	protected Vec3f pathwayNodeColor;
	protected Vec3f pathwayNodeColorGeneMapped;
	
	protected Vec3f highlightedNodeColor;
	
	/**
	 * The color of the neighborhood node with the distance 
	 * to the clicked node of [1..neighborhoodNodeColorArraysize]
	 */
	protected Vec3f [] neighborhoodNodeColorArray;
	
	protected Vec3f layerConnectionLinesColor;
	
	public static final int neighborhoodNodeColorArraysize = 4;
	
	protected EdgeLineStyle relationEdgeLineStyle;
	protected EdgeLineStyle reactionEdgeLineStyle;
	protected EdgeLineStyle maplinkEdgeLineStyle;
	
	protected Vec3f relationEdgeColor;
	protected Vec3f reactionEdgeColor;
	protected Vec3f maplinkEdgeColor;
	
	protected EdgeArrowHeadStyle relationEdgeArrowHeadStyle;
	protected EdgeArrowHeadStyle reactionEdgeArrowHeadStyle;
	protected EdgeArrowHeadStyle mapEdgeArrowHeadStyle;
	
	protected float fEnzymeNodeWidth;
	protected float fEnzymeNodeHeight;
	protected float fCompoundNodeWidth;
	protected float fCompoundNodeHeight;
	protected float fEnzymeNodeWidthGL;
	protected float fEnzymeNodeHeightGL;
	protected float fCompoundNodeWidthGL;
	protected float fCompoundNodeHeightGL;
	
	private static final float SCALING_FACTOR_X = 0.0025f;
	private static final float SCALING_FACTOR_Y = 0.0025f;

	public PathwayRenderStyle(IViewFrustum viewFrustum)
	{
		
		super(viewFrustum);
		init();
	}
	
	public PathwayRenderStyle()
	{
		init();
	}
	
	/**
	 * Constructor.
	 * Initializes the pathway render style.
	 * 
	 * TODO: load pathway style from XML file.
	 */
	private void init()
	{
			
		
		enzymeNodeShape 	= NodeShape.RECTANGULAR;
		compoundNodeShape 	= NodeShape.ROUND;
		pathwayNodeShape 	= NodeShape.ROUNDRECTANGULAR;
		
		enzymeNodeColor 			= new Vec3f(0.7f, 0.9f, 1.0f);
		enzymeNodeColorGeneMapped 	= new Vec3f(0.8f, 0.8f, 0.8f);
		compoundNodeColor 			= new Vec3f(1.0f, 0.65f, 0.0f); // DARK YELLOW
		compoundNodeColorGeneMapped = new Vec3f(0.3f,0.3f,0.3f);
		pathwayNodeColor 			= new Vec3f(0.5f, 1f, 0.5f);
		pathwayNodeColorGeneMapped 	= new Vec3f(0.8f,0.8f,0.8f); // light gray
		highlightedNodeColor 		= new Vec3f(1,0,0);
		neighborhoodNodeColorArray 	= new Vec3f [neighborhoodNodeColorArraysize];
		
		/* currently highlighted */
		neighborhoodNodeColorArray[0] = highlightedNodeColor;
		
//		neighborhoodNodeColorArray[1] = new Color(1.0f, 0.39f, 0.0f);
//		neighborhoodNodeColorArray[2] = new Color(1.0f, 0.67f, 0.0f);
//		neighborhoodNodeColorArray[3] = new Color(0.95f, 1.0f, 0.0f);
		
		neighborhoodNodeColorArray[1] = new Vec3f(0.2f, 0.2f, 1.0f);
		neighborhoodNodeColorArray[2] = new Vec3f(0.5f, 0.5f, 1.0f);
		neighborhoodNodeColorArray[3] = new Vec3f(0.8f, 0.8f, 1.0f);

		layerConnectionLinesColor = highlightedNodeColor;
		
		relationEdgeLineStyle 	= EdgeLineStyle.NORMAL;
		reactionEdgeLineStyle 	= EdgeLineStyle.NORMAL;
		maplinkEdgeLineStyle 	= EdgeLineStyle.DASHED;
		
		relationEdgeArrowHeadStyle 	= EdgeArrowHeadStyle.FILLED;
		reactionEdgeArrowHeadStyle 	= EdgeArrowHeadStyle.FILLED;
		mapEdgeArrowHeadStyle 		= EdgeArrowHeadStyle.EMPTY;
		
		relationEdgeColor 	= new Vec3f(0,0,1); // blue
		reactionEdgeColor 	= new Vec3f(0,0,1); // blue
		maplinkEdgeColor 	= new Vec3f(1,0,1); // magenta
		
		fEnzymeNodeWidth 	= 45;
		fEnzymeNodeHeight 	= 17;
		fCompoundNodeWidth 	= 8;
		fCompoundNodeHeight = 8;
		
		fEnzymeNodeWidthGL 	= fEnzymeNodeWidth * SCALING_FACTOR_X / 2.0f;
		fEnzymeNodeHeightGL 	= fEnzymeNodeHeight * SCALING_FACTOR_Y / 2.0f;
		fCompoundNodeWidthGL 	= fCompoundNodeWidth * SCALING_FACTOR_X / 2.0f;
		fCompoundNodeHeightGL = fCompoundNodeHeight * SCALING_FACTOR_Y / 2.0f;
	}

	public Vec3f getCompoundNodeColor(boolean bGeneMappingEnabled) {
	
		if (bGeneMappingEnabled)
			return compoundNodeColorGeneMapped;
		
		return compoundNodeColor;
	}

	public void setCompoundNodeColor(Vec3f compoundNodeColor) {
	
		this.compoundNodeColor = compoundNodeColor;
	}

	public NodeShape getCompoundNodeShape() {
	
		return compoundNodeShape;
	}

	public void setCompoundNodeShape(NodeShape compoundNodeShape) {
	
		this.compoundNodeShape = compoundNodeShape;
	}

	public Vec3f getEnzymeNodeColor(boolean bGeneMappingEnabled) {
	
		if (bGeneMappingEnabled)
			return enzymeNodeColorGeneMapped;
		
		return enzymeNodeColor;
	}

	public void setEnzymeNodeColor(Vec3f enzymeNodeColor) {
	
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

	public Vec3f getMaplinkEdgeColor() {
	
		return maplinkEdgeColor;
	}

	public void setMaplinkEdgeColor(Vec3f maplinkEdgeColor) {
	
		this.maplinkEdgeColor = maplinkEdgeColor;
	}

	public EdgeLineStyle getMaplinkEdgeLineStyle() {
	
		return maplinkEdgeLineStyle;
	}

	public void setMaplinkEdgeLineStyle(EdgeLineStyle maplinkEdgeLineStyle) {
	
		this.maplinkEdgeLineStyle = maplinkEdgeLineStyle;
	}

	public Vec3f getPathwayNodeColor(boolean bGeneMappingEnabled) {
	
		if (bGeneMappingEnabled)
			return pathwayNodeColorGeneMapped;
		
		return pathwayNodeColor;
	}

	public void setPathwayNodeColor(Vec3f pathwayNodeColor) {
	
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

	public Vec3f getReactionEdgeColor() {
	
		return reactionEdgeColor;
	}

	public void setReactionEdgeColor(Vec3f reactionEdgeColor) {
	
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

	public Vec3f getRelationEdgeColor() {
	
		return relationEdgeColor;
	}

	public void setRelationEdgeColor(Vec3f relationEdgeColor) {
	
		this.relationEdgeColor = relationEdgeColor;
	}

	public EdgeLineStyle getRelationEdgeLineStyle() {
	
		return relationEdgeLineStyle;
	}

	public void setRelationEdgeLineStyle(EdgeLineStyle relationEdgeLineStyle) {
	
		this.relationEdgeLineStyle = relationEdgeLineStyle;
	}

	public float getCompoundNodeHeight(boolean bForGL) {
	
		if (bForGL)
			return fCompoundNodeHeightGL;
		
		return fCompoundNodeHeight;
	}

	public void setCompoundNodeHeight(float compoundNodeHeight) {
	
		fCompoundNodeHeight = compoundNodeHeight;
	}
	
	public float getCompoundNodeWidth(boolean bForGL) {
	
		if (bForGL)
			return fCompoundNodeWidthGL;
		
		return fCompoundNodeWidth;
	}

	public void setCompoundNodeWidth(float compoundNodeWidth) {
	
		fCompoundNodeWidth = compoundNodeWidth;
	}

	public float getEnzymeNodeWidth(boolean bForGL) {
	
		if (bForGL)
			return fEnzymeNodeWidthGL;
		
		return fEnzymeNodeWidth;
	}

	public void setEnzymeNodeWidth(float enzymeNodeWidth) {
	
		fEnzymeNodeWidth = enzymeNodeWidth;
	}
	
	public float getEnzymeNodeHeight(boolean bForGL) {
	
		if (bForGL)
			return fEnzymeNodeHeightGL;
		
		return fEnzymeNodeHeight;
	}

	public void setEnzymeNodeHeight(float enzymeNodeHeight) {
	
		fEnzymeNodeHeight = enzymeNodeHeight;
	}

	public Vec3f getHighlightedNodeColor() {
		
		return highlightedNodeColor;
	}
	
	public Vec3f getLayerConnectionLinesColor() {
		
		return layerConnectionLinesColor;
	}
	
	/**
	 * 
	 * @see org.geneview.core.data.view.rep.renderstyle.PathwayRenderStyle#neighborhoodNodeColorArraysize
	 * @see org.geneview.core.data.view.rep.renderstyle.PathwayRenderStyle#highlightedNodeColor
	 * 
	 * @param depth [0..1.. (neighborhoodNodeColorArraysize-1) ] ; if depth ==0 highlightedNodeColor is returened
	 * @return
	 */
	public Vec3f getNeighborhoodNodeColorByDepth( final int depth ) {
		if ( depth >= neighborhoodNodeColorArraysize) {
			throw new GeneViewRuntimeException("getNeighborhoodNodeColorByDepth(" + depth + ") exceed range!");
		}
		return this.neighborhoodNodeColorArray[depth];
	}
}