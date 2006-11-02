package cerberus.data.view.rep.pathway.jgraph;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;

public class PathwayVertexRep 
implements IPathwayVertexRep {

	protected PathwayVertex refVertex;
	protected String sName;
	protected String sShapeType;
	protected int iHeight = 0;
	protected int iWidth = 0;
	protected int iXPosition = 0;
	protected int iYPosition = 0;

	/**
	 * Constructor.
	 */
	public PathwayVertexRep(
			PathwayVertex refVertex,
			String sName, 
			int iHeight,
			int iWidth, 
			int iXPosition, 
			int iYPosition, 
			String sShapeType) {
		
		this.refVertex = refVertex;
		this.sName = sName;
		this.iHeight = iHeight;
		this.iWidth = iWidth;
		this.iXPosition = iXPosition;
		this.iYPosition = iYPosition;
		this.sShapeType = sShapeType;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.IPathwayVertexRep#getHeight()
	 */
	public int getHeight() {
		return iHeight;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.IPathwayVertexRep#getWidth()
	 */
	public int getWidth() {
		return iWidth;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.jgraph.VertexRepInter#getXPosition()
	 */
	public int getXPosition() {
		return iXPosition;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.jgraph.VertexRepInter#getYPosition()
	 */
	public int getYPosition() {	
		return iYPosition;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.jgraph.VertexRepInter#getName()
	 */
	public String getName() {
		return sName;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.IPathwayVertexRep#getType()
	 */
	public String getShapeType() {	
		return sShapeType;
	}	
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.view.rep.pathway.IPathwayVertexRep#getVertex()
	 */
	public PathwayVertex getVertex() {
		
		return refVertex;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		return sName;
	}
}
