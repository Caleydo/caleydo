package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.mixer.MixerViewRep;

/**
 * Class implements the command for creating a mixer view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateMixer
	extends ACmdCreate_IdTargetLabelParentXY
{

	int iNumberOfSliders = 1;

	/**
	 * Constructor.
	 */
	public CmdViewCreateMixer(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		if (iExternalID != -1)
		{
			iParentContainerId = 
				generalManager.getIDManager().getInternalFromExternalID(iParentContainerId);
		}
		
		MixerViewRep mixerView = (MixerViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_MIXER, iParentContainerId, sLabel);

		viewManager.registerItem(mixerView);

		mixerView.setAttributes(iWidthX, iHeightY, iNumberOfSliders);
		mixerView.initView();
		mixerView.drawView();
		
		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(mixerView.getID(), iExternalID);
		}

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		parameterHandler.setValueAndTypeAndDefault("iNumberOfSliders", parameterHandler
				.getValueString(CommandType.TAG_ATTRIBUTE1.getXmlKey()),
				IParameterHandler.ParameterHandlerType.INT, "-1");

		iNumberOfSliders = parameterHandler.getValueInt("iNumberOfSliders");
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
