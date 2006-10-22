package cerberus.data.pathway.element;

import java.util.Vector;

import keggapi.PathwayElement;
import cerberus.data.view.rep.pathway.IPathwayEdgeRep;

public abstract class APathwayEdge 
extends PathwayElement {
	
	public enum EdgeType {
		RELATION,
		REACTION
	};
	
	protected Vector<IPathwayEdgeRep> edgeReps;
	
	protected EdgeType edgeType;
	
	public EdgeType getEdgeType() {
	
		return edgeType;
	}
}