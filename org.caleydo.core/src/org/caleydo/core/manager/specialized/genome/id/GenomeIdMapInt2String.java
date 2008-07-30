package org.caleydo.core.manager.specialized.genome.id;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;

import org.caleydo.core.data.mapping.EGenomeMappingDataType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;
import org.caleydo.core.manager.specialized.genome.id.AGenomeIdMap;
import org.caleydo.core.util.ConversionStringInteger;

/**
 * Genome ID map for Integer to String
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GenomeIdMapInt2String
	extends AGenomeIdMap<Integer, String>
	implements IGenomeIdMap
{

	/**
	 * Constructor.
	 */
	public GenomeIdMapInt2String(final IGeneralManager generalManager,
			final EGenomeMappingDataType dataType)
	{

		super(generalManager, dataType);
	}

	/**
	 * Constructor.
	 */
	public GenomeIdMapInt2String(final IGeneralManager generalManager,
			final EGenomeMappingDataType dataType, int iSizeHashMap)
	{

		super(generalManager, dataType, iSizeHashMap);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getKeysInteger()
	 */
	public final Set<Integer> getKeysInteger()
	{

		return this.getKeys();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getKeysString()
	 */
	public final Set<String> getKeysString()
	{

		return ConversionStringInteger.convertSet_Integer2String(this.getKeys());
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getValuesInteger()
	 */
	public Collection<Integer> getValuesInteger()
	{

		return ConversionStringInteger.convertCollection_String2Integer(this.getValues());
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getValuesString()
	 */
	public Collection<String> getValuesString()
	{

		return this.getValues();
	}

	public void put(final String key, final String value)
	{

		hashGeneric.put(Integer.valueOf(key), value);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.event.IEventPublisherMap#getStringByInt(int)
	 */
	public String getStringByInt(int key)
	{

		return hashGeneric.get(key);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.genome.IGenomeIdMap#getStringByIntChecked
	 * (int)
	 */
	public String getStringByIntChecked(int key)
	{

		// Check if the ID has a mapping
		if (hashGeneric.containsKey(key))
		{
			return hashGeneric.get(key);
		}
		else
		{
			generalManager.getLogger().log(Level.FINE,
					"No mapping found for requested ID " + key);

			return "Invalid";
		}
	}
}
