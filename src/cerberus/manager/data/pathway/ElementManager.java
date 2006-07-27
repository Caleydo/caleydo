package cerberus.manager.data.pathway;

import java.util.HashMap;

import cerberus.data.pathway.element.Vertex;
import cerberus.data.pathway.element.Edge;
import cerberus.data.view.rep.pathway.VertexRepInter;
import cerberus.data.view.rep.pathway.jgraph.VertexRep;

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
	
	//FIXME: this is just a temporary workaround.
	private Vertex currentVertex = null;
	private Edge currentEdge = null;
	
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
		vertexLUT = new HashMap<Integer, Vertex>();
		edgeLUT = new HashMap<Integer, Edge>();
		
//		iCurrentUniqueElementId = 
//			CollectionManager.calculateId( 
//					GeneralManager.iUniqueId_TypeOffset_Memento, 
//					refGeneralManager );
		
		iCurrentUniqueElementId = 0;
	}
	
	public int createVertex(String sName, String sType)
	{
		int iGeneratedId = generateId();
		Vertex newVertex = new Vertex(iGeneratedId, sName, sType);
		currentVertex = newVertex;
		vertexLUT.put(iGeneratedId, newVertex);
		PathwayManager.getInstance().getCurrentPathway().addVertex(newVertex);
		return iGeneratedId;
	}
	
	public void createVertexRepresentation(String sName, int iHeight, int iWidth,
			int iXPosition, int iYPosition)
	{
		VertexRepInter newVertexRep = new VertexRep(sName, iHeight, iWidth,
			iXPosition, iYPosition);
		
		currentVertex.addVertexRep(newVertexRep);
	}
	
	public void createEdge(int iVertexId1, int iVertexId2, String sType)
	{
		int iGeneratedId = generateId();
		Edge newEdge = new Edge(iVertexId1, iVertexId2, sType);
		edgeLUT.put(iGeneratedId, newEdge);
		PathwayManager.getInstance().getCurrentPathway().addEdge(newEdge);
		currentEdge = newEdge;
	}

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

	public HashMap<Integer, Vertex> getVertexLUT() 
	{
		return vertexLUT;
	}

}

