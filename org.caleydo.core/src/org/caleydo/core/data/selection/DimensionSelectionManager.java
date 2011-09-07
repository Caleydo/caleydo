package org.caleydo.core.data.selection;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;

public class DimensionSelectionManager
	extends SelectionManager {

	public DimensionSelectionManager(IDMappingManager idMappingManager, IDType idType) {
		super(idMappingManager, idType);
	}
}
