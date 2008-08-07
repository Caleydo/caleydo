package org.caleydo.core.command.data;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Class creates a selection.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdDataCreateSelection
	extends ACmdCreate_IdTargetLabelAttrDetail
{
	/**
	 * Constructor.
	 */
	public CmdDataCreateSelection(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		generalManager.getSelectionManager().createSelection(iExternalID);

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		// Nothing else to do here because the command only
		// needs an target Set ID, which is already
		// read by the super class.
	}

	public void setAttributes(int iSelectionSetId)
	{
		this.iExternalID = iSelectionSetId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACommand#getInfoText()
	 */
	public String getInfoText()
	{
		return super.getInfoText() + " -> " + this.iExternalID + ": " + this.sLabel;
	}
}
