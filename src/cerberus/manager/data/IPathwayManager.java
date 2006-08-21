package cerberus.manager.data;

import java.util.HashMap;

import cerberus.data.pathway.Pathway;

public interface IPathwayManager
{

	public abstract HashMap<Integer, Pathway> getPathwayLUT();

	public abstract void createPathway(String sTitle, String sImageLink,
			String sLink, int iPathwayID);

	public abstract Pathway getCurrentPathway();

}