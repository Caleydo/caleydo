package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;

/**
 * Command for triggering simple actions from the RCP interface that are
 * executed in org.caleydo.core.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdExternalActionTrigger
	extends ACmdExternalAttributes
{
	private EExternalActionType externalActionType;

	private int iViewId;

	/**
	 * Constructor.
	 */
	public CmdExternalActionTrigger(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		try
		{
			Object viewObject = generalManager.getViewGLCanvasManager().getItem(iViewId);

			if (viewObject instanceof GLRemoteRendering)
			{
				if (externalActionType.equals(EExternalActionType.CLEAR_ALL))
				{
					((GLRemoteRendering) viewObject).clearAll();
				}
				else if (externalActionType
						.equals(EExternalActionType.REMOTE_RENDERING_TOGGLE_LAYOUT_MODE))
				{
					((GLRemoteRendering) viewObject).toggleLayoutMode();
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

	@Override
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
