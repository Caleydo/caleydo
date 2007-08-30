/**
 * 
 */
package cerberus.manager.data.genome;

import java.util.Collection;
import java.util.Set;

import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.manager.data.genome.AGenomeIdMap;
import cerberus.manager.data.genome.IGenomeIdMap;
import cerberus.util.ConversionStringInteger;

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

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getKeysInteger()
	 */
	public final Set<Integer> getKeysInteger() {

		return this.getKeys();
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getKeysString()
	 */
	public final Set<String> getKeysString() {

		return ConversionStringInteger.convertSet_Integer2String( this.getKeys() );
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getValuesInteger()
	 */
	public final Collection<Integer> getValuesInteger() {

		return ConversionStringInteger.convertCollection_String2Integer( this.getValuesString() );
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getValuesString()
	 */
	public final Collection<String> getValuesString() {

		return this.getValuesString();
	}
	
	/* ----- end public final ----- */
	
	
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
	
}
