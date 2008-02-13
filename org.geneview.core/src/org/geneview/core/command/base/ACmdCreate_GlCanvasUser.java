package org.geneview.core.command.base;

import javax.media.opengl.GLEventListener;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewGLCanvasManager;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * 
 * Command for creating a GL event listener
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class ACmdCreate_GlCanvasUser 
extends ACmdCreate_IdTargetParentGLObject {
	
	protected CommandQueueSaxType localManagerObjectType;
	
	protected GLEventListener gLEventListener;
	
	/**
	 *
	 * Constructor.
	 * 
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

	public void doCommand()
	{
		IViewGLCanvasManager glCanvasManager = 
			generalManager.getSingelton().getViewGLCanvasManager();
		
		gLEventListener = glCanvasManager.createGLCanvas(
				localManagerObjectType,
				iUniqueId,
				iParentContainerId,
				sLabel );

		refCommandManager.runDoCommand(this);
	}

	public void undoCommand()
	{

		refCommandManager.runUndoCommand(this);
	}
}
