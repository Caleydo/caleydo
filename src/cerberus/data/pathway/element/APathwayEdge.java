package cerberus.data.pathway.element;

import java.util.Vector;

import keggapi.PathwayElement;
import cerberus.data.view.rep.pathway.IPathwayEdgeRep;

public abstract class APathwayEdge 
extends PathwayElement {
	
	protected Vector<IPathwayEdgeRep> edgeReps;
	
}