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
public class GenomeIdMapInt2Int 
extends AGenomeIdMap <Integer,Integer> 
implements IGenomeIdMap {
	
	/**
	 * 
	 */
	public GenomeIdMapInt2Int(final GenomeMappingDataType dataType) {
		super(dataType);
	}

	/**
	 * @param iSizeHashMap
	 */
	public GenomeIdMapInt2Int(final GenomeMappingDataType dataType, int iSizeHashMap) {
		super(dataType, iSizeHashMap);
	
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public int getIntByInt(int key) {

		return hashGeneric.get(key);
	}
	
	public void put( final String key, 
			final String value) {
		hashGeneric.put( Integer.valueOf(key), Integer.valueOf(value));
	}
	
}
