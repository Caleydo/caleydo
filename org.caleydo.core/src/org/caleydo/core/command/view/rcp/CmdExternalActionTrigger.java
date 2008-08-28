package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;

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
			Object viewObject = generalManager.getViewGLCanvasManager().getGLEventListener(
					iViewId);

			if (viewObject instanceof GLRemoteRendering)
			{
				switch (externalActionType)
				{
					case CLEAR_ALL:
						((GLRemoteRendering) viewObject).clearAll();
						break;
					case REMOTE_RENDERING_TOGGLE_LAYOUT_MODE:
						((GLRemoteRendering) viewObject).toggleLayoutMode();
						break;
				}
			}
			else if (viewObject instanceof GLParallelCoordinates
					|| viewObject instanceof GLHeatMap)
			{
				switch (externalActionType)
				{
					case STORAGEBASED_TOGGLE_RENDER_CONTEXT:
						((AStorageBasedView) viewObject).toggleRenderContext();
						break;
					case STORAGEBASED_PROPAGATE_SELECTIONS:
						((AStorageBasedView) viewObject).broadcastElements();
						break;
				}

				if (viewObject instanceof GLParallelCoordinates)
				{
					switch (externalActionType)
					{
						case STORAGEBASED_SWITCH_AXES_TO_POLYLINES:
							((GLParallelCoordinates) viewObject).toggleAxisPolylineSwap();
							break;
						case PARCOORDS_ANGULAR_BRUSHING:
							((GLParallelCoordinates) viewObject).triggerAngularBrushing();
							break;
					}
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
