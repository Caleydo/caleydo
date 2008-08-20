package org.caleydo.core.manager.specialized.genome.id;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;
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
			final EMappingDataType dataType)
	{

		super(generalManager, dataType);
	}

	/**
	 * Constructor.
	 */
	public GenomeIdMapString2String(final IGeneralManager generalManager,
			final EMappingDataType dataType, int iSizeHashMap)
	{

		super(generalManager, dataType, iSizeHashMap);
	}

	@Override
	public final Set<Integer> getKeysInteger()
	{

		return ConversionStringInteger.convertSet_String2Integer(this.getKeys());
	}

	@Override
	public final Set<String> getKeysString()
	{

		return this.getKeys();
	}

	@Override
	public Collection<Integer> getValuesInteger()
	{

		return ConversionStringInteger.convertCollection_String2Integer(this.getValues());
	}

	@Override
	public Collection<String> getValuesString()
	{

		return this.getValues();
	}

	@Override
	public String getStringByString(String key)
	{

		return hashGeneric.get(key);
	}

	@Override
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

	@Override
	public void put(final String key, final String value)
	{

		hashGeneric.put(key, value);
	}
}
