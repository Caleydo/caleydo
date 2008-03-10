package org.geneview.core.util.slerp;

import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * Slerp action in 3D scene.
 * 
 * @author Marc Streit
 *
 */
public class SlerpAction {

	private int iElementId = -1;
	
	private JukeboxHierarchyLayer originHierarchyLayer;
	
	private JukeboxHierarchyLayer destinationHierarchyLayer;

	private int iOriginPosIndex = -1;
	
	private int iDestinationPosIndex = -1;

	/**
	 * 
	 * Constructor.
	 * 
	 * @param iElementId
	 * @param originHierarchyLayer
	 * @param bSlerpUpInHierarchy
	 */
	public SlerpAction(int iElementId, 
			JukeboxHierarchyLayer originHierarchyLayer,
			boolean bSlerpUpInHierarchy) {
		
		if (bSlerpUpInHierarchy)
			destinationHierarchyLayer = originHierarchyLayer.getParentLayer();
		else
			destinationHierarchyLayer = originHierarchyLayer.getChildLayer(); 
		
		init(iElementId, originHierarchyLayer, destinationHierarchyLayer);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param iElementId
	 * @param originHierarchyLayer
	 * @param destinationHierarchyLayer
	 */
	public SlerpAction(int iElementId, 
			JukeboxHierarchyLayer originHierarchyLayer,
			JukeboxHierarchyLayer destinationHierarchyLayer) {
		
		init(iElementId, originHierarchyLayer, destinationHierarchyLayer);
	}
	
	private void init(int iElementId, 
			JukeboxHierarchyLayer originHierarchyLayer,
			JukeboxHierarchyLayer destinationHierarchyLayer) {
		
		this.iElementId = iElementId;
		this.originHierarchyLayer = originHierarchyLayer;
		this.destinationHierarchyLayer = destinationHierarchyLayer;
	}
	
	public void start() {
	
		iOriginPosIndex = originHierarchyLayer.getPositionIndexByElementId(iElementId);
		
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
