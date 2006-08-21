package cerberus.manager.data.pathway;

import java.util.HashMap;

import cerberus.data.pathway.Pathway;
import cerberus.manager.data.IPathwayManager;

/**
 * The pathway manager is in charge for handling
 * the pathways.
 * The class is implemented as a Singleton. 
 * @author	Marc Streit
 */
public class PathwayManager implements IPathwayManager 
{
	private static IPathwayManager instance = null;

	private HashMap<Integer, Pathway> pathwayLUT;
	private Pathway currentPathway;
	
	/**
	 * Returns the instance of the pathway manager.
	 * If no instance exists a new one is created
	 * and returned. 
	 *
	 * @return      Instance of the element manager.
	 */
	public static IPathwayManager getInstance()
	{
		if (instance == null)
		{
			instance = new PathwayManager();
		}
		
		return instance;
	}
	
	/**
	 * Private Constructor
	 * The class is implemented as a Singleton and 
	 * therefore it is not allowed to create a new instance.
	 * To get a instance call the getInstance() method. 
	 */
	private PathwayManager()
	{
		pathwayLUT = new HashMap<Integer, Pathway>();
	}
	

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayManager#getPathwayLUT()
	 */
	public HashMap<Integer, Pathway> getPathwayLUT() 
	{
		return pathwayLUT;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayManager#createPathway(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public void createPathway(String sTitle, String sImageLink, String sLink, int iPathwayID) 
	{
		Pathway newPathway = 
			new Pathway(sTitle, sImageLink, sLink, iPathwayID);
		
		pathwayLUT.put(iPathwayID, newPathway);	
		
		currentPathway = newPathway;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayManager#getCurrentPathway()
	 */
	public Pathway getCurrentPathway() 
	{
		return currentPathway;
	}
}
