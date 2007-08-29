package cerberus.command.window.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentAttr;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;

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
					iUniqueTargetId, 
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