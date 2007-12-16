/**
 * 
 */
package org.geneview.core.manager.data.genome;

import java.util.Collection;
import java.util.Set;

import org.geneview.core.data.mapping.EGenomeMappingDataType;
import org.geneview.core.manager.data.genome.AGenomeIdMap;
import org.geneview.core.manager.data.genome.IGenomeIdMap;
import org.geneview.core.util.ConversionStringInteger;

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
	public GenomeIdMapString2Int(final EGenomeMappingDataType dataType) {
		super(dataType);
	}

	/**
	 * @param iSizeHashMap
	 */
	public GenomeIdMapString2Int(final EGenomeMappingDataType dataType, final int iSizeHashMap) {
		super(dataType, iSizeHashMap);
	}


	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#getKeysInteger()
	 */
	public final Set<Integer> getKeysInteger() {

		return ConversionStringInteger.convertSet_String2Integer(this.getKeys());
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#getKeysString()
	 */
	public final Set<String> getKeysString() {

		return this.getKeys();
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#getValuesInteger()
	 */
	public Collection<Integer> getValuesInteger() {

		return this.getValues();
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#getValuesString()
	 */
	public Collection<String> getValuesString() {

		return ConversionStringInteger.convertCollection_Integer2String(this.getValues());
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public int getIntByString(String key) {

		return hashGeneric.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#getIntByStringChecked(Stringt)
	 */
	public int getIntByStringChecked(String key) {

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
		hashGeneric.put( key, Integer.valueOf(value));
	}

}
