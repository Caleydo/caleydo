package org.caleydo.core.data.mapping;

import java.util.HashMap;

public class IDType {

	private static HashMap<IDType, Boolean> registeredTypes = new HashMap<IDType, Boolean>();
	
	private String typeName;
	
	private IDType(String typeName) {
		this.typeName = typeName;
	}
	
	public static IDType registerType(String typeName) {
		
		IDType idType = new IDType(typeName);
		registeredTypes.put(idType, null);
		
		return idType;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
