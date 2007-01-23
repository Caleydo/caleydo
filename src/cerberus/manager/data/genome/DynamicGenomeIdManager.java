/**
 * 
 */
package cerberus.manager.data.genome;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

//import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeIdType;
import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;


/**
 * @author Michael Kalkusch
 *
 */
public class DynamicGenomeIdManager
extends AAbstractManager
implements IGenomeIdManager {

	private AtomicBoolean bHasMapActiveWriter = new AtomicBoolean(false); 
	
	private GenomeMappingType currentEditingType;
	
	private IGenomeIdMap currentGenomeIdMap;
	
	protected HashMap<GenomeMappingType, IGenomeIdMap> hashType2Map;
	
	public static final int iInitialSizeHashMap = 1000;
	
	public static final int iInitialCountAllLookupTables = 10;
	
	
	
	/**
	 * @param setGeneralManager
	 */
	public DynamicGenomeIdManager(IGeneralManager setGeneralManager) {

		super(setGeneralManager, 66, ManagerType.GENOME_ID );
		
		hashType2Map = new HashMap<GenomeMappingType, IGenomeIdMap> (iInitialCountAllLookupTables);
	}

	
	public final boolean createMapByType( final GenomeMappingType codingLutType, 
			final GenomeMappingDataType dataType ) {
		
		return createMapByType(codingLutType, 
				dataType, 
				DynamicGenomeIdManager.iInitialSizeHashMap );
	}
	
	public boolean createMapByType( final GenomeMappingType codingLutType, 
			final GenomeMappingDataType dataType,
			final int iInitialSizeHashMap ) {
		
		/* conisitency check */
		
		if ( hashType2Map.containsKey( codingLutType ) ) 
		{
			return false;
		}
		
		IGenomeIdMap newMap = null;
		
		switch ( dataType ) 
		{
		case INT2INT:
			newMap = new GenomeIdMapInt2Int(iInitialSizeHashMap);
			break;
			
		case INT2STRING:
			newMap = new GenomeIdMapInt2String(iInitialSizeHashMap);
			break;
			
		case STRING2INT:
			newMap = new GenomeIdMapString2Int(iInitialSizeHashMap);
			break;
			
		case STRING2STRING:
			newMap = new GenomeIdMapString2String(iInitialSizeHashMap);
			break;
			
		default:
			assert false : "createMap() type=" + dataType + " is not supported";
			return false;
			
		} // switch ( dataType ) 
		
		try 
		{
			hashType2Map.put( codingLutType, newMap );
		} 
		catch ( OutOfMemoryError oee ) 
		{
			System.err.println(" Could not allocate memory for HashMap [" +
					codingLutType + "] tpye=[" + dataType + "]");
					
			throw oee;
//			return false;
		}
		
		return true;
	}
	
	public final IGenomeIdMap getMapByType( final GenomeMappingType type ) {
		
		return hashType2Map.get( type );
	}
	
	public final boolean hasMapByType( final GenomeMappingType codingLutType ) {
		
		return hashType2Map.containsKey( codingLutType );
	}	
	
	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT_startEditingSetTypes(cerberus.data.mapping.GenomeIdType, cerberus.data.mapping.GenomeIdType)
	 */
	public boolean buildLUT_startEditing( final GenomeMappingType type ) {

		if ( ! bHasMapActiveWriter.compareAndSet(false,true) ) {
			return false;
		}
		
		currentEditingType = type;
		
		currentGenomeIdMap = hashType2Map.get( type );
		
		if ( currentGenomeIdMap == null ) {
			throw new CerberusRuntimeException(
					"buildLUT_startEditingSetTypes(" + 
					type + ") is not allocated!",
					CerberusExceptionType.DATAHANDLING);
		}
		
		return true;

	}

	


	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT_stopEditing()
	 */
	public boolean buildLUT_stopEditing( final GenomeMappingType type ) {

		if ( ! bHasMapActiveWriter.compareAndSet(true,false) ) 
		{
			return false;
		}
		
		/* consistency check */
		if ( ! currentEditingType.equals( type ) ) 
		{
			throw new CerberusRuntimeException("buildLUT_stopEditing(" + type +
					") differs from current type=[" +
					currentEditingType + "]");
		}

		currentEditingType = GenomeMappingType.NON_MAPPING;
		
		return true;
	}

	
	public int getIdFromStringByMapping(
			final String sCerberusId, 
			final GenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer != null : "getIdFromStringByMapping(" + type +") type is not allocated";
		
		return buffer.getIntByString( sCerberusId );
	}


	public int getIdFromIntByMapping(
			final int iCerberusId, 
			final GenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer != null : "getIdFromIntByMapping(" + type +") type is not allocated";
		
		return buffer.getIntByInt( iCerberusId );
	}
	
	public String getStringIdFromStringByMapping(
			final String sCerberusId, 
			final GenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer != null : "getStringIdFromStringByMapping(" + type +") type is not allocated";
		
		return buffer.getStringByString( sCerberusId );
	}


	public String getStringIdFromIntByMapping(
			final int iCerberusId, 
			final GenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer != null : "getStringIdFromIntByMapping(" + type +") type is not allocated";
		
		return buffer.getStringByInt( iCerberusId );
	}


	public ArrayList<Integer> getIdListByType(int iCerberusId, GenomeIdType type) {

		// TODO Auto-generated method stub
		return null;
	}


	public boolean isBuildLUTinProgress() {

		return bHasMapActiveWriter.get();
	}


	public boolean hasItem(int iItemId) {

		assert false : "methode not implemented";
		return false;
	}


	public Object getItem(int iItemId) {

		assert false : "methode not implemented";
		return null;
	}


	public int size() {

		return 0;
	}


	public boolean registerItem(Object registerItem, int iItemId, ManagerObjectType type) {

		assert false : "methode not implemented";
		return false;
	}


	public boolean unregisterItem(int iItemId, ManagerObjectType type) {

		assert false : "methode not implemented";
		return false;
	}

}
