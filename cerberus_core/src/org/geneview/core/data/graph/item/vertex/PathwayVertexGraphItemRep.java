package org.geneview.core.data.graph.item.vertex;

import org.geneview.graph.EGraphItemKind;
import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.item.GraphItem;

public class PathwayVertexGraphItemRep 
extends GraphItem {

	protected String sName;
	
	protected EPathwayVertexShape shape;
	
	protected int iHeight;
	
	protected int iWidth;
	
	protected int iXPosition;
	
	protected int iYPosition;

	public PathwayVertexGraphItemRep(
			final int iId,
			final String sName,
			final String sShapeType,
			final int iHeight,
			final int iWidth,
			final int iXPosition,
			final int iYPosition) {

		super(iId, EGraphItemKind.NODE);
		
		shape = EPathwayVertexShape.valueOf(sShapeType);
		
		this.sName = sName;
		this.iHeight = iHeight;
		this.iWidth = iWidth;
		this.iXPosition = iXPosition;
		this.iYPosition = iYPosition;
	}
	
	public String getName() {
		
		return sName;
	}
	
	public EPathwayVertexShape getShapeType() {
		
		return shape;
	}
	
	public int getHeight() {
		
		return iHeight;
	}
	
	public int getWidth() {
		
		return iWidth;
	}
	
	public int getXPosition() {
		
		return iXPosition;
	}
	
	public int getYPosition() {
		
		return iYPosition;
	}
	
	public PathwayVertexGraphItem getPathwayVertexGraphItem() {
		
		return ((PathwayVertexGraphItem)this.getAllItemsByProp(
				EGraphItemProperty.ALIAS_PARENT).toArray()[0]);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		return sName;
	}
}
