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
		
		MixerViewRep mixerView = (MixerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_MIXER,
						iUniqueId, 
						iParentContainerId, 
						sLabel);
		
		viewManager.registerItem(
				mixerView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		mixerView.setAttributes(iWidthX, iHeightY, iNumberOfSliders);
		mixerView.initView();
		mixerView.drawView();
		
		commandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	

		refParameterHandler.setValueAndTypeAndDefault("iNumberOfSliders",
				refParameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey()),
				IParameterHandler.ParameterHandlerType.INT, "-1");
		
		iNumberOfSliders = refParameterHandler.getValueInt("iNumberOfSliders");
	}
	
	public void undoCommand() throws CaleydoRuntimeException {
		
		commandManager.runUndoCommand(this);
	}
}
