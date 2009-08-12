package org.caleydo.core.manager;

import java.util.Map;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;

/**
 * Generic interface for the mapping ID managers.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @TODO documentation
 */
public interface IIDMappingManager {

	public <K, V> void createMap(EMappingType mappingType);

	public <SrcType, DestType> void createReverseMap(EMappingType sourceType, EMappingType reverseType);

	public <KeyType, ValueType> void createCodeResolvedMap(EMappingType mappingType,
		EMappingType destMappingType);

	public <KeyType, ValueType> Map<KeyType, ValueType> getMapping(EMappingType type);

	public boolean hasMapping(EMappingType type);

	/**
	 * Tries to find the mapping from the source IDType to the destination IDType of the specified sourceID
	 * along a path of IDTypes where mappings exist. If no such path is found, null is returned. If the path
	 * includes multimappings, a Set of values is returned. Note that there will always be chosen a path that
	 * does not include multimappings over paths that include multimappings if more than one path exists.
	 * 
	 * @param <K>
	 *            Type of the sourceID
	 * @param <V>
	 *            Type of the expected result of the mapping
	 * @param source
	 *            IDType of the source data
	 * @param destination
	 *            IDType of the destination data
	 * @param sourceID
	 *            ID for which the mapping shall be found
	 * @return If no mapping is found, null, otherwise the corresponding ID, or Set of IDs. 
	 */
	public <K, V> V getID(EIDType source, EIDType destination, K sourceID);

}
