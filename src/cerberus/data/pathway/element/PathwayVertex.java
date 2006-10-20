package cerberus.data.pathway.element;

import java.util.Vector;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;


public class PathwayVertex 
extends APathwayElement {
	
	protected PathwayVertexType vertexType;
	
	protected Vector<IPathwayVertexRep> vertexReps;
	
	protected String sVertexLink; 
	
	/*
	 * The enzyme's reaction name
	 */
	protected String sReactionName;
	
	public PathwayVertex(
			int iVertexID, 
			String sName, 
			String sType, 
			String sLink,
			String sReactionName) {
		
		super(iVertexID, sName);
		
		vertexReps = new Vector<IPathwayVertexRep>();
		
		vertexType = PathwayVertexType.valueOf( sType );
		
		sVertexLink = sLink;
		
		this.sReactionName = sReactionName;
	}
	
	public void addVertexRep(IPathwayVertexRep vertexRep) {
		
		vertexReps.add(vertexRep);
	}

	public Vector<IPathwayVertexRep> getVertexReps() {
		
		return vertexReps;
	}

	public PathwayVertexType getVertexType() {
		
		return vertexType;
	}
	
	public String getVertexLink() {
		
		return sVertexLink;
	}
	
	public String getVertexReactionName() {
		
		return sReactionName;
	}
}
