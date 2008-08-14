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

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		generalManager.getSWTGUIManager().createComposite(iExternalID, iParentContainerId,
				sAttribute2);

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

		super.setParameterHandler(parameterHandler);
	}
}