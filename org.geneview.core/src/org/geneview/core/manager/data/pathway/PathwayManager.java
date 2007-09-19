package org.geneview.core.manager.data.pathway;

import java.util.HashMap;

import org.geneview.util.graph.core.Graph;

import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.view.rep.pathway.jgraph.PathwayImageMap;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.base.AAbstractManager;
import org.geneview.core.manager.data.IPathwayManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.type.ManagerType;

/**
 * The pathway manager is in charge for 
 * creating and handling the pathways.
 * The class is implemented as a singleton.
 * 
 * @author Marc Streit
 *
 */
public class PathwayManager 
extends AAbstractManager
implements IPathwayManager {

	private HashMap<Integer, PathwayGraph> hashPathwayLUT;
	
	private String sPathwayXMLPath;	
	
	private String sPathwayImagePath;
	
	private String sPathwayImageMapPath;
	
	/**
	 * Root pathway contains all nodes that are loaded into the system.
	 * Therefore it represents the overall topological network.
	 * (The root pathway is independent from the representation of the nodes.)
	 */
	private Graph rootPathwayGraph;
	
	/**
	 * Used for pathways where only images
	 * can be loaded. The image map defines the clickable
	 * regions on that pathway image.
	 */
	protected PathwayImageMap refCurrentPathwayImageMap;

	
	/**
	 * Constructor
	 */
	public PathwayManager(final IGeneralManager refGeneralManager) {

		super(refGeneralManager, 
			IGeneralManager.iUniqueId_TypeOffset_Pathways_Pathway,
			ManagerType.DATA_PATHWAY_ELEMENT );
		
		hashPathwayLUT = new HashMap<Integer, PathwayGraph>();
		
		rootPathwayGraph = new Graph(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManagerNew#createPathway(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public PathwayGraph createPathway(
			final int iKEGGId,
			final String sName,
			final String sTitle, 
			final String sImageLink, 
			final String sExternalLink) {

		PathwayGraph pathway = new PathwayGraph(
				iKEGGId, sName, sTitle, sImageLink, sExternalLink);

		hashPathwayLUT.put(iKEGGId, pathway);
		
		return pathway;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#loadPathwayById(int)
	 */
	public boolean loadPathwayById(final int iPathwayID) {
		
		// Check if pathway was previously loaded
		if (hashPathwayLUT.containsKey(iPathwayID))
		{
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": loadPathwayById(): Pathway "+ iPathwayID + " is already loaded. SKIP.",
					LoggerType.VERBOSE);
			
			return true;
		}

		String sPathwayFilePath = "";
		boolean bLoadingOK = false;
		
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
		
		sPathwayFilePath = sPathwayXMLPath + sPathwayFilePath +".xml";		
		
		bLoadingOK = refGeneralManager.getSingelton().getXmlParserManager().parseXmlFileByName(sPathwayFilePath);

		if (bLoadingOK)
			return true;
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": loadPathwayById(): No HSA pathway available - " +
				"try to load reference pathway.",
				LoggerType.VERBOSE);
		
		// Replace HSA with MAP and therefore try to load reference pathway
		sPathwayFilePath = sPathwayFilePath.replace("hsa", "map");
		
		return refGeneralManager.getSingelton().getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#getRootPathway()
	 */
	public Graph getRootPathway() {
		
		return rootPathwayGraph;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#getPathwayXMLPath()
	 */
	public String getPathwayXMLPath() {
		
		assert !sPathwayXMLPath.isEmpty() : "Pathway XML path is not set!";
		
		return sPathwayXMLPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#setPathwayXMLPath(java.lang.String)
	 */
	public void setPathwayXMLPath(final String sPathwayXMLPath) {

		this.sPathwayXMLPath = sPathwayXMLPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#getPathwayImagePath()
	 */
	public final String getPathwayImagePath() {

		assert !sPathwayImagePath.isEmpty() : "Pathway image path is not set!";
		
		return sPathwayImagePath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#setPathwayImagePath(java.lang.String)
	 */
	public void setPathwayImagePath(final String sPathwayImagePath) {
		
		this.sPathwayImagePath = sPathwayImagePath;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#getPathwayImageMapPath()
	 */
	public final String getPathwayImageMapPath() {
		
		assert !sPathwayImageMapPath.isEmpty() : "Pathway image map path is not set!";
		
		return sPathwayImageMapPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#setPathwayImageMapPath(java.lang.String)
	 */
	public void setPathwayImageMapPath(final String sPathwayImageMapPath) {
		
		this.sPathwayImageMapPath = sPathwayImageMapPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#createPathwayImageMap(java.lang.String)
	 */
	public void createPathwayImageMap(final String sImageLink) {
		
		refCurrentPathwayImageMap = new PathwayImageMap(sImageLink);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.IPathwayManager#getCurrentPathwayImageMap()
	 */
	public PathwayImageMap getCurrentPathwayImageMap () {
		
		return refCurrentPathwayImageMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {

		return(hashPathwayLUT.get(iItemId));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {

		if (hashPathwayLUT.containsKey(iItemId))
			return true;
			
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#registerItem(java.lang.Object, int, org.geneview.core.manager.type.ManagerObjectType)
	 */
	public boolean registerItem(Object registerItem, int itemId,
			ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#size()
	 */
	public int size() {

		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#unregisterItem(int, org.geneview.core.manager.type.ManagerObjectType)
	 */
	public boolean unregisterItem(int itemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}
}
