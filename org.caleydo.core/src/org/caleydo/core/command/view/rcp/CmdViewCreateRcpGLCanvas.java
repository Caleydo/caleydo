package org.caleydo.core.command.view.rcp;

import javax.media.opengl.GLCapabilities;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.manager.IViewManager;
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
		GLCapabilities glCapabilities = new GLCapabilities();
		glCapabilities.setStencilBits(1);
		glCapabilities.setSampleBuffers(true);
		glCapabilities.setNumSamples(2);
	    
		createdObject = new GLCaleydoCanvas(glCapabilities);

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		// Register GL canvas to view manager
		viewManager.registerGLCanvas(createdObject);

		if (iExternalID != -1) {
			generalManager.getIDManager().mapInternalToExternalID(createdObject.getID(), iExternalID);
		}

		createdObject.setNavigationModes(bEnablePan, bEnableRotate, bEnableZoom);

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

	public void setAttributes(int iParentCanvasID, boolean bEnablePan, boolean bEnableRotate,
		boolean bEnableZoom) {
		iExternalID = iParentCanvasID;

		this.bEnablePan = bEnablePan;
		this.bEnableRotate = bEnableRotate;
		this.bEnableZoom = bEnableZoom;
	}
}
