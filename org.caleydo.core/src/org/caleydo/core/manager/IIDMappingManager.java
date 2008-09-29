package org.caleydo.core.manager;

import java.util.HashMap;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;

/**
 * Generic interface for the mapping ID managers.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * 
 * @TODO documentation
 */
public interface IIDMappingManager
{
	public void createMap(EMappingType type, EMappingDataType dataType);
	
	public <SrcType, DestType> void createReverseMap(EMappingType sourceType, EMappingType reverseType);

	public <KeyType, ValueType> void createCodeResolvedMap(EMappingType mappingType, EMappingType destMappingType);
	
	public <KeyType, ValueType> HashMap<KeyType, ValueType> getMapping(EMappingType type);
	
	public boolean hasMapping(EMappingType type);
	
	public <KeyType, ValueType> ValueType getID(EMappingType type, KeyType key);
}
