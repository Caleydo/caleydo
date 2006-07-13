package cerberus.pathways.element;

import java.util.Map;

public class Element 
{	
	protected int iElementID = 0;
	protected int iElementType = 0;
	protected String sTitle = "";

	public int getElementID()
	{
		return iElementID;
	}
	
	public void setElementID(int iElementID)
	{
	  this.iElementID = iElementID;	
	}
	
	public String getElementTitle()
	{
		return sTitle;
	}
}
