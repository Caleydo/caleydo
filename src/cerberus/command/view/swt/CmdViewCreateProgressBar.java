package cerberus.command.view.swt;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.base.AManagedCommand;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.swt.progressbar.ProgressBarViewRep;


public class CmdViewCreateProgressBar
extends AManagedCommand
implements ICommand 
{
	protected int iUniqueCommandId;
	protected int iUniqueDataExplorerId;
	protected int iUniqueParentContainerId;
	protected int iProgressPercentage;
	
	public CmdViewCreateProgressBar( IGeneralManager refGeneralManager,
		final LinkedList <String> listAttributes ) 
	{
		super( -1, refGeneralManager );	
		
		setAttributes( listAttributes );
	}

	public void doCommand() throws CerberusRuntimeException
	{
		ProgressBarViewRep progressBarView = (ProgressBarViewRep) ((IViewManager)refGeneralManager.
				getManagerByBaseType(ManagerObjectType.VIEW)).
					createView(ManagerObjectType.VIEW_SWT_PROGRESS_BAR, 
							iUniqueDataExplorerId);

		//pathwayView.setLabel(sLabel);
		progressBarView.setParentContainerId(iUniqueParentContainerId);
		progressBarView.retrieveNewGUIContainer();
		progressBarView.setProgressBarStyle( iProgressPercentage );
		progressBarView.initView();
		progressBarView.drawView();
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
	
	protected boolean setAttributes( final LinkedList <String> listAttrib ) 
	{
		assert listAttrib != null: "can not handle null object!";		
		
		Iterator <String> iter = listAttrib.iterator();		
		final int iSizeList= listAttrib.size();
		
		assert iSizeList > 1 : "can not handle empty argument list!";					
		
		try 
		{		
			iUniqueCommandId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			iUniqueDataExplorerId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			
			// Skip label argument
			iter.next();
			
			// Skip process, memenoto and detail argument
			iter.next();
			iter.next();
			iter.next();
			
			iUniqueParentContainerId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			iProgressPercentage = (StringConversionTool.convertStringToInt( iter.next(), 0 ) );
			
			setId( iUniqueCommandId );
			
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
