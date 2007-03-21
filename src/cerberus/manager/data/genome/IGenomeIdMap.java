package cerberus.manager.data.genome;

//import java.util.HashMap;

import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.data.IGenomeIdManager;
//import cerberus.data.mapping.GenomeMappingDataType;


/**
 * Interface for Map
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * 
 * @see cerberus.base.map.MultiHashArrayStringMap
 * @see cerberus.base.map.MultiHashArrayIntegerMap
 */
public interface IGenomeIdMap {

//	public Object getObjectByType( final Object byObject, GenomeMappingDataType type );
	
	public int getIntByInt( final int key);
	
	public int getIntByString( final String key);
	
	public String getStringByInt( final int key);
	
	public String getStringByString( final String key);

	
	public int getIntByIntChecked( final int key);
	
	public int getIntByStringChecked( final String key);
	
	public String getStringByIntChecked( final int key);
	
	public String getStringByStringChecked( final String key);
	
	/**
	 * Adds a key-value pair. 
	 * Each implementation has to cast to the propper types.
	 * 
	 * @param key
	 * @param value
	 */
	public void put( final String key, 
			final String value);
	
//	// Object cast to int or String
//	public int getInt( final Object byObject);
//	
//	public String getString( final Object byObject);
	
	/**
	 * Get size of the map.
	 * 
	 * @see java.util.Map#size()
	 * 
	 * @return size of the Map
	 */
	public int size();	
	
	/**
	 * Creates a copy of this HashMap by adding 
	 * each <Key,Value> as <Value,Key> to the new HashMap.
	 * 
	 * Not very efficient, since each <Key,Value> is converted to a 
	 * String and than cast to either (String) or (int)
	 * 
	 * @return reversed HashMap of this HashMap
	 */
	public IGenomeIdMap getReversedMap();
	
	/**
	 * Method reolves the codes in the map.
	 * That means all codes are looked up the mapping tables
	 * (Code -> ID) and the Codes are replaced by the internal
	 * IDs.
	 * 
	 * @param refGenomeIdManager
	 * @param genomeMappingLUT_1
	 * @param genomeMappingLUT_2
	 * @param targetMappingDataType
	 * @param sourceMappingDataType
	 * @return
	 */
	public IGenomeIdMap getCodeResolvedMap(
			IGenomeIdManager refGenomeIdManager,
			GenomeMappingType genomeMappingLUT_1,
			GenomeMappingType genomeMappingLUT_2,
			GenomeMappingDataType targetMappingDataType,
			GenomeMappingDataType sourceMappingDataType);
}
