package cerberus.data.pathway;

import java.util.Vector;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.APathwayEdge;

public class Pathway 
{
	private int iPathwayID;
	private String sTitle;
	private String sImageLink;
	private String sInformationLink;
	
	private Vector<PathwayVertex> vertexList;
	private Vector<APathwayEdge> edgeList;

	public Pathway(String sTitle, String sImageLink, String sLink, int iPathwayID)
	{
		vertexList = new Vector<PathwayVertex>();
		edgeList = new Vector<APathwayEdge>();
		
		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sInformationLink = sLink;
		this.iPathwayID = iPathwayID;
	}
	
	public void addVertex(PathwayVertex vertex)
	{
		vertexList.add(vertex);
	}
	
	public void addEdge(APathwayEdge edge)
	{
		edgeList.add(edge);
	}

	public Vector<PathwayVertex> getVertexList() 
	{
		return vertexList;
	}

	public Vector<APathwayEdge> getEdgeList() 
	{
		return edgeList;
	}
}
