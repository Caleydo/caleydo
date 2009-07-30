package org.caleydo.core.manager.specialized.genetic.pathway;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.IPathwayManager;
import org.caleydo.core.parser.xml.sax.handler.specialized.pathway.PathwayImageMap;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.core.Graph;
import org.eclipse.core.runtime.Status;

/**
 * The pathway manager is in charge of creating and handling the pathways. The class is implemented as a
 * singleton.
 * 
 * @author Marc Streit
 */
public class PathwayManager
	extends AManager<PathwayGraph>
	implements IPathwayManager, Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<PathwayGraph, Boolean> hashPathwayToVisibilityState;

	private HashMap<String, PathwayGraph> hashPathwayTitleToPathway;

	private HashMap<EPathwayDatabaseType, PathwayDatabase> hashPathwayDatabase;

	/**
	 * Root pathway contains all nodes that are loaded into the system. Therefore it represents the overall
	 * topological network. (The root pathway is independent from the representation of the nodes.)
	 */
	private Graph rootPathwayGraph;

	/**
	 * Used for pathways where only images can be loaded. The image map defines the clickable regions on that
	 * pathway image.
	 */
	private PathwayImageMap currentPathwayImageMap;

	private PathwayGraph currentPathwayGraph;

	private boolean pathwayLoadingFinished;

	/**
	 * Constructor.
	 */
	public PathwayManager() {
		hashPathwayTitleToPathway = new HashMap<String, PathwayGraph>();
		hashPathwayDatabase = new HashMap<EPathwayDatabaseType, PathwayDatabase>();
		hashPathwayToVisibilityState = new HashMap<PathwayGraph, Boolean>();

		rootPathwayGraph = new Graph(0);
	}

	public void createPathwayDatabase(final EPathwayDatabaseType type, final String sXMLPath,
		final String sImagePath, final String sImageMapPath) {
		// Check if requested pathway database is already loaded (e.g. using
		// caching)
		if (hashPathwayDatabase.containsKey(type))
			return;

		PathwayDatabase tmpPathwayDatabase = new PathwayDatabase(type, sXMLPath, sImagePath, sImagePath);

		hashPathwayDatabase.put(type, tmpPathwayDatabase);

		GeneralManager.get().getLogger().log(new Status(Status.INFO, GeneralManager.PLUGIN_ID,
			"Setting pathway loading path: database-type:[" + type + "] " + "xml-path:["
				+ tmpPathwayDatabase.getXMLPath() + "] image-path:[" + tmpPathwayDatabase.getImagePath()
				+ "] image-map-path:[" + tmpPathwayDatabase.getImageMapPath() + "]"));
	}

	@Override
	public void triggerParsingPathwayDatabases() {
		new PathwayLoaderThread(hashPathwayDatabase.values());
	}

	@Override
	public PathwayGraph createPathway(final EPathwayDatabaseType type, final String sName,
		final String sTitle, final String sImageLink, final String sExternalLink) {
		PathwayGraph pathway = new PathwayGraph(type, sName, sTitle, sImageLink, sExternalLink);

		registerItem(pathway);
		hashPathwayTitleToPathway.put(sTitle, pathway);
		hashPathwayToVisibilityState.put(pathway, false);

		rootPathwayGraph.addGraph(pathway, EGraphItemHierarchy.GRAPH_CHILDREN);

		currentPathwayGraph = pathway;

		return pathway;
	}

	@Override
	public PathwayGraph searchPathwayByName(final String sPathwayName, EPathwayDatabaseType ePathwayDatabaseType) {
		waitUntilPathwayLoadingIsFinished();

		Iterator<String> iterPathwayName = hashPathwayTitleToPathway.keySet().iterator();
		Pattern pattern = Pattern.compile(sPathwayName, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;
		String sTmpPathwayName;

		while (iterPathwayName.hasNext()) {
			sTmpPathwayName = iterPathwayName.next();
			regexMatcher = pattern.matcher(sTmpPathwayName);

			if (regexMatcher.find()) {
				PathwayGraph pathway = hashPathwayTitleToPathway.get(sTmpPathwayName);

				// Ignore the found pathway if it has the same name but is
				// contained
				// in a different database
				if (getItem(pathway.getID()).getType() != ePathwayDatabaseType) {
					continue;
				}

				return pathway;
			}
		}

		return null;
	}

	public Graph getRootPathway() {
		return rootPathwayGraph;
	}

	@Override
	public Collection<PathwayGraph> getAllItems() {
		waitUntilPathwayLoadingIsFinished();

		return super.getAllItems();
	}

	@Override
	public void setPathwayVisibilityState(final PathwayGraph pathway, final boolean bVisibilityState) {
		waitUntilPathwayLoadingIsFinished();

		hashPathwayToVisibilityState.put(pathway, bVisibilityState);
	}

	@Override
	public void resetPathwayVisiblityState() {
		waitUntilPathwayLoadingIsFinished();

		for (PathwayGraph pathway : hashPathwayToVisibilityState.keySet()) {
			hashPathwayToVisibilityState.put(pathway, false);
		}
	}

	@Override
	public boolean isPathwayVisible(final PathwayGraph pathway) {
		waitUntilPathwayLoadingIsFinished();

		return hashPathwayToVisibilityState.get(pathway);
	}

	@Override
	public void createPathwayImageMap(final String sImageLink) {
		currentPathwayImageMap = new PathwayImageMap(sImageLink);
	}

	@Override
	public PathwayDatabase getPathwayDatabaseByType(EPathwayDatabaseType type) {
		return hashPathwayDatabase.get(type);
	}

	protected PathwayGraph getCurrenPathwayGraph() {
		return currentPathwayGraph;
	}

	@Override
	public PathwayImageMap getCurrentPathwayImageMap() {
		return currentPathwayImageMap;
	}

	public void notifyPathwayLoadingFinished(boolean pathwayLoadingFinished) {
		this.pathwayLoadingFinished = pathwayLoadingFinished;
	}

	public void waitUntilPathwayLoadingIsFinished() {
		while (!pathwayLoadingFinished) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				throw new IllegalThreadStateException("Pathway loader thread has been interrupted!");
			}
		}
	}

	@Override
	public boolean isPathwayLoadingFinished() {
		return pathwayLoadingFinished;
	}
}
