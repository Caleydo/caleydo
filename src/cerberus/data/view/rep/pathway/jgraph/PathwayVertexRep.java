package cerberus.data.view.rep.pathway.jgraph;

import cerberus.data.view.rep.pathway.IPathwayVertexRep;

public class PathwayVertexRep implements IPathwayVertexRep 
{
	private String sName;
	private int iHeight = 0;
	private int iWidth = 0;
	private int iXPosition = 0;
	private int iYPosition = 0;

	public PathwayVertexRep(String sName, int iHeight, int iWidth, 
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

	/* (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.jgraph.VertexRepInter#getIXPosition()
	 */
	public int getIXPosition() 
	{
		return iXPosition;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.jgraph.VertexRepInter#getIYPosition()
	 */
	public int getIYPosition() 
	{
		return iYPosition;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.jgraph.VertexRepInter#getSName()
	 */
	public String getSName() 
	{
		return sName;
	}
}
