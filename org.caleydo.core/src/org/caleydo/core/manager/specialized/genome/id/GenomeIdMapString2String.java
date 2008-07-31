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
 * Genome ID map for String to String
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GenomeIdMapString2String
	extends AGenomeIdMap<String, String>
	implements IGenomeIdMap
{

	/**
	 * Constructor.
	 */
	public GenomeIdMapString2String(final IGeneralManager generalManager,
			final EGenomeMappingDataType dataType)
	{

		super(generalManager, dataType);
	}

	/**
	 * Constructor.
	 */
	public GenomeIdMapString2String(final IGeneralManager generalManager,
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

		return ConversionStringInteger.convertSet_String2Integer(this.getKeys());
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap#getKeysString()
	 */
	public final Set<String> getKeysString()
	{

		return this.getKeys();
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

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.event.IEventPublisherMap#getStringByString(Stringt
	 * )
	 */
	public String getStringByString(String key)
	{

		return hashGeneric.get(key);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.genome.IGenomeIdMap#getStringByStringChecked
	 * (Stringt)
	 */
	public String getStringByStringChecked(String key)
	{

		// Check if the code has a mapping
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

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.genome.IGenomeIdMap#put(java.lang.String,
	 * java.lang.String)
	 */
	public void put(final String key, final String value)
	{

		hashGeneric.put(key, value);
	}
}
