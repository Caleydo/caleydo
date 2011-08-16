package org.caleydo.core.data.virtualarray.events;


import org.caleydo.core.manager.event.IListenerOwner;

public interface IRecordVAUpdateHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a virtual array update event is catched by a related
	 * {@link RecordVAUpdateListener}.
	 * 
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleRecordVAUpdate(String info);

}

