package org.caleydo.core.view.swt.slider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;

public class SelectionSliderViewRep 
extends ASliderViewRep {

	protected int iSelectionId = 0;
	
	protected String sSelectionFieldName = "";
	
	public SelectionSliderViewRep(IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}
	
	protected void initViewSwtComposit(Composite swtContainer) {
		
		super.initViewSwtComposit(swtContainer);
		
		IVirtualArray tmpSelection =
			generalManager.getVirtualArrayManager()
				.getItemVirtualArray(iSelectionId);
		
		if (sSelectionFieldName.equals("length"))
		{
			iCurrentSliderValue = tmpSelection.length();
		}
		else if (sSelectionFieldName.equals("offset"))
		{
			iCurrentSliderValue = tmpSelection.getOffset();
		}
		else if (sSelectionFieldName.equals("multioffset"))
		{
			iCurrentSliderValue = tmpSelection.getMultiOffset();
		}
		else if (sSelectionFieldName.equals("multirepeat"))
		{
			iCurrentSliderValue = tmpSelection.getMultiRepeat();
		}
		
	    refSlider.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				IVirtualArray tmpSelection =
					generalManager.getVirtualArrayManager()
						.getItemVirtualArray(iSelectionId);
				
				tmpSelection.getWriteToken();
				
				if (sSelectionFieldName.equals("length"))
				{
					tmpSelection.setLength(refSlider.getSelection());
				}
				else if (sSelectionFieldName.equals("offset"))
				{
					tmpSelection.setOffset(refSlider.getSelection());
				}
				else if (sSelectionFieldName.equals("multioffset"))
				{
					tmpSelection.setMultiOffset(refSlider.getSelection());
				}
				else if (sSelectionFieldName.equals("multirepeat"))
				{
					tmpSelection.setMultiRepeat(refSlider.getSelection());
				}	
				
				tmpSelection.returnWriteToken();
			}
		});
	}
	
	public void setAttributes(int iWidth, int iHeight,
			int iSelectionId, String sSelectionFieldNumber) {
		
		super.setAttributes(iWidth, iHeight);
		
		this.iSelectionId = iSelectionId;
		this.sSelectionFieldName = sSelectionFieldNumber;
	}
	
	// TODO: retrieve locking token!
	public void updateReceiver(Object eventTrigger) {
		
		int triggerId = ((IVirtualArray)eventTrigger).getId();	
		generalManager.logMsg( 
				"Slider update called by " +triggerId,
				LoggerType.VERBOSE );
		
		if (eventTrigger instanceof IVirtualArray)
		{	
			if (sSelectionFieldName.equals("length"))
			{
				iCurrentSliderValue = ((IVirtualArray)eventTrigger).length();
			}
			else if (sSelectionFieldName.equals("offset"))
			{
				iCurrentSliderValue = ((IVirtualArray)eventTrigger).getOffset();
			}
			else if (sSelectionFieldName.equals("multioffset"))
			{
				iCurrentSliderValue = ((IVirtualArray)eventTrigger).getMultiOffset();
			}
			else if (sSelectionFieldName.equals("multirepeat"))
			{
				iCurrentSliderValue = ((IVirtualArray)eventTrigger).getMultiRepeat();
			}
			
			drawView();
		}
	}
}
