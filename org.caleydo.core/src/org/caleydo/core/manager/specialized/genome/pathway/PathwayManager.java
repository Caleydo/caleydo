package org.caleydo.core.manager.specialized.genome.pathway;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.view.rep.jgraph.PathwayImageMap;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.core.Graph;

/**
 * The pathway manager is in charge for creating and handling the pathways. The
 * class is implemented as a singleton.
 * 
 * @author Marc Streit
 */
public class PathwayManager
	extends AManager<PathwayGraph>
	implements IPathwayManager, Serializable
{
	private static final long serialVersionUID = 1L;

	private HashMap<Integer, Boolean> hashPathwayIdToVisibilityState;

	private HashMap<String, Integer> hashPathwayTitleToPathwayId;

	private HashMap<EPathwayDatabaseType, PathwayDatabase> hashPathwayDatabase;

	/**
	 * Root pathway contains all nodes that are loaded into the system.
	 * Therefore it represents the overall topological network. (The root
	 * pathway is independent from the representation of the nodes.)
	 */
	private Graph rootPathwayGraph;

	/**
	 * Used for pathways where only images can be loaded. The image map defines
	 * the clickable regions on that pathway image.
	 */
	private PathwayImageMap currentPathwayImageMap;

	private PathwayGraph currentPathwayGraph;

	/**
	 * Constructor.
	 */
	public PathwayManager()
	{
		hashPathwayTitleToPathwayId = new HashMap<String, Integer>();
		hashPathwayDatabase = new HashMap<EPathwayDatabaseType, PathwayDatabase>();
		hashPathwayIdToVisibilityState = new HashMap<Integer, Boolean>();

		rootPathwayGraph = new Graph(0);
	}

	public void createPathwayDatabase(final EPathwayDatabaseType type, final String sXMLPath,
			final String sImagePath, final String sImageMapPath)
	{
		// Check if requested pathway database is already loaded (e.g. using
		// caching)
		if (hashPathwayDatabase.containsKey(type))
			return;

		PathwayDatabase tmpPathwayDatabase = new PathwayDatabase(type, sXMLPath, sImagePath,
				sImagePath);

		hashPathwayDatabase.put(type, tmpPathwayDatabase);
		
		GeneralManager.get().getLogger().log(
				Level.INFO,
				"Setting pathway loading path: database-type:[" + type + "] " + "xml-path:["
						+ tmpPathwayDatabase.getXMLPath() + "] image-path:[" 
						+ tmpPathwayDatabase.getImagePath() + "] image-map-path:["
						+ tmpPathwayDatabase.getImageMapPath() + "]");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.IPathwayManager#triggerParsingPathwayDatabases
	 * ()
	 */
	public void triggerParsingPathwayDatabases()
	{
		new PathwayLoaderThread(hashPathwayDatabase.values());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.IPathwayManagerNew#createPathway(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public PathwayGraph createPathway(final EPathwayDatabaseType type, final String sName,
			final String sTitle, final String sImageLink, final String sExternalLink)
	{
		PathwayGraph pathway = new PathwayGraph(type, sName, sTitle, sImageLink,
				sExternalLink);

		registerItem(pathway);
		hashPathwayTitleToPathwayId.put(sTitle, pathway.getID());
		hashPathwayIdToVisibilityState.put(pathway.getID(), false);

		rootPathwayGraph.addGraph(pathway, EGraphItemHierarchy.GRAPH_CHILDREN);

		currentPathwayGraph = pathway;

		return pathway;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.specialized.genome.IPathwayManager#searchPathwayIdByName(java.lang.String)
	 */
	public int searchPathwayIdByName(final String sPathwayName)
	{
		Iterator<String> iterPathwayName = hashPathwayTitleToPathwayId.keySet().iterator();
		Pattern pattern = Pattern.compile(sPathwayName, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;
		String sTmpPathwayName;

		while (iterPathwayName.hasNext())
		{
			sTmpPathwayName = iterPathwayName.next();
			regexMatcher = pattern.matcher(sTmpPathwayName);

			if (regexMatcher.find())
			{
				return hashPathwayTitleToPathwayId.get(sTmpPathwayName);
			}
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#getRootPathway()
	 */
	public Graph getRootPathway()
	{
		return rootPathwayGraph;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.IPathwayManager#setPathwayVisibilityStateByID
	 * (int, boolean)
	 */
	public void setPathwayVisibilityStateByID(final int iPathwayID,
			final boolean bVisibilityState)
	{
		hashPathwayIdToVisibilityState.put(iPathwayID, bVisibilityState);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IPathwayManager#isPathwayVisible(int)
	 */
	public boolean isPathwayVisible(final int iPathwayID)
	{
		return hashPathwayIdToVisibilityState.get(iPathwayID);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.IPathwayManager#createPathwayImageMap(java
	 * .lang.String)
	 */
	public void createPathwayImageMap(final String sImageLink)
	{
		currentPathwayImageMap = new PathwayImageMap(sImageLink);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.manager.specialized.genome.IPathwayManager#
	 * getPathwayDatabaseByType
	 * (org.caleydo.core.manager.specialized.genome.pathway
	 * .EPathwayDatabaseType)
	 */
	public PathwayDatabase getPathwayDatabaseByType(EPathwayDatabaseType type)
	{
		return hashPathwayDatabase.get(type);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.manager.specialized.genome.IPathwayManager#
	 * getCurrenPathwayGraph()
	 */
	public PathwayGraph getCurrenPathwayGraph()
	{
		return currentPathwayGraph;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.IPathwayManager#getCurrentPathwayImageMap()
	 */
	public PathwayImageMap getCurrentPathwayImageMap()
	{
		return currentPathwayImageMap;
	}

}
