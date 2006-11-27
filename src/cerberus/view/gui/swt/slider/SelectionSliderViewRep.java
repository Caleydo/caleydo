package cerberus.view.gui.swt.slider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import cerberus.data.collection.IVirtualArray;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;

public class SelectionSliderViewRep 
extends ASliderViewRep {

	protected int iSelectionId;
	
	protected String sSelectionFieldName;
	
	public SelectionSliderViewRep(IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}
	
	public void initView() {
		
		super.initView();
		
		IVirtualArray tmpSelection =
			refGeneralManager.getSingelton().getVirtualArrayManager()
				.getItemSelection(iSelectionId);
		
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
					refGeneralManager.getSingelton().getVirtualArrayManager()
						.getItemSelection(iSelectionId);
				
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
	
	/**
	 * Retrieves the slider selection ID 
	 * from the parameter handler
	 * and sets the local variable.
	 */
	public void extractAttributes() {
		
		//TODO: optimize this workflow!
		
		iSelectionId = refParameterHandler.getValueInt( "iSelectionId" );
		
		sSelectionFieldName = refParameterHandler.getValueString( "sSelectionFieldName" );
	}
	
	// TODO: retrieve locking token!
	public void update(Object eventTrigger) {
		
		int triggerId = ((IVirtualArray)eventTrigger).getId();	
		refGeneralManager.getSingelton().getLoggerManager().logMsg( 
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
