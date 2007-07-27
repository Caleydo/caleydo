package cerberus.manager.data.pathway;

import java.util.HashMap;
import java.util.Iterator;

import cerberus.data.pathway.Pathway;
import cerberus.data.view.rep.pathway.jgraph.PathwayImageMap;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;

/**
 * The pathway manager is in charge for handling
 * the pathways.
 * The class is implemented as a Singleton.
 *  
 * @author Marc Streit
 * @author Michael Kalkusch
 */

public class PathwayManager 
extends AAbstractManager
implements IPathwayManager {

	protected String sPathwayXMLPath = "";	
	
	protected String sPathwayImagePath = "";
	
	protected String sPathwayImageMapPath = "";
	
	protected HashMap<Integer, Pathway> pathwayLUT;

	protected Pathway refCurrentPathway;
	
	/**
	 * Used for pathways where only images
	 * can be loaded. The image map defines the clickable
	 * regions on that pathway image.
	 */
	protected PathwayImageMap refCurrentPathwayImageMap;

	/**
	 * Constructor
	 */
	public PathwayManager(IGeneralManager refGeneralManager) {

		super( refGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Pathways_Pathway,
				ManagerType.DATA_PATHWAY_ELEMENT );
		
		pathwayLUT = new HashMap<Integer, Pathway>();
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayManager#getPathwayLUT()
	 */
	public HashMap<Integer, Pathway> getPathwayLUT() {

		return pathwayLUT;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayManager#createPathway(Stringt, Stringt, Stringt, int)
	 */
	public void createPathway(String sName,
			String sTitle, 
			String sImageLink, 
			String sLink,
			int iPathwayID) {

		refCurrentPathway = new Pathway(
				sName, sTitle, sImageLink, sLink, iPathwayID);

		pathwayLUT.put(iPathwayID, refCurrentPathway);
	}
	
	public void loadPathwayById(int iPathwayID) {
		
		// Check if pathway was previously loaded
		if (pathwayLUT.containsKey(iPathwayID))
		{
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": loadPathwayById(): Pathway "+ iPathwayID + " is already loaded. SKIP.",
					LoggerType.VERBOSE);
			
			return;
		}

		String sPathwayFilePath = "";
		
		if (iPathwayID < 10)
		{
			sPathwayFilePath = "hsa0000" + Integer.toString(iPathwayID);
		}
		else if (iPathwayID < 100 && iPathwayID >= 10)
		{
			sPathwayFilePath = "hsa000" + Integer.toString(iPathwayID);
		}
		else if (iPathwayID < 1000 && iPathwayID >= 100)
		{
			sPathwayFilePath = "hsa00" + Integer.toString(iPathwayID);
		}
		else if (iPathwayID < 10000 && iPathwayID >= 1000)
		{
			sPathwayFilePath = "hsa0" + Integer.toString(iPathwayID);
		}
		
		sPathwayFilePath = refGeneralManager.getSingelton().getPathwayManager().getPathwayXMLPath()
			+ sPathwayFilePath +".xml";		
		
		refGeneralManager.getSingelton().
		getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManager#getPathwayIterator()
	 */
	public Iterator<Pathway> getPathwayIterator() {
		
		return pathwayLUT.values().iterator();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayManager#getCurrentPathway()
	 */
	protected Pathway getCurrentPathway() {

		return refCurrentPathway;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManager#createPathwayImageMap()
	 */
	public void createPathwayImageMap(String sImageLink) {
		
		refCurrentPathwayImageMap = new PathwayImageMap(sImageLink);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManager#getCurrentPathwayImageMap()
	 */
	public PathwayImageMap getCurrentPathwayImageMap () {
		
		return refCurrentPathwayImageMap;
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManager#getPathwayXMLPath()
	 */
	public final String getPathwayXMLPath() {
		
		assert !sPathwayXMLPath.isEmpty() : "Pathway XML path is not set!";
		
		return sPathwayXMLPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManager#setPathwayXMLPath(Stringt)
	 */
	public void setPathwayXMLPath(String sPathwayXMLPath) {

		this.sPathwayXMLPath = sPathwayXMLPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManager#getPathwayImagePath()
	 */
	public final String getPathwayImagePath() {

		assert !sPathwayImagePath.isEmpty() : "Pathway image path is not set!";
		
		return sPathwayImagePath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManager#setPathwayImagePath(Stringt)
	 */
	public void setPathwayImagePath(String sPathwayImagePath) {
		
		this.sPathwayImagePath = sPathwayImagePath;
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManager#getPathwayImageMapPath()
	 */
	public final String getPathwayImageMapPath() {
		
		assert !sPathwayImageMapPath.isEmpty() : "Pathway image map path is not set!";
		
		return sPathwayImageMapPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManager#setPathwayImageMapPath(Stringt)
	 */
	public void setPathwayImageMapPath(String sPathwayImageMapPath) {
		
		this.sPathwayImageMapPath = sPathwayImageMapPath;
	}
	
	public boolean hasItem(int iItemId) {

		if (pathwayLUT.containsKey(iItemId))
			return true;
		
		return false;
	}

	public Object getItem(int iItemId) {

		return(pathwayLUT.get(iItemId));
	}

	public int size() {

		// TODO Auto-generated method stub
		return 0;
	}

	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}
}
