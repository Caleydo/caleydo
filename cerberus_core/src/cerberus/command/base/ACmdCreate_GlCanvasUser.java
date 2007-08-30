/**
 * 
 */
package cerberus.command.base;

import cerberus.command.CommandQueueSaxType;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.opengl.IGLCanvasUser;
import cerberus.view.opengl.IGLCanvasDirector;

/**
 * @see cerberus.command.ICommand
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_GlCanvasUser 
extends ACmdCreate_IdTargetParentGLObject {
	
	protected IGLCanvasUser openGLCanvasUser;
	
	protected CommandQueueSaxType localManagerObjectType;
	
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_GlCanvasUser(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * registers to ViewManager, calls doCommandPart()
	 *  
	 * @see cerberus.command.base.ACmdCreate_GlCanvasUser#doCommandPart()
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public final void doCommand() throws GeneViewRuntimeException
	{
		IViewGLCanvasManager glCanvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		/**
		 * create a new IGLCanvasUser object...
		 */
		openGLCanvasUser = glCanvasManager.createGLCanvasUser(
				localManagerObjectType,
				iUniqueId,
				iParentContainerId,
				sLabel );

		this.doCommandPart();
		
		glCanvasManager.registerGLCanvasUser( 
				openGLCanvasUser, 
				iUniqueId );
		
		IGLCanvasDirector canvasDirector = 
			glCanvasManager.getGLCanvasDirector( iParentContainerId );
		
		canvasDirector.addGLCanvasUser( (IGLCanvasUser) openGLCanvasUser );

		refCommandManager.runDoCommand(this);
	}

	/**
	 * unregisters from ViewManager, calls undoCommandPart()
	 * 
	 * @see cerberus.command.base.ACmdCreate_GlCanvasUser#undoCommandPart()
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public final void undoCommand() throws GeneViewRuntimeException
	{
		IViewGLCanvasManager glCanvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		glCanvasManager.unregisterGLCanvasUser( openGLCanvasUser );		
		
		IGLCanvasDirector canvasDirector = 
			glCanvasManager.getGLCanvasDirector( iParentContainerId );
		
		canvasDirector.removeGLCanvasUser( openGLCanvasUser );		
			
		openGLCanvasUser.destroyGLCanvas();
		

		refCommandManager.runUndoCommand(this);
	}
	
	/**
	 * 
	 * @see cerberus.command.base.ACmdCreate_GlCanvasUser#doCommand()
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public abstract void doCommandPart() throws GeneViewRuntimeException;
	
	/**
	 * 
	 * @see cerberus.command.base.ACmdCreate_GlCanvasUser#undoCommand()
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public abstract void undoCommandPart() throws GeneViewRuntimeException;

}
