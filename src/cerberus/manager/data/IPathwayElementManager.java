package cerberus.manager.data;

import java.util.HashMap;

import cerberus.data.pathway.element.PathwayVertex;

public interface IPathwayElementManager
{

	public abstract int createVertex(String sName, String sType);

	public abstract void createVertexRepresentation(String sName, int iHeight,
			int iWidth, int iXPosition, int iYPosition);

	public abstract void createEdge(int iVertexId1, int iVertexId2, String sType);

	public abstract void addCompoundForEdge(int iCompoundId);

	public abstract HashMap<Integer, PathwayVertex> getVertexLUT();

}