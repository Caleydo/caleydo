package org.geneview.core.manager.data.genome;

//import java.util.HashMap;

import java.util.Collection;
import java.util.Set;

import org.geneview.core.data.mapping.EGenomeMappingDataType;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.data.IGenomeIdManager;
//import org.geneview.core.data.mapping.EGenomeMappingDataType;


/**
 * Interface for Map
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * 
 * @see org.geneview.core.data.map.MultiHashArrayStringMap
 * @see org.geneview.core.data.map.MultiHashArrayIntegerMap
 */
public interface IGenomeIdMap {

	public static final boolean ENABLE_DEBUG = false;
	
//	public Object getObjectByType( final Object byObject, EGenomeMappingDataType type );
	
	public int getIntByInt( final int key);
	
	public int getIntByString( final String key);
	
	public String getStringByInt( final int key);
	
	public String getStringByString( final String key);	
	
	public int getIntByIntChecked( final int key);
	
	public int getIntByStringChecked( final String key);
	
	public String getStringByIntChecked( final int key);
	
	public String getStringByStringChecked( final String key);
	
	/**
	 * Expose keys in a Set <Integer>
	 * Note if values can not be converted an exception is thrown.
	 * Attention: the internal data type could be Integer or String
	 * 
	 * @see IGenomeIdMap#getKeysString()
	 * @see IGenomeIdMap#getValuesString()
	 * @see IGenomeIdMap#getValuesInteger()
	 * 
	 * @return keys in a set
	 */
	public Set <Integer> getKeysInteger();
	
	/**
	 * Expose keys in a Set <String>
	 * Attention: the internal data type could be Integer or String
	 * 
	 * @see IGenomeIdMap#getKeysInteger()
	 * @see IGenomeIdMap#getValuesString()
	 * @see IGenomeIdMap#getValuesInteger()
	 * 
	 * @return keys exposed as Set <String>
	 */
	public Set <String> getKeysString();
	
	/**
	 * Expose keys in a Set <Integer>
	 * Note if values can not be converted to Integer an exception is thrown.
	 * Attention: the internal data type could be Integer or String
	 * 
	 * @see IGenomeIdMap#getValuesString()
	 * @see IGenomeIdMap#getKeysInteger()
	 * @see IGenomeIdMap#getKeysString()
	 * 
	 * @return
	 */
	public Collection <Integer> getValuesInteger();
	
	/**
	 * Expose keys in a Set <String>
	 * Attention: the internal data type could be Integer or String
	 * 
	 * @see IGenomeIdMap#getValuesInteger()
	 * @see IGenomeIdMap#getKeysInteger()
	 * @see IGenomeIdMap#getKeysString()
	 * 
	 * @return
	 */
	public Collection <String> getValuesString();
	
	/**
	 * Adds a key-value pair. 
	 * Each implementation has to cast to the proper types.
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
			EGenomeMappingType genomeMappingLUT_1,
			EGenomeMappingType genomeMappingLUT_2,
			EGenomeMappingDataType targetMappingDataType,
			EGenomeMappingDataType sourceMappingDataType);
	
}
