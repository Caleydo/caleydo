/**
 * 
 */
package cerberus.manager.data.genome;

import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.manager.data.genome.AGenomeIdMap;
import cerberus.manager.data.genome.IGenomeIdMap;

/**
 * @author Michael Kalkusch
 *
 */
public class GenomeIdMapString2Int
extends AGenomeIdMap <String,Integer> 
implements IGenomeIdMap {
	
	/**
	 * 
	 */
	public GenomeIdMapString2Int(final GenomeMappingDataType dataType) {
		super(dataType);
	}

	/**
	 * @param iSizeHashMap
	 */
	public GenomeIdMapString2Int(final GenomeMappingDataType dataType, final int iSizeHashMap) {
		super(dataType, iSizeHashMap);
	}


	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public int getIntByString(String key) {

		return hashGeneric.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getIntByStringChecked(java.lang.String)
	 */
	public int getIntByStringChecked(String key) {

		// Check if the ID has a mapping
		if (hashGeneric.containsKey(key))
		{
			return hashGeneric.get(key);
		}
		else
		{
			System.err.println("No mapping found for requested ID: " +key);
			return -1;
		}
	}

	public void put( final String key, 
			final String value) {
		hashGeneric.put( key, Integer.valueOf(value));
	}
}
