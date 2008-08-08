package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;

/**
 * Class implements the command for creating a SWT-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateSwtGLCanvas
	extends ACmdCreate_IdTargetLabelParentXY
{
	/**
	 * Constructor.
	 */
	public CmdViewCreateSwtGLCanvas(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		IViewGLCanvasManager viewManager = generalManager.getViewGLCanvasManager();

		if (iExternalID != -1)
		{
			iParentContainerId = 
				generalManager.getIDManager().getInternalFromExternalID(iParentContainerId);
		}
		
		SwtJoglGLCanvasViewRep swtGLCanvasView = (SwtJoglGLCanvasViewRep) viewManager
				.createGLView(EManagedObjectType.VIEW_GL_CANVAS, iParentContainerId, sLabel);
		
		swtGLCanvasView.setAttributes(iWidthX, iHeightY);
		swtGLCanvasView.initViewSwtComposite(null);
		swtGLCanvasView.drawView();

		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(
					swtGLCanvasView.getGLCanvasID(), iExternalID);
		}
		
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
