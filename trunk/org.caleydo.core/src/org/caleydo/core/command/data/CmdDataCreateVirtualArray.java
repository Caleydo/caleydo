package org.caleydo.core.command.data;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Command creates a virtual array.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdDataCreateVirtualArray
	extends ACmdExternalAttributes {
	/**
	 * Constructor.
	 */
	public CmdDataCreateVirtualArray(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {
		commandManager.runUndoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);
	}
}
