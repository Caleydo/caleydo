package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.datagraph.GLDataViewIntegrator;
import org.caleydo.view.datagraph.event.ShowDataConnectionsEvent;

/**
 * Event handler for {@link ShowDataConnectionsEvent}.
 * 
 * @author Christian
 *
 */
public class ShowDataConnectionsEventListener
	extends AEventListener<GLDataViewIntegrator>
{

	@Override
	public void handleEvent(AEvent event)
	{
		if (event instanceof ShowDataConnectionsEvent)
		{
			handler.showDataConnections(((ShowDataConnectionsEvent) (event))
					.isShowDataConnections());
		}

	}

}
