package cerberus.manager.data.pathway;

import java.util.HashMap;
import java.util.LinkedList;

import cerberus.data.pathway.element.PathwayReactionEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.APathwayEdge;
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

	protected HashMap<Integer, APathwayEdge> edgeLUT;
	
	protected HashMap<String, LinkedList<PathwayVertex>> refHashVertexNameToVertexList;
	
	// FIXME: this is just a temporary workaround.
	protected PathwayVertex currentVertex;

	protected PathwayRelationEdge currentRelationEdge;
	
	protected PathwayReactionEdge currentReactionEdge;
	
	protected HashMap<String, Integer> reactionName2EdgeIdLUT;

	/**
	 * Constructor
	 * 
	 */
	public PathwayElementManager(IGeneralManager refGeneralManager) {
		
		this.refGeneralManager = refGeneralManager;
		
		vertexLUT = new HashMap<Integer, PathwayVertex>();
		edgeLUT = new HashMap<Integer, APathwayEdge>();
		refHashVertexNameToVertexList = new HashMap<String, LinkedList<PathwayVertex>>();
		reactionName2EdgeIdLUT = new HashMap<String, Integer>();

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
		
		LinkedList<PathwayVertex> ll = refHashVertexNameToVertexList.get(sName);
        if (ll == null)
        {
            refHashVertexNameToVertexList.put(sName, ll = new LinkedList<PathwayVertex>());
        }
           
        ll.add(currentVertex);
		
		return iGeneratedId;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayElementManager#addVertexToPathway(int)
	 */
	public void addVertexToPathway(int iVertexId) {
		
		((PathwayManager)(refGeneralManager.getSingelton().getPathwayManager())).
			getCurrentPathway().addVertex(vertexLUT.get(iVertexId));
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
		
		IPathwayVertexRep newVertexRep = 
			new PathwayVertexRep(
					currentVertex,
					sName, 
					iHeight, 
					iWidth,
					iXPosition, 
					iYPosition, 
					sType);

		// Creates vertex representation with the index 0 in the 
		// vertexRep array.
		currentVertex.addVertexRep(newVertexRep, 0);
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#createEdge(int, int, java.lang.String)
	 */
	public void createRelationEdge(
			int iVertexId1, 
			int iVertexId2, 
			String sType) {
		
		int iGeneratedId = generateId();
		
		PathwayRelationEdge newEdge = 
			new PathwayRelationEdge(iVertexId1, iVertexId2, sType);
		
		edgeLUT.put(iGeneratedId, newEdge);
		
		((PathwayManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY))).
			getCurrentPathway().addEdge(newEdge);
		
		currentRelationEdge = newEdge;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayElementManager#addRelationCompound(int)
	 */
	public void addRelationCompound(int iCompoundId) {
		
		if (currentRelationEdge != null)
		{
			currentRelationEdge.setCompoundId(iCompoundId);
		}

		currentRelationEdge = null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayElementManager#createReactionEdge(java.lang.String, java.lang.String)
	 */
	public void createReactionEdge(String sReactionName, String sReactionType) {

		int iGeneratedId = generateId();
		currentReactionEdge = null;
		
		PathwayReactionEdge newEdge = 
			new PathwayReactionEdge(sReactionName, sReactionType);
		
		edgeLUT.put(iGeneratedId, newEdge);
		reactionName2EdgeIdLUT.put(sReactionName, iGeneratedId);
		
		((PathwayManager)(refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY))).
			getCurrentPathway().addEdge(newEdge);
		
		currentReactionEdge = newEdge;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayElementManager#addReactionSubstrate(int)
	 */
	public void addReactionSubstrate(int iCompoundId) {

		currentReactionEdge.addSubstrate(iCompoundId);		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayElementManager#addReactionProduct(int)
	 */
	public void addReactionProduct(final int iCompoundId) {

		currentReactionEdge.addProduct(iCompoundId);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayElementManager#getReactionName2EdgeIdLUT()
	 */
	public final HashMap<String, Integer> getReactionName2EdgeIdLUT() {
		
		return reactionName2EdgeIdLUT;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayElementManager#getPathwayVertexListByName(java.lang.String)
	 */
	public final LinkedList<PathwayVertex> getPathwayVertexListByName(
			final String sVertexName) {
		
		return refHashVertexNameToVertexList.get(sVertexName);
	}
	
	//TODO: Method needs to be replaced with createNewId method from interface.
	private int generateId() {
		
		return iCurrentUniqueElementId++;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#getVertexLUT()
	 */
	public final HashMap<Integer, PathwayVertex> getVertexLUT() {
		
		return vertexLUT;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#getEdgeLUT()
	 */
	public final HashMap<Integer, APathwayEdge> getEdgeLUT() {
		
		return edgeLUT;
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
