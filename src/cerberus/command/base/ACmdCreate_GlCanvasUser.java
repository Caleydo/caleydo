/**
 * 
 */
package cerberus.command.base;

import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author java
 *
 */
public abstract class ACmdCreate_GlCanvasUser 
extends ACmdCreate_IdTargetParentGLObject
implements ICommand
{
	
	protected IGLCanvasUser openGLCanvasUser;
	
	protected CommandQueueSaxType localManagerObjectType;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_GlCanvasUser(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);

	}

	/**
	 * registers to ViewManager, calls doCommandPart()
	 *  
	 * @see cerberus.command.base.ACmdCreate_GlCanvasUser#doCommandPart()
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public final void doCommand() throws CerberusRuntimeException
	{
		IViewGLCanvasManager glCanvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		/**
		 * create a new IGLCanvasUser object...
		 */
		openGLCanvasUser = glCanvasManager.createGLCanvasUser(
				localManagerObjectType,
				iUniqueTargetId,
				iParentContainerId,
				sLabel );

		this.doCommandPart();
		
		glCanvasManager.registerGLCanvasUser( 
				openGLCanvasUser, 
				iUniqueTargetId );
		
		IGLCanvasDirector canvasDirector = 
			glCanvasManager.getGLCanvasDirector( iParentContainerId );
		
		canvasDirector.addGLCanvasUser( (IGLCanvasUser) openGLCanvasUser );

	}

	/**
	 * unregisters from ViewManager, calls undoCommandPart()
	 * 
	 * @see cerberus.command.base.ACmdCreate_GlCanvasUser#undoCommandPart()
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public final void undoCommand() throws CerberusRuntimeException
	{
		IViewGLCanvasManager glCanvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		glCanvasManager.unregisterGLCanvasUser( openGLCanvasUser );		
		
		IGLCanvasDirector canvasDirector = 
			glCanvasManager.getGLCanvasDirector( iParentContainerId );
		
		canvasDirector.removeGLCanvasUser( openGLCanvasUser );		
			
		openGLCanvasUser.destroy();
	}
	
	/**
	 * 
	 * @see cerberus.command.base.ACmdCreate_GlCanvasUser#doCommand()
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public abstract void doCommandPart() throws CerberusRuntimeException;
	
	/**
	 * 
	 * @see cerberus.command.base.ACmdCreate_GlCanvasUser#undoCommand()
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public abstract void undoCommandPart() throws CerberusRuntimeException;

}
