package cerberus.data.pathway.element;

import java.util.Vector;
import cerberus.data.view.rep.pathway.PathwayVertexRepInter;


public class PathwayVertex extends PathwayElement 
{
	private PathwayVertexType vertexType;
	
	private Vector<PathwayVertexRepInter> vertexReps;
	
	public PathwayVertex(int iVertexID, String sName, String sType)
	{	
		super(iVertexID, sName);
		vertexReps = new Vector<PathwayVertexRepInter>();
		
		vertexType = PathwayVertexType.valueOf( sType );

	}
	
	public void addVertexRep(PathwayVertexRepInter vertexRep)
	{
		vertexReps.add(vertexRep);
	}

	public Vector<PathwayVertexRepInter> getVertexReps()
	{
		return vertexReps;
	}

	public PathwayVertexType getVertexType() 
	{
		return vertexType;
	}
}
