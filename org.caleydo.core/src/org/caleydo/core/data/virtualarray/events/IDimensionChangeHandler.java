package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.event.IListenerOwner;

/**
 * Interface for ListenerOwners handling dimension VA updates
 * 
 * @author Alexander Lex
 */
public interface IDimensionChangeHandler
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
	 * Handler method to be called by the {@link ReplaceRecordPerspectiveListener} when a {@link ReplacePerspectiveEvent} was
	 * received.
	 */
	public void replaceDimensionPerspective(String dataDomainID, String perspectiveID,
		PerspectiveInitializationData data);

}
