package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.slider.StorageSliderViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a slider view 
 * that is able to change storage data.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateStorageSlider 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
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
	public void doCommand() throws CerberusRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		StorageSliderViewRep sliderView = (StorageSliderViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_STORAGE_SLIDER,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				sliderView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		sliderView.setAttributes(iWidthX, iHeightY, iSetId);
		sliderView.retrieveGUIContainer();
		sliderView.initView();
		sliderView.drawView();
		
		refCommandManager.runDoCommand(this);
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
	
	public void undoCommand() throws CerberusRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}
