package cerberus.command.view.swt;

import java.util.Iterator;
import java.util.LinkedList;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.base.AManagedCommand;
import cerberus.data.collection.ISet;
import cerberus.manager.GeneralManager;
import cerberus.manager.ViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.swt.pathway.jgraph.PathwayViewRep;

public class CmdViewCreatePathway 
extends AManagedCommand
implements ICommand
{
	protected int iUniqueCommandId;
	protected int iUniquePathwayId;
	protected String sLabel;
	protected int iUniqueParentContainerId;
	
	public CmdViewCreatePathway( GeneralManager refGeneralManager,
		final LinkedList <String> listAttributes ) 
	{
		super( -1, refGeneralManager );	
		setAttributes( listAttributes );
	}

	public void doCommand() throws CerberusRuntimeException
	{				
		PathwayViewRep pathwayView = (PathwayViewRep) ((ViewManager)refGeneralManager.
			getManagerByBaseType(ManagerObjectType.VIEW)).
				createView(ManagerObjectType.VIEW_PATHWAY, iUniquePathwayId);

		//pathwayView.setLabel(sLabel);
		pathwayView.setParentContainerId(iUniqueParentContainerId);
		pathwayView.retrieveNewGUIContainer();
		pathwayView.drawView();
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
	
	/**
	 * Method extracts the command arguments.
	 * 
	 * List of expected Strings inside LinkedList <String>: <br>
	 * sData_CmdId <br>
	 * sData_Cmd_label <br>
	 * sData_Cmd_process <br> 
	 * sData_Cmd_MementoId <br> 
	 * sData_Cmd_attribute1 <br>
	 * sData_Cmd_attribute2 <br>
	 * 
	 * @param sUniqueId uniqueId of new target ISet
	 * @return TRUE on successful conversion of Strgin to interger
	 */
	protected boolean setAttributes( final LinkedList <String> listAttrib ) 
	{
		assert listAttrib != null: "can not handle null object!";		
		
		Iterator <String> iter = listAttrib.iterator();		
		final int iSizeList= listAttrib.size();
		
		assert iSizeList > 1 : "can not handle empty argument list!";					
		
		try 
		{		
			iUniqueCommandId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			iUniquePathwayId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			sLabel = iter.next();
			
			// Skip process, memenoto and detail argument
			iter.next();
			iter.next();
			iter.next();
			
			iUniqueParentContainerId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			return true;
		}
		catch ( NumberFormatException nfe ) 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"CmdDataCreateWindow::doCommand() error on attributes!");			
			return false;
		}	
	}

}
