/**
 * 
 */
package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.event.ClearPathEvent;

/**
 * Listener for {@link ClearPathEvent}.
 * 
 * @author Christian Partl
 *
 */
public class ClearPathEventListener extends AEventListener<GLPathway> {


	@Override
	public void handleEvent(AEvent event) {
		if(event instanceof ClearPathEvent) {
			handler.clearPath();
		}
		
	}

}
