package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.view.swt.mixer.MixerViewRep;

/**
 * Class implements the command for creating a mixer view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateMixer
	extends ACmdExternalAttributes
{

	int iNumberOfSliders = 1;

	/**
	 * Constructor.
	 */
	public CmdViewCreateMixer(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand()
	{
		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		if (iExternalID != -1)
		{
			iParentContainerId = generalManager.getIDManager().getInternalFromExternalID(
					iParentContainerId);
		}

		MixerViewRep mixerView = (MixerViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_MIXER, iParentContainerId, sLabel);

		viewManager.registerItem(mixerView);

		mixerView.setAttributes(iNumberOfSliders);
		mixerView.initView();
		mixerView.drawView();

		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(mixerView.getID(),
					iExternalID);
		}

		commandManager.runDoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		parameterHandler.setValueAndTypeAndDefault("iNumberOfSliders", parameterHandler
				.getValueString(ECommandType.TAG_ATTRIBUTE1.getXmlKey()),
				IParameterHandler.ParameterHandlerType.INT, "-1");

		iNumberOfSliders = parameterHandler.getValueInt("iNumberOfSliders");
	}

	@Override
	public void undoCommand()
	{
		commandManager.runUndoCommand(this);
	}
}
