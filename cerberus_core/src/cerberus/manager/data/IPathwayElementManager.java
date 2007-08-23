package cerberus.manager.data;

import java.util.HashMap;
import java.util.LinkedList;

import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.manager.IGeneralManager;

/**
 * @deprecated Use IPathwayItemManager instead
 * 
 * @author Marc Streit
 *
 */
public interface IPathwayElementManager extends IGeneralManager {
	
	public int createVertex(
			String sName, 
			String sType, 
			String sLink,
			String sReactionId);

	public void createVertexRepresentation(
			String sName, 
			int iHeight,
			int iWidth, 
			int iXPosition, 
			int iYPosition,
			String sType);
	
	public void addVertexToPathway(int iVertexId);

	public void createRelationEdge(
			int iVertexId1, 
			int iVertexId2, 
			String sType);
	
	public abstract void createReactionEdge(
			String sReactionName,
			String sReactionType);

	public void addRelationCompound(int iCompoundId);
	
	public void addReactionSubstrate(int iSubstrateId);

	public void addReactionProduct(int iProductId);
	
	public HashMap<Integer, PathwayVertex> getVertexLUT();
	
	public HashMap<Integer, APathwayEdge> getEdgeLUT();
		
	public HashMap<String, Integer> getReactionName2EdgeIdLUT();
	
	/**
	 * Method returnd all vertices that exists with the same name.
	 * This method is needed for the finding (and highlighting) of identical vertices.
	 * 
	 * FIXME: Will be replaced with new graph structure.
	 * 
	 * @param sVertexName
	 * @return
	 */
	public LinkedList<PathwayVertex> getPathwayVertexListByName(final String sVertexName);
}