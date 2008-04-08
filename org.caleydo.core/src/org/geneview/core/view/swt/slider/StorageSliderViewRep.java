package org.geneview.core.view.swt.slider;

import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.manager.IGeneralManager;

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
	
	public void updateReceiver(Object eventTrigger) {
		
		if (eventTrigger instanceof IVirtualArray)
		{
			//iCurrentSliderValue = ((IVirtualArray)eventTrigger).getOffset();
			drawView();
		}
	}
}
