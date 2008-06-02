package org.caleydo.core.command.window.swt;


import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command class triggers the creation of
 * a SWT GUI window.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 *
 */
public class CmdWindowCreate
extends ACmdCreate_IdTargetLabelAttrDetail {
	protected String sLayoutAttributes;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param commandManager
	 * @param commandQueueSaxType
	 */
	public CmdWindowCreate(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {
		
		super(generalManager, 
				commandManager,
				commandQueueSaxType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		generalManager.
			getSWTGUIManager().createWindow( 
					iUniqueId, sLabel, sLayoutAttributes);	
		
		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
	
	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		
		assert parameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(parameterHandler);	
		
		sLayoutAttributes = sAttribute1;
	}
}
