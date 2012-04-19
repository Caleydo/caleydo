/**
 * 
 */
package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.event.ShowBubbleSetForPathwayVertexRepsEvent;

/**
 * Listener for {@link ShowBubbleSetForPathwayVertexRepsEvent}.
 * 
 * @author Christian
 * 
 */
public class ShowBubbleSetForPathwayVertexRepsEventListener extends
		AEventListener<GLPathway> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ShowBubbleSetForPathwayVertexRepsEvent) {
			handler.showBubbleSet(((ShowBubbleSetForPathwayVertexRepsEvent) event)
					.getVertexReps());
		}

	}

}
