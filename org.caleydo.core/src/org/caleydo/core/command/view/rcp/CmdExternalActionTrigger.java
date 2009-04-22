package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.EViewCommand;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.event.ViewCommandEventContainer;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;

/**
 * Command for triggering simple actions from the RCP interface that are executed in org.caleydo.core.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdExternalActionTrigger
	extends ACmdExternalAttributes
	implements IMediatorSender {
	
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

		// Clear all selections in all GL views.
		// This method is not good because the selection in SWT views (e.g. the tabular data viewer) is not cleared.
		// In general a better way is needed - maybe by using a event command. But for this reason the sender (this class)
		// must be registered in the event system. This is also bad in case of commands like this.
		if (externalActionType == EExternalActionType.CLEAR_SELECTIONS) {
			IEventPublisher eventPublisher = generalManager.getEventPublisher();

			eventPublisher.addSender(EMediatorType.SELECTION_MEDIATOR, this);
			
			ViewCommandEventContainer viewCommandEventContainer =
				new ViewCommandEventContainer(EViewCommand.CLEAR_SELECTIONS);
			triggerEvent(EMediatorType.SELECTION_MEDIATOR, viewCommandEventContainer);
			
			eventPublisher.removeSender(EMediatorType.SELECTION_MEDIATOR, this);
		}

		AGLEventListener viewObject = generalManager.getViewGLCanvasManager().getGLEventListener(iViewId);
		if (viewObject instanceof GLRemoteRendering) {
			switch (externalActionType) {
				case CLOSE_OR_RESET_CONTAINED_VIEWS:
					((GLRemoteRendering) viewObject).clearAll();
					return;
				case REMOTE_RENDERING_TOGGLE_LAYOUT_MODE:
					((GLRemoteRendering) viewObject).toggleLayoutMode();
					return;
				case REMOTE_RENDERING_TOGGLE_CONNECTION_LINES_MODE:
					((GLRemoteRendering) viewObject).toggleConnectionLines();
					return;
			}
		}
		else if (viewObject instanceof AStorageBasedView) {
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

	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer) {
		generalManager.getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);
	}
}
