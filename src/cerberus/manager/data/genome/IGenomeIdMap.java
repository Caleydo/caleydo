package cerberus.manager.data.genome;

import java.util.HashMap;

//import cerberus.data.mapping.GenomeMappingDataType;


/**
 * Interface for Map
 * 
 * @author Michael Kalkusch
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
	
}
