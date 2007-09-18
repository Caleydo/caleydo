package org.geneview.core.util.slerp;

import org.geneview.core.view.opengl.canvas.pathway.JukeboxHierarchyLayer;

public class SlerpAction {

	private int iElementId = -1;
	
	private JukeboxHierarchyLayer originHierarchyLayer;
	
	private JukeboxHierarchyLayer destinationHierarchyLayer;
	
	private boolean bSlerpUpInHierarchy = true;

	private int iOriginPosIndex = -1;
	
	private int iDestinationPosIndex = -1;

	public SlerpAction(int iElementId, 
			JukeboxHierarchyLayer originHierarchyLayer,
			boolean bSlerpUpInHierarchy) {
		
		this.iElementId = iElementId;
		this.originHierarchyLayer = originHierarchyLayer;
		this.bSlerpUpInHierarchy = bSlerpUpInHierarchy;
		
		iOriginPosIndex = originHierarchyLayer.getPositionIndexByElementId(iElementId);
		
		if (bSlerpUpInHierarchy)
			destinationHierarchyLayer = originHierarchyLayer.getParentLayer();
		else
			destinationHierarchyLayer = originHierarchyLayer.getChildLayer(); 
		
		// If pathway is already in this layer - slerp to the existing position.
		if (destinationHierarchyLayer.containsElement(iElementId))
			this.iDestinationPosIndex = destinationHierarchyLayer.getPositionIndexByElementId(iElementId);
		else
			this.iDestinationPosIndex = destinationHierarchyLayer.getNextPositionIndex();

		// Only add element if it is not contained already in this layer
		if (!destinationHierarchyLayer.containsElement(iElementId))
		{
			destinationHierarchyLayer.addElement(iElementId);
		}
	}
	
	public int getElementId() {
	
		return iElementId;
	}

	public JukeboxHierarchyLayer getOriginHierarchyLayer() {
	
		return originHierarchyLayer;
	}
	
	public boolean isSlerpUpInHierarchy() {
	
		return bSlerpUpInHierarchy;
	}
	
	public int getOriginPosIndex() {
	
		return iOriginPosIndex;
	}

	public int getDestinationPosIndex() {
	
		return iDestinationPosIndex;
	}
	
	public JukeboxHierarchyLayer getDestinationHierarchyLayer() {
		return destinationHierarchyLayer;
	}
}
