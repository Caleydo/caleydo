package cerberus.data.pathway.element;

import java.util.Vector;

import cerberus.data.view.rep.pathway.IPathwayEdgeRep;

public abstract class APathwayEdge 
extends APathwayElement {
	
	public enum EdgeType {
		RELATION,
		REACTION
	};
	
	protected Vector<IPathwayEdgeRep> edgeReps;
	
	protected EdgeType edgeType;
	
	public EdgeType getEdgeType() {
	
		return edgeType;
	}

	/**
	 * Method needed for the JGraph labeling of the edges.
	 */
	public String toString() {
		return "";
	}
}