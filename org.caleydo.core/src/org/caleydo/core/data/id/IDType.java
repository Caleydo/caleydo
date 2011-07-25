package org.caleydo.core.data.id;

import java.util.HashMap;

import org.caleydo.core.data.collection.DimensionType;

public class IDType {

	private static HashMap<String, IDType> registeredTypes = new HashMap<String, IDType>();

	private String typeName;
	private IDCategory idCategory;

	private DimensionType storageType;

	/** flag determining whether a type is internal only (true), or publicly known (eg refseq) */
	private boolean isInternalType = false;

	/**
	 * Constructor for de-serialization only. Use {@link #registerType(String, IDCategory, EStorageType)} to
	 * create a new IDType.
	 */
//	public IDType() {
//	}

	/**
	 * Should be used for de-serialization only
	 * 
	 * @param idCategory
	 */
	public void setIdCategory(IDCategory idCategory) {
		this.idCategory = idCategory;
	}

	/**
	 * Should be used for de-serialization only
	 * 
	 * @param idCategory
	 */
	public void setStorageType(DimensionType storageType) {
		this.storageType = storageType;
	}

	private IDType(String typeName, IDCategory idCategory, DimensionType storageType) {
		this.typeName = typeName;
		this.idCategory = idCategory;
		this.storageType = storageType;

	}

	public static IDType registerType(String typeName, IDCategory idCategory, DimensionType storageType) {

		if (registeredTypes.containsKey(typeName))
			return registeredTypes.get(typeName);

		IDType idType = new IDType(typeName, idCategory, storageType);
		registeredTypes.put(typeName, idType);

		return idType;
	}

	public static IDType getIDType(String typeName) {
		return registeredTypes.get(typeName);
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public DimensionType getStorageType() {
		return storageType;
	}

	public boolean isInternalType() {
		return isInternalType;
	}

	public void setInternalType(boolean isInternalType) {
		this.isInternalType = isInternalType;
	}

	public IDCategory getIDCategory() {
		return idCategory;
	}

	@Override
	public String toString() {
		return typeName;
	}

}
