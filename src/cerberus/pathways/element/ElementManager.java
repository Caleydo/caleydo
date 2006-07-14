package cerberus.pathways.element;

import java.util.HashMap;

/**
 * The element manager is in charge for handling
 * the elements. Elements are vertices and edges.
 * The class is implemented as a Singleton. 
 * @author	Marc Streit
 */
public class ElementManager
{
	private static ElementManager instance = null;
	
	protected HashMap<Integer, Vertex> vertexLUT;
	protected HashMap<Integer, Edge> edgeLUT;
	
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
	
	public void createVertex(String sName, String sType)
	{
		Vertex newVertex = new Vertex(sName, sType);
		//TODO: generate ID here
		vertexLUT.put(0, newVertex);
	}
	
	public void createEdge()
	{
		Edge newEdge = new Edge();
		//edgeLUT.put(iEdgeID, newEdge);
	}
	
	private int generateID()
	{
		//TODO: implement ID generation
		return 0;
	}
}

