/**
 * 
 */
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.RenameLabelHolderEvent;

/**
 * Listener for {@link RenameLabelHolderEvent}.
 * 
 * @author Christian Partl
 * 
 */
public class RenameLabelHolderEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RenameLabelHolderEvent) {
			RenameLabelHolderEvent renameLabelHolderEvent = (RenameLabelHolderEvent) event;
			handler.renameLabelHolder(renameLabelHolderEvent.getLabelHolder());
		}

	}

}
