package org.caleydo.core.command.view.rcp;

import javax.media.opengl.GLCapabilities;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

/**
 * Class implements the command for creating a RCP-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateRcpGLCanvas
	extends ACmdExternalAttributes
{

	/**
	 * Constructor.
	 */
	public CmdViewCreateRcpGLCanvas(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		GLCapabilities glCapabilities = new GLCapabilities();
		glCapabilities.setStencilBits(1);

		GLCaleydoCanvas gLCanvas = new GLCaleydoCanvas(glCapabilities);

		IViewGLCanvasManager canvasManager = generalManager.getViewGLCanvasManager();

		// Register GL canvas to view manager
		canvasManager.registerGLCanvas(gLCanvas);

		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(gLCanvas.getID(),
					iExternalID);
		}

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
