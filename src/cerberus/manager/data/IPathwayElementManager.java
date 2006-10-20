package cerberus.manager.data;

import java.util.HashMap;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.manager.IGeneralManager;

public interface IPathwayElementManager extends IGeneralManager {
	
	public abstract int createVertex(
			String sName, 
			String sType, 
			String sLink,
			String sReactionId);

	public abstract void createVertexRepresentation(
			String sName, 
			int iHeight,
			int iWidth, 
			int iXPosition, 
			int iYPosition,
			String sType);

	public abstract void createEdge(
			int iVertexId1, 
			int iVertexId2, 
			String sType);

	public abstract void addCompoundForEdge(int iCompoundId);
	
	public abstract void addProductForEdge(int iCompoundId);
	
	public abstract void addSubstrateForEdge(int iCompoundId);

	public abstract HashMap<Integer, PathwayVertex> getVertexLUT();

}