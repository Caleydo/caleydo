/**
 * 
 */
package cerberus.manager.data.genome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.base.map.MultiHashArrayStringMap;
import cerberus.data.mapping.GenomeIdType;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.ISingelton;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGenomeIdManager 
extends AAbstractManager
implements IGenomeIdManager {

	protected boolean bLUTcreationIsNotInPorgress = false;
	
	protected MultiHashArrayMap multiMapNCBI_GENEID_2_KEGGID;
	
	protected MultiHashArrayMap multiMapNCBI_GENEID_2_KEGGID_reverse;
	
	protected MultiHashArrayMap multiMapKEGG_2_ENZYMEID;
	
	protected MultiHashArrayMap multiMapKEGG_2_ENZYMEID_reverse;
	
	protected MultiHashArrayStringMap multiMapMICROARRAY_2_ACCESSION;
	
	protected HashMap <String,Integer> hashENZYME_CODE_2_ENZYMEID;
	
	protected HashMap <Integer,String> hashENZYME_CODE_2_ENZYMEID_reverse;
	
	protected HashMap <String,Integer> hashPATHWAY_2_NCBI_GENEID;
	
	protected HashMap <String,Integer> hashMICROARRAY_2_NCBI_GENEID;
	
	protected HashMap <Integer,Integer> hashNCBI_GENEID_2_KEGGID;
	
	protected HashMap <String,String> hashMICROARRAY_2_ACCESSION;
	
	/**
	 * 
	 */
	protected AGenomeIdManager( final IGeneralManager setGeneralManager) {

		super( setGeneralManager, 66, ManagerType.GENOME_ID );
		
		initAll();
	}

	/**
	 * Initialize all data structures!
	 * Needs a lot of memory and a large heap size!
	 *
	 */
	protected void initAll() {
	
		if ( multiMapNCBI_GENEID_2_KEGGID != null) 
		{
			getGeneralManager().getSingelton().getLoggerManager().logMsg("AGenomeIdManager.initAll() called twice!",
					LoggerType.STATUS );
			return;
		}
		
		multiMapMICROARRAY_2_ACCESSION = new MultiHashArrayStringMap();
		
		multiMapNCBI_GENEID_2_KEGGID = new MultiHashArrayMap();
		
		multiMapNCBI_GENEID_2_KEGGID_reverse = new MultiHashArrayMap();
		
		multiMapKEGG_2_ENZYMEID = new MultiHashArrayMap();
		
		multiMapKEGG_2_ENZYMEID_reverse = new MultiHashArrayMap();
		
		
		hashENZYME_CODE_2_ENZYMEID = new HashMap <String,Integer> ();
		
		hashENZYME_CODE_2_ENZYMEID_reverse = new HashMap <Integer,String> ();
		
		hashPATHWAY_2_NCBI_GENEID = new HashMap <String,Integer> ();
		
		hashMICROARRAY_2_NCBI_GENEID = new HashMap <String,Integer> ();
		
		hashNCBI_GENEID_2_KEGGID = new HashMap <Integer,Integer> ();
		
		hashMICROARRAY_2_ACCESSION = new HashMap <String,String> ();
	}
	
	public Map getMapByGenomeType( final GenomeMappingType type ) {
		
		switch ( type ) {
		
		case PATHWAY_2_NCBI_GENEID:
			return hashPATHWAY_2_NCBI_GENEID;
			
		case MICROARRAY_2_NCBI_GENEID:
			return hashMICROARRAY_2_NCBI_GENEID;
		
		case ENZYME_CODE_2_ENZYME:
			return hashENZYME_CODE_2_ENZYMEID;
			
		case ENZYME_CODE_2_ENZYME_R:
			return hashENZYME_CODE_2_ENZYMEID_reverse;
			
		case NCBI_GENEID_2_KEGG:
			return hashNCBI_GENEID_2_KEGGID;
			
		case MICROARRAY_2_ACCESSION:
			return	hashMICROARRAY_2_ACCESSION;
			
		case MICROARRAY_2_ACCESSION_STRING:
			return multiMapMICROARRAY_2_ACCESSION;
			
		default:
			assert false : "unknown type!";
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
