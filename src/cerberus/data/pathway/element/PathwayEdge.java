package cerberus.data.pathway.element;

import java.util.Vector;
import cerberus.data.view.rep.pathway.PathwayEdgeRepInter;

public class PathwayEdge extends PathwayElement
{
	private PathwayEdgeType vertexType;
	
	private Vector<PathwayEdgeRepInter> edgeReps;
	
	private int iElementId1 = 0;
	private int iElementId2 = 0;
	private String sType = "";
	private int iCompoundId = -1;

	public PathwayEdge(int iElementId1, int iElementId2, String sType)
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