package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;

public class ReplaceContentVAEvent
	extends ReplaceVAEvent<ContentVirtualArray, ContentVAType> {

	public ReplaceContentVAEvent() {
		// nothing to initialize here
	}

	public ReplaceContentVAEvent(EIDCategory idCategory, ContentVAType vaType) {
		super(idCategory, vaType);
	}

	public ReplaceContentVAEvent(EIDCategory idCategory, ContentVAType vaType,
		ContentVirtualArray virtualArray) {
		super(idCategory, vaType, virtualArray);
	}

}
