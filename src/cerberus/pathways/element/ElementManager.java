package cerberus.pathways.element;

import java.util.Map;

/**
 * The element manager is in charge for handling
 * the elements. Elements are vertices and edges.
 * The class is implemented as a Singleton. 
 * @author	Marc Streit
 */
public class ElementManager 
{
	private static ElementManager instance = null;
	
	protected Map<Integer, Vertex> vertexMap;
	protected Map<Integer, Edge> edgeMap;
	
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
	{}
	
	public Vertex createVertex()
	{
		Vertex newVertex = new Vertex();
		vertexMap.put(0, newVertex);
		return (newVertex);
	}
	
	public Edge createEdge()
	{
		Edge newEdge = new Edge();
		int iEdgeID = generateID();
		
		// store new edge in the map
		edgeMap.put(iEdgeID, newEdge);

		return newEdge;
	}
	
	private int generateID()
	{
		//TODO: implement ID generation
		return 0;
	}
}

