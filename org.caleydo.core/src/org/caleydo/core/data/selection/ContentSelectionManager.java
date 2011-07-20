package org.caleydo.core.data.selection;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;

public class ContentSelectionManager
	extends VABasedSelectionManager<ContentSelectionManager, ContentVirtualArray, ContentVADelta> {

	public ContentSelectionManager(IDType idType) {
		super(idType);
	}
}
