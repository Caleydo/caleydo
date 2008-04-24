package org.caleydo.core.command.window.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentAttr;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command class triggers the creation of
 * a GUI container inside a window.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 *
 */
public class CmdContainerCreate
extends ACmdCreate_IdTargetLabelParentAttr {
	protected String sLayoutAttributes;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdContainerCreate(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	public void doCommand() throws CaleydoRuntimeException
	{
		generalManager.getSingleton().
			getSWTGUIManager().createComposite(
					iUniqueId, 
					iParentContainerId, 
					sLayoutAttributes);	
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		sLayoutAttributes = sAttribute2;
	}
	
	public void undoCommand() throws CaleydoRuntimeException
	{
		refCommandManager.runUndoCommand(this);		
	}
}