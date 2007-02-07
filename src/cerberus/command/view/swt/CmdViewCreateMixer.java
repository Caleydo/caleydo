package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.mixer.MixerViewRep;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a mixer view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateMixer 
extends ACmdCreate_IdTargetLabelParentXY
implements ICommand {
	
	int iNumberOfSliders = 1;
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateMixer(
			IGeneralManager refGeneralManager) {
		
		super(refGeneralManager);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		MixerViewRep mixerView = (MixerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_MIXER,
						iUniqueTargetId, 
						iParentContainerId, 
						sLabel);
		
		viewManager.registerItem(
				mixerView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		mixerView.setAttributes(iWidthX, iHeightY, iNumberOfSliders);
		mixerView.retrieveGUIContainer();
		mixerView.initView();
		mixerView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	

		refParameterHandler.setValueAndTypeAndDefault("iNumberOfSliders",
				refParameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey()),
				IParameterHandler.ParameterHandlerType.INT, "-1");
		
		iNumberOfSliders = refParameterHandler.getValueInt("iNumberOfSliders");
	}
	
	public void undoCommand() throws CerberusRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
