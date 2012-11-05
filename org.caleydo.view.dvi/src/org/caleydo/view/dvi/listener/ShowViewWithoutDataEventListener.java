/**
 * 
 */
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.ShowViewWithoutDataEvent;

/**
 * Listener for {@link ShowViewWithoutDataEvent}.
 * 
 * @author Christian Partl
 *
 */
public class ShowViewWithoutDataEventListener extends AEventListener<GLDataViewIntegrator> {

	/* (non-Javadoc)
	 * @see org.caleydo.core.event.AEventListener#handleEvent(org.caleydo.core.event.AEvent)
	 */
	@Override
	public void handleEvent(AEvent event) {
		if(event instanceof ShowViewWithoutDataEvent) {
			ShowViewWithoutDataEvent e = (ShowViewWithoutDataEvent)event;
			handler.createViewWithoutData(e.getViewID());
		}
		
	}

}
