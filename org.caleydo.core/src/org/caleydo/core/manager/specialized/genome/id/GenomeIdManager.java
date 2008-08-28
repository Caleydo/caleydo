package org.caleydo.core.manager.specialized.genome.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.caleydo.core.data.map.MultiHashArrayIntegerMap;
import org.caleydo.core.data.map.MultiHashArrayStringMap;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Manages mapping tables for genomic entities.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class GenomeIdManager
	// extends AManager
	implements IGenomeIdManager
{
	private static final int iSortBufferInitialsize = 200;

	private AtomicBoolean bHasMapActiveWriter = new AtomicBoolean(false);

	private EMappingType currentEditingType;

	private IGenomeIdMap currentGenomeIdMap;

	protected HashMap<EMappingType, IGenomeIdMap> hashType2Map;

	protected HashMap<EMappingType, MultiHashArrayIntegerMap> hashType2MultiMapInt;

	protected HashMap<EMappingType, MultiHashArrayStringMap> hashType2MultiMapString;

	public static final int iInitialSizeHashMap = 1000;

	public static final int iInitialCountAllLookupTables = 10;

	public static final int iInitialCountMultiMapLookupTables = 4;

	private IGeneralManager generalManager = GeneralManager.get();

	/**
	 * Constructor.
	 * 
	 */
	public GenomeIdManager()
	{
		hashType2Map = new HashMap<EMappingType, IGenomeIdMap>(iInitialCountAllLookupTables);

		hashType2MultiMapInt = new HashMap<EMappingType, MultiHashArrayIntegerMap>(
				iInitialCountMultiMapLookupTables);

		hashType2MultiMapString = new HashMap<EMappingType, MultiHashArrayStringMap>(
				iInitialCountMultiMapLookupTables);
	}

	public final boolean createMapByType(final EMappingType codingLutType,
			final EMappingDataType dataType)
	{

		return createMapByType(codingLutType, dataType, GenomeIdManager.iInitialSizeHashMap);
	}

	public boolean createMapByType(final EMappingType codingLutType,
			final EMappingDataType dataType, final int iSetInitialSizeHashMap)
	{

		/* consistency check */
		int iCurrentInitialSizeHashMap = iSetInitialSizeHashMap;

		if (hashType2Map.containsKey(codingLutType))
		{
			generalManager.getLogger().log(Level.SEVERE,
					"Mapping type=" + codingLutType + " is alreay registered!");

			return false;
		}

		if (iSetInitialSizeHashMap < 2)
		{
			iCurrentInitialSizeHashMap = iInitialSizeHashMap;
		}

		IGenomeIdMap newMap = null;

		generalManager.getLogger().log(Level.INFO,
				"Create lookup table for type=" + codingLutType);

		switch (dataType)
		{
			case INT2INT:
				newMap = new GenomeIdMapInt2Int(dataType);
				break;
			case INT2STRING:
				newMap = new GenomeIdMapInt2String(dataType);
				break;
			case STRING2INT:
				newMap = new GenomeIdMapString2Int(dataType);
				break;
			case STRING2STRING:
				newMap = new GenomeIdMapString2String(dataType);
				break;

			/* Multi Map's */
			case MULTI_STRING2STRING:
				MultiHashArrayStringMap newMultiMapString = new MultiHashArrayStringMap(
						iCurrentInitialSizeHashMap);
				hashType2MultiMapString.put(codingLutType, newMultiMapString);
				return true;

			case MULTI_INT2INT:
				MultiHashArrayIntegerMap newMultiMapInt = new MultiHashArrayIntegerMap(
						iCurrentInitialSizeHashMap);
				hashType2MultiMapInt.put(codingLutType, newMultiMapInt);
				return true;

			default:
				assert false : "createMap() type=" + dataType + " is not supported";
				return false;

		} // switch ( dataType )

		hashType2Map.put(codingLutType, newMap);

		return true;
	}

	public final IGenomeIdMap getMapByType(final EMappingType type)
	{

		return hashType2Map.get(type);
	}

	public final MultiHashArrayIntegerMap getMultiMapIntegerByType(final EMappingType type)
	{
		return hashType2MultiMapInt.get(type);
	}

	public final MultiHashArrayStringMap getMultiMapStringByType(final EMappingType type)
	{
		return hashType2MultiMapString.get(type);
	}

	public final boolean hasAnyMapByType(final EMappingType codingLutType)
	{

		if (hasMapByType(codingLutType))
		{
			return true;
		}

		return hasMultiMapByType(codingLutType);
	}

	public final boolean hasMapByType(final EMappingType codingLutType)
	{

		return hashType2Map.containsKey(codingLutType);
	}

	public final boolean hasMultiMapByType(final EMappingType codingLutType)
	{

		if (hashType2MultiMapInt.containsKey(codingLutType))
		{
			return true;
		}

		if (hashType2MultiMapString.containsKey(codingLutType))
		{
			return true;
		}

		return false;
	}

	@Override
	public boolean buildLUT_startEditing(final EMappingType type)
	{

		if (!bHasMapActiveWriter.compareAndSet(false, true))
		{
			return false;
		}

		currentEditingType = type;

		if (type.isMultiMap())
		{
			// TODO: register multi hash map to!
			// assert false : "TODO: register multi hash map to!";

			return true;
		}
		else
		{ // if ( type.isMultiMap() ) ... else
			currentGenomeIdMap = hashType2Map.get(type);

			if (currentGenomeIdMap == null)
			{
				throw new CaleydoRuntimeException("buildLUT_startEditingSetTypes(" + type
						+ ") is not allocated!", CaleydoRuntimeExceptionType.DATAHANDLING);
			}

			return true;
		} // if ( type.isMultiMap() )

	}

	@Override
	public boolean buildLUT_stopEditing(final EMappingType type)
	{

		if (!bHasMapActiveWriter.compareAndSet(true, false))
		{
			return false;
		}

		/* consistency check */

		if (type.isMultiMap())
		{
			// FIXME
			// currentEditingType = EMappingType.NON_MAPPING;
			currentEditingType = null;

			return true;
		} // if ( type.isMultiMap() ) ... else

		if (!currentEditingType.equals(type))
		{
			throw new CaleydoRuntimeException("buildLUT_stopEditing(" + type
					+ ") differs from current type=[" + currentEditingType + "]");
		}

		// FIXME
		// currentEditingType = EMappingType.NON_MAPPING;
		currentEditingType = null;

		return true;
	}

	public int getIdIntFromStringByMapping(final String sCaleydoId, final EMappingType type)
	{
		IGenomeIdMap buffer = hashType2Map.get(type);

		assert buffer != null : "getIdFromStringByMapping(" + type + ") type is not allocated";

		return buffer.getIntByStringChecked(sCaleydoId);
	}

	public int getIdIntFromIntByMapping(final int iUniqueId, final EMappingType type)
	{

		IGenomeIdMap buffer = hashType2Map.get(type);

		assert buffer != null : "getIdFromIntByMapping(" + type + ") type is not allocated";

		return buffer.getIntByIntChecked(iUniqueId);
	}

	public String getIdStringFromStringByMapping(final String sCaleydoId,
			final EMappingType type)
	{

		IGenomeIdMap buffer = hashType2Map.get(type);

		assert buffer != null : "getStringIdFromStringByMapping(" + type
				+ ") type is not allocated";

		return buffer.getStringByStringChecked(sCaleydoId);
	}

	public String getIdStringFromIntByMapping(final int iUniqueId, final EMappingType type)
	{

		IGenomeIdMap buffer = hashType2Map.get(type);

		assert buffer != null : "getStringIdFromIntByMapping(" + type
				+ ") type is not allocated";

		return buffer.getStringByIntChecked(iUniqueId);
	}

	// MARC: changed parameter from EIDType to EMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the
	// EMappingType as key.
	public ArrayList<Integer> getIdIntListByType(int iId, EMappingType genomeMappingType)
	{

		return hashType2MultiMapInt.get(genomeMappingType).get(iId);
	}

	// MARC: changed parameter from EIDType to EMappingType.
	// Because in the hashType2MultiMapString the maps are stored with the
	// EMappingType as key.
	public ArrayList<String> getIdStringListByType(String sId, EMappingType genomeMappingType)
	{

		return hashType2MultiMapString.get(genomeMappingType).get(sId);
	}

	public final boolean isBuildLUTinProgress()
	{

		return bHasMapActiveWriter.get();
	}

	/**
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdManager#setMapByType(org.caleydo.core.data.mapping.EMappingType,
	 *      java.lang.Object)
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdMap
	 * @see org.caleydo.core.data.map.MultiHashArrayStringMap
	 * @see org.caleydo.core.data.map.MultiHashArrayIntegerMap
	 */
	public void setMapByType(final EMappingType codingLutType, Object map)
	{

		if (map instanceof MultiHashArrayIntegerMap)
		{
			hashType2MultiMapInt.put(codingLutType, (MultiHashArrayIntegerMap) map);
			return;
		}

		if (map instanceof MultiHashArrayStringMap)
		{
			hashType2MultiMapString.put(codingLutType, (MultiHashArrayStringMap) map);
			return;
		}

		try
		{
			hashType2Map.put(codingLutType, (IGenomeIdMap) map);
		}
		catch (NullPointerException npe)
		{
			throw new CaleydoRuntimeException(
					"setMapByType(final EMappingType codingLutType, Object map) unsupported object="
							+ map.getClass().toString(),
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
	}

	@Override
	public void removeMapByType(final EMappingType codingLutType)
	{

		if (hashType2MultiMapInt.containsKey(codingLutType))
		{
			hashType2MultiMapInt.remove(codingLutType);
			return;
		}
		else if (hashType2MultiMapString.containsKey(codingLutType))
		{
			hashType2MultiMapString.remove(codingLutType);
			return;
		}
		else if (hashType2Map.containsKey(codingLutType))
		{
			hashType2Map.remove(codingLutType);
			return;
		}
	}

	public Collection<Integer> getIdIntListFromIdListByType(Collection<Integer> iIdList,
			EMappingType type)
	{

		assert iIdList != null : "can not handle null pointer";

		HashMap<Integer, Integer> sortBuffer = new HashMap<Integer, Integer>(
				iSortBufferInitialsize);

		Integer constantValue = new Integer(-1);

		Iterator<Integer> iterInputList = iIdList.iterator();
		while (iterInputList.hasNext())
		{
			ArrayList<Integer> buffer = getIdIntListByType(iterInputList.next().intValue(),
					type);

			if (buffer != null)
			{
				Iterator<Integer> iterInnerArrayList = buffer.iterator();
				while (iterInnerArrayList.hasNext())
				{
					sortBuffer.put(iterInnerArrayList.next(), constantValue);
				} // while ( iterInnerArrayList.hasNext() )

			} // if (buffer!= null))

		} // while ( iterInputList.hasNext() )

		return sortBuffer.keySet();
	}

	public Collection<String> getIdStringListFromIdListByType(Collection<String> sIdList,
			EMappingType type)
	{

		assert sIdList != null : "can not handle null pointer";

		HashMap<String, String> sortBuffer = new HashMap<String, String>(
				iSortBufferInitialsize);

		String constantValue = "-1";

		Iterator<String> iterInputList = sIdList.iterator();
		while (iterInputList.hasNext())
		{
			ArrayList<String> buffer = getIdStringListByType(iterInputList.next(), type);

			if (buffer != null)
			{
				Iterator<String> iterInnerArrayList = buffer.iterator();
				while (iterInnerArrayList.hasNext())
				{
					sortBuffer.put(iterInnerArrayList.next(), constantValue);
				} // while ( iterInnerArrayList.hasNext() )

			} // if (buffer!= null))

		} // while ( iterInputList.hasNext() )

		return sortBuffer.keySet();
	}

	private Set<Integer> getKeysFromExposedDataStructues(final EMappingType type)
	{

		IGenomeIdMap bufferMap = this.hashType2Map.get(type);

		if (bufferMap == null)
		{
			return null;
		}
		Set<Integer> keyset = bufferMap.getKeysInteger();

		// assert keyset == null : "EMappingType=[" + type +
		// "] was not mapped to keys of type Integer";

		return keyset;
	}

	private Collection<Integer> getValuesFromExposedDataStructues(final EMappingType type)
	{

		IGenomeIdMap bufferMap = this.hashType2Map.get(type);

		if (bufferMap == null)
		{
			return null;
		}

		try
		{
			Collection<Integer> keyset = bufferMap.getValuesInteger();

			assert keyset == null : "EMappingType=[" + type
					+ "] was not mapped to keys of type Integer";

			return keyset;
		}
		catch (CaleydoRuntimeException cre)
		{

			generalManager.getLogger().log(Level.SEVERE, "Type convertsion problem!", cre);

			/* return empty collection .. */
			return new ArrayList<Integer>();
		}
	}

	/**
	 * Creates a HashMap from the the MultiMap requested using (EMappingType)
	 * type; < EMappingType - Id , index from [0.. multiMap.keySet().size()-1) >
	 * 
	 * @param type
	 * @return HashMap with < EMappingType - Id , index from [0..
	 *         multiMap.keySet().size()-1) >
	 * @see EMappingType.NCBI_GENEID_2_GENE_SHORT_NAME
	 * @see EMappingType."NCBI_GENEID_2_NCBI_GENEID_CODE REVERSE
	 */
	public HashMap<Integer, Integer> getAllKeysByGenomeIdTypeHashMap(final EMappingType type)
	{

		try
		{
			Set<Integer> keyset = getKeysFromExposedDataStructues(type);

			if (keyset == null)
			{
				return null;
			}

			/* create result data structure.. */
			HashMap<Integer, Integer> resultHashMap = new HashMap<Integer, Integer>(keyset
					.size());

			Iterator<Integer> iter = keyset.iterator();

			for (int i = 0; iter.hasNext(); i++)
			{
				resultHashMap.put(iter.next(), i);
			}

			return resultHashMap;

		}
		catch (OutOfMemoryError e)
		{
			System.err.println("Out of memroy while allocation of memory! "
					+ e.getStackTrace());
			throw e;
		}
	}

	/**
	 * @see EMappingType.NCBI_GENEID_2_GENE_SHORT_NAME
	 * @see EMappingType."NCBI_GENEID_2_NCBI_GENEID_CODE REVERSE
	 */
	public int[] getAllKeysByGenomeIdType(EMappingType type)
	{

		try
		{
			Set<Integer> keyset = getKeysFromExposedDataStructues(type);

			if (keyset == null)
			{
				return null;
			}

			/* create result data structure.. */
			int[] resultArray = new int[keyset.size()];

			Iterator<Integer> iter = keyset.iterator();

			for (int i = 0; iter.hasNext(); i++)
			{
				resultArray[i] = iter.next().intValue();
			}
			return resultArray;

		}
		catch (OutOfMemoryError e)
		{
			System.err.println("Out of memroy while allocation of memory! "
					+ e.getStackTrace());
			throw e;
		}
	}

	@Override
	public HashMap<Integer, Integer> getAllValuesByGenomeIdTypeHashMap(EMappingType type)
	{

		try
		{
			Collection<Integer> keyset = getValuesFromExposedDataStructues(type);

			if (keyset == null)
			{
				return null;
			}

			/* create result data structure.. */
			HashMap<Integer, Integer> resultHashMap = new HashMap<Integer, Integer>(keyset
					.size());

			Iterator<Integer> iter = keyset.iterator();

			for (int i = 0; iter.hasNext(); i++)
			{
				resultHashMap.put(iter.next(), i);
			}

			return resultHashMap;

		}
		catch (OutOfMemoryError e)
		{

			generalManager.getLogger().log(Level.SEVERE,
					"Could not allocate memory for lookup table.", e);
			throw e;
		}
	}
}
