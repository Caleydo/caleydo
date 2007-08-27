package cerberus.command.window.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentAttr;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Command class triggers the creation of
 * a GUI container inside a window.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 *
 */
public class CmdContainerCreate
extends ACmdCreate_IdTargetLabelParentAttr
implements ICommand 
{
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