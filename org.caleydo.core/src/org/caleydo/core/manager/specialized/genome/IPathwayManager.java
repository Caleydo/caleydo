package org.caleydo.core.manager.specialized.genome;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayDatabase;
import org.caleydo.core.parser.xml.sax.handler.specialized.pathway.PathwayImageMap;

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

	public void createPathwayImageMap(final String sImageLink);

	public PathwayImageMap getCurrentPathwayImageMap();

	public PathwayGraph searchPathwayByName(final String sPathwayName, EPathwayDatabaseType ePathwayDatabaseType);

	public PathwayDatabase getPathwayDatabaseByType(EPathwayDatabaseType type);

	public void setPathwayVisibilityState(final PathwayGraph pathway, final boolean bVisibilityState);

	public boolean isPathwayVisible(final PathwayGraph pathway);

	public void resetPathwayVisiblityState();

	public void notifyPathwayLoadingFinished(boolean bIsPathwayLoadingFinisched);

	public void waitUntilPathwayLoadingIsFinished();
}
