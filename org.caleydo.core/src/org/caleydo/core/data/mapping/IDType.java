package org.caleydo.core.data.mapping;

import java.util.HashMap;

import org.caleydo.core.data.collection.EStorageType;

public class IDType {

	private static HashMap<String, IDType> registeredTypes = new HashMap<String, IDType>();
	
	private String typeName;
	
	private EStorageType storageType;
	
	private IDType(String typeName, EStorageType storageType) {
		this.typeName = typeName;
		this.storageType = storageType;
	}
	
	public static IDType registerType(String typeName, EStorageType storageType) {
		
		if (registeredTypes.containsKey(typeName))
			return registeredTypes.get(typeName);
		
		IDType idType = new IDType(typeName, storageType);
		registeredTypes.put(typeName, idType);
		
		return idType;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public EStorageType getStorageType() {
		return storageType;
	}
}
