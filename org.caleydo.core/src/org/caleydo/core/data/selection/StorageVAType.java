package org.caleydo.core.data.selection;

import java.util.HashMap;
import java.util.Set;

public class StorageVAType
	extends IVAType {

	/**
	 * All storages (initially)
	 */
	public static final StorageVAType STORAGE = new StorageVAType("STORAGE");

	private static StorageVAType primaryVAType = STORAGE;

	private static HashMap<StorageVAType, Boolean> registeredTypes;

	StorageVAType() {
		if (registeredTypes == null)
			registeredTypes = new HashMap<StorageVAType, Boolean>();
		registeredTypes.put(this, null);
	
	}
	
	StorageVAType(String stringRep)
	{
		this();
		this.stringRep = stringRep;
	}

	public static StorageVAType getPrimaryVAType() {
		return primaryVAType;
	}

	// public static VAType getVATypeForPrimaryVAType(String primaryVAType) {
	// if (primaryVAType.equals(CONTENT_PRIMARY))
	// return CONTENT;
	// else if (primaryVAType.equals(STORAGE_PRIMARY))
	// return STORAGE;
	// else
	// throw new IllegalArgumentException("Unknown primaryVAType: " + primaryVAType);
	// }

	public static synchronized Set<StorageVAType> getRegisteredVATypes() {
		return registeredTypes.keySet();
	}
}
