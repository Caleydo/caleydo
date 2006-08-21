package cerberus.manager.data.pathway;

import java.util.HashMap;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayEdge;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;
import cerberus.manager.data.IPathwayElementManager;

/**
 * The element manager is in charge for handling the elements. Elements are
 * vertices and edges. The class is implemented as a Singleton.
 * 
 * @author Marc Streit
 */
public class PathwayElementManager implements IPathwayElementManager
{
	private static IPathwayElementManager instance = null;

	private int iCurrentUniqueElementId;

	private HashMap<Integer, PathwayVertex> vertexLUT;

	private HashMap<Integer, PathwayEdge> edgeLUT;

	// FIXME: this is just a temporary workaround.
	private PathwayVertex currentVertex = null;

	private PathwayEdge currentEdge = null;

	/**
	 * Returns the instance of the element manager. If no instance exists a new
	 * one is created and returned.
	 * 
	 * @return Instance of the element manager.
	 */
	public static IPathwayElementManager getInstance()
	{
		if (instance == null)
		{
			instance = new PathwayElementManager();
		}

		return instance;
	}

	/**
	 * Private Constructor The class is implemented as a Singleton and therefore
	 * it is not allowed to create a new instance. To get a instance call the
	 * getInstance() method.
	 */
	public PathwayElementManager()
	{
		vertexLUT = new HashMap<Integer, PathwayVertex>();
		edgeLUT = new HashMap<Integer, PathwayEdge>();

		// iCurrentUniqueElementId =
		// ICollectionManager.calculateId(
		// IGeneralManager.iUniqueId_TypeOffset_Memento,
		// refGeneralManager );

		iCurrentUniqueElementId = 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#createVertex(java.lang.String, java.lang.String)
	 */
	public int createVertex(String sName, String sType)
	{
		int iGeneratedId = generateId();
		PathwayVertex newVertex = new PathwayVertex(iGeneratedId, sName, sType);
		currentVertex = newVertex;
		vertexLUT.put(iGeneratedId, newVertex);
		PathwayManager.getInstance().getCurrentPathway().addVertex(newVertex);
		return iGeneratedId;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#createVertexRepresentation(java.lang.String, int, int, int, int)
	 */
	public void createVertexRepresentation(String sName, int iHeight,
			int iWidth, int iXPosition, int iYPosition)
	{
		IPathwayVertexRep newVertexRep = new PathwayVertexRep(sName, iHeight, iWidth,
				iXPosition, iYPosition);

		currentVertex.addVertexRep(newVertexRep);
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#createEdge(int, int, java.lang.String)
	 */
	public void createEdge(int iVertexId1, int iVertexId2, String sType)
	{
		int iGeneratedId = generateId();
		PathwayEdge newEdge = new PathwayEdge(iVertexId1, iVertexId2, sType);
		edgeLUT.put(iGeneratedId, newEdge);
		PathwayManager.getInstance().getCurrentPathway().addEdge(newEdge);
		currentEdge = newEdge;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#addCompoundForEdge(int)
	 */
	public void addCompoundForEdge(int iCompoundId)
	{
		if (currentEdge != null)
		{
			currentEdge.setICompoundId(iCompoundId);
		}

		currentEdge = null;
	}

	private int generateId()
	{
		return iCurrentUniqueElementId++;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayElementManager#getVertexLUT()
	 */
	public HashMap<Integer, PathwayVertex> getVertexLUT()
	{
		return vertexLUT;
	}

}
