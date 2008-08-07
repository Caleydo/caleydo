package org.caleydo.core.command.view.rcp;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

/**
 * Class implements the command for creating a RCP-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateRcpGLCanvas
	extends ACmdCreate_IdTargetLabelParentAttrOpenGL
{

	/**
	 * Constructor.
	 */
	public CmdViewCreateRcpGLCanvas(final CommandType cmdType)
	{
		super(cmdType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		GLCapabilities glCapabilities = new GLCapabilities();
		glCapabilities.setStencilBits(1);

		GLCanvas gLCanvas = new GLCaleydoCanvas(generalManager, iGLCanvasID, glCapabilities);

		assert gLCanvas != null : "GLCanvas was not be created";

		IViewGLCanvasManager canvasManager = generalManager.getViewGLCanvasManager();

		// Register GL canvas to view manager
		canvasManager.registerGLCanvas(gLCanvas, iGLCanvasID);

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
