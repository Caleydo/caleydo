package cerberus.pathways.element;

import java.util.HashMap;

import cerberus.manager.GeneralManager;
import cerberus.manager.collection.CollectionManager;

/**
 * The element manager is in charge for handling
 * the elements. Elements are vertices and edges.
 * The class is implemented as a Singleton. 
 * @author	Marc Streit
 */
public class ElementManager
{
	private static ElementManager instance = null;
	private int iCurrentUniqueElementId;
	private HashMap<Integer, Vertex> vertexLUT;
	private HashMap<Integer, Edge> edgeLUT;
	
	//FIXME: this is just a workaround.
	private Vertex currentVertex = null;
	
	/**
	 * Returns the instance of the element manager.
	 * If no instance exists a new one is created
	 * and returned. 
	 *
	 * @return      Instance of the element manager.
	 */
	public static ElementManager getInstance()
	{
		if (instance == null)
		{
			instance = new ElementManager();
		}
		
		return instance;
	}
	
	/**
	 * Private Constructor
	 * The class is implemented as a Singleton and 
	 * therefore it is not allowed to create a new instance.
	 * To get a instance call the getInstance() method. 
	 */
	private ElementManager()
	{
//		iCurrentUniqueElementId = 
//			CollectionManager.calculateId( 
//					GeneralManager.iUniqueId_TypeOffset_Memento, 
//					refGeneralManager );
		
		iCurrentUniqueElementId = 0;
	}
	
	public int createVertex(String sName, String sType)
	{
		int iGeneratedID = generateID();
		Vertex newVertex = new Vertex(iGeneratedID, sName, sType);
		currentVertex = newVertex;
		vertexLUT.put(iGeneratedID, newVertex);
		return iGeneratedID;
	}
	
	public void createVertexRepresentation(String sName, int iHeight, int iWidth,
			int iXPosition, int iYPosition)
	{
		VertexRepresentation newVertexRep = new VertexRepresentation(sName, iHeight, iWidth,
			iXPosition, iYPosition);
		
		currentVertex.addVertexRepresentation(newVertexRep);
	}
	
	public void createEdge()
	{
		Edge newEdge = new Edge();
		//edgeLUT.put(iEdgeID, newEdge);
	}
	
	private int generateID()
	{
		return iCurrentUniqueElementId++;

	}

	public HashMap<Integer, Vertex> getVertexLUT() 
	{
		return vertexLUT;
	}
}

