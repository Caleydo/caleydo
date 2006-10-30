package cerberus.manager.data;

import java.util.HashMap;

import cerberus.data.pathway.Pathway;
import cerberus.data.view.rep.pathway.jgraph.PathwayImageMap;
import cerberus.manager.IGeneralManager;

public interface IPathwayManager 
extends IGeneralManager {

	public HashMap<Integer, Pathway> getPathwayLUT();

	public void createPathway(String sTitle, String sImageLink,
			String sLink, int iPathwayID);

	public Pathway getCurrentPathway();
	
	public void createPathwayImageMap(String sLink);
	
	public PathwayImageMap getCurrentPathwayImageMap();
}