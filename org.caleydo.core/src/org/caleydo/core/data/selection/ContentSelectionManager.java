package org.caleydo.core.data.selection;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.delta.ContentVADelta;

public class ContentSelectionManager
	extends
	VABasedSelectionManager<ContentSelectionManager, ContentVirtualArray, ContentVAType, ContentVADelta> {

	public ContentSelectionManager(IDType idType) {
		super(idType);
	}
}
