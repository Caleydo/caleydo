package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;

/**
 * Command for triggering simple actions from the RCP interface that are executed in org.caleydo.core.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdExternalActionTrigger
	extends ACmdExternalAttributes {

	private EExternalActionType externalActionType;

	private int iViewId;

	/**
	 * Constructor.
	 */
	public CmdExternalActionTrigger(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {

		AGLEventListener viewObject = generalManager.getViewGLCanvasManager().getGLEventListener(iViewId);
		if (viewObject instanceof AStorageBasedView) {
			switch (externalActionType) {
				case STORAGEBASED_PROPAGATE_SELECTIONS:
					((AStorageBasedView) viewObject).broadcastElements();
					return;
				case STORAGEBASED_RESET_VIEW:
					((AStorageBasedView) viewObject).resetView();
			}

			if (viewObject instanceof GLParallelCoordinates) {
				switch (externalActionType) {
					case PARCOORDS_ANGULAR_BRUSHING:
						((GLParallelCoordinates) viewObject).triggerAngularBrushing();
						return;
					case PARCOORDS_SAVE_SELECTIONS:
						((GLParallelCoordinates) viewObject).saveSelection();
						return;
					case PARCOORDS_RESET_AXIS_SPACING:
						((GLParallelCoordinates) viewObject).resetAxisSpacing();
						return;

				}
			}
		}
		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

	public void setAttributes(final int iViewId, final EExternalActionType externalActionType) {
		this.externalActionType = externalActionType;
		this.iViewId = iViewId;
	}

}
