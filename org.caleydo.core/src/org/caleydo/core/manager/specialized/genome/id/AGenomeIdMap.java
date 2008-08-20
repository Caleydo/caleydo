package org.caleydo.core.manager.specialized.genome.id;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;

/**
 * Abstract class of genome ID maps for all types.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AGenomeIdMap<K, V>
	implements IGenomeIdMap
{
	protected IGeneralManager generalManager;

	protected HashMap<K, V> hashGeneric;

	protected final EMappingDataType dataType;

	/**
	 * Constructor.
	 */
	public AGenomeIdMap(final EMappingDataType dataType)
	{
		hashGeneric = new HashMap<K, V>();
		this.generalManager = GeneralManager.get();
		this.dataType = dataType;
	}

	public final Set<K> getKeys()
	{

		return hashGeneric.keySet();
	}

	public final Collection<V> getValues()
	{

		return hashGeneric.values();
	}

	/**
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdMap#size()
	 * @see java.util.Map#size()
	 */
	public final int size()
	{

		return hashGeneric.size();
	}

	/**
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdMap#getReversedMap()
	 */
	public final IGenomeIdMap getReversedMap()
	{

		IGenomeIdMap reversedMap = null;

		switch (dataType)
		{
			case INT2INT:
				reversedMap = new GenomeIdMapInt2Int(dataType);
				break;

			case STRING2STRING:
				reversedMap = new GenomeIdMapString2String(dataType);
				break;

			/* invert type for reverse map! */
			case INT2STRING:
				/* ==> use STRING2INT */
				reversedMap = new GenomeIdMapString2Int(EMappingDataType.STRING2INT);
				break;

			case STRING2INT:
				/* ==> use INT2STRING */
				reversedMap = new GenomeIdMapInt2String(EMappingDataType.INT2STRING);
				break;

			default:
				assert false : "unsupported data type=" + dataType.toString();
		}

		/**
		 * Read HashMap and write it to new HashMap
		 */
		Set<Entry<K, V>> entrySet = hashGeneric.entrySet();
		Iterator<Entry<K, V>> iterOrigin = entrySet.iterator();

		while (iterOrigin.hasNext())
		{
			Entry<K, V> entryBuffer = iterOrigin.next();

			reversedMap
					.put(entryBuffer.getValue().toString(), entryBuffer.getKey().toString());
		}

		return reversedMap;
	}

	@Override
	public final IGenomeIdMap getCodeResolvedMap(IGenomeIdManager genomeIdManager,
			EMappingType genomeMappingLUT_1, EMappingType genomeMappingLUT_2,
			EMappingDataType targetMappingDataType,
			EMappingDataType sourceMappingDataType)
	{

		IGenomeIdMap codeResolvedMap = null;

		switch (targetMappingDataType)
		{
			case INT2INT:
			{
				codeResolvedMap = new GenomeIdMapInt2Int(targetMappingDataType);

				/**
				 * Read HashMap and write it to new HashMap
				 */
				Set<Entry<K, V>> entrySet = hashGeneric.entrySet();
				Iterator<Entry<K, V>> iterOrigin = entrySet.iterator();
				int iResolvedID_1 = 0;
				int iResolvedID_2 = 0;

				Entry<K, V> entryBuffer = null;

				while (iterOrigin.hasNext())
				{
					entryBuffer = iterOrigin.next();

					iResolvedID_1 = genomeIdManager.getIdIntFromStringByMapping(entryBuffer
							.getKey().toString(), genomeMappingLUT_1);

					if (sourceMappingDataType == EMappingDataType.STRING2INT)
					{
						codeResolvedMap.put(Integer.toString(iResolvedID_1), entryBuffer
								.getValue().toString());
					}
					else if (sourceMappingDataType == EMappingDataType.STRING2STRING)
					{
						iResolvedID_2 = genomeIdManager.getIdIntFromStringByMapping(
								entryBuffer.getValue().toString(), genomeMappingLUT_2);

						codeResolvedMap.put(new Integer(iResolvedID_1).toString(),
								new Integer(iResolvedID_2).toString());
					}
				}

				break;
			}
			case INT2STRING:
			{
				codeResolvedMap = new GenomeIdMapInt2String(targetMappingDataType);

				/**
				 * Read HashMap and write it to new HashMap
				 */
				Set<Entry<K, V>> entrySet = hashGeneric.entrySet();
				Iterator<Entry<K, V>> iterOrigin = entrySet.iterator();
				int iResolvedID_1 = 0;

				Entry<K, V> entryBuffer = null;

				while (iterOrigin.hasNext())
				{
					entryBuffer = iterOrigin.next();

					iResolvedID_1 = genomeIdManager.getIdIntFromStringByMapping(entryBuffer
							.getKey().toString(), genomeMappingLUT_1);

					codeResolvedMap.put(new Integer(iResolvedID_1).toString(), entryBuffer
							.getValue().toString());
				}

				break;
			}
			case STRING2INT:
			{
				codeResolvedMap = new GenomeIdMapString2Int(targetMappingDataType);

				/**
				 * Read HashMap and write it to new HashMap
				 */
				Set<Entry<K, V>> entrySet = hashGeneric.entrySet();
				Iterator<Entry<K, V>> iterOrigin = entrySet.iterator();
				int iResolvedID = 0;

				Entry<K, V> entryBuffer = null;

				while (iterOrigin.hasNext())
				{
					entryBuffer = iterOrigin.next();

					iResolvedID = genomeIdManager.getIdIntFromStringByMapping(entryBuffer
							.getValue().toString(), genomeMappingLUT_2);

					codeResolvedMap.put(entryBuffer.getKey().toString(), new Integer(
							iResolvedID).toString());
				}

				break;
			}

			default:
				generalManager.getLogger().log(Level.SEVERE,
						"Unspported data type " + dataType.toString());

		}

		return codeResolvedMap;
	}

	/* ----------------------------------------------- */
	/* ----- Methods to overload in subclasses ----- */

	@Override
	public int getIntByInt(int key)
	{

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return 0;
	}

	@Override
	public int getIntByString(String key)
	{

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return 0;
	}

	@Override
	public String getStringByInt(int key)
	{

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return "";
	}

	@Override
	public String getStringByString(String key)
	{

		assert false : "getIntByInt() is not overloaded and thus can not be used!";
		return "";
	}

	@Override
	public int getIntByIntChecked(int key)
	{

		assert false : "getIntByIntChecked() is not overloaded and thus can not be used!";
		return 0;
	}

	@Override
	public int getIntByStringChecked(String key)
	{

		assert false : "getIntByIntChecked() is not overloaded and thus can not be used!";
		return 0;
	}

	@Override
	public String getStringByIntChecked(int key)
	{

		assert false : "getIntByIntChecked() is not overloaded and thus can not be used!";
		return "";
	}

	@Override
	public String getStringByStringChecked(String key)
	{

		assert false : "getIntByIntChecked() is not overloaded and thus can not be used!";
		return "";
	}

	/* ----- Methods to overload in subclasses ----- */
	/* ----------------------------------------------- */

}
