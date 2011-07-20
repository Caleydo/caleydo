package org.caleydo.core.command.view;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreational;

/**
 * Class implements the command for creating a RCP-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateRcpGLCanvas
	extends ACmdCreational<GLCanvas> {

	/**
	 * Constructor.
	 */
	public CmdViewCreateRcpGLCanvas() {
		super(CommandType.CREATE_VIEW_RCP_GLCANVAS);
	}

	@Override
	public void doCommand() {
		GLCapabilities glCapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		glCapabilities.setStencilBits(1);

		createdObject = new GLCanvas(glCapabilities);
	}

	@Override
	public void undoCommand() {
	}

	public void setAttributes(int iParentCanvasID, boolean bEnablePan, boolean bEnableRotate,
		boolean bEnableZoom) {
		externalID = iParentCanvasID;
	}
}
