package cerberus.data.view.rep.pathway.renderstyle;

import java.awt.Color;

import cerberus.data.view.rep.pathway.renderstyle.ARenderStyle;

public class PathwayRenderStyle 
extends ARenderStyle {
	
	public enum NodeShape {
		RECTANGULAR,
		ROUND,
		ROUNDRECTANGULAR
	};
	
	public enum NodeColor {
		BLUE,
		GREEN,
		YELLOW,
		ORANGE,
		RED
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
	
	protected Color enzymeNodeColor;
	protected Color compoundNodeColor;
	protected Color pathwayNodeColor;
	
	protected EdgeLineStyle relationEdgeLineStyle;
	protected EdgeLineStyle reactionEdgeLineStyle;
	protected EdgeLineStyle maplinkEdgeLineStyle;
	
	protected Color relationEdgeColor;
	protected Color reactionEdgeColor;
	protected Color maplinkEdgeColor;
	
	protected EdgeArrowHeadStyle relationEdgeArrowHeadStyle;
	protected EdgeArrowHeadStyle reactionEdgeArrowHeadStyle;
	protected EdgeArrowHeadStyle mapEdgeArrowHeadStyle;

	/**
	 * Constructor.
	 * Initializes the pathway render style.
	 * 
	 * TODO: load pathway style from XML file.
	 */
	public PathwayRenderStyle() {
		
		enzymeNodeShape = NodeShape.RECTANGULAR;
		compoundNodeShape = NodeShape.ROUND;
		pathwayNodeShape = NodeShape.ROUNDRECTANGULAR;
		
		enzymeNodeColor = Color.YELLOW;
		compoundNodeColor = Color.GREEN;
		pathwayNodeColor = Color.ORANGE;
		
		relationEdgeLineStyle = EdgeLineStyle.NORMAL;
		reactionEdgeLineStyle = EdgeLineStyle.NORMAL;
		maplinkEdgeLineStyle = EdgeLineStyle.DASHED;
		
		relationEdgeArrowHeadStyle = EdgeArrowHeadStyle.FILLED;
		reactionEdgeArrowHeadStyle = EdgeArrowHeadStyle.FILLED;
		mapEdgeArrowHeadStyle = EdgeArrowHeadStyle.EMPTY;
		
		relationEdgeColor = Color.GREEN;
		reactionEdgeColor = Color.BLUE;
		maplinkEdgeColor = Color.RED;
	}

	public Color getCompoundNodeColor() {
	
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

	public Color getEnzymeNodeColor() {
	
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

	public Color getPathwayNodeColor() {
	
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
}
