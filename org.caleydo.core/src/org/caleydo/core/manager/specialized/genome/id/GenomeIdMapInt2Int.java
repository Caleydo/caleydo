/**
 * 
 */
package org.caleydo.core.manager.specialized.genome.id;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;
import org.caleydo.core.util.ConversionStringInteger;

/**
 * Genome ID map for Integer to Integer
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GenomeIdMapInt2Int
	extends AGenomeIdMap<Integer, Integer>
	implements IGenomeIdMap
{

	/**
	 * Constructor.
	 */
	public GenomeIdMapInt2Int(final IGeneralManager generalManager,
			final EMappingDataType dataType)
	{

		super(generalManager, dataType);
	}

	/**
	 * Constructor.
	 */
	public GenomeIdMapInt2Int(final IGeneralManager generalManager,
			final EMappingDataType dataType, int iSizeHashMap)
	{

		super(generalManager, dataType, iSizeHashMap);
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
	public final Collection<Integer> getValuesInteger()
	{

		return ConversionStringInteger
				.convertCollection_String2Integer(this.getValuesString());
	}

	@Override
	public final Collection<String> getValuesString()
	{

		return this.getValuesString();
	}

	/* ----- end public final ----- */

	@Override
	public int getIntByInt(int key)
	{

		return hashGeneric.get(key);
	}

	@Override
	public int getIntByIntChecked(int key)
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

		hashGeneric.put(Integer.valueOf(key), Integer.valueOf(value));
	}
}
