package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.base.AManagedCommand;
import cerberus.manager.GeneralManager;
import cerberus.manager.ViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;


public class CmdViewCreateDataExplorer
extends AManagedCommand
implements ICommand 
{
	public CmdViewCreateDataExplorer( GeneralManager refGeneralManager,
		final LinkedList <String> listAttributes ) 
	{
		super( -1, refGeneralManager );	
	}

	public void doCommand() throws CerberusRuntimeException
	{
		DataExplorerViewRep dataExplorerView = (DataExplorerViewRep) ((ViewManager)refGeneralManager.
				getManagerByBaseType(ManagerObjectType.VIEW)).
					createView(ManagerObjectType.VIEW_SWT_DATA_EXPLORER);
		//TODO: register view
		
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
