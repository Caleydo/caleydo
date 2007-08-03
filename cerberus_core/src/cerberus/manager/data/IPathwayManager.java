package cerberus.manager.data;

import java.util.HashMap;
import java.util.Iterator;

import cerberus.data.pathway.Pathway;
import cerberus.data.view.rep.pathway.jgraph.PathwayImageMap;
import cerberus.manager.IGeneralManager;


public interface IPathwayManager 
extends IGeneralManager {

	public HashMap<Integer, Pathway> getPathwayLUT();

	public void createPathway(String sName, 
			String sTitle, String sImageLink,
			String sLink, int iPathwayID);

	public Iterator<Pathway> getPathwayIterator();
	
	public void createPathwayImageMap(String sLink);
	
	/**
	 * Methods checks if pathway is already loaded.
	 * If not the pathway will be loaded.
	 * If the pathway doesn't exist FALSE will be returned.
	 * If everything is ok TRUE will be returned.
	 */
	public boolean loadPathwayById(int iPathwayID);
	
	public PathwayImageMap getCurrentPathwayImageMap();
	
	public String getPathwayXMLPath();
	
	public void setPathwayXMLPath(String sPathwayXMLPath);
	
	public String getPathwayImageMapPath();
	
	public void setPathwayImageMapPath(String sPathwayImageMapPath);
	
	public String getPathwayImagePath();
	
	public void setPathwayImagePath(String sPathwayImagePath);	
}