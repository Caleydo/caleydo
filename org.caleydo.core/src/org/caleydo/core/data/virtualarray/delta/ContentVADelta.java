package org.caleydo.core.data.virtualarray.delta;

import org.caleydo.core.data.id.IDType;

public class ContentVADelta
	extends VirtualArrayDelta<ContentVADelta> {

	public ContentVADelta() {
	}

	public ContentVADelta(String vaType, IDType idType) {
		super(vaType, idType);
	}

	public ContentVADelta(String vaType, IDType idType, IDType secondaryIDType) {
		super(vaType, idType, secondaryIDType);
	}

	@Override
	public ContentVADelta getInstance() {
		return new ContentVADelta();
	}

}
