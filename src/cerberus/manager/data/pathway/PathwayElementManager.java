package cerberus.manager.data.pathway;

import java.util.HashMap;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayEdge;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISingelton;
import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.type.ManagerObjectType;

/**
 * The element manager is in charge for handling the elements. Elements are
 * vertices and edges. The class is implemented as a Singleton.
 * 
 * @author Marc Streit
 */
public class PathwayElementManager 
implements IPathwayElementManager {
	
	protected IGeneralManager refGeneralManager;
	
	protected int iCurrentUniqueElementId;

	protected HashMap<Integer, PathwayVertex> vertexLUT;

	protected HashMap<Integer, PathwayEdge> edgeLUT;

	// FIXME: this is just a temporary workaround.
	protected PathwayVertex currentVertex;

	protected PathwayEdge currentEdge;

	/**
	 * Constructor
	 * 
	 */
	public PathwayElementManager(IGeneralManager refGeneralManager) {
		
		this.refGeneralManager = refGeneralManager;
		
		vertexLUT = new HashMap<Integer, PathwayVertex>();
		edgeLUT = new HashMap<Integer, PathwayEdge>();

		iCurrentUniqueElementId = 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#createVertex(java.lang.String, java.lang.String)
	 */
	public int createVertex(
			String sName, 
			String sType, 
			String sLink,
			String sReactionId) {
		
		int iGeneratedId = generateId();
		
		PathwayVertex newVertex = 
			new PathwayVertex(iGeneratedId, sName, sType, sLink, sReactionId);
		
		currentVertex = newVertex;
		vertexLUT.put(iGeneratedId, newVertex);
		
		((PathwayManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY))).
			getCurrentPathway().addVertex(newVertex);
		//PathwayManager.getInstance().getCurrentPathway().addVertex(newVertex);
		
		return iGeneratedId;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#createVertexRepresentation(java.lang.String, int, int, int, int)
	 */
	public void createVertexRepresentation(
			String sName, 
			int iHeight,
			int iWidth, 
			int iXPosition, 
			int iYPosition, 
			String sType) {
		
		IPathwayVertexRep newVertexRep = new PathwayVertexRep(sName, iHeight, iWidth,
				iXPosition, iYPosition, sType);

		currentVertex.addVertexRep(newVertexRep);
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#createEdge(int, int, java.lang.String)
	 */
	public void createEdge(
			int iVertexId1, 
			int iVertexId2, 
			String sType) {
		
		int iGeneratedId = generateId();
		
//		PathwayEdge newEdge = new PathwayEdge(iVertexId1, iVertexId2, sType);
//		
//		edgeLUT.put(iGeneratedId, newEdge);
//		
//		((PathwayManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY))).
//			getCurrentPathway().addEdge(newEdge);
//		
//		currentEdge = newEdge;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#addCompoundForEdge(int)
	 */
	public void addCompoundForEdge(int iCompoundId) {
		
		if (currentEdge != null)
		{
//			currentEdge.setICompoundId(iCompoundId);
		}

		currentEdge = null;
	}
	

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayElementManager#addProductForEdge(int)
	 */
	public void addProductForEdge(int iCompoundId) {

		// TODO Auto-generated method stub
		
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayElementManager#addSubstrateForEdge(int)
	 */
	public void addSubstrateForEdge(int iCompoundId) {

		// TODO Auto-generated method stub
		
	}

	private int generateId() {
		
		return iCurrentUniqueElementId++;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#getVertexLUT()
	 */
	public HashMap<Integer, PathwayVertex> getVertexLUT() {
		
		return vertexLUT;
	}

	public boolean hasItem(int iItemId) {
		
		// TODO Auto-generated method stub
		return false;
	}

	public Object getItem(int iItemId) {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ManagerObjectType getManagerType() {
		// TODO Auto-generated method stub
		return null;
	}

	public IGeneralManager getGeneralManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISingelton getSingelton() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerItem(Object registerItem, int iItemId, ManagerObjectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public int createNewId(ManagerObjectType setNewBaseType) {
		// TODO Auto-generated method stub
		return 0;
	}

	public IGeneralManager getManagerByBaseType(ManagerObjectType managerType) {
		// TODO Auto-generated method stub
		return null;
	}

}
