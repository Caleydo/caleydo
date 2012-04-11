package org.caleydo.core.data.id;

import java.util.HashMap;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * An IDType defines a semantic grouping of a set of IDs. Examples for IDTypes are global id types, such as
 * "RefSeq", or "David" which are internationally recognized IDType for genes, or custom IDTypes, such as for
 * example to identify the columns in a tabular data file.
 * </p>
 * <p>
 * IDTypes are used to identify that events are to be applied for a component. An example would be a brushing
 * which can only be directly applied if the IDType of the event is the same as the IDType of the receiver.
 * </p>
 * <p>
 * IDTypes belong to {@link IDCategory}. The contract for an IDCategory is that all elements for the types
 * registered with one category can be mapped to each other using the {@link IDMappingManager}.
 * </p>
 * 
 * @author Alexander Lex
 */
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
	 * Register a new IDType. Checks whether whether the columnType is legal. If the typeName is already
	 * registered, a check is conducted whether the registered and the new one match, and if they do, the
	 * previously registered type is returned. Else an exception is thrown.
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
		IDType idType = registeredTypes.get(typeName);
		if (idType != null) {
			if (!idType.getIDCategory().equals(idCategory) || !idType.getColumnType().equals(columnType))
				throw new IllegalStateException("Tried to register id type: " + typeName
					+ " but was already registered with conflicting parameters. \n Previousl registered type "
					+ idType + ", Category: " + idCategory + ", ColumnType: " + columnType);
		}
		else {
			idType = new IDType(typeName, idCategory, columnType);
			registeredTypes.put(typeName, idType);
			Logger.log(new Status(Status.INFO, "IDType", "Registering IDType " + typeName));
		}
		return idType;
	}

	/**
	 * Unregister an IDType. Checks whether the IDType is registered.
	 * 
	 * @param idType
	 *            see {@link #idType}
	 */
	public static void unregisterType(IDType idType) {
		if (idType == null)
			return;
		if (registeredTypes.containsKey(idType.getTypeName())) {
			Logger.log(new Status(Status.INFO, "IDType", "Unregistered IDType " + idType.getTypeName()));
			registeredTypes.remove(idType.getTypeName());
			idType.setTypeName("INVALID");
		}
		else {
			Logger.log(new Status(Status.INFO, "IDType", "Unable to unregister IDType "
				+ idType.getTypeName() + " because it does not exist."));
		}
	}

	/** Returns the IDType for the typeName specified, or null if no such type is registered */
	public static IDType getIDType(String typeName) {
		IDType requestedType = registeredTypes.get(typeName);
		if (requestedType == null)
			throw new IllegalStateException("Requested IDType for typeName \"" + typeName
				+ "\" not registered.");
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
