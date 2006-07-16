package cerberus.pathways;

import java.util.HashMap;

public class Pathway 
{
	protected int iPathwayID;
	protected String sTitle;
	protected String sImageLink;
	protected String sInformationLink;
	
	protected HashMap elementsLUT;

	public Pathway(String sTitle, String sImageLink, String sLink, int iPathwayID)
	{
		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sInformationLink = sLink;
		this.iPathwayID = iPathwayID;
	}
}
