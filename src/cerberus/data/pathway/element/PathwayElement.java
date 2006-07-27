package cerberus.data.pathway.element;

public class PathwayElement 
{	
	private int iElementId = 0;
	//protected int iElementType = 0;
	private String sElementTitle = "";

	public PathwayElement()
	{}
	
	public PathwayElement(int iElementId, String sElementTitle)
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
