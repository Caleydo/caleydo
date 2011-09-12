package org.caleydo.core.data.id;

import java.util.HashMap;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.mapping.IDMappingManager;

public class IDType {

	private static HashMap<String, IDType> registeredTypes = new HashMap<String, IDType>();

	/**
	 * Type name that needs to be a unique value for every new IDType. Besides being unique, it should also be
	 * human-readable to some extend, to make debugging easier.
	 */
	private String typeName;

	/**
	 * The {@link IDCategory} of an {@link IDType} specifies to which category an ID belongs. The contract is,
	 * that between two IDTypes that share the same IDCategory there must exist a mapping in the
	 * {@link IDMappingManager}.
	 */
	private IDCategory idCategory;

	/**
	 * Specifies the data type of the IDType. Allowed values are {@link EColumnType#INT} and
	 * {@link EColumnType#STRING}
	 */
	private EColumnType columnType;
	/**
	 * flag determining whether a type is internal only, meaning that it is dynamically generated using a
	 * running number (true), or publicly known (eg refseq)
	 */
	private boolean isInternalType = false;

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
	public void setDimensionType(EColumnType dimensionType) {
		this.columnType = dimensionType;
	}

	private IDType(String typeName, IDCategory idCategory, EColumnType dimensionType) {
		this.typeName = typeName;
		this.idCategory = idCategory;
		this.columnType = dimensionType;
	}

	/**
	 * Register a new IDType. Checks whether the typeName is already used, and whether the columnType is
	 * legal.
	 * 
	 * @param typeName
	 *            see {@link #typeName}
	 * @param idCategory
	 *            see {@link #idCategory}
	 * @param columnType
	 *            see {@link #columnType}
	 * @return the created ID Type
	 */
	public static IDType registerType(String typeName, IDCategory idCategory, EColumnType columnType) {
		if (!(columnType == EColumnType.STRING || columnType == EColumnType.INT))
			throw new IllegalStateException(
				"IDTypes are allowed to be only either of type STRING or INT, but was: " + columnType);
		if (registeredTypes.containsKey(typeName))
			throw new IllegalStateException("IDType for typeName " + typeName + " already created");

		IDType idType = new IDType(typeName, idCategory, columnType);
		registeredTypes.put(typeName, idType);

		return idType;
	}

	/** Returns the IDType for the typeName specified, or null if no such type is registered */
	public static IDType getIDType(String typeName) {
		IDType requestedType = registeredTypes.get(typeName);
		if(requestedType == null)
			throw new IllegalStateException("Requested IDType for typeName \"" + typeName + "\" not registered.");
		return requestedType;
	}

	/**
	 * @return the typeName, see {@link #typeName}
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName
	 *            setter, see {@link #typeName}
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return the columnType, see {@link #columnType}
	 */
	public EColumnType getColumnType() {
		return columnType;
	}

	/**
	 * @return the isInternalType, see {@link #isInternalType}
	 */
	public boolean isInternalType() {
		return isInternalType;
	}

	/**
	 * @param isInternalType
	 *            setter, see {@link #isInternalType}
	 */
	public void setInternalType(boolean isInternalType) {
		this.isInternalType = isInternalType;
	}

	/**
	 * @return the idCategory, see {@link #idCategory}
	 */
	public IDCategory getIDCategory() {
		return idCategory;
	}

	@Override
	public String toString() {
		return typeName;
	}

}
