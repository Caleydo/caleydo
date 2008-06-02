package org.caleydo.core.view.swt.slider;

import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.manager.IGeneralManager;

public class StorageSliderViewRep 
extends ASliderViewRep {
	
	protected int iSetId;
	
	public StorageSliderViewRep(IGeneralManager generalManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(generalManager, iViewId, iParentContainerId, sLabel);
	}

	public void setAttributes(int iWidth, int iHeight, int iSetId) {
	
		super.setAttributes(iWidth, iHeight);
		
		this.iSetId = iSetId;
	}
	
	public void updateReceiver(Object eventTrigger) {
		
		if (eventTrigger instanceof IVirtualArray)
		{
			//iCurrentSliderValue = ((IVirtualArray)eventTrigger).getOffset();
			drawView();
		}
	}
}
