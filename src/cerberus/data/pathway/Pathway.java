package cerberus.data.pathway;

import java.util.Iterator;
import java.util.Vector;

import cerberus.data.pathway.element.PathwayReactionEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;

/**
 * @author Marc Streit
 *
 */
public class Pathway {

	protected int iPathwayID;

	protected String sTitle;

	protected String sImageLink;

	protected String sInformationLink;

	protected Vector<PathwayVertex> refVecVertices;

	protected Vector<APathwayEdge> refVecEdges;
	
	protected Vector<PathwayRelationEdge> refVecRelationEdges;
	
	protected Vector<PathwayReactionEdge> refVecReactionEdges;
	
	public Pathway(String sTitle,
			String sImageLink,
			String sLink,
			int iPathwayID) {

		refVecVertices = new Vector<PathwayVertex>();
		refVecEdges = new Vector<APathwayEdge>();
		refVecRelationEdges = new Vector<PathwayRelationEdge>();
		refVecReactionEdges = new Vector<PathwayReactionEdge>();

		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sInformationLink = sLink;
		this.iPathwayID = iPathwayID;
	}

	public void addVertex(PathwayVertex vertex) {

		refVecVertices.add(vertex);
	}

	public void addEdge(APathwayEdge edge) {

		refVecEdges.add(edge);
		
		if (edge.getEdgeType() == EdgeType.RELATION)
		{
			refVecRelationEdges.add((PathwayRelationEdge) edge);
		}
		else if (edge.getEdgeType() == EdgeType.REACTION)
		{
			refVecReactionEdges.add((PathwayReactionEdge) edge);
		}
	}

	public Vector<PathwayVertex> getVertexList() {

		return refVecVertices;
	}

	public Vector<APathwayEdge> getEdgeList() {

		return refVecEdges;
	}

	public int getPathwayID() {
	
		return iPathwayID;
	}
	
	public String getTitle() {
	
		return sTitle;
	}
	
	public Iterator<PathwayVertex> getVertexListIterator() {
		
        return refVecVertices.iterator();
	}
	
	public Iterator<APathwayEdge> getEdgeListIterator() {
		
		return refVecEdges.iterator();
	}
	
	public Iterator<PathwayReactionEdge> getReactionEdgeIterator() {
		
		return refVecReactionEdges.iterator();
	}

	public Iterator<PathwayRelationEdge> getRelationEdgeIterator() {
		
		return refVecRelationEdges.iterator();
	}
}
