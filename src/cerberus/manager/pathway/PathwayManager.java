package cerberus.manager.pathway;

import java.util.HashMap;

import cerberus.data.pathway.Pathway;

/**
 * The pathway manager is in charge for handling
 * the pathways.
 * The class is implemented as a Singleton. 
 * @author	Marc Streit
 */
public class PathwayManager 
{
	private static PathwayManager instance = null;

	private HashMap<Integer, Pathway> pathwayLUT;
	private Pathway currentPathway;
	
	/**
	 * Returns the instance of the pathway manager.
	 * If no instance exists a new one is created
	 * and returned. 
	 *
	 * @return      Instance of the element manager.
	 */
	public static PathwayManager getInstance()
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
	

	public HashMap<Integer, Pathway> getPathwayLUT() 
	{
		return pathwayLUT;
	}

	public void createPathway(String sTitle, String sImageLink, String sLink, int iPathwayID) 
	{
		Pathway newPathway = 
			new Pathway(sTitle, sImageLink, sLink, iPathwayID);
		
		pathwayLUT.put(iPathwayID, newPathway);	
		
		currentPathway = newPathway;
	}

	public Pathway getCurrentPathway() 
	{
		return currentPathway;
	}
}
