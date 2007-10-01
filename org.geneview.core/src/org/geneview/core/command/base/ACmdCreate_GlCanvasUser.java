/**
 * 
 */
package org.geneview.core.command.base;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewGLCanvasManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.opengl.IGLCanvasUser;
import org.geneview.core.view.opengl.IGLCanvasDirector;

/**
 * @see org.geneview.core.command.ICommand
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
	 * @see org.geneview.core.command.base.ACmdCreate_GlCanvasUser#doCommandPart()
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public final void doCommand() throws GeneViewRuntimeException
	{
		IViewGLCanvasManager glCanvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		try 
		{
			try 
			{
				/**
				 * create a new IGLCanvasUser object...
				 */
				openGLCanvasUser = glCanvasManager.createGLCanvasUser(
						localManagerObjectType,
						iUniqueId,
						iParentContainerId,
						sLabel );
			}
			catch (NoClassDefFoundError ncde) 
			{
				String errorMsg = "ACmdCreate_GlCanvasUser().doCommand() missing class; most probably jogl.jar is missing; can not create OpenGL frame; ";
				refGeneralManager.getSingelton().logMsg(
						errorMsg + ncde.toString(), 
						LoggerType.ERROR);
				
				throw new GeneViewRuntimeException(errorMsg);
			}
		
			this.doCommandPart();
		
		}
		catch (RuntimeException re) 
		{
			String errorMsg = "ACmdCreate_GlCanvasUser().doCommand() error while creating OpenGL frame; ";
			refGeneralManager.getSingelton().logMsg(
					errorMsg + re.toString(), 
					LoggerType.ERROR);
			
			throw new GeneViewRuntimeException(errorMsg);
		}
		
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
	 * @see org.geneview.core.command.base.ACmdCreate_GlCanvasUser#undoCommandPart()
	 * @see org.geneview.core.command.ICommand#undoCommand()
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
	 * @see org.geneview.core.command.base.ACmdCreate_GlCanvasUser#doCommand()
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public abstract void doCommandPart() throws GeneViewRuntimeException;
	
	/**
	 * 
	 * @see org.geneview.core.command.base.ACmdCreate_GlCanvasUser#undoCommand()
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public abstract void undoCommandPart() throws GeneViewRuntimeException;

}
