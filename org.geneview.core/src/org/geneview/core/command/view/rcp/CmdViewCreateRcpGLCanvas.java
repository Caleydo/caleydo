package org.geneview.core.command.view.rcp;

import javax.media.opengl.GLCanvas;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewGLCanvasManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.jogl.JoglCanvasForwarder;

/**
 * Class implements the command for creating a RCP-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateRcpGLCanvas 
extends ACmdCreate_IdTargetLabelParentAttrOpenGL {

	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateRcpGLCanvas(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}
	
	/**
	 * Method creates a test triangle view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		GLCanvas gLCanvas = new JoglCanvasForwarder(generalManager, iGLCanvasID);
		
		assert gLCanvas != null : "GLCanvas was not be created";
		
		IViewGLCanvasManager canvasManager = 
			generalManager.getSingelton().getViewGLCanvasManager();
		
		// Register GL canvas to view manager
		canvasManager.registerGLCanvas(gLCanvas, iGLCanvasID);
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
	}
	
	public void undoCommand() throws GeneViewRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
