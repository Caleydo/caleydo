package cerberus.pathways.element;

public class Element 
{	
	private int iElementId = 0;
	//protected int iElementType = 0;
	private String sElementTitle = "";

	public Element()
	{}
	
	public Element(int iElementId, String sElementTitle)
	{
		this.iElementId = iElementId;
		//this.iElementType = iElementType;
		this.sElementTitle = sElementTitle;
	}

	public int getIElementId() 
	{
		return iElementId;
	}
}
