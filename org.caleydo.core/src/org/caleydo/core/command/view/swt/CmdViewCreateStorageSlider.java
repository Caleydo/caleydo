package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.slider.StorageSliderViewRep;

/**
 * Class implementes the command for creating a slider view 
 * that is able to change storage data.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateStorageSlider 
extends ACmdCreate_IdTargetLabelParentXY {
	
	protected int iSetId = 0;
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateStorageSlider(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByObjectType(ManagerObjectType.VIEW));
		
		StorageSliderViewRep sliderView = (StorageSliderViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_STORAGE_SLIDER,
							iUniqueId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				sliderView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		sliderView.setAttributes(iWidthX, iHeightY, iSetId);
		sliderView.initView();
		sliderView.drawView();
		
		commandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		refParameterHandler.setValueAndTypeAndDefault("iSetId",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.INT,
				"0");
		

		iSetId = refParameterHandler.getValueInt( "iSetId" );
	}
	
	public void undoCommand() throws CaleydoRuntimeException {

		commandManager.runUndoCommand(this);
	}
}
