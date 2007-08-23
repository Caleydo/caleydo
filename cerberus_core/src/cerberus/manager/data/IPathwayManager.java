package cerberus.manager.data;

import org.geneview.graph.core.Graph;

import cerberus.data.graph.core.PathwayGraph;
import cerberus.manager.IGeneralManager;


public interface IPathwayManager
extends IGeneralManager {

	public PathwayGraph createPathway(
			final int iKeggId,
			final String sName, 
			final String sTitle, 
			final String sImageLink,
			final String sExternalLink);
	
	public boolean loadPathwayById(int iPathwayID);
	
	public Graph getRootPathway();
	
	public String getPathwayXMLPath();
	
	public void setPathwayXMLPath(String sPathwayXMLPath);
	
	public String getPathwayImageMapPath();
	
	public void setPathwayImageMapPath(String sPathwayImageMapPath);
	
	public String getPathwayImagePath();
	
	public void setPathwayImagePath(String sPathwayImagePath); 
}
