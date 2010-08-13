package org.caleydo.core.command.system;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Command triggers loading of pathways.
 * 
 * @author Marc Streit
 */
public class CmdLoadPathwayData
	extends ACmdExternalAttributes {

	/**
	 * Constructor.
	 */
	public CmdLoadPathwayData(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {
		commandManager.runDoCommand(this);
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
