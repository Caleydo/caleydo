package org.caleydo.core.manager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.mapping.MappingType;

/**
 * Generic interface for the mapping ID managers.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IIDMappingManager {

	/**
	 * Adds a new map for the specified mapping type. To fill that map with elements, use the method
	 * getMapping after calling this one.
	 * 
	 * @param <K>
	 *            Type of Keys of the map
	 * @param <V>
	 *            Type of Values of the map
	 * @param fromIDType
	 *            Specifies the source ID type.
	 * @param toIDType
	 *            Specifies the target ID type.
	 * @param isMultiMap
	 *            Determines if a multi map will be created.
	 */
	public <K, V> void createMap(IDType fromIDType, IDType toIDType, boolean isMultiMap);

	/**
	 * Creates a reverse map to an already existent map.
	 * 
	 * @param <SrcType>
	 * @param <DestType>
	 * @param sourceType
	 *            Mapping type the reverse map shall be created for.
	 * @param reverseType
	 *            Mapping type of the reverse map.
	 */
	public <SrcType, DestType> void createReverseMap(MappingType sourceType, MappingType reverseType);

	/**
	 * Method takes a map that contains identifier codes and creates a new resolved codes. Resolving means
	 * mapping from code to internal ID.
	 * 
	 * @param <KeyType>
	 * @param <ValueType>
	 * @param mappingType
	 *            Mapping type that specifies the already existent map which is used for creating the code
	 *            resolved map.
	 * @param destMappingType
	 *            Mapping type of the resolved map.
	 */
	public <KeyType, ValueType> void createCodeResolvedMap(MappingType mappingType,
		MappingType destMappingType);

	/**
	 * Gets the map of the specified mapping type for manipulation.
	 * 
	 * @param <KeyType>
	 * @param <ValueType>
	 * @param type
	 *            Mapping type that identifies the map.
	 * @return Map that corresponds to the specified mapping type. If no such map exists, null is returned.
	 */
	public <KeyType, ValueType> Map<KeyType, ValueType> getMap(MappingType type);

	/**
	 * Returns, whether a mapping is possible from the specified source IDType to the destination IDType.
	 * 
	 * @param source
	 *            Source IDType of the mapping.
	 * @param destination
	 *            Destination IDType of the mapping.
	 * @return True, if a mapping is possible, false otherwise.
	 */
	public boolean hasMapping(IDType source, IDType destination);

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
	public <K, V> V getID(IDType source, IDType destination, K sourceID);

	/**
	 * Tries to find the mapping from the source IDType to the destination IDType of the specified sourceID
	 * along a path of IDTypes where mappings exist. If no such path is found, null is returned. The result
	 * will always be a Set of the found mappings.
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
	 * @return If no mapping is found, null, otherwise the Set containing the corresponding ID(s).
	 */
	public <K, V> Set<V> getIDAsSet(IDType source, IDType destination, K sourceID);

	/**
	 * Determines whether the IIDMappingManager holds a map that contains the specified element of the
	 * specified type.
	 * 
	 * @param <T>
	 * @param idType
	 *            IDType of the element.
	 * @param element
	 *            Element to be found.
	 * @return True, if such an element is fund, fals otherwise.
	 */
	public <T> boolean doesElementExist(IDType idType, T element);

	/**
	 * Returns all id types registered in this ID Mapping Manager
	 * 
	 * @return
	 */
	public HashSet<IDType> getIDTypes();

}
