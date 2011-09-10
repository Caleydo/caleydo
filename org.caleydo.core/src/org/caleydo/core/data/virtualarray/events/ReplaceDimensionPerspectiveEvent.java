package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.perspective.PerspectiveInitializationData;

/**
 * @author Alexander Lex
 */
public class ReplaceDimensionPerspectiveEvent
	extends ReplacePerspectiveEvent {

	public ReplaceDimensionPerspectiveEvent() {
		// nothing to initialize here
	}

	public ReplaceDimensionPerspectiveEvent(String dataDomainType, String perspectiveID,
		PerspectiveInitializationData data) {
		super(dataDomainType, perspectiveID, data);
	}

}
