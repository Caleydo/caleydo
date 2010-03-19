package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;

public class ReplaceContentVAForSetEvent
	extends ReplaceVAEvent<ContentVirtualArray, ContentVAType> {

	private ISet set;
	
	public ReplaceContentVAForSetEvent() {
		// nothing to initialize here
	}

	public ReplaceContentVAForSetEvent(EIDCategory idCategory, ContentVAType vaType, ISet set) {
		super(idCategory, vaType);
		this.set = set;
	}

	public ReplaceContentVAForSetEvent(EIDCategory idCategory, ContentVAType vaType,
		ContentVirtualArray virtualArray, ISet set) {
		super(idCategory, vaType, virtualArray);
		this.set = set;
	}

	public ISet getSet() {
		return set;
	}
}
