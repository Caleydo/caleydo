package org.caleydo.core.manager.specialized.genome.id;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;
import org.caleydo.core.util.ConversionStringInteger;

/**
 * Genome ID map for String to Integer
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GenomeIdMapString2Int
	extends AGenomeIdMap<String, Integer>
	implements IGenomeIdMap
{

	/**
	 * Constructor.
	 */
	public GenomeIdMapString2Int(final IGeneralManager generalManager,
			final EMappingDataType dataType)
	{

		super(generalManager, dataType);
	}

	/**
	 * Constructor.
	 */
	public GenomeIdMapString2Int(final IGeneralManager generalManager,
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

		return this.getValues();
	}

	@Override
	public Collection<String> getValuesString()
	{

		return ConversionStringInteger.convertCollection_Integer2String(this.getValues());
	}

	@Override
	public int getIntByString(String key)
	{

		return hashGeneric.get(key);
	}

	@Override
	public int getIntByStringChecked(String key)
	{

		Integer dummy = hashGeneric.get(key);

		// Check if the ID has a mapping
		if (dummy == null)
		{
			generalManager.getLogger().log(Level.FINE,
					"No mapping found for requested ID " + key);
			return -1;
		}

		return dummy.intValue();
	}

	@Override
	public void put(final String key, final String value)
	{

		hashGeneric.put(key, Integer.valueOf(value));
	}
}
