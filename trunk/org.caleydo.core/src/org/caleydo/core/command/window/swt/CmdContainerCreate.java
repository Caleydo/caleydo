package org.caleydo.core.command.window.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Command class triggers the creation of a GUI container inside a window.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class CmdContainerCreate
	extends ACmdExternalAttributes {

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdContainerCreate(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {
		generalManager.getSWTGUIManager().createComposite(iExternalID, iParentContainerId, sAttribute2);

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {

		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {

		assert parameterHandler != null : "ParameterHandler object is null!";

		super.setParameterHandler(parameterHandler);
	}
}