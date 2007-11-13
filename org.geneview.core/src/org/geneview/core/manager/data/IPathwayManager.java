package org.geneview.core.manager.data;

import org.geneview.util.graph.core.Graph;

import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.view.rep.pathway.jgraph.PathwayImageMap;
import org.geneview.core.manager.IGeneralManager;


public interface IPathwayManager
extends IGeneralManager {

	public PathwayGraph createPathway(
			final int iKeggId,
			final String sName, 
			final String sTitle, 
			final String sImageLink,
			final String sExternalLink);
	
	public boolean loadPathwayById(final int iPathwayID);
	
	public Graph getRootPathway();
	
	public String getPathwayXMLPath();
	
	public void setPathwayXMLPath(final String sPathwayXMLPath);
	
	public String getPathwayImageMapPath();
	
	public void setPathwayImageMapPath(final String sPathwayImageMapPath);
	
	public String getPathwayImagePath();
	
	public void setPathwayImagePath(final String sPathwayImagePath); 
	
	public void createPathwayImageMap(final String sImageLink);
	
	public PathwayImageMap getCurrentPathwayImageMap();
	
	public int searchPathwayIdByName(final String sPathwayName);
}
