package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.manager.event.IListenerOwner;

public interface IDimensionVAUpdateHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a {@link DimensionVAUpdateEvent} event is caught by a
	 * {@link DimensionVAUpdateListener}
	 * 
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleDimensionVAUpdate(int dataTableID, String info);

}
