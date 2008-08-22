package org.caleydo.core.manager.specialized.genome.id;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;
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
	public GenomeIdMapInt2String(final EMappingDataType dataType)
	{
		super(dataType);
	}

	@Override
	public final Set<Integer> getKeysInteger()
	{

		return this.getKeys();
	}

	@Override
	public final Set<String> getKeysString()
	{

		return ConversionStringInteger.convertSet_Integer2String(this.getKeys());
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

	public void put(final String key, final String value)
	{

		hashGeneric.put(Integer.valueOf(key), value);
	}

	@Override
	public String getStringByInt(int key)
	{

		return hashGeneric.get(key);
	}

	@Override
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
