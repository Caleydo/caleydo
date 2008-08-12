package org.caleydo.core.command.window.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command class triggers the creation of a GUI container inside a window.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class CmdContainerCreate
	extends ACmdExternalAttributes
{

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdContainerCreate(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		generalManager.getSWTGUIManager().createComposite(iExternalID, iParentContainerId,
				sAttribute2);

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
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParent#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

		super.setParameterHandler(parameterHandler);
	}
}