package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Handler interface for listeners for {@link RecordVADeltaEvent} and {@link ReplaceRecordPerspectiveEvent}.
 * 
 * @author Alexander Lex
 *
 */
public interface IRecordVADeltaHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a virtual array update event is catched by a related
	 * {@link RecordVADeltaListener}.
	 * 
	 * @param delta
	 *            difference in the old and new virtual array
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleRecordVADelta(RecordVADelta vaDelta, String info);

	/**
	 * Handler method to be called by the {@link ReplaceRecordPerspectiveListener} when a {@link ReplacePerspectiveEvent} was
	 * received.
	 * 
	 */
	public void replaceRecordPerspective(String dataDomainID, String recordPerspectiveID,  PerspectiveInitializationData data);

}