package org.caleydo.core.command.window.swt;


import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttr;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
//import org.caleydo.core.xml.parser.command.CommandQueueSaxType;

/**
 * Command class triggers the creation of
 * a SWT GUI window.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 *
 */
public class CmdWindowCreate
extends ACmdCreate_IdTargetLabelAttr {
	protected String sLayoutAttributes;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdWindowCreate(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}
	
	public void doCommand() throws CaleydoRuntimeException
	{
		generalManager.
			getSWTGUIManager().createWindow( 
					iUniqueId, sLabel, sLayoutAttributes);	
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		sLayoutAttributes = sAttribute1;
	}
	
	public void undoCommand() throws CaleydoRuntimeException
	{
		refCommandManager.runUndoCommand(this);
	}
}
