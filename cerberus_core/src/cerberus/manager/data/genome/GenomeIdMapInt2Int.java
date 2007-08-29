/**
 * 
 */
package cerberus.manager.data.genome;

import java.util.Set;

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
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getIntByIntChecked(int)
	 */
	public int getIntByIntChecked(int key) {

		Integer dummy = hashGeneric.get(key);
		
		// Check if the ID has a mapping
		if (dummy==null)
		{
			if  (ENABLE_DEBUG) {
				System.err.println("No mapping found for requested ID: " +key);
			}
			return -1;
		}
		
		return dummy.intValue();
	}
	
	public void put( final String key, 
			final String value) {
		hashGeneric.put( Integer.valueOf(key), Integer.valueOf(value));
	}
	
	public final Set<Integer> getKeysInteger() {

		return this.getKeys();
	}

	public final Set<String> getKeysString() {

		return null;
	}
	
}
