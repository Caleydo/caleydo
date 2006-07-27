package cerberus.data.pathway.element;

import java.util.Vector;
import cerberus.data.view.rep.pathway.jgraph.EdgeRep;

public class Edge extends Element
{
	public enum EdgeType
	{
		ECel, 	// enzyme-enzyme relation, 
				// indicating two entymes catalyzing succesive reaction steps
		PPrel,	// protein-protein interactionm such as binding and modification
		GErel,	// gene expression interaction, indicating relation of 
				// transcription factor and target gene product
		PCrel	// protein-compound interaction
	}
	
	private EdgeType vertexType;
	
	private Vector<EdgeRep> edgeReps;
	
	private int iElementId1 = 0;
	private int iElementId2 = 0;
	private String sType = "";
	private int iCompoundId = -1;

	public Edge(int iElementId1, int iElementId2, String sType)
	{
		this.iElementId1 = iElementId1;
		this.iElementId2 = iElementId2;
		this.sType = sType;
	}

	public int getIElementId1() 
	{
		return iElementId1;
	}

	public int getIElementId2() 
	{
		return iElementId2;
	}

	public String getSType() 
	{
		return sType;
	}

	public void setICompoundId(int iCompoundId) 
	{
		this.iCompoundId = iCompoundId;
	}

	public int getICompoundId() 
	{
		return iCompoundId;
	}
	
}