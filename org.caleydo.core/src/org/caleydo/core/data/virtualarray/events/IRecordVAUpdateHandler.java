package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Handler interface for {@link RecordVAUpdateEvent}. For documentation see {@link VAUpdateEvent}.
 * 
 * @author Alexander Lex
 */
public interface IRecordVAUpdateHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a virtual array update event is caught by a related
	 * {@link RecordVAUpdateListener}.
	 * 
	 * @param dataTableID
	 *            the id for the {@link DataTable} with which the VA to be updated is associated
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleRecordVAUpdate(int dataTableID, String info);

}
