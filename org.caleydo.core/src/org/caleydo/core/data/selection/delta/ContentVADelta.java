package org.caleydo.core.data.selection.delta;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVAType;

public class ContentVADelta
	extends VirtualArrayDelta<ContentVADelta, ContentVAType> {

	public ContentVADelta() {
	}

	public ContentVADelta(ContentVAType vaType, EIDType idType) {
		super(vaType, idType);
	}

	public ContentVADelta(ContentVAType vaType, EIDType idType, EIDType secondaryIDType) {
		super(vaType, idType, secondaryIDType);
	}

	@Override
	public ContentVADelta getInstance() {
		return new ContentVADelta();
	}

}
