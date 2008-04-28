package org.caleydo.core.manager.data;

import org.caleydo.core.data.graph.core.PathwayGraph;
import org.caleydo.core.data.view.rep.jgraph.PathwayImageMap;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.data.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.data.pathway.PathwayDatabase;
import org.caleydo.util.graph.core.Graph;

/**
 * Interface for creating and accessing pathways.
 * 
 * @author Marc Streit
 *
 */
public interface IPathwayManager
extends IManager
{
	public PathwayGraph createPathway(
			final EPathwayDatabaseType type,
			final String sName, 
			final String sTitle, 
			final String sImageLink,
			final String sExternalLink,
			final int iWidth,
			final int iHeight);
	
	public void createPathwayDatabase(final EPathwayDatabaseType type,
			final String sXMLPath,
			final String sImagePath,
			final String sImageMapPath);
	
	/**
	 * @deprecated Use loadAllPathwaysByType(EPathwayDatabaseType type) instead
	 */
	public boolean loadPathwayById(final int iPathwayID);
	
	public void loadAllPathwaysByType(final EPathwayDatabaseType type);
	
	public Graph getRootPathway();
	
	public void createPathwayImageMap(final String sImageLink);
	
	public PathwayImageMap getCurrentPathwayImageMap();
	
	public int searchPathwayIdByName(final String sPathwayName);
	
	public PathwayDatabase getPathwayDatabaseByType(EPathwayDatabaseType type);
	
	public void setPathwayVisibilityStateByID(final int iPathwayID,
			final boolean bVisibilityState);
	
	public boolean isPathwayVisible(final int iPathwayID);
}
