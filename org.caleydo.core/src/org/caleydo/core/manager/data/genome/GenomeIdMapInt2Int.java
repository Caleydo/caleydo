/**
 * 
 */
package org.caleydo.core.manager.data.genome;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;

import org.caleydo.core.data.mapping.EGenomeMappingDataType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.ConversionStringInteger;

/**
 * Genome ID map for Integer to Integer
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class GenomeIdMapInt2Int 
extends AGenomeIdMap <Integer,Integer> 
implements IGenomeIdMap {
	
	/**
	 * Constructor.
	 */
	public GenomeIdMapInt2Int(final IGeneralManager generalManager,
			final EGenomeMappingDataType dataType) {
		
		super(generalManager, dataType);
	}

	/**
	 * Constructor.
	 */
	public GenomeIdMapInt2Int(final IGeneralManager generalManager,
			final EGenomeMappingDataType dataType, int iSizeHashMap) {
		
		super(generalManager, dataType, iSizeHashMap);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getKeysInteger()
	 */
	public final Set<Integer> getKeysInteger() {

		return this.getKeys();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getKeysString()
	 */
	public final Set<String> getKeysString() {

		return ConversionStringInteger.convertSet_Integer2String( this.getKeys() );
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getValuesInteger()
	 */
	public final Collection<Integer> getValuesInteger() {

		return ConversionStringInteger.convertCollection_String2Integer( this.getValuesString() );
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getValuesString()
	 */
	public final Collection<String> getValuesString() {

		return this.getValuesString();
	}
	
	/* ----- end public final ----- */
	
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public int getIntByInt(int key) {

		return hashGeneric.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getIntByIntChecked(int)
	 */
	public int getIntByIntChecked(int key) {

		Integer dummy = hashGeneric.get(key);
		
		// Check if the ID has a mapping
		if (dummy==null)
		{
			generalManager.getLogger().log(Level.FINE, 
					"No mapping found for requested ID " +key);
			
			return -1;
		}
		
		return dummy.intValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#put(java.lang.String, java.lang.String)
	 */
	public void put( final String key, final String value) {
		
		hashGeneric.put( Integer.valueOf(key), Integer.valueOf(value));
	}
}
