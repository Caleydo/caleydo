package org.caleydo.core.data.selection;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.delta.ContentVADelta;

public class ContentSelectionManager
	extends
	VABasedSelectionManager<ContentSelectionManager, ContentVirtualArray, ContentVAType, ContentVADelta> {

	public ContentSelectionManager(EIDType idType) {
		super(idType);
	}
}
