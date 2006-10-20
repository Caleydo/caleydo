package cerberus.manager.data.pathway;

import java.util.HashMap;

import cerberus.data.pathway.Pathway;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISingelton;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.type.ManagerObjectType;

/**
 * The pathway manager is in charge for handling
 * the pathways.
 * The class is implemented as a Singleton. 
 * @author	Marc Streit
 */
public class PathwayManager implements IPathwayManager 
{
    protected IGeneralManager refGeneralManager;
	
	protected HashMap<Integer, Pathway> pathwayLUT;
	
	protected Pathway currentPathway;
	
	/**
	 * Constructor
	 */
	public PathwayManager(IGeneralManager refGeneralManager)
	{
		this.refGeneralManager = refGeneralManager;
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

	public boolean hasItem(int iItemId)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Object getItem(int iItemId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public ManagerObjectType getManagerType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public IGeneralManager getGeneralManager()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ISingelton getSingelton()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerItem(Object registerItem, int iItemId, ManagerObjectType type)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public int createNewId(ManagerObjectType setNewBaseType)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public IGeneralManager getManagerByBaseType(ManagerObjectType managerType)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
