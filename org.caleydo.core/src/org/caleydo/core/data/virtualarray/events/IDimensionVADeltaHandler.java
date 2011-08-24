package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Interface for ListenerOwners handling dimension VA updates
 * 
 * @author Alexander Lex
 */
public interface IDimensionVADeltaHandler
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
	public void handleDimensionVADelta(DimensionVADelta vaDelta, String info);

	/**
	 * Handler method to be called by the {@link RecordReplaceVAListener} when a {@link VAReplaceEvent} was
	 * received.
	 */
	public void replaceDimensionVA(String dataDomainID, String perspectiveID,
		DimensionVirtualArray virtualArray);

}
