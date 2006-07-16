package cerberus.pathways.element;

public class VertexRepresentation 
{
	private String sName;
	private int iHeight = 0;
	private int iWidth = 0;
	private int iXPosition = 0;
	private int iYPosition = 0;

	public VertexRepresentation(String sName, int iHeight, int iWidth, 
			int iXPosition, int iYPosition) 
	{
		this.sName = sName;
		this.iHeight = iHeight;
		this.iWidth = iWidth;
		this.iXPosition = iXPosition;
		this.iYPosition = iYPosition;
	}
}
