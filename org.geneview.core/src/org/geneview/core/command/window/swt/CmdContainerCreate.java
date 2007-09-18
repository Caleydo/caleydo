package org.geneview.core.command.window.swt;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttr;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;

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

	public void doCommand() throws GeneViewRuntimeException
	{
		refGeneralManager.getSingelton().
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
	
	public void undoCommand() throws GeneViewRuntimeException
	{
		refCommandManager.runUndoCommand(this);		
	}
}