package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.perspective.RecordPerspective;
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
	 * @param perspectiveID
	 *            the id for the {@link RecordPerspective} with which the VA to be updated is associated
	 */
	public void handleRecordVAUpdate(String perspectiveID);

}
