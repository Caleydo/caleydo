package cerberus.pathways;

import java.util.HashMap;

import cerberus.pathways.Pathway;

/**
 * The pathway manager is in charge for handling
 * the pathways.
 * The class is implemented as a Singleton. 
 * @author	Marc Streit
 */
public class PathwayManager 
{
	private static PathwayManager instance = null;

	protected HashMap<Integer, Pathway> pathwayLUT;
	
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
	{}
	
	public void registerPathway(int iPathwayID, Pathway newPathway)
	{
		pathwayLUT.put(iPathwayID, newPathway);	
	}
}
