package org.caleydo.core.manager;

import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;

/**
 * Generic interface for the mapping ID managers.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @TODO documentation
 */
public interface IIDMappingManager {
	public void createMap(EMappingType type, EMappingDataType dataType);

	public <SrcType, DestType> void createReverseMap(EMappingType sourceType, EMappingType reverseType);

	public <KeyType, ValueType> void createCodeResolvedMap(EMappingType mappingType,
		EMappingType destMappingType);

	public <KeyType, ValueType> Map<KeyType, ValueType> getMapping(EMappingType type);

	public boolean hasMapping(EMappingType type);

	/**
	 * Returns the mapped ID of type ValueType or null if no such mapping exists.
	 * 
	 * @param <KeyType>
	 *            the type of the key used in the mapping
	 * @param <ValueType>
	 *            the type of the value used in the mapping
	 * @param type
	 *            the mapping type, specifying the actual relationship between key and value
	 * @param key
	 *            the key for which the mapping is requested
	 * @return the value, or null if no such mapping exists
	 */
	public <KeyType, ValueType> ValueType getID(EMappingType type, KeyType key);

	public <KeyType, ValueType> Set<ValueType> getMultiID(EMappingType type, KeyType key);
}
