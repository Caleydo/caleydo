package cerberus.manager.data.pathway;

import java.util.HashMap;
import java.util.Iterator;

import cerberus.data.pathway.Pathway;
import cerberus.data.view.rep.pathway.jgraph.PathwayImageMap;
import cerberus.manager.IGeneralManager;
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

	protected String sPathwayImagePath = "data/GenomeData/pathways/images";
	
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
				ManagerType.PATHWAY_ELEMENT );
		
		pathwayLUT = new HashMap<Integer, Pathway>();
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayManager#getPathwayLUT()
	 */
	public HashMap<Integer, Pathway> getPathwayLUT() {

		return pathwayLUT;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.pathway.IPathwayManager#createPathway(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public void createPathway(String sTitle, 
			String sImageLink, 
			String sLink,
			int iPathwayID) {

		refCurrentPathway = new Pathway(
				sTitle, sImageLink, sLink, iPathwayID);

		pathwayLUT.put(iPathwayID, refCurrentPathway);
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

	public final String getPathwayImagePath() {
		
		return sPathwayImagePath;
	}
	
	public void setPathwayImagePath(String sPathwayImagePath) {
		
		this.sPathwayImagePath = sPathwayImagePath;
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
