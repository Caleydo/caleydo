package org.caleydo.core.manager.data.pathway;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.view.rep.jgraph.PathwayImageMap;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IPathwayManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.core.Graph;

/**
 * The pathway manager is in charge for 
 * creating and handling the pathways.
 * The class is implemented as a singleton.
 * 
 * @author Marc Streit
 *
 */
public class PathwayManager 
extends AManager
implements IPathwayManager, Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<Integer, PathwayGraph> hashPathwayIdToPathwayGraphLUT;
	
	private HashMap<Integer, Boolean> hashPathwayIdToVisibilityState;
	
	private HashMap<String, Integer> hashPathwayTitleToPathwayIdLUT;
	
	private HashMap<EPathwayDatabaseType, PathwayDatabase> hashPathwayDatabase;	
	
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
	private PathwayImageMap currentPathwayImageMap;
	
	/**
	 * Constructor.
	 */
	public PathwayManager(final IGeneralManager generalManager) {

		super(generalManager, 
			IGeneralManager.iUniqueId_TypeOffset_Pathways_Pathway,
			ManagerType.DATA_PATHWAY_ELEMENT );
		
		hashPathwayIdToPathwayGraphLUT = new HashMap<Integer, PathwayGraph>();
		hashPathwayTitleToPathwayIdLUT = new HashMap<String, Integer>();
		hashPathwayDatabase = new HashMap<EPathwayDatabaseType, PathwayDatabase>();
		hashPathwayIdToVisibilityState = new HashMap<Integer, Boolean>();
		
		rootPathwayGraph = new Graph(0);
	}
	
	public void createPathwayDatabase(final EPathwayDatabaseType type,
			final String sXMLPath, 
			final String sImagePath,
			final String sImageMapPath) {
		
		// Check if requested pathway database is already loaded (e.g. using caching)
		if (hashPathwayDatabase.containsKey(type))
			return;
		
		PathwayDatabase tmpPathwayDatabase = new PathwayDatabase(type,
				sXMLPath, sImagePath, sImagePath);
		
		hashPathwayDatabase.put(type, tmpPathwayDatabase);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#triggerParsingPathwayDatabases()
	 */
	public void triggerParsingPathwayDatabases() 
	{
		new PathwayLoaderThread(generalManager, hashPathwayDatabase.values());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManagerNew#createPathway(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public PathwayGraph createPathway(
			final EPathwayDatabaseType type,
			final String sName,
			final String sTitle, 
			final String sImageLink, 
			final String sExternalLink) {

//		if (hashPathwayIdToPathwayGraphLUT.containsKey(iKEGGId))
//			return hashPathwayIdToPathwayGraphLUT.get(iKEGGId);

		int iPathwayId = this.createId(null);
		
		PathwayGraph pathway = new PathwayGraph(
				type, iPathwayId, sName, sTitle, sImageLink, sExternalLink);

		hashPathwayIdToPathwayGraphLUT.put(iPathwayId, pathway);
		hashPathwayTitleToPathwayIdLUT.put(sTitle, iPathwayId);
		hashPathwayIdToVisibilityState.put(iPathwayId, false);
		
		rootPathwayGraph.addGraph(pathway, EGraphItemHierarchy.GRAPH_CHILDREN);
	
		return pathway;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#loadPathwayById(int)
	 */
//	public boolean loadPathwayById(final int iPathwayID) {
//		
//		// Check if pathway was previously loaded
//		if (hashPathwayIdToPathwayGraphLUT.containsKey(iPathwayID))
//		{
//			generalManager.getLogger().log(
//					Level.INFO, "Pathway " +iPathwayID +" is already loaded. SKIP.");
//			
//			return true;
//		}
//
//		String sPathwayFilePath = "";
//		boolean bLoadingOK = false;
//		
//		if (iPathwayID < 10)
//		{
//			sPathwayFilePath = "hsa0000" + Integer.toString(iPathwayID);
//		}
//		else if (iPathwayID < 100 && iPathwayID >= 10)
//		{
//			sPathwayFilePath = "hsa000" + Integer.toString(iPathwayID);
//		}
//		else if (iPathwayID < 1000 && iPathwayID >= 100)
//		{
//			sPathwayFilePath = "hsa00" + Integer.toString(iPathwayID);
//		}
//		else if (iPathwayID < 10000 && iPathwayID >= 1000)
//		{
//			sPathwayFilePath = "hsa0" + Integer.toString(iPathwayID);
//		}
//		
//		sPathwayFilePath = hashPathwayDatabase.get(EPathwayDatabaseType.KEGG).getXMLPath() + sPathwayFilePath +".xml";		
//		
//		bLoadingOK = generalManager.getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
//
//		if (bLoadingOK)
//			return true;
//
//		generalManager.getLogger().log(
//				Level.WARNING, "No HSA pathway available. Try loading reference pathway instead.");
//		
//		// Replace HSA with MAP and therefore try to load reference pathway
//		sPathwayFilePath = sPathwayFilePath.replace("hsa", "map");
//		
//		return generalManager.getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
//	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#searchPathwayByName(java.lang.String)
	 */
	public int searchPathwayIdByName(final String sPathwayName) {
		
		Iterator<String> iterPathwayName = hashPathwayTitleToPathwayIdLUT.keySet().iterator();
		Pattern pattern = Pattern.compile(sPathwayName, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;
		String sTmpPathwayName;
		
		while(iterPathwayName.hasNext())
		{
			sTmpPathwayName = iterPathwayName.next();
			regexMatcher = pattern.matcher(sTmpPathwayName);
			
			if(regexMatcher.find()) 
			{
				return hashPathwayTitleToPathwayIdLUT.get(sTmpPathwayName);
			}
		}
		
		return -1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#getRootPathway()
	 */
	public Graph getRootPathway() {
		
		return rootPathwayGraph;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#setPathwayVisibilityStateByID(int, boolean)
	 */
	public void setPathwayVisibilityStateByID(final int iPathwayID,
			final boolean bVisibilityState) {
		
		hashPathwayIdToVisibilityState.put(iPathwayID, bVisibilityState);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#isPathwayVisible(int)
	 */
	public boolean isPathwayVisible(final int iPathwayID) {
		
		return hashPathwayIdToVisibilityState.get(iPathwayID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#createPathwayImageMap(java.lang.String)
	 */
	public void createPathwayImageMap(final String sImageLink) {
		
		currentPathwayImageMap = new PathwayImageMap(sImageLink);
	}
	
	public PathwayDatabase getPathwayDatabaseByType(EPathwayDatabaseType type) {
		
		return hashPathwayDatabase.get(type);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#getCurrentPathwayImageMap()
	 */
	public PathwayImageMap getCurrentPathwayImageMap () {
		
		return currentPathwayImageMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {

		return(hashPathwayIdToPathwayGraphLUT.get(iItemId));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {

		if (hashPathwayIdToPathwayGraphLUT.containsKey(iItemId))
			return true;
			
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#registerItem(java.lang.Object, int, org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public boolean registerItem(Object registerItem, int itemId,
			ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#size()
	 */
	public int size() {

		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#unregisterItem(int, org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public boolean unregisterItem(int itemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}
}
