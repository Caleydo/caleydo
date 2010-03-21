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

	public ReplaceContentVAEvent(ISet set, EIDCategory idCategory, ContentVAType vaType) {
		super(set, idCategory, vaType);
	}

	protected ReplaceContentVAEvent(ISet set, EIDCategory idCategory, ContentVAType vaType,
		ContentVirtualArray virtualArray) {
		super(set, idCategory, vaType, virtualArray);
	}

	protected ReplaceContentVAEvent(EIDCategory idCategory, ContentVAType vaType,
		ContentVirtualArray virtualArray) {
		super(idCategory, vaType, virtualArray);
	}
}
