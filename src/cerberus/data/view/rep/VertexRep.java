package cerberus.data.view.rep;

public class VertexRep 
{
	private String sName;
	private int iHeight = 0;
	private int iWidth = 0;
	private int iXPosition = 0;
	private int iYPosition = 0;

	public VertexRep(String sName, int iHeight, int iWidth, 
			int iXPosition, int iYPosition) 
	{
		this.sName = sName;
		this.iHeight = iHeight;
		this.iWidth = iWidth;
		this.iXPosition = iXPosition;
		this.iYPosition = iYPosition;
	}

	public int getIHeight() 
	{
		return iHeight;
	}

	public int getIWidth() 
	{
		return iWidth;
	}

	public int getIXPosition() 
	{
		return iXPosition;
	}

	public int getIYPosition() 
	{
		return iYPosition;
	}

	public String getSName() 
	{
		return sName;
	}
}
