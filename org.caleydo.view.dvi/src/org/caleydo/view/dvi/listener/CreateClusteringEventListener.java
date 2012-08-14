/**
 * 
 */
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.CreateClusteringEvent;

/**
 * Event handler for {@link CreateClusteringEvent}.
 * 
 * @author Christian Partl
 * 
 */
public class CreateClusteringEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CreateClusteringEvent) {
			CreateClusteringEvent createClusteringEvent = (CreateClusteringEvent) event;
			handler.createClustering(createClusteringEvent.getDataDomain(),
					createClusteringEvent.isDimensionClustering());
		}
	}

}
