package cerberus.command.window.swt;

import java.util.Iterator;
import java.util.LinkedList;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.base.AManagedCommand;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;

public class CmdWindowCreate
extends AManagedCommand
implements ICommand 
{
	protected int iCommandId;
	protected int iWindowId;
	protected String sLayoutAttributes;
	protected String sLabel;
	
	public CmdWindowCreate( IGeneralManager refGeneralManager,
		final LinkedList <String> listAttributes ) 
	{
		super( -1, refGeneralManager );	
		setAttributes( listAttributes );
	}

	public void doCommand() throws CerberusRuntimeException
	{
		refGeneralManager.getSingelton().
			getSWTGUIManager().createWindow(iWindowId, sLabel, sLayoutAttributes);	
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
			this.iCommandId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			this.iWindowId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			
			// Skip some attributes
			sLabel = iter.next();
			iter.next();
			iter.next();
			iter.next();
			
			sLayoutAttributes = iter.next();

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
