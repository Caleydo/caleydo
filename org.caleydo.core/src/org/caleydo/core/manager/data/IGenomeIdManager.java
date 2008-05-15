package org.caleydo.core.manager.data;


import java.util.Collection;
import java.util.HashMap;

import org.caleydo.core.data.mapping.EGenomeMappingDataType;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.data.genome.IGenomeIdMap;

/**
 * Generic interface for genome ID managers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public interface IGenomeIdManager 
extends IManager
{	
	public int getIdIntFromStringByMapping( final String sCaleydoId, 
			final EGenomeMappingType type );
	
	/**
	 *  expose all keys for one EGenomeIdType from <key,value>.
	 *  Note is was required for EGenomeIdType.NCBI_GENEID in specific.
	 *  
	 * @see IGenomeIdManager#getAllValuesByGenomeIdTypeHashMap(EGenomeMappingType)
	 * 
	 * @param type specify one EGenomeIdType
	 * @return HashMap<Integer,Integer> containing all <EGenomeIdType id's, incremented index [0.. max] > 
	 */
	public HashMap<Integer,Integer> getAllKeysByGenomeIdTypeHashMap(EGenomeMappingType type); 
	
	/**
	 *  expose all values for one EGenomeIdType from <key,value>.
	 *  Note is was required for EGenomeIdType.NCBI_GENEID in specific.
	 *  
	 * @see IGenomeIdManager#getAllKeysByGenomeIdTypeHashMap(EGenomeMappingType)
	 * @param type specify one EGenomeIdType
	 * @return HashMap<Integer,Integer> containing all <EGenomeIdType id's, incremented index [0.. max] > 
	 */
	public HashMap<Integer,Integer> getAllValuesByGenomeIdTypeHashMap(EGenomeMappingType type); 
	
	/**
	 *  expose all keys for one EGenomeIdType.
	 *  Note is was required for EGenomeIdType.NCBI_GENEID in specific.
	 *  
	 * @param type specify one EGenomeIdType
	 * @return int[] containing all EGenomeIdType id's
	 */
	public int[] getAllKeysByGenomeIdType( final EGenomeMappingType type ); 
	
	
	/**
	 * Get one "target" id mapped to one "origin" id defiend by iUniqueId 
	 * using type.
	 * 
	 * @param iUniqueId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public int getIdIntFromIntByMapping( final int iUniqueId, 
			final EGenomeMappingType type );
	
	/**
	 * Get one "target" id as String mapped to one String as "origin" id 
	 * defiend by sCaleydoId using type.
	 * 
	 * @param sCaleydoId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public String getIdStringFromStringByMapping(final String sCaleydoId, 
			final EGenomeMappingType type);
	
	/**
	 * Get one "target" id as String mapped to one "origin" id defiend 
	 * by iUniqueId using type.
	 * 
	 * @param iUniqueId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public String getIdStringFromIntByMapping(final int iUniqueId, 
			final EGenomeMappingType type);

	
//	public ArrayList<Integer> getIdListByType( final int iUniqueId, 
//			final EGenomeIdType type );
	
//	public String getIdListByTypeToString( final int iUniqueId, 
//			final EGenomeIdType type );
//	
//	public void setIdLUTByType( final int iUniqueId, final EGenomeIdType type );
	
	/**
	 * Get lock to start adding key, value pairs to a specific HashMap defined by type.
	 * 
	 * @param type addess one HashMap
	 * @return ture if lock was granted and data can be inserted, false indicates, that another map is being inserted.
	 */
	public boolean buildLUT_startEditing( final EGenomeMappingType type );
	
	//public void buildLUT( Object first, Object second );
	
	/**
	 * After calling buildLUT_startEditing(EGenomeMappingType) this closes the map and frees the lock.
	 * Now another map can be filled with data.
	 * 
	 * @return true if stop editing was successful.
	 */
	public boolean buildLUT_stopEditing( final EGenomeMappingType type );
	
	/**
	 * Tests, if data is writen to the IGenimeIdManager.
	 * 
	 * @return true if a LUT is currently created
	 */
	public boolean isBuildLUTinProgress();
	
	/**
	 * Get the IGenomeIdMap that is assigned to one EGenomeMappingType type.
	 * 
	 * @param type specify the IGenomeIdMap assinged to this type
	 * @return IGenomeIdMap assigned to the type
	 */
	public IGenomeIdMap getMapByType(EGenomeMappingType type);
	
	/**
	 * Test if any map is registered to the type, no matter if it is a MultiMap or a regular Map.
	 * 
	 * @param type type to be testet
	 * @return TRUE if any Map or MultiMap is registered to that type
	 */
	public boolean hasAnyMapByType(EGenomeMappingType type);
	
	
	public boolean createMapByType( final EGenomeMappingType codingLutType, 
			final EGenomeMappingDataType dataType,
			final int iInitialSizeHashMap );
	
	/**
	 * Set a Map and register it to a EGenomeMappingType.
	 * 
	 * Note: (Object) map must be an object of type:
	 * IGenomeIdMap, MultiHashArrayStringMap or MultiHashArrayIntegerMap
	 * 
	 * @param codingLutType define EGenomeMappingType used for identifying
	 * @param map to be added, must be IGenomeIdMap
	 * 
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap
	 * @see org.caleydo.core.data.map.MultiHashArrayStringMap
	 * @see org.caleydo.core.data.map.MultiHashArrayIntegerMap
	 */
	public void setMapByType( final EGenomeMappingType codingLutType, Object map );
	
	/**
	 * Remove a Map using the EGenomeMappingType.
	 * 
	 * Note: (Object) map must be an object of type:
	 * IGenomeIdMap, MultiHashArrayStringMap or MultiHashArrayIntegerMap
	 * 
	 * @param codingLutType define EGenomeMappingType used for identifying
	 * 
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap
	 * @see org.caleydo.core.data.map.MultiHashArrayStringMap
	 * @see org.caleydo.core.data.map.MultiHashArrayIntegerMap
	 */
	public void removeMapByType(final EGenomeMappingType codingLutType);
	
	//MARC: changed parameter from EGenomeIdType to EGenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the EGenomeMappingType as key.
	public Collection<Integer> getIdIntListByType(int iId, EGenomeMappingType type);
	
	//MARC: changed parameter from EGenomeIdType to EGenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the EGenomeMappingType as key.
	public Collection<String> getIdStringListByType(String sId, EGenomeMappingType type);
	
	//MARC: changed parameter from EGenomeIdType to EGenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the EGenomeMappingType as key.
	public Collection<Integer> getIdIntListFromIdListByType(Collection<Integer> iIdList, EGenomeMappingType type);
	
	//MARC: changed parameter from EGenomeIdType to EGenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the EGenomeMappingType as key.
	public Collection<String> getIdStringListFromIdListByType(Collection<String > sIdList, EGenomeMappingType type);
}
