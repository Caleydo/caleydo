package org.geneview.core.manager.data;

import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.view.rep.jgraph.PathwayImageMap;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.pathway.EPathwayDatabaseType;
import org.geneview.core.manager.data.pathway.PathwayDatabase;
import org.geneview.util.graph.core.Graph;

/**
 * Interface for creating and accessing pathways.
 * 
 * @author Marc Streit
 *
 */
public interface IPathwayManager
extends IGeneralManager {

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
