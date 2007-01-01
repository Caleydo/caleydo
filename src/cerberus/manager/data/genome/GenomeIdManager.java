/**
 * 
 */
package cerberus.manager.data.genome;

import java.util.HashMap;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeIdType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;


/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class GenomeIdManager 
extends AGenomeIdManager
implements IGenomeIdManager {

	protected MultiHashArrayMap refInsertingIntoMap;
	protected MultiHashArrayMap refInsertingIntoMap_revers;
	
	protected HashMap refInsertingIntoHashMap;
	protected HashMap refInsertingIntoHashMap_revers;
	
	protected HashMap <String,Integer> refHashMap_String2Int;
	protected HashMap <Integer,String> refHashMap_String2Int_revers;
	
	/**
	 * @param setGeneralManager
	 */
	public GenomeIdManager(IGeneralManager setGeneralManager) {

		super(setGeneralManager);
		
		super.initAll();
		
		buildLUT_stopEditing();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT_startEditingSetTypes(cerberus.data.mapping.GenomeIdType, cerberus.data.mapping.GenomeIdType)
	 */
	public void buildLUT_startEditingSetTypes( final GenomeIdType typeFromId,
			final GenomeIdType typeToId) {

		switch ( typeFromId ) {
		
//		case ACCESSION:	
//			break;
//		case METHOBOLIT:		
//			break;
		
		/**
		 * NCBI_GENEID
		 */
		case NCBI_GENEID:
			
			switch (typeToId) {
			
			case KEGG_ID:
				//refInsertingIntoHashMap = multiMapNCBI_GENEID_2_KEGGID;
				break;
				
			default:
				refInsertingIntoMap = null;
			
				throw new CerberusRuntimeException(
						"buildLUT_startEditingSetTypes() unsupported second type [" + 
						typeFromId + "]",
						CerberusExceptionType.CONVERSION);
			}
			break;
			
		/**
		 * ENZYME
		 */
		case ENZYME_ID:
			
			break;
			
		case KEGG_ID:
			
			break;
			
			
//		case NCBI_GI:
//			break;
//			
//		case PATHWAY:
//			break;
			
		default:
			refInsertingIntoMap = null;
			throw new CerberusRuntimeException(
					"buildLUT_startEditingSetTypes() unsupported first type [" + 
					typeFromId + "]",
					CerberusExceptionType.CONVERSION);
		}
	}

	public boolean buildLUT_String2Int_startEditing(final GenomeIdType typeFromId,
			final GenomeIdType typeToId) {
		
		if ( typeFromId == GenomeIdType.MICROARRAY) 
		{
			if ( typeFromId == GenomeIdType.NCBI_GENEID) 
			{				
				//refHashMap_String2Int = this.hashMICROARRAY_2_NCBI_GENEID;
				return true;
			}
		}
		
		if ( typeFromId == GenomeIdType.ENZYME_CODE) 
		{
			if ( typeFromId == GenomeIdType.ENZYME_ID) 
			{				
				//refHashMap_String2Int = this.hashENZYME_CODE_2_ENZYMEID;
				//refHashMap_String2Int_revers = this.hashENZYME_CODE_2_ENZYMEID_reverse;
				return true;
			}
		}
		
		return false;
	}
	
	
	public void buildLUT_String2Int( final String sText, final int iValue ) {
		
		refHashMap_String2Int.put( sText, iValue );
	}
	
	
	public void buildLUT_String2IntAndRevers( final String sText, final int iValue ) {
		
		refHashMap_String2Int.put( sText, iValue );
		refHashMap_String2Int_revers.put( iValue, sText );
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT(int, int)
	 */
	public void buildLUT( Object first, Object second ) {
		

	}


	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT_stopEditing()
	 */
	public void buildLUT_stopEditing() {

		refInsertingIntoMap = null;		
		refInsertingIntoMap_revers = null;			
		refInsertingIntoHashMap = null;
		refInsertingIntoHashMap_revers = null;
		refHashMap_String2Int = null;
		
		bLUTcreationIsNotInPorgress = false;
	}

}
