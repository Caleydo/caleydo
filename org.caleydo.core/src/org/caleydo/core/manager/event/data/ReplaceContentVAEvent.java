package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;

public class ReplaceContentVAEvent
	extends ReplaceVAEvent<ContentVirtualArray, ContentVAType> {

	public ReplaceContentVAEvent() {
		// nothing to initialize here
	}

	public ReplaceContentVAEvent(ISet set, String dataDomainType, ContentVAType vaType) {
		super(set, dataDomainType, vaType);
	}

	protected ReplaceContentVAEvent(ISet set, String dataDomainType, ContentVAType vaType,
		ContentVirtualArray virtualArray) {
		super(set, dataDomainType, vaType, virtualArray);
	}

	protected ReplaceContentVAEvent(String dataDomainType, ContentVAType vaType,
		ContentVirtualArray virtualArray) {
		super(dataDomainType, vaType, virtualArray);
	}
}
