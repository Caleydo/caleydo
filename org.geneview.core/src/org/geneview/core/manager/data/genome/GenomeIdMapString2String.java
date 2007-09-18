/**
 * 
 */
package org.geneview.core.manager.data.genome;

import java.util.Collection;
import java.util.Set;

import org.geneview.core.data.mapping.GenomeMappingDataType;
import org.geneview.core.manager.data.genome.AGenomeIdMap;
import org.geneview.core.manager.data.genome.IGenomeIdMap;
import org.geneview.core.util.ConversionStringInteger;

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

		return ConversionStringInteger.convertCollection_String2Integer(this.getValues());
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#getValuesString()
	 */
	public Collection<String> getValuesString() {

		return this.getValues();
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.event.IEventPublisherMap#getStringByString(Stringt)
	 */
	public String getStringByString(String key) {

		return hashGeneric.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap#getStringByStringChecked(Stringt)
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
