package cerberus.data.pathway;

import java.util.ArrayList;
import java.util.Iterator;
//import java.util.Vector;

import cerberus.data.pathway.element.PathwayReactionEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
//import cerberus.data.view.rep.pathway.IPathwayVertexRep;
//import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;

/**
 * @author Marc Streit
 *
 */
public class Pathway {

	protected int iPathwayID;

	protected String sTitle;

	protected String sImageLink;

	protected String sInformationLink;

	protected ArrayList<PathwayVertex> refArVertexList;

	protected ArrayList<APathwayEdge> refArEdgeList;
	
	protected ArrayList<PathwayRelationEdge> refArRelationEdges;
	
	protected ArrayList<PathwayReactionEdge> refArReactionEdges;
	
	public Pathway(String sTitle,
			String sImageLink,
			String sLink,
			int iPathwayID) {

		refArVertexList = new ArrayList<PathwayVertex>();
		refArEdgeList = new ArrayList<APathwayEdge>();
		refArRelationEdges = new ArrayList<PathwayRelationEdge>();
		refArReactionEdges = new ArrayList<PathwayReactionEdge>();

		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sInformationLink = sLink;
		this.iPathwayID = iPathwayID;
	}

	public void addVertex(PathwayVertex vertex) {

		refArVertexList.add(vertex);
	}

	public void addEdge(APathwayEdge edge) {

		refArEdgeList.add(edge);
		
		if (edge.getEdgeType() == EdgeType.RELATION)
		{
			refArRelationEdges.add((PathwayRelationEdge) edge);
		}
		else if (edge.getEdgeType() == EdgeType.REACTION)
		{
			refArReactionEdges.add((PathwayReactionEdge) edge);
		}
	}

	public ArrayList<PathwayVertex> getVertexList() {

		return refArVertexList;
	}

	public ArrayList<APathwayEdge> getEdgeList() {

		return refArEdgeList;
	}

	public int getPathwayID() {
	
		return iPathwayID;
	}
	
	public String getTitle() {
	
		return sTitle;
	}
	
	public Iterator<PathwayVertex> getVertexListIterator() {
		
        return refArVertexList.iterator();
	}
	
	public Iterator<APathwayEdge> getEdgeListIterator() {
		
		return refArEdgeList.iterator();
	}
	
	public Iterator<PathwayReactionEdge> getReactionEdgeIterator() {
		
		return refArReactionEdges.iterator();
	}

	public Iterator<PathwayRelationEdge> getRelationEdgeIterator() {
		
		return refArRelationEdges.iterator();
	}
	
	public boolean isVertexInPathway(PathwayVertex refPathwayVertex) {
		
		if (refArVertexList.contains(refPathwayVertex))
			return true;
		
		return false;
	}
}
