/**
 * 
 */
package org.geneview.core.manager.data;


import java.util.Collection;
import java.util.HashMap;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.genome.IGenomeIdMap;

//import org.geneview.core.data.mapping.GenomeIdType;
import org.geneview.core.data.mapping.GenomeMappingDataType;
import org.geneview.core.data.mapping.GenomeMappingType;

/**
 * Generic interface for genome ID managers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public interface IGenomeIdManager 
extends IGeneralManager {

//	/**
//	 * Search inside all Map's for the id.
//	 * 
//	 * @param iUniqueId
//	 * @return
//	 */
//	public String getNameById( int iUniqueId );
//	
//	public int getIdByName( String name );
//	
//	public int getIdByTypeInt( final int iUniqueId, 
//			final GenomeIdType type );
//	
//	public int getIdByTypeString( final String sGeneViewId, 
//			final GenomeIdType type );
	
	
	public int getIdIntFromStringByMapping( final String sGeneViewId, 
			final GenomeMappingType type );
	
	/**
	 *  expose all keys for one GenomeIdType from <key,value>.
	 *  Note is was required for GenomeIdType.NCBI_GENEID in specific.
	 *  
	 * @see IGenomeIdManager#getAllValuesByGenomeIdTypeHashMap(GenomeMappingType)
	 * 
	 * @param type specify one GenomeIdType
	 * @return HashMap<Integer,Integer> containing all <GenomeIdType id's, incremented index [0.. max] > 
	 */
	public HashMap<Integer,Integer> getAllKeysByGenomeIdTypeHashMap(GenomeMappingType type); 
	
	/**
	 *  expose all values for one GenomeIdType from <key,value>.
	 *  Note is was required for GenomeIdType.NCBI_GENEID in specific.
	 *  
	 * @see IGenomeIdManager#getAllKeysByGenomeIdTypeHashMap(GenomeMappingType)
	 * @param type specify one GenomeIdType
	 * @return HashMap<Integer,Integer> containing all <GenomeIdType id's, incremented index [0.. max] > 
	 */
	public HashMap<Integer,Integer> getAllValuesByGenomeIdTypeHashMap(GenomeMappingType type); 
	
	/**
	 *  expose all keys for one GenomeIdType.
	 *  Note is was required for GenomeIdType.NCBI_GENEID in specific.
	 *  
	 * @param type specify one GenomeIdType
	 * @return int[] containing all GenomeIdType id's
	 */
	public int[] getAllKeysByGenomeIdType( final GenomeMappingType type ); 
	
	
	/**
	 * Get one "target" id mapped to one "origin" id defiend by iUniqueId 
	 * using type.
	 * 
	 * @param iUniqueId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public int getIdIntFromIntByMapping( final int iUniqueId, 
			final GenomeMappingType type );
	
	/**
	 * Get one "target" id as String mapped to one String as "origin" id 
	 * defiend by sGeneViewId using type.
	 * 
	 * @param sGeneViewId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public String getIdStringFromStringByMapping(final String sGeneViewId, 
			final GenomeMappingType type);
	
	/**
	 * Get one "target" id as String mapped to one "origin" id defiend 
	 * by iUniqueId using type.
	 * 
	 * @param iUniqueId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public String getIdStringFromIntByMapping(final int iUniqueId, 
			final GenomeMappingType type);

	
//	public ArrayList<Integer> getIdListByType( final int iUniqueId, 
//			final GenomeIdType type );
	
//	public String getIdListByTypeToString( final int iUniqueId, 
//			final GenomeIdType type );
//	
//	public void setIdLUTByType( final int iUniqueId, final GenomeIdType type );
	
	/**
	 * Get lock to start adding key, value pairs to a specific HashMap defined by type.
	 * 
	 * @param type addess one HashMap
	 * @return ture if lock was granted and data can be inserted, false indicates, that another map is being inserted.
	 */
	public boolean buildLUT_startEditing( final GenomeMappingType type );
	
	//public void buildLUT( Object first, Object second );
	
	/**
	 * After calling buildLUT_startEditing(GenomeMappingType) this closes the map and frees the lock.
	 * Now another map can be filled with data.
	 * 
	 * @return true if stop editing was successful.
	 */
	public boolean buildLUT_stopEditing( final GenomeMappingType type );
	
	/**
	 * Tests, if data is writen to the IGenimeIdManager.
	 * 
	 * @return true if a LUT is currently created
	 */
	public boolean isBuildLUTinProgress();
	
	/**
	 * Get the IGenomeIdMap that is assigned to one GenomeMappingType type.
	 * 
	 * @param type specify the IGenomeIdMap assinged to this type
	 * @return IGenomeIdMap assigned to the type
	 */
	public IGenomeIdMap getMapByType(GenomeMappingType type);
	
	/**
	 * Test if any map is registered to the type, no matter if it is a MultiMap or a regular Map.
	 * 
	 * @param type type to be testet
	 * @return TRUE if any Map or MultiMap is registered to that type
	 */
	public boolean hasAnyMapByType(GenomeMappingType type);
	
	
	public boolean createMapByType( final GenomeMappingType codingLutType, 
			final GenomeMappingDataType dataType,
			final int iInitialSizeHashMap );
	
	/**
	 * Set a Map and register it to a GenomeMappingType.
	 * 
	 * Note: (Object) map must be an object of type:
	 * IGenomeIdMap, MultiHashArrayStringMap or MultiHashArrayIntegerMap
	 * 
	 * @param codingLutType define GenomeMappingType used for identifying
	 * @param map to be added, must be IGenomeIdMap
	 * 
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap
	 * @see org.geneview.core.data.map.MultiHashArrayStringMap
	 * @see org.geneview.core.data.map.MultiHashArrayIntegerMap
	 */
	public void setMapByType( final GenomeMappingType codingLutType, Object map );
	
	/**
	 * Remove a Map using the GenomeMappingType.
	 * 
	 * Note: (Object) map must be an object of type:
	 * IGenomeIdMap, MultiHashArrayStringMap or MultiHashArrayIntegerMap
	 * 
	 * @param codingLutType define GenomeMappingType used for identifying
	 * 
	 * @see org.geneview.core.manager.data.genome.IGenomeIdMap
	 * @see org.geneview.core.data.map.MultiHashArrayStringMap
	 * @see org.geneview.core.data.map.MultiHashArrayIntegerMap
	 */
	public void removeMapByType(final GenomeMappingType codingLutType);
	
	//MARC: changed parameter from GenomeIdType to GenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the GenomeMappingType as key.
	public Collection<Integer> getIdIntListByType(int iId, GenomeMappingType type);
	
	//MARC: changed parameter from GenomeIdType to GenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the GenomeMappingType as key.
	public Collection<String> getIdStringListByType(String sId, GenomeMappingType type);
	
	//MARC: changed parameter from GenomeIdType to GenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the GenomeMappingType as key.
	public Collection<Integer> getIdIntListFromIdListByType(Collection<Integer> iIdList, GenomeMappingType type);
	
	//MARC: changed parameter from GenomeIdType to GenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the GenomeMappingType as key.
	public Collection<String> getIdStringListFromIdListByType(Collection<String > sIdList, GenomeMappingType type);

}
