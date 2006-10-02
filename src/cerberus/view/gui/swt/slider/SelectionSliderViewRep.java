package cerberus.view.gui.swt.slider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import cerberus.data.collection.ISelection;
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
		
	    refSlider.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				ISelection tmpSelection =
					refGeneralManager.getSingelton().getSelectionManager()
						.getItemSelection(iSelectionId);
				
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
		
		int triggerId = ((ISelection)eventTrigger).getId();	
		refGeneralManager.getSingelton().getLoggerManager().logMsg( 
				"Slider update called by " +triggerId,
				LoggerType.VERBOSE );
		
		if (eventTrigger instanceof ISelection)
		{
			if (sSelectionFieldName.equals("length"))
			{
				iCurrentSliderValue = ((ISelection)eventTrigger).length();
			}
			else if (sSelectionFieldName.equals("offset"))
			{
				iCurrentSliderValue = ((ISelection)eventTrigger).getOffset();
			}
			else if (sSelectionFieldName.equals("multioffset"))
			{
				iCurrentSliderValue = ((ISelection)eventTrigger).getMultiOffset();
			}
			else if (sSelectionFieldName.equals("multirepeat"))
			{
				iCurrentSliderValue = ((ISelection)eventTrigger).getMultiRepeat();
			}
			
			drawView();
		}
	}
}
