/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.id;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * An IDType defines a semantic grouping of a set of IDs. Examples for IDTypes
 * are global id types, such as "RefSeq", or "David" which are internationally
 * recognized IDType for genes, or custom IDTypes, such as for example to
 * identify the columns in a tabular data file.
 * </p>
 * <p>
 * IDTypes are used, among others, to identify that events are to be applied for
 * a component. An example would be a brushing which can only be directly
 * applied if the IDType of the event is the same as the IDType of the receiver.
 * </p>
 * <p>
 * IDTypes belong to an {@link IDCategory}. The contract for IDTypes belonging
 * to the same IDCategory is that all elements for the types registered with one
 * category can be mapped to each other using the {@link IDMappingManager}.
 * </p>
 * <p>
 * IDTypes can also hold rules on how to parse them correctly from a string (see
 * {@link #idTypeParsingRules}).
 * </p>
 * <p>
 * This class is also a singleton that manages all IDTypes.
 * </p>
 *
 * @author Alexander Lex
 */
public class IDType {

	private static Map<String, IDType> registeredTypes = new HashMap<String, IDType>();

	/**
	 * The {@link IDCategory} of an {@link IDType} specifies to which category
	 * an ID belongs. The contract is, that between two IDTypes that share the
	 * same IDCategory there must exist a mapping in the
	 * {@link IDMappingManager}.
	 */
	private IDCategory idCategory;

	/**
	 * Type name that needs to be a unique value for every new IDType. Besides
	 * being unique, it should also be human-readable to some extend, to make
	 * debugging easier.
	 */
	private String typeName;

	/**
	 * Specifies the data type of the IDType. Allowed values are
	 * {@link EDataClass#NATURAL_NUMBER} and {@link EDataClass#UNIQUE_OBJECT}
	 */
	private EDataClass dataType;

	/**
	 * flag determining whether a type is internal only, meaning that it is
	 * dynamically generated using, e.g., a running number (true), or an
	 * externally provided ID (e.g., refseq, false)
	 */
	private boolean isInternalType = false;

	/**
	 * Rules for parsing an id type. Defaults to null.
	 */
	private IDTypeParsingRules idTypeParsingRules = null;

	private IDType(String typeName, IDCategory idCategory, EDataClass dimensionType) {
		this.typeName = typeName;
		this.idCategory = idCategory;
		this.dataType = dimensionType;
	}

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
	public void setDataType(EDataClass dataType) {
		this.dataType = dataType;
	}

	/**
	 * @param idTypeParsingRules
	 *            setter, see {@link #idTypeParsingRules}
	 */
	public void setIdTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
		if (!idTypeParsingRules.isDefault())
			throw new IllegalArgumentException(
					"A parsing rule set to an id type must be a default parsing rule but the passed one isn't.");
		this.idTypeParsingRules = idTypeParsingRules;
	}

	/**
	 * @return the idTypeParsingRules, see {@link #idTypeParsingRules}
	 */
	public IDTypeParsingRules getIdTypeParsingRules() {
		return idTypeParsingRules;
	}

	/**
	 * Register a new IDType. Checks whether whether the columnType is legal. If
	 * the typeName is already registered, a check is conducted whether the
	 * registered and the new one match, and if they do, the previously
	 * registered type is returned. Else an exception is thrown.
	 *
	 * @param typeName
	 *            see {@link #typeName}
	 * @param idCategory
	 *            see {@link #idCategory}
	 * @param columnType
	 *            see {@link #dataType}
	 * @return the created ID Type
	 */
	public static IDType registerType(String typeName, IDCategory idCategory,
			EDataClass columnType) {
		if (!(columnType == EDataClass.UNIQUE_OBJECT || columnType == EDataClass.NATURAL_NUMBER))
			throw new IllegalStateException(
					"IDTypes are allowed to be only either of type UNIQUE_OBJECT or NATURAL_NUMBER, but was: "
							+ columnType);
		synchronized (IDType.class) {
			IDType idType = registeredTypes.get(typeName);
			if (idType != null) {
				if (!idType.getIDCategory().equals(idCategory) || !idType.getColumnType().equals(columnType))
					throw new IllegalStateException("Tried to register id type: " + typeName + ", Category: "
							+ idCategory + ", ColumnType: " + columnType
							+ "\n but was already registered with conflicting parameters. \n"
							+ "Previously registered type: " + idType + ", Category: " + idType.getIDCategory()
							+ ", ColumnType: " + idType.getColumnType());
			} else {
				idType = new IDType(typeName, idCategory, columnType);
				registeredTypes.put(typeName, idType);
				idCategory.addIDType(idType);
				Logger.log(new Status(IStatus.INFO, "IDType", "Registering IDType " + typeName));
			}
			return idType;
		}
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
		synchronized (IDType.class) {
			if (registeredTypes.containsKey(idType.getTypeName())) {
				Logger.log(new Status(IStatus.INFO, "IDType", "Unregistered IDType " + idType.getTypeName()));
				registeredTypes.remove(idType.getTypeName());
				idType.setTypeName("INVALID");
				idType.getIDCategory().removeIDType(idType);
			} else {
				Logger.log(new Status(IStatus.INFO, "IDType", "Unable to unregister IDType " + idType.getTypeName()
						+ " because it does not exist."));
			}
		}

	}

	/**
	 * Returns the IDType for the typeName specified, or null if no such type is
	 * registered
	 */
	public static IDType getIDType(String typeName) {
		synchronized (IDType.class) {
			return registeredTypes.get(typeName);
		}
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
	 * @return the columnType, see {@link #dataType}
	 */
	public EDataClass getColumnType() {
		return dataType;
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

	/**
	 * Calculates the probability of the specified list of ids to belong to this
	 * IDType.
	 *
	 * @param idList
	 * @return Probability of affiliation as value from 0 to 1. 0 means that
	 *         none of the specified ids matched this IDType, wheras 1 means
	 *         that all matched this IDType.
	 */
	public float calcProbabilityOfIDTypeAffiliation(List<String> idList) {

		if (idList == null || idList.isEmpty())
			return 0;

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(idCategory);

		int numMatchedIDs = 0;

		for (String currentID : idList) {

			if (getColumnType().equals(EDataClass.NATURAL_NUMBER)) {
				try {
					Integer idInt = Integer.valueOf(currentID);
					if (idMappingManager.doesElementExist(this, idInt)) {
						numMatchedIDs++;
					}
				} catch (NumberFormatException e) {
				}
			} else if (getColumnType().equals(EDataClass.UNIQUE_OBJECT)) {
				if (idMappingManager.doesElementExist(this, currentID)) {
					numMatchedIDs++;
				} else if (getTypeName().equals("REFSEQ_MRNA")) {
					if (currentID.contains(".")) {
						if (idMappingManager.doesElementExist(this,
								currentID.substring(0, currentID.indexOf(".")))) {
							numMatchedIDs++;
						}
					}
				}
			}
		}

		return (float) numMatchedIDs / (float) idList.size();
	}

}
