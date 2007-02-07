package cerberus.view.gui.swt.slider;

import cerberus.data.collection.IVirtualArray;
import cerberus.manager.IGeneralManager;

public class StorageSliderViewRep 
extends ASliderViewRep {
	
	protected int iSetId;
	
	public StorageSliderViewRep(IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}

	public void setAttributes(int iWidth, int iHeight, int iSetId) {
	
		super.setAttributes(iWidth, iHeight);
		
		this.iSetId = iSetId;
	}
	
	public void update(Object eventTrigger) {
		
		if (eventTrigger instanceof IVirtualArray)
		{
			//iCurrentSliderValue = ((IVirtualArray)eventTrigger).getOffset();
			drawView();
		}
	}
}
