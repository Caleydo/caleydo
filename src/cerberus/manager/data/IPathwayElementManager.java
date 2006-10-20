package cerberus.manager.data;

import java.util.HashMap;

import cerberus.data.pathway.element.APathwayEdge;
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

	public abstract void createRelationEdge(
			int iVertexId1, 
			int iVertexId2, 
			String sType);
	
	public abstract void createReactionEdge(
			String sReactionName,
			String sReactionType);

	public abstract void addRelationCompound(int iCompoundId);
	
	public abstract void addReactionSubstrate(int iSubstrateId);

	public abstract void addReactionProduct(int iProductId);
	
	public abstract HashMap<Integer, PathwayVertex> getVertexLUT();
	
	public abstract HashMap<Integer, APathwayEdge> getEdgeLUT();
		
	public abstract HashMap<String, Integer> getReactionName2EdgeIdLUT();
}