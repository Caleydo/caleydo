package cerberus.data.pathway;

import java.util.Vector;

import cerberus.data.pathway.element.Vertex;
import cerberus.data.pathway.element.Edge;

public class Pathway 
{
	private int iPathwayID;
	private String sTitle;
	private String sImageLink;
	private String sInformationLink;
	
	private Vector<Vertex> vertexList;
	private Vector<Edge> edgeList;

	public Pathway(String sTitle, String sImageLink, String sLink, int iPathwayID)
	{
		vertexList = new Vector<Vertex>();
		edgeList = new Vector<Edge>();
		
		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sInformationLink = sLink;
		this.iPathwayID = iPathwayID;
	}
	
	public void addVertex(Vertex vertex)
	{
		vertexList.add(vertex);
	}
	
	public void addEdge(Edge edge)
	{
		edgeList.add(edge);
	}

	public Vector<Vertex> getVertexList() 
	{
		return vertexList;
	}

	public Vector<Edge> getEdgeList() 
	{
		return edgeList;
	}
}
