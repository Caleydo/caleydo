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
public class GenomeIdMapString2String 
extends AGenomeIdMap <String,String> 
implements IGenomeIdMap {
	
	/**
	 * 
	 */
	public GenomeIdMapString2String(final GenomeMappingDataType dataType) {
		super(dataType);
	}

	/**
	 * @param iSizeHashMap
	 */
	public GenomeIdMapString2String(final GenomeMappingDataType dataType,
			final int iSizeHashMap) {
		
		super(dataType, iSizeHashMap);		
	}

	
	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getStringByString(Stringt)
	 */
	public String getStringByString(String key) {

		return hashGeneric.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getStringByStringChecked(Stringt)
	 */
	public String getStringByStringChecked(String key) {

		// Check if the code has a mapping
		if (hashGeneric.containsKey(key))
		{
			return hashGeneric.get(key);
		}
		else
		{
			System.err.println("No mapping found for requested code: " +key);
			return "Invalid";
		}
	}
	
	public void put( final String key, 
			final String value) {
		hashGeneric.put( key, value);
	}

}
