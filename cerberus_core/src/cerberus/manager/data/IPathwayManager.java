package cerberus.manager.data;

import org.geneview.graph.core.Graph;

import cerberus.data.graph.core.PathwayGraph;
import cerberus.data.view.rep.pathway.jgraph.PathwayImageMap;
import cerberus.manager.IGeneralManager;


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
}
