package cerberus.command.window.swt;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.base.AManagedCommand;
import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;

public class CmdContainerCreate
extends AManagedCommand
implements ICommand 
{
	protected int iUniqueCommandId;
	protected int iUniqueContainerId;
	protected int iUniqueParentContainerId;
	protected String sLayoutAttributes;
	
	public CmdContainerCreate( IGeneralManager refGeneralManager,
		final LinkedList <String> listAttributes ) 
	{
		super( -1, refGeneralManager );	
		setAttributes( listAttributes );
	}

	public void doCommand() throws CerberusRuntimeException
	{
		refGeneralManager.getSingelton().
			getSWTGUIManager().createComposite(
					iUniqueContainerId, iUniqueParentContainerId, sLayoutAttributes);	
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
			iUniqueContainerId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			
			// Skip label argument
			iter.next();
			
			// Skip process, memenoto and detail argument
			iter.next();
			iter.next();
			iter.next();
			
			iUniqueParentContainerId = (StringConversionTool.convertStringToInt( iter.next(), -1 ) );

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