package cerberus.data.pathway.element;

import java.util.Vector;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;


public class PathwayVertex extends PathwayElement 
{
	private PathwayVertexType vertexType;
	
	private Vector<IPathwayVertexRep> vertexReps;
	
	public PathwayVertex(int iVertexID, String sName, String sType)
	{	
		super(iVertexID, sName);
		vertexReps = new Vector<IPathwayVertexRep>();
		
		vertexType = PathwayVertexType.valueOf( sType );

	}
	
	public void addVertexRep(IPathwayVertexRep vertexRep)
	{
		vertexReps.add(vertexRep);
	}

	public Vector<IPathwayVertexRep> getVertexReps()
	{
		return vertexReps;
	}

	public PathwayVertexType getVertexType() 
	{
		return vertexType;
	}
}
