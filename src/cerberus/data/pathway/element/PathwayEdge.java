package cerberus.data.pathway.element;

import java.util.Vector;
import cerberus.data.view.rep.pathway.IPathwayEdgeRep;

public abstract class PathwayEdge 
extends PathwayElement {
	
	enum PathwayEdgeType {
		REACTION,
		RELATION
	};
	
	protected PathwayEdgeType edgeType;
	
	protected Vector<IPathwayEdgeRep> edgeReps;
}