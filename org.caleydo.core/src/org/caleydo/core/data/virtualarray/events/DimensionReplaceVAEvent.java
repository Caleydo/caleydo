package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.virtualarray.DimensionVirtualArray;

/**
 * @author Alexander Lex
 */
public class DimensionReplaceVAEvent
	extends VAReplaceEvent<DimensionVirtualArray> {

	public DimensionReplaceVAEvent() {
		// nothing to initialize here
	}

	public DimensionReplaceVAEvent(String dataDomainType, String perspectiveID,
		DimensionVirtualArray virtualArray) {
		super(dataDomainType, perspectiveID, virtualArray);
	}

}
