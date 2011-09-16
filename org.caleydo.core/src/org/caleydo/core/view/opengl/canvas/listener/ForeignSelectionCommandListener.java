package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.SelectionCommandEvent;

/**
 * Listener for TriggerSelectionCommand events for a dataDomain other then the one specified in the
 * dataDomainType. This is used to translate an event accross dataDomains and therefore should only be used in
 * a dataDomain.
 * 
 * @author Alexander Lex
 */
public class ForeignSelectionCommandListener
	extends AEventListener<ATableBasedDataDomain> {

	/**
	 * Handles {@link SelectionCommandEvent}s by extracting the events payload and calling the related handler
	 * 
	 * @param event
	 *            {@link SelectionCommandEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SelectionCommandEvent) {
			SelectionCommandEvent selectionCommandEvent = (SelectionCommandEvent) event;
			SelectionCommand selectionCommand = selectionCommandEvent.getSelectionCommand();
			IDCategory idCategory = selectionCommandEvent.getIdCategory();
			String dataDomainType = selectionCommandEvent.getDataDomainID();
			handler.handleForeignSelectionCommand(dataDomainType, idCategory, selectionCommand);
		}
	}

}
