/**
 * 
 */
package cerberus.manager.data;

import java.util.ArrayList;
import java.util.Map;

import cerberus.manager.IGeneralManager;
import cerberus.manager.data.genome.IGenomeIdMap;

import cerberus.data.mapping.GenomeIdType;
import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.data.mapping.GenomeMappingType;

/**
 * @author Michael Kalkusch
 *
 */
public interface IGenomeIdManager extends IGeneralManager {

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
//	public int getIdByTypeInt( final int iCerberusId, 
//			final GenomeIdType type );
//	
//	public int getIdByTypeString( final String sCerberusId, 
//			final GenomeIdType type );
	
	
	public int getIdFromStringByMapping( final String sCerberusId, 
			final GenomeMappingType type );
	
	/**
	 * Get one "target" id mapped to one "origin" id defiend by iCerberusId 
	 * using type.
	 * 
	 * @param iCerberusId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public int getIdFromIntByMapping( final int iCerberusId, 
			final GenomeMappingType type );
	
	/**
	 * Get one "target" id as String mapped to one String as "origin" id 
	 * defiend by sCerberusId using type.
	 * 
	 * @param sCerberusId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public String getStringIdFromStringByMapping(final String sCerberusId, 
			final GenomeMappingType type);
	
	/**
	 * Get one "target" id as String mapped to one "origin" id defiend 
	 * by iCerberusId using type.
	 * 
	 * @param iCerberusId define "origin" id
	 * @param type defines, which id is mapped to the other id
	 * @return "target" id using type
	 */
	public String getStringIdFromIntByMapping(final int iCerberusId, 
			final GenomeMappingType type);

	
//	public ArrayList<Integer> getIdListByType( final int iCerberusId, 
//			final GenomeIdType type );
	
//	public String getIdListByTypeToString( final int iCerberusId, 
//			final GenomeIdType type );
//	
//	public void setIdLUTByType( final int iCerberusId, final GenomeIdType type );
	
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
	 * Now anotehr map can be filled with data.
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
	public IGenomeIdMap getMapByType(GenomeMappingType type) ;
	
	public boolean createMapByType( final GenomeMappingType codingLutType, 
			final GenomeMappingDataType dataType,
			final int iInitialSizeHashMap );
}
