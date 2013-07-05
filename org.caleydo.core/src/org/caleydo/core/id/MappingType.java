/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.id;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Edge of a mapping graph. It holds the the source and target ID type.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class MappingType extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;

	private final IDType fromIDType;
	private final IDType toIDType;

	private final boolean isMultiMap;
	/**
	 * Flag telling whether this MappingType has an equivalent reverse
	 * MappingType
	 */
	private final boolean hasReverseMap;

	private static Map<String, MappingType> registeredMappingTypes = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param fromIDType
	 *            Source ID type.
	 * @param toIDType
	 *            Target ID type.
	 */
	private MappingType(IDType fromIDType, IDType toIDType, boolean isMultiMap,
			boolean hasReverseMap) {
		this.fromIDType = fromIDType;
		this.toIDType = toIDType;
		this.isMultiMap = isMultiMap;
		this.hasReverseMap = hasReverseMap;
	}

	/**
	 * Factory method for a new mapping type. Creates a new mapping type or retrieves an existing mapping type if for
	 * the given parameters a mapping type was previously registered.
	 * 
	 * @param fromIDType
	 * @param toIDType
	 * @param isMultiMap
	 * @return
	 */
	public static MappingType registerMappingType(IDType fromIDType, IDType toIDType,
			boolean isMultiMap, boolean createReverseMap) {
		String key = generateKey(fromIDType, toIDType);
		MappingType mappingType = registeredMappingTypes.get(key);
		if (mappingType != null) {
			if (mappingType.isHasReverseMap() == createReverseMap)
				return mappingType;
			else {
				throw new IllegalStateException(
						"Asked for an already existing mapping type, but specified different parameters. Existing reversemap: "
								+ mappingType.isHasReverseMap()
								+ ", new parameter: "
								+ createReverseMap);
			}
		}
		mappingType = new MappingType(fromIDType, toIDType, isMultiMap, createReverseMap);
		registeredMappingTypes.put(key, mappingType);
		return mappingType;
	}

	/**
	 * Get a previously registered mapping type based on iths source and target id type
	 * 
	 * @param fromIDType
	 * @param toIDType
	 * @return
	 */
	public static MappingType getType(IDType fromIDType, IDType toIDType) {
		return registeredMappingTypes.get(generateKey(fromIDType, toIDType));
	}

	private static String generateKey(IDType fromIDType, IDType toIDType) {
		return fromIDType.toString() + toIDType.toString();
	}

	/**
	 * @return the fromIDType, see {@link #fromIDType}
	 */
	public IDType getFromIDType() {
		return fromIDType;
	}

	/**
	 * @return the toIDType, see {@link #toIDType}
	 */
	public IDType getToIDType() {
		return toIDType;
	}


	/**
	 * @return the isMultiMap, see {@link #isMultiMap}
	 */
	public boolean isMultiMap() {
		return isMultiMap;
	}

	/**
	 * @return the hasReverseMap, see {@link #hasReverseMap}
	 */
	public boolean isHasReverseMap() {
		return hasReverseMap;
	}

	@Override
	public String toString() {
		return fromIDType + " to " + toIDType;
	}
}
