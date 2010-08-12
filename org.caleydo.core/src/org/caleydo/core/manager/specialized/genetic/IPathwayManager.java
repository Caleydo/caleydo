package org.caleydo.core.manager.specialized.genetic;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.specialized.genetic.pathway.IPathwayResourceLoader;
import org.caleydo.core.manager.specialized.genetic.pathway.PathwayDatabase;

/**
 * Interface for creating and accessing pathways.
 * 
 * @author Marc Streit
 */
public interface IPathwayManager
	extends IManager<PathwayGraph> {

	public PathwayGraph createPathway(final EPathwayDatabaseType type, final String sName,
		final String sTitle, final String sImageLink, final String sExternalLink);

	public void createPathwayDatabase(final EPathwayDatabaseType type, final String sXMLPath,
		final String sImagePath, final String sImageMapPath);

	public void triggerParsingPathwayDatabases();

	//public void createPathwayImageMap(final String sImageLink);

	//public PathwayImageMap getCurrentPathwayImageMap();

	public PathwayGraph searchPathwayByName(final String sPathwayName,
		EPathwayDatabaseType ePathwayDatabaseType);

	public PathwayDatabase getPathwayDatabaseByType(EPathwayDatabaseType type);

	public void setPathwayVisibilityState(final PathwayGraph pathway, final boolean bVisibilityState);

	public boolean isPathwayVisible(final PathwayGraph pathway);

	public void resetPathwayVisiblityState();

	public void notifyPathwayLoadingFinished(boolean bIsPathwayLoadingFinisched);

	public void waitUntilPathwayLoadingIsFinished();

	/**
	 * Obtains the pathway loading state, <code>true</code> means that pathway-loading is finished.
	 * 
	 * @return state of pathway loading
	 */
	public boolean isPathwayLoadingFinished();

	public void createPathwayResourceLoader(EPathwayDatabaseType type);

	public IPathwayResourceLoader getPathwayResourceLoader(EPathwayDatabaseType type);
}
