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
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Edge of a mapping graph. It holds the the source and target ID type.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class MappingType extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;

	private IDType fromIDType;
	private IDType toIDType;

	private boolean isMultiMap = false;
	/**
	 * Flag telling whether this MappingType has an equivalent reverse
	 * MappingType
	 */
	private boolean hasReverseMap = false;

	private static HashMap<String, MappingType> registeredMappingTypes = new HashMap<String, MappingType>();

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
		super();
		this.fromIDType = fromIDType;
		this.toIDType = toIDType;
		this.isMultiMap = isMultiMap;
		this.hasReverseMap = hasReverseMap;
	}

	/**
	 * Factory method for a new mapping type. Creates a new mapping type or
	 * retrieves an existing mapping type if for the given parameters a mapping
	 * type was previously registered.
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
	 * Get a previously registered mapping type based on iths source and target
	 * id type
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
	 * @param fromIDType
	 *            setter, see {@link #fromIDType}
	 */
	public void setFromIDType(IDType fromIDType) {
		this.fromIDType = fromIDType;
	}

	/**
	 * @return the toIDType, see {@link #toIDType}
	 */
	public IDType getToIDType() {
		return toIDType;
	}

	/**
	 * @param toIDType
	 *            setter, see {@link #toIDType}
	 */
	public void setToIDType(IDType toIDType) {
		this.toIDType = toIDType;
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
