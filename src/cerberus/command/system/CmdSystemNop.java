/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system;

import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.base.CommandAbstractBase;
import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.util.exception.PrometheusCommandException;

/**
 * No Opoeration command does nothing.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemNop
extends CommandAbstractBase
implements CommandInterface {

	/**
	 * 
	 */
	public CmdSystemNop() {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws PrometheusCommandException {
		CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo("");
		exitWarning.setText("DEBUG-INFO","do nop ...");
		exitWarning.doCommand();
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws PrometheusCommandException {
		
	}


	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws PrometheusCommandException {
		return CommandType.SYSTEM_NOP;
	}

}
