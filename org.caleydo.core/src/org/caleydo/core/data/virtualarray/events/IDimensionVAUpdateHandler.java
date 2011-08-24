package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.manager.event.IListenerOwner;

public interface IDimensionVAUpdateHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a {@link DimensionVAUpdateEvent} event is caught by a
	 * {@link DimensionVAUpdateListener}
	 * 
	 * @param perspectiveID
	 *            the id for the {@link DimensionPerspective} with which the VA to be updated is associated
	 */
	public void handleDimensionVAUpdate(String dimensionPerspectiveID);

}
