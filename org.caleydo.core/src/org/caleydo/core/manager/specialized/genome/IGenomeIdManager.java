package org.caleydo.core.manager.specialized.genome;

import java.util.Collection;
import java.util.HashMap;
import org.caleydo.core.data.map.MultiHashArrayIntegerMap;
import org.caleydo.core.data.map.MultiHashArrayStringMap;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;

/**
 * Generic interface for genome ID managers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IGenomeIdManager
{

	public int getIdIntFromStringByMapping(final String sCaleydoId,
			final EMappingType type);

	/**
	 * expose all keys for one EIDType from <key,value>. Note is was
	 * required for EIDType.NCBI_GENEID in specific.
	 * 
	 * @see IGenomeIdManager#getAllValuesByGenomeIdTypeHashMap(EMappingType)
	 * @param type specify one EIDType
	 * @return HashMap<Integer,Integer> containing all <EIDType id's,
	 *         incremented index [0.. max] >
	 */
	public HashMap<Integer, Integer> getAllKeysByGenomeIdTypeHashMap(EMappingType type);

	/**
	 * expose all values for one EIDType from <key,value>. Note is was
	 * required for EIDType.NCBI_GENEID in specific.
	 * 
	 * @see IGenomeIdManager#getAllKeysByGenomeIdTypeHashMap(EMappingType)
	 * @param type specify one EIDType
	 * @return HashMap<Integer,Integer> containing all <EIDType id's,
	 *         incremented index [0.. max] >
	 */
	public HashMap<Integer, Integer> getAllValuesByGenomeIdTypeHashMap(EMappingType type);

	/**
	 * expose all keys for one EIDType. Note is was required for
	 * EIDType.NCBI_GENEID in specific.
	 * 
	 * @param type specify one EIDType
	 * @return int[] containing all EIDType id's
	 */
	public int[] getAllKeysByGenomeIdType(final EMappingType type);

	/**
	 * Get one "target" id mapped to one "origin" id defiend by iUniqueID using
	 * type.
	 * 
	 * @param iUniqueID define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public int getIdIntFromIntByMapping(final int iUniqueId, final EMappingType type);

	/**
	 * Get one "target" id as String mapped to one String as "origin" id defiend
	 * by sCaleydoId using type.
	 * 
	 * @param sCaleydoId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public String getIdStringFromStringByMapping(final String sCaleydoId,
			final EMappingType type);

	/**
	 * Get one "target" id as String mapped to one "origin" id defiend by
	 * iUniqueID using type.
	 * 
	 * @param iUniqueID define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public String getIdStringFromIntByMapping(final int iUniqueId,
			final EMappingType type);

	/**
	 * Get lock to start adding key, value pairs to a specific HashMap defined
	 * by type.
	 * 
	 * @param type addess one HashMap
	 * @return ture if lock was granted and data can be inserted, false
	 *         indicates, that another map is being inserted.
	 */
	public boolean buildLUT_startEditing(final EMappingType type);

	/**
	 * After calling buildLUT_startEditing(EMappingType) this closes the
	 * map and frees the lock. Now another map can be filled with data.
	 * 
	 * @return true if stop editing was successful.
	 */
	public boolean buildLUT_stopEditing(final EMappingType type);

	/**
	 * Tests, if data is writen to the IGenimeIdManager.
	 * 
	 * @return true if a LUT is currently created
	 */
	public boolean isBuildLUTinProgress();

	/**
	 * Get the IGenomeIdMap that is assigned to one EMappingType type.
	 * 
	 * @param type specify the IGenomeIdMap assinged to this type
	 * @return IGenomeIdMap assigned to the type
	 */
	public IGenomeIdMap getMapByType(EMappingType type);

	/**
	 * Test if any map is registered to the type, no matter if it is a MultiMap
	 * or a regular Map.
	 * 
	 * @param type type to be tested
	 * @return TRUE if any Map or MultiMap is registered to that type
	 */
	public boolean hasAnyMapByType(EMappingType type);

	public boolean createMapByType(final EMappingType codingLutType,
			final EMappingDataType dataType);

	/**
	 * Set a Map and register it to a EMappingType. Note: (Object) map
	 * must be an object of type: IGenomeIdMap, MultiHashArrayStringMap or
	 * MultiHashArrayIntegerMap
	 * 
	 * @param codingLutType define EMappingType used for identifying
	 * @param map to be added, must be IGenomeIdMap
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdMap
	 * @see org.caleydo.core.data.map.MultiHashArrayStringMap
	 * @see org.caleydo.core.data.map.MultiHashArrayIntegerMap
	 */
	public void setMapByType(final EMappingType codingLutType, Object map);

	/**
	 * Remove a Map using the EMappingType. Note: (Object) map must be an
	 * object of type: IGenomeIdMap, MultiHashArrayStringMap or
	 * MultiHashArrayIntegerMap
	 * 
	 * @param codingLutType define EMappingType used for identifying
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdMap
	 * @see org.caleydo.core.data.map.MultiHashArrayStringMap
	 * @see org.caleydo.core.data.map.MultiHashArrayIntegerMap
	 */
	public void removeMapByType(final EMappingType codingLutType);

	public Collection<Integer> getIdIntListByType(int iId, EMappingType type);

	public Collection<String> getIdStringListByType(String sId, EMappingType type);

	public Collection<Integer> getIdIntListFromIdListByType(Collection<Integer> iIdList,
			EMappingType type);

	public Collection<String> getIdStringListFromIdListByType(Collection<String> sIdList,
			EMappingType type);
	
	public MultiHashArrayIntegerMap getMultiMapIntegerByType(final EMappingType type);
	
	public MultiHashArrayStringMap getMultiMapStringByType(final EMappingType type);
}
