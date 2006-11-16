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

	/**
	 * Retrieves the slider set ID
	 * from the parameter handler
	 * and sets the local variable.
	 */
	public void extractAttributes() {
		
		//TODO: optimize this workflow!
		
		iSetId = refParameterHandler.getValueInt( "iSetId" );
	}
	
	public void update(Object eventTrigger) {
		
		if (eventTrigger instanceof IVirtualArray)
		{
			//iCurrentSliderValue = ((IVirtualArray)eventTrigger).getOffset();
			drawView();
		}
	}
}
