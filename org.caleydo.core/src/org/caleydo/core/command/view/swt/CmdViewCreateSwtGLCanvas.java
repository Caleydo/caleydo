package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;

/**
 * Class implements the command for creating a SWT-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateSwtGLCanvas
	extends ACmdExternalAttributes {
	/**
	 * Constructor.
	 */
	public CmdViewCreateSwtGLCanvas(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {
		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		if (externalID != -1) {
			parentContainerID = generalManager.getIDManager().getInternalFromExternalID(parentContainerID);
		}

		SwtJoglGLCanvasViewRep swtGLCanvasView =
			(SwtJoglGLCanvasViewRep) viewManager.createGLView(EManagedObjectType.VIEW_GL_CANVAS,
				parentContainerID, label);

		swtGLCanvasView.initViewSWTComposite(null);
		swtGLCanvasView.drawView();

		if (externalID != -1) {
			generalManager.getIDManager().mapInternalToExternalID(swtGLCanvasView.getGLCanvasID(),
				externalID);
		}

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}
}
