package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.IListenerOwner;

// FIXME: split up interface. bucket and pathway events should be handled separately.
public interface IRemoteRenderingHandler
	extends IListenerOwner {

	public void setConnectionLinesEnabled(boolean enabled);

	public void addPathwayView(final int iPathwayID, String dataDomainID);

	public void setGeneMappingEnabled(boolean geneMappingEnabled);

	public void setNeighborhoodEnabled(boolean neighborhoodEnabled);

	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled);

	public void toggleNavigationMode();

	public void toggleZoom();
}
