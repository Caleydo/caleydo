package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.mixer.MixerViewRep;

/**
 * Class implementes the command for creating a mixer view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateMixer 
extends ACmdCreate_IdTargetLabelParentXY {
	
	int iNumberOfSliders = 1;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdViewCreateMixer(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {
		
		super(generalManager, 
				commandManager,
				commandQueueSaxType);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByObjectType(ManagerObjectType.VIEW));
		
		MixerViewRep mixerView = (MixerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_MIXER,
						iUniqueId, 
						iParentContainerId, 
						sLabel);
		
		viewManager.registerItem(
				mixerView, 
				iUniqueId);

		mixerView.setAttributes(iWidthX, iHeightY, iNumberOfSliders);
		mixerView.initView();
		mixerView.drawView();
		
		commandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		
		assert parameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(parameterHandler);	

		parameterHandler.setValueAndTypeAndDefault("iNumberOfSliders",
				parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey()),
				IParameterHandler.ParameterHandlerType.INT, "-1");
		
		iNumberOfSliders = parameterHandler.getValueInt("iNumberOfSliders");
	}
	
	public void undoCommand() throws CaleydoRuntimeException {
		
		commandManager.runUndoCommand(this);
	}
}
