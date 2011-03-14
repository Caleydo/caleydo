package org.caleydo.core.command.view;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.manager.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

/**
 * Class implements the command for creating a RCP-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateRcpGLCanvas
	extends ACmdCreational<GLCaleydoCanvas> {

	protected boolean bEnablePan = true;
	protected boolean bEnableRotate = true;
	protected boolean bEnableZoom = true;

	/**
	 * Constructor.
	 */
	public CmdViewCreateRcpGLCanvas(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {
		GLCapabilities glCapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		glCapabilities.setStencilBits(1);

		createdObject = new GLCaleydoCanvas(glCapabilities);

		ViewManager viewManager = generalManager.getViewGLCanvasManager();

		// Register GL2 canvas to view manager
		viewManager.registerGLCanvas(createdObject);

		if (externalID != -1) {
			generalManager.getIDCreator().mapInternalToExternalID(createdObject.getID(), externalID);
		}

		createdObject.setNavigationModes(bEnablePan, bEnableRotate, bEnableZoom);
	}

	@Override
	public void undoCommand() {
	}

	public void setAttributes(int iParentCanvasID, boolean bEnablePan, boolean bEnableRotate,
		boolean bEnableZoom) {
		externalID = iParentCanvasID;

		this.bEnablePan = bEnablePan;
		this.bEnableRotate = bEnableRotate;
		this.bEnableZoom = bEnableZoom;
	}
}
