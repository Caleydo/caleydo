/**
 * 
 */
package cerberus.manager.data.genome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeIdType;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.type.ManagerObjectType;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class AGenomeIdManager 
extends AAbstractManager
implements IGenomeIdManager {

	protected boolean bLUTcreationIsNotInPorgress = false;
	
	protected HashMap hashMap_ACCESSION_NUMBER_2_GENE_ID;
	
	protected HashMap hashMap_ACCESSION_NUMBER_2_GENE_ID_reverse;
	
	protected HashMap hashMap_ENZYME_CODE_2_ENZYME_ID;
	
	protected HashMap hashMap_ENZYME_CODE_2_ENZYME_ID_reverse;
	
	protected MultiHashArrayMap multiMap_ACCESSION_NUMBER_2_ENZYME_ID;
	
	protected MultiHashArrayMap multiMap_ACCESSION_NUMBER_2_ENZYME_ID_reverse;
	
//	protected HashMap <String,Integer> hash_ENZYME_CODE_2_ENZYME_ID;
//	
//	protected HashMap <Integer,String> hash_ENZYME_CODE_2_ENZYME_ID_reverse;
//	
//	protected HashMap <String,Integer> hash_PATHWAY_2_ACCESSION_NUMBER;
//	
//	protected HashMap <String,Integer> hash_MICROARRAY_2_ACCESSION_NUMBER;
	
	/**
	 * Constructor.
	 */
	protected AGenomeIdManager( final IGeneralManager setGeneralManager) {

		super( setGeneralManager, 66);
		
		initAll();
	}

	/**
	 * Initialize all data structures!
	 * Needs a lot of memory and a large heap size!
	 *
	 */
	protected void initAll() {
	
		// Check if init is called twice.
		if (hashMap_ACCESSION_NUMBER_2_GENE_ID != null) 
		{
			getGeneralManager().getSingelton().getLoggerManager().
				logMsg("AGenomeIdManager.initAll() called twice!",
					LoggerType.STATUS );
			return;
		}
		
		hashMap_ACCESSION_NUMBER_2_GENE_ID = new HashMap();
		hashMap_ACCESSION_NUMBER_2_GENE_ID_reverse = new HashMap();
		
		hashMap_ENZYME_CODE_2_ENZYME_ID = new HashMap();
		hashMap_ENZYME_CODE_2_ENZYME_ID_reverse = new HashMap();
		
		multiMap_ACCESSION_NUMBER_2_ENZYME_ID = new MultiHashArrayMap();
		multiMap_ACCESSION_NUMBER_2_ENZYME_ID_reverse = new MultiHashArrayMap();
		
//		multiMapNCBI_GENE_ID_2_KEGGID = new MultiHashArrayMap();		
//		multiMapKEGG_GENE_ID_2_KEGGID_reverse = new MultiHashArrayMap();
//		multiMapKEGG_2_ENZYME_ID = new MultiHashArrayMap();
//		multiMapKEGG_2_ENZYME_ID_reverse = new MultiHashArrayMap();
//		
//		hashENZYME_CODE_2_ENZYME_ID = new HashMap <String,Integer> ();
//		hashENZYME_CODE_2_ENZYME_ID_reverse = new HashMap <Integer,String> ();
//		hashPATHWAY_2_NCBI_GENE_ID = new HashMap <String,Integer> ();
//		hashMICROARRAY_2_NCBI_GENE_ID = new HashMap <String,Integer> ();
	}
	
	/**
	 * Method returns the requested hash map.
	 * 
	 * @param type
	 * @param bIsReverse TRUE if reverse hash map is needed
	 * @return Requested hash map
	 */
	public Map getMapByGenomeType(final GenomeMappingType genomeMappingType, 
			final boolean bIsReverse) {
		
		switch (genomeMappingType) {
		
		case ACCESSION_NUMBER_2_GENE_ID:
		{
			if (!bIsReverse)
			{
				return hashMap_ACCESSION_NUMBER_2_GENE_ID;
			}
			else 
			{
				return hashMap_ACCESSION_NUMBER_2_GENE_ID_reverse;
			}
		}
		case ENZYME_CODE_2_ENZYME_ID:
		{
			if (!bIsReverse)
			{
				return hashMap_ENZYME_CODE_2_ENZYME_ID;
			}
			else 
			{
				return hashMap_ENZYME_CODE_2_ENZYME_ID_reverse;
			}
		}
		case ACCESSION_NUMBER_2_ENZYME_ID:
		{
			if (!bIsReverse)
			{
				return multiMap_ACCESSION_NUMBER_2_ENZYME_ID;
			}
			else 
			{
				return multiMap_ACCESSION_NUMBER_2_ENZYME_ID_reverse;
			}
		}
		default:
			assert false : "unknown genome mapping type!";
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#getNameById(int)
	 */
	public String getNameById(int iUniqueId) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#getIdByName(java.lang.String)
	 */
	public int getIdByName(String name) {

		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#getIdByType(int, cerberus.data.mapping.GenomeIdType)
	 */
	public int getIdByType(int iCerberusId, GenomeIdType type) {

		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#getIdListByType(int, cerberus.data.mapping.GenomeIdType)
	 */
	public ArrayList<Integer> getIdListByType(int iCerberusId, GenomeIdType type) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#getIdListByTypeToString(int, cerberus.data.mapping.GenomeIdType)
	 */
	public String getIdListByTypeToString(int iCerberusId, GenomeIdType type) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#setIdLUTByType(int, cerberus.data.mapping.GenomeIdType)
	 */
	public void setIdLUTByType(int iCerberusId, GenomeIdType type) {

		// TODO Auto-generated method stub

	}

//	/* (non-Javadoc)
//	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT_startEditingSetTypes(cerberus.data.mapping.GenomeIdType, cerberus.data.mapping.GenomeIdType)
//	 */
//	public void buildLUT_startEditingSetTypes(GenomeIdType typeFromId,
//			GenomeIdType typeToId) {
//		bLUTcreationIsNotInPorgress = false;
//
//	}
//
//	/* (non-Javadoc)
//	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT(int, int)
//	 */
//	public void buildLUT(int iFirst, int iSecond) {
//
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT(java.lang.String, java.lang.String)
//	 */
//	public void buildLUT(String iFirst, String iSecond) {
//
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT_stopEditing()
//	 */
//	public boolean buildLUT_stopEditing() {
//		bLUTcreationIsNotInPorgress = true;
//		
//		return false;
//	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#isBuildLUTfinished()
	 */
	public boolean isBuildLUTfinished() {
		return bLUTcreationIsNotInPorgress;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#size()
	 */
	public int size() {

		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#getManagerType()
	 */
	public ManagerObjectType getManagerType() {

		return null;
	}


	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#registerItem(java.lang.Object, int, cerberus.manager.type.ManagerObjectType)
	 */
	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#unregisterItem(int, cerberus.manager.type.ManagerObjectType)
	 */
	public boolean unregisterItem(int iItemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#createNewId(cerberus.manager.type.ManagerObjectType)
	 */
	protected int createNewId(GenomeIdType type) {

		return 0;
	}



}
