package org.caleydo.core.data.virtualarray.delta;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.virtualarray.ContentVAType;

public class ContentVADelta
	extends VirtualArrayDelta<ContentVADelta, ContentVAType> {

	public ContentVADelta() {
	}

	public ContentVADelta(ContentVAType vaType, IDType idType) {
		super(vaType, idType);
	}

	public ContentVADelta(ContentVAType vaType, IDType idType, IDType secondaryIDType) {
		super(vaType, idType, secondaryIDType);
	}

	@Override
	public ContentVADelta getInstance() {
		return new ContentVADelta();
	}

}
