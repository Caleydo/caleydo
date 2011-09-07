package org.caleydo.core.data.selection;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;

public class RecordSelectionManager
	extends SelectionManager {

	public RecordSelectionManager(IDMappingManager idMappingManager, IDType idType) {
		super(idMappingManager, idType);
	}
}
