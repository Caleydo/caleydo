package org.geneview.core.command.window.swt;


import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelAttr;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
//import org.geneview.core.xml.parser.command.CommandQueueSaxType;

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
	
	public void doCommand() throws GeneViewRuntimeException
	{
		refGeneralManager.getSingelton().
			getSWTGUIManager().createWindow( 
					iUniqueId, sLabel, sLayoutAttributes);	
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		sLayoutAttributes = sAttribute1;
	}
	
	public void undoCommand() throws GeneViewRuntimeException
	{
		refCommandManager.runUndoCommand(this);
	}
}
