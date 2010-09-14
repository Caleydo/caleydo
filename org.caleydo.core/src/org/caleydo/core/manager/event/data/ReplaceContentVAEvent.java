package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;

public class ReplaceContentVAEvent
	extends ReplaceVAEvent<ContentVirtualArray> {

	public ReplaceContentVAEvent() {
		// nothing to initialize here
	}

	public ReplaceContentVAEvent(ISet set, String dataDomainType, String vaType) {
		super(set, dataDomainType, vaType);
	}

	protected ReplaceContentVAEvent(ISet set, String dataDomainType, String vaType,
		ContentVirtualArray virtualArray) {
		super(set, dataDomainType, vaType, virtualArray);
	}

	protected ReplaceContentVAEvent(String dataDomainType, String vaType, ContentVirtualArray virtualArray) {
		super(dataDomainType, vaType, virtualArray);
	}
}
