package cerberus.command.window.swt;

import java.util.LinkedList;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.base.AManagedCommand;
import cerberus.manager.GeneralManager;
import cerberus.util.exception.CerberusRuntimeException;

public class CmdWindowCreate
extends AManagedCommand
implements ICommand {

	public CmdWindowCreate( GeneralManager refGeneralManager,
		final LinkedList <String> listAttributes ) 
	{
		super( -1, refGeneralManager );	
	}

	public void doCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
		
	}

	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
		
	}

	public CommandType getCommandType() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
