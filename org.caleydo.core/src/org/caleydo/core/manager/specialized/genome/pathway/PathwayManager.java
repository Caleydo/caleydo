package org.caleydo.core.manager.specialized.genome.pathway;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.view.rep.jgraph.PathwayImageMap;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.manager.type.EManagerType;
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
	
	private PathwayGraph currentPathwayGraph;
	
	/**
	 * Constructor.
	 */
	public PathwayManager(final IGeneralManager generalManager) {

		super(generalManager, 
			IGeneralManager.iUniqueId_TypeOffset_Pathways_Pathway,
			EManagerType.DATA_PATHWAY_ELEMENT );
		
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

		int iPathwayId = sName.hashCode();//this.createId(null);
		
		PathwayGraph pathway = new PathwayGraph(
				type, iPathwayId, sName, sTitle, sImageLink, sExternalLink);

		hashPathwayIdToPathwayGraphLUT.put(iPathwayId, pathway);
		hashPathwayTitleToPathwayIdLUT.put(sTitle, iPathwayId);
		hashPathwayIdToVisibilityState.put(iPathwayId, false);
		
		rootPathwayGraph.addGraph(pathway, EGraphItemHierarchy.GRAPH_CHILDREN);
	
		currentPathwayGraph = pathway;
		
		return pathway;
	}

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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.specialized.genome.IPathwayManager#getPathwayDatabaseByType(org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType)
	 */
	public PathwayDatabase getPathwayDatabaseByType(EPathwayDatabaseType type) {
		
		return hashPathwayDatabase.get(type);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.specialized.genome.IPathwayManager#getCurrenPathwayGraph()
	 */
	public PathwayGraph getCurrenPathwayGraph() {
		
		return currentPathwayGraph;
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
	public boolean registerItem(Object registerItem, int itemId) {

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
	public boolean unregisterItem(int itemId) {

		// TODO Auto-generated method stub
		return false;
	}
}
