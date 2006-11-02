package cerberus.data.pathway;

import java.util.Iterator;
import java.util.Vector;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.APathwayEdge;
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

	protected Vector<PathwayVertex> vertexList;

	protected Vector<APathwayEdge> edgeList;

	public Pathway(String sTitle,
			String sImageLink,
			String sLink,
			int iPathwayID) {

		vertexList = new Vector<PathwayVertex>();
		edgeList = new Vector<APathwayEdge>();

		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sInformationLink = sLink;
		this.iPathwayID = iPathwayID;
	}

	public void addVertex(PathwayVertex vertex) {

		vertexList.add(vertex);
	}

	public void addEdge(APathwayEdge edge) {

		edgeList.add(edge);
	}

	public Vector<PathwayVertex> getVertexList() {

		return vertexList;
	}

	public Vector<APathwayEdge> getEdgeList() {

		return edgeList;
	}

	public int getPathwayID() {
	
		return iPathwayID;
	}
	
	public String getTitle() {
	
		return sTitle;
	}
	
	public Iterator<PathwayVertex> getVertexListIterator() {
		
        return vertexList.iterator();
	}
	
	public Iterator<APathwayEdge> getEdgeListIterator() {
		
		return edgeList.iterator();
	}
}
