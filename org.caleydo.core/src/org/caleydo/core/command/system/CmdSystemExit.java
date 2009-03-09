package org.caleydo.core.command.system;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACommand;

/**
 * Command shuts down application.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdSystemExit
	extends ACommand {
	/**
	 * Constructor.
	 */
	public CmdSystemExit(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {
		System.exit(0);
	}

	@Override
	public void undoCommand() {
		// no undo of system shutdown!
	}
}
