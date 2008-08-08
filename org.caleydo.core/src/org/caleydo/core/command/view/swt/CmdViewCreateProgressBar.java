package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.progressbar.ProgressBarViewRep;

/**
 * Class implements the command for creating a progress bar view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateProgressBar
	extends ACmdCreate_IdTargetLabelParentXY
{

	int iProgressBarCurrentValue = 0;

	/**
	 * Constructor.
	 */
	public CmdViewCreateProgressBar(final CommandType cmdType)
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
		
		ProgressBarViewRep progressBarView = (ProgressBarViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_PROGRESS_BAR, iParentContainerId,
				sLabel);

		viewManager.registerItem(progressBarView);

		progressBarView.setAttributes(iProgressBarCurrentValue);
		progressBarView.initView();
		progressBarView.drawView();
		
		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(progressBarView.getID(), iExternalID);
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

		parameterHandler.setValueAndTypeAndDefault("iProgressBarCurrentValue",
				parameterHandler.getValueString(CommandType.TAG_DETAIL.getXmlKey()),
				IParameterHandler.ParameterHandlerType.INT, "0");

		iProgressBarCurrentValue = parameterHandler.getValueInt("iProgressBarCurrentValue");
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
