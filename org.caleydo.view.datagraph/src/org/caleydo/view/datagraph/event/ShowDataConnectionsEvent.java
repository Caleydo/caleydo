package org.caleydo.view.datagraph.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.datagraph.GLDataGraph;

/**
 * Event that specifies whether the connection between data nodes shall be
 * displayed per default in {@link GLDataGraph}.
 * 
 * @author Christian
 * 
 */
public class ShowDataConnectionsEvent
	extends AEvent
{
	private boolean showDataConnections;

	public ShowDataConnectionsEvent(boolean showDataConnections)
	{
		this.setShowDataConnections(showDataConnections);
	}

	@Override
	public boolean checkIntegrity()
	{
		return true;
	}

	public boolean isShowDataConnections()
	{
		return showDataConnections;
	}

	public void setShowDataConnections(boolean showDataConnections)
	{
		this.showDataConnections = showDataConnections;
	}

}
