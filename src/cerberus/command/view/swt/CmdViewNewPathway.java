package cerberus.command.view.swt;

import org.eclipse.swt.widgets.Shell;

import cerberus.command.CommandType;
import cerberus.command.base.AManagedCommand;
import cerberus.manager.GeneralManager;
import cerberus.manager.ViewManager;
import cerberus.manager.gui.SWTGUIManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.pathway.jgraph.PathwayViewRep;

public class CmdViewNewPathway extends AManagedCommand
{

	public CmdViewNewPathway(GeneralManager setGeneralManager)
	{
		//FIXME: for what is the collection ID needed here?
		super(0, setGeneralManager);
	}

	public void doCommand() throws CerberusRuntimeException
	{
		PathwayViewRep pathwayView = (PathwayViewRep) ((ViewManager)refGeneralManager.
			getManagerByBaseType(ManagerObjectType.VIEW)).
				createView(ManagerObjectType.VIEW_PATHWAY);
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
