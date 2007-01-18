package cerberus.command.window.swt;


import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelAttr;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
//import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;


public class CmdWindowCreate
extends ACmdCreate_IdTargetLabelAttr
implements ICommand 
{
	protected String sLayoutAttributes;
	
	public CmdWindowCreate( final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler ) 
	{
		super( refGeneralManager, refParameterHandler );	
		setAttributes( refParameterHandler );
	}

	public void doCommand() throws CerberusRuntimeException
	{
		refGeneralManager.getSingelton().
			getSWTGUIManager().createWindow( iUniqueTargetId, sLabel, sLayoutAttributes);	
	}

	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
	}

	
	protected void setAttributes( final IParameterHandler refParameterHandler ) 
	{						
		sLayoutAttributes = sAttribute1;
	}
}
