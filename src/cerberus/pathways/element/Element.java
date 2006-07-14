package cerberus.pathways.element;

public class Element 
{	
	protected int iElementID = 0;
	protected int iElementType = 0;
	protected String sElementTitle = "";

	public Element()
	{}
	
	public Element(int iElementID, int iElementType, String sElementTitle)
	{
		this.iElementID = iElementID;
		this.iElementType = iElementType;
		this.sElementTitle = sElementTitle;
	}
}
