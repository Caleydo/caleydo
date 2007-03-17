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
public class GenomeIdMapInt2String 
extends AGenomeIdMap <Integer,String> 
implements IGenomeIdMap {

	/**
	 * 
	 */
	public GenomeIdMapInt2String(final GenomeMappingDataType dataType) {
		super(dataType);
	}

	/**
	 * @param iSizeHashMap
	 */
	public GenomeIdMapInt2String(final GenomeMappingDataType dataType, final int iSizeHashMap) {
		super(dataType, iSizeHashMap);
		
	}
	
	public void put( final String key, 
			final String value) {
		hashGeneric.put( Integer.valueOf(key), value);
	}



	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public String getStringByInt(int key) {

		// Check if the ID has a mapping
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

}
