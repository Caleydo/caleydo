package org.caleydo.core.data.id;

import java.util.HashMap;

import org.caleydo.core.data.collection.EDimensionType;

public class IDType {

	private static HashMap<String, IDType> registeredTypes = new HashMap<String, IDType>();

	private String typeName;
	private IDCategory idCategory;

	private EDimensionType dimensionType;

	/** flag determining whether a type is internal only (true), or publicly known (eg refseq) */
	private boolean isInternalType = false;

	/**
	 * Constructor for de-serialization only. Use {@link #registerType(String, IDCategory, EDimensionType)} to
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
	public void setDimensionType(EDimensionType dimensionType) {
		this.dimensionType = dimensionType;
	}

	private IDType(String typeName, IDCategory idCategory, EDimensionType dimensionType) {
		this.typeName = typeName;
		this.idCategory = idCategory;
		this.dimensionType = dimensionType;

	}

	public static IDType registerType(String typeName, IDCategory idCategory, EDimensionType dimensionType) {

		if (registeredTypes.containsKey(typeName))
			return registeredTypes.get(typeName);

		IDType idType = new IDType(typeName, idCategory, dimensionType);
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

	public EDimensionType getDimensionType() {
		return dimensionType;
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
