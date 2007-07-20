package cerberus.util.slerp;

import cerberus.application.core.CerberusBootloader;
import cerberus.view.gui.opengl.canvas.pathway.JukeboxHierarchyLayer;


public class SlerpAction {

	private int iElementId = -1;
	
	private JukeboxHierarchyLayer originHierarchyLayer;
	
	private JukeboxHierarchyLayer destinationHierarchyLayer;
	
	private boolean bSlerpUpInHierarchy = true;

	private boolean bSlerpDone = false;
	
	private boolean bReverseSlerp = false;
	
	private int iOriginPosIndex = -1;
	
	private int iDestinationPosIndex = -1;

	public SlerpAction(int iElementId, 
			JukeboxHierarchyLayer originHierarchyLayer,
			boolean bSlerpUpInHierarchy,
			boolean bReverseSlerp,
			int iDestinationPosIndex) {
		
		this.iElementId = iElementId;
		this.originHierarchyLayer = originHierarchyLayer;
		this.bSlerpUpInHierarchy = bSlerpUpInHierarchy;
		this.bReverseSlerp = bReverseSlerp;
		
		iOriginPosIndex = originHierarchyLayer.getPositionIndexByElementId(iElementId);
		
		if (bSlerpUpInHierarchy)
			destinationHierarchyLayer = originHierarchyLayer.getParentLayer();
		else
			destinationHierarchyLayer = originHierarchyLayer.getChildLayer(); 
		
		// If pathway is already in this layer - slerp to the existing position.
		if (destinationHierarchyLayer.containsElement(iElementId))
			this.iDestinationPosIndex = destinationHierarchyLayer.getPositionIndexByElementId(iElementId);
		else
			this.iDestinationPosIndex = iDestinationPosIndex;
	}
	
	public SlerpAction(int iElementId, 
			JukeboxHierarchyLayer originHierarchyLayer,
			JukeboxHierarchyLayer destinationHierarchyLayer,
			boolean bReverseSlerp,
			int iDestinationPosIndex) {
		
		this.iElementId = iElementId;
		this.originHierarchyLayer = originHierarchyLayer;
		this.bSlerpUpInHierarchy = true;
		this.bReverseSlerp = bReverseSlerp;
		
		iOriginPosIndex = originHierarchyLayer.getPositionIndexByElementId(iElementId);
		this.destinationHierarchyLayer = destinationHierarchyLayer;
		
		// If pathway is already in this layer - slerp to the existing position.
		if (destinationHierarchyLayer.containsElement(iElementId))
			this.iDestinationPosIndex = destinationHierarchyLayer.getPositionIndexByElementId(iElementId);
		else
			this.iDestinationPosIndex = iDestinationPosIndex;
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
	
	public boolean isSlerpDone() {
	
		return bSlerpDone;
	}
	
	public boolean isReversSlerp() {
		
		return bReverseSlerp;
	}
	
	public int getOriginPosIndex() {
	
		return iOriginPosIndex;
	}

	public int getDestinationPosIndex() {
	
		return iDestinationPosIndex;
	}
	
	public void setSlerpDone(boolean bSlerpDone) {

		this.bSlerpDone = bSlerpDone;
		
		if (!destinationHierarchyLayer.containsElement(iElementId))
			destinationHierarchyLayer.setElement(iDestinationPosIndex, iElementId);
	}
	
	public JukeboxHierarchyLayer getDestinationHierarchyLayer() {
		return destinationHierarchyLayer;
	}
}
