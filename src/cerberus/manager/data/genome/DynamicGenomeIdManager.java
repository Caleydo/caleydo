/**
 * 
 */
package cerberus.manager.data.genome;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

//import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeIdType;
import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.data.mapping.GenomeMappingType;
//import cerberus.base.map.MultiHashArrayStringMap;
import cerberus.base.map.MultiHashArrayIntegerMap;
import cerberus.base.map.MultiHashArrayStringMap;
import cerberus.manager.IGeneralManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeExceptionType;
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
	
	protected HashMap<GenomeMappingType, MultiHashArrayIntegerMap> hashType2MultiMap;
	
	protected HashMap<GenomeMappingType, MultiHashArrayStringMap> hashType2MultiMapString;
	
	public static final int iInitialSizeHashMap = 1000;
	
	public static final int iInitialCountAllLookupTables = 10;
	
	public static final int iInitialCountMultiMapLookupTables = 4;
	
	
	
	/**
	 * @param setGeneralManager
	 */
	public DynamicGenomeIdManager(IGeneralManager setGeneralManager) {

		super(setGeneralManager, 66, ManagerType.GENOME_ID );
		
		hashType2Map = new HashMap<GenomeMappingType, IGenomeIdMap> (iInitialCountAllLookupTables);
		
		hashType2MultiMap = new  HashMap<GenomeMappingType, MultiHashArrayIntegerMap> (iInitialCountMultiMapLookupTables);
		
		hashType2MultiMapString = new HashMap<GenomeMappingType, MultiHashArrayStringMap> (iInitialCountMultiMapLookupTables);
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
		
		
		try //catch ( OutOfMemoryError oee ) 
		{
			
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
		
			/* Multi Map's */
				
			case MULTI_STRING2STRING:
				MultiHashArrayStringMap newMultiMapString = new MultiHashArrayStringMap(iInitialSizeHashMap);
				hashType2MultiMapString.put( codingLutType, newMultiMapString );
				return true;
				
			case MULTI_INT2INT:
			case MULTI_STRING2STRING_USE_LUT:
				MultiHashArrayIntegerMap newMultiMap = new MultiHashArrayIntegerMap(iInitialSizeHashMap);
				hashType2MultiMap.put( codingLutType, newMultiMap );
				return true;
				
			default:
				assert false : "createMap() type=" + dataType + " is not supported";
				return false;
				
			} // switch ( dataType ) 
			

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
	
	public final MultiHashArrayIntegerMap getMultiMapByType( final GenomeMappingType type ) {
		
		return hashType2MultiMap.get( type );
	}
	
	public final MultiHashArrayStringMap getMultiMapStringByType( final GenomeMappingType type ) {
		
		return hashType2MultiMapString.get( type );
	}
	
	
	
	public final boolean hasMapByType( final GenomeMappingType codingLutType ) {
		
		return hashType2Map.containsKey( codingLutType );
	}
	
	public final boolean hasMultiMapByType( final GenomeMappingType codingLutType ) {
		
		if ( hashType2MultiMap.containsKey( codingLutType ) ) 
		{
			return true;
		}
		
		if ( hashType2MultiMapString.containsKey( codingLutType ) ) 
		{
			return true;
		}
		
		return false;
	}	
	
	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT_startEditingSetTypes(cerberus.data.mapping.GenomeIdType, cerberus.data.mapping.GenomeIdType)
	 */
	public boolean buildLUT_startEditing( final GenomeMappingType type ) {

		if ( ! bHasMapActiveWriter.compareAndSet(false,true) ) {
			return false;
		}
		
		currentEditingType = type;
		
		if ( type.isMultiMap() )
		{
			//TODO: register multi hash map to!
			//assert false :  "TODO: register multi hash map to!";
			
			return true;
		}
		else
		{ // if ( type.isMultiMap() ) ... else 
			currentGenomeIdMap = hashType2Map.get( type );
			
			if ( currentGenomeIdMap == null ) {
				throw new CerberusRuntimeException(
						"buildLUT_startEditingSetTypes(" + 
						type + ") is not allocated!",
						CerberusRuntimeExceptionType.DATAHANDLING);
			}
			
			return true;
		} //if ( type.isMultiMap() )

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
		
		if ( type.isMultiMap() )
		{
			//TODO: register multi hash map to!
			//assert false :  "TODO: register multi hash map to!";
			
			currentEditingType = GenomeMappingType.NON_MAPPING;
			
			return true;
		} // if ( type.isMultiMap() ) ... else 
		
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
