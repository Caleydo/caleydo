package org.caleydo.core.view.opengl.canvas.listener;

import java.util.List;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.manager.event.view.TriggerSelectionCommandEvent;

/**
 * Listener for TriggerSelectionCommand events.
 * This listener gets the payload from a {@link TriggerSelectionCommandEvent} and calls 
 * a related {@link ITriggerSelectionCommandHandler}. 
 * @author Werner Puff
 */
public class TriggerSelectionCommandListener 
	implements IEventListener {

	/** {@link ITriggerSelectionCommandHandler} this listener is related to */
	protected ITriggerSelectionCommandHandler handler = null;

	/**
	 * Handles {@link TriggerSelectionCommandEvent}s by extracting the events payload 
	 * and calling the related handler
	 * @param event {@link TriggerSelectionCommandEvent} to handle, other events will be ignored 
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof TriggerSelectionCommandEvent) {
			TriggerSelectionCommandEvent triggerSelectionCommandEvent = (TriggerSelectionCommandEvent) event; 
			EIDType type = triggerSelectionCommandEvent.getType();
			List<SelectionCommand> selectionCommands= triggerSelectionCommandEvent.getSelectionCommands();
			switch (type) {
				case DAVID:
				case REFSEQ_MRNA_INT:
				case EXPRESSION_INDEX:
					handler.handleContentTriggerSelectionCommand(type, selectionCommands);
					break;
				case EXPERIMENT_INDEX:
					handler.handleStorageTriggerSelectionCommand(type, selectionCommands);
					break;
			}
		}
	}

	public ITriggerSelectionCommandHandler getHandler() {
		return handler;
	}

	public void setHandler(ITriggerSelectionCommandHandler handler) {
		this.handler = handler;
	}

}
