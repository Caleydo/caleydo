package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;

/**
 * Command for triggering simple actions from the RCP interface that are
 * executed in org.caleydo.core.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdExternalActionTrigger
	extends ACmdCreate_IdTargetLabelAttrDetail
{
	private EExternalActionType externalActionType;

	private int iViewId;

	/**
	 * Constructor.
	 */
	public CmdExternalActionTrigger(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		try
		{
			Object viewObject = generalManager.getViewGLCanvasManager().getItem(iViewId);

			if (viewObject instanceof GLCanvasRemoteRendering3D)
			{
				if (externalActionType.equals(EExternalActionType.CLEAR_ALL))
				{
					((GLCanvasRemoteRendering3D) viewObject).clearAll();
				}
				else if (externalActionType
						.equals(EExternalActionType.REMOTE_RENDERING_TOGGLE_LAYOUT_MODE))
				{
					((GLCanvasRemoteRendering3D) viewObject).toggleLayoutMode();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			throw new CaleydoRuntimeException("ERROR in CMD: " + e.toString(),
					CaleydoRuntimeExceptionType.DATAHANDLING);
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

	public void setAttributes(final int iViewId, final EExternalActionType externalActionType)
	{
		this.externalActionType = externalActionType;
		this.iViewId = iViewId;
	}
}
