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

		return ConversionStringInteger.convertSet_Integer2String(this.getKeys());
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getValuesInteger()
	 */
	public Collection<Integer> getValuesInteger() {
		
		return ConversionStringInteger.convertCollection_String2Integer(this.getValues());
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getValuesString()
	 */
	public Collection<String> getValuesString() {

		return this.getValues();
	}
	
	
	public void put( final String key, 
			final String value) {
		hashGeneric.put( Integer.valueOf(key), value);
	}



	/* (non-Javadoc)
	 * @see cerberus.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public String getStringByInt(int key) {

		return hashGeneric.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.genome.IGenomeIdMap#getStringByIntChecked(int)
	 */
	public String getStringByIntChecked(int key) {

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
