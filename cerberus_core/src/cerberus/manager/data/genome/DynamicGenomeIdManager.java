/**
 * 
 */
package cerberus.manager.data.genome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
//import java.util.Iterator;
//import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

//import cerberus.base.map.MultiHashArrayMap;
//import cerberus.data.mapping.GenomeIdType;
import cerberus.data.map.MultiHashArrayIntegerMap;
import cerberus.data.map.MultiHashArrayStringMap;
import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.data.mapping.GenomeMappingType;
//import cerberus.base.map.MultiHashArrayStringMap;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.GeneViewRuntimeExceptionType;
import cerberus.util.exception.GeneViewRuntimeException;


/**
 * @author Michael Kalkusch
 *
 */
public class DynamicGenomeIdManager
extends AAbstractManager
implements IGenomeIdManager {

	private static final int iSortBufferInitialsize = 200;
	
	private AtomicBoolean bHasMapActiveWriter = new AtomicBoolean(false); 
	
	private GenomeMappingType currentEditingType;
	
	private IGenomeIdMap currentGenomeIdMap;
	
	protected HashMap<GenomeMappingType, IGenomeIdMap> hashType2Map;
	
	protected HashMap<GenomeMappingType, MultiHashArrayIntegerMap> hashType2MultiMapInt;
	
	protected HashMap<GenomeMappingType, MultiHashArrayStringMap> hashType2MultiMapString;
	
	public static final int iInitialSizeHashMap = 1000;
	
	public static final int iInitialCountAllLookupTables = 10;
	
	public static final int iInitialCountMultiMapLookupTables = 4;
	
	
	
	/**
	 * @param setGeneralManager
	 */
	public DynamicGenomeIdManager(IGeneralManager setGeneralManager) {

		super(setGeneralManager, 66, ManagerType.DATA_GENOME_ID );
		
		hashType2Map = new HashMap<GenomeMappingType, IGenomeIdMap> (iInitialCountAllLookupTables);
		
		hashType2MultiMapInt = new  HashMap<GenomeMappingType, MultiHashArrayIntegerMap> (iInitialCountMultiMapLookupTables);
		
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
			final int iSetInitialSizeHashMap ) {
		
		/* conisitency check */
		int iCurrentInitialSizeHashMap = iSetInitialSizeHashMap;
		
		if ( hashType2Map.containsKey( codingLutType ) ) 
		{
			refGeneralManager.getSingelton().logMsg(
					"createMapByType(" + 
					codingLutType.toString() + "," +
					dataType.toString() + ",*) WARNING! type is already registered!",
					LoggerType.VERBOSE);
			
			return false;
		}
		
		if  (iSetInitialSizeHashMap < 2)
		{
			iCurrentInitialSizeHashMap = iInitialSizeHashMap;
		}
		
		IGenomeIdMap newMap = null;
		
		refSingelton.logMsg("createMapByType(" +
				codingLutType.toString() + "," +
				dataType.toString() + ",*) ...",
				LoggerType.VERBOSE);
		
		try //catch ( OutOfMemoryError oee ) 
		{
			
			switch ( dataType ) 
			{
			case INT2INT:
				newMap = new GenomeIdMapInt2Int(dataType,iCurrentInitialSizeHashMap);
				break;
				
			case INT2STRING:
				newMap = new GenomeIdMapInt2String(dataType, iCurrentInitialSizeHashMap);
				break;
				
			case STRING2INT:
				newMap = new GenomeIdMapString2Int(dataType, iCurrentInitialSizeHashMap);
				break;
				
			case STRING2STRING:
				newMap = new GenomeIdMapString2String(dataType,iCurrentInitialSizeHashMap);
				break;
		
			/* Multi Map's */
				
			case MULTI_STRING2STRING:
				MultiHashArrayStringMap newMultiMapString = new MultiHashArrayStringMap(iCurrentInitialSizeHashMap);
				hashType2MultiMapString.put( codingLutType, newMultiMapString );
				return true;
				
			case MULTI_INT2INT:
				MultiHashArrayIntegerMap newMultiMapInt = new MultiHashArrayIntegerMap(iCurrentInitialSizeHashMap);
				hashType2MultiMapInt.put( codingLutType, newMultiMapInt );
				return true;
				
//			case MULTI_STRING2STRING_USE_LUT:
//				MultiHashArrayIntegerMap newMultiMap = new MultiHashArrayIntegerMap(iCurrentInitialSizeHashMap);
//				hashType2MultiMapInt.put( codingLutType, newMultiMap );
//				return true;
				
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
	
	public final MultiHashArrayIntegerMap getMultiMapIntegerByType( final GenomeMappingType type ) {
		
		return hashType2MultiMapInt.get( type );
	}
	
	public final MultiHashArrayStringMap getMultiMapStringByType( final GenomeMappingType type ) {
		
		return hashType2MultiMapString.get( type );
	}
	
	/**
	 * @see cerberus.manager.data.IGenomeIdManager#hasAnyMapByType(cerberus.data.mapping.GenomeMappingType)
	 */
	public final boolean hasAnyMapByType( final GenomeMappingType codingLutType ) {
		
		if (  hasMapByType( codingLutType ) ) {
			return true;
		}
		
		return hasMultiMapByType(codingLutType);
	}
	
	public final boolean hasMapByType( final GenomeMappingType codingLutType ) {
		
		return hashType2Map.containsKey( codingLutType );
	}
	
	public final boolean hasMultiMapByType( final GenomeMappingType codingLutType ) {
		
		if ( hashType2MultiMapInt.containsKey( codingLutType ) ) 
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
				throw new GeneViewRuntimeException(
						"buildLUT_startEditingSetTypes(" + 
						type + ") is not allocated!",
						GeneViewRuntimeExceptionType.DATAHANDLING);
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
			throw new GeneViewRuntimeException("buildLUT_stopEditing(" + type +
					") differs from current type=[" +
					currentEditingType + "]");
		}

		currentEditingType = GenomeMappingType.NON_MAPPING;
		
		return true;
	}

	
	public int getIdIntFromStringByMapping(
			final String sCerberusId, 
			final GenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer != null : "getIdFromStringByMapping(" + type +") type is not allocated";
		
		return buffer.getIntByStringChecked( sCerberusId );
	}
	

	public int getIdIntFromIntByMapping(
			final int iCerberusId, 
			final GenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer != null : "getIdFromIntByMapping(" + type +") type is not allocated";

		return buffer.getIntByIntChecked( iCerberusId );
	}
	
	public String getIdStringFromStringByMapping(
			final String sCerberusId, 
			final GenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer != null : "getStringIdFromStringByMapping(" + type +") type is not allocated";
		
		return buffer.getStringByStringChecked( sCerberusId );
	}


	public String getIdStringFromIntByMapping(
			final int iCerberusId, 
			final GenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer != null : "getStringIdFromIntByMapping(" + type +") type is not allocated";
		
		return buffer.getStringByIntChecked( iCerberusId );
	}

	//MARC: changed parameter from GenomeIdType to GenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the GenomeMappingType as key.
	public ArrayList<Integer> getIdIntListByType(int iId, GenomeMappingType genomeMappingType) {

		return hashType2MultiMapInt.get(genomeMappingType).get(iId);
	}
	
	//MARC: changed parameter from GenomeIdType to GenomeMappingType.
	// Because in the hashType2MultiMapString the maps are stored with the GenomeMappingType as key.	
	public ArrayList<String> getIdStringListByType(String sId, GenomeMappingType genomeMappingType) {
		
		return hashType2MultiMapString.get(genomeMappingType).get(sId);
	}


	public final boolean isBuildLUTinProgress() {

		return bHasMapActiveWriter.get();
	}


	public boolean hasItem(int iItemId) {

		//assert false : "method not implemented";
		return false;
	}


	public Object getItem(int iItemId) {

		assert false : "method not implemented";
		return null;
	}


	public int size() {

		return 0;
	}


	public boolean registerItem(Object registerItem, int iItemId, ManagerObjectType type) {

		assert false : "method not implemented";
		return false;
	}


	public boolean unregisterItem(int iItemId, ManagerObjectType type) {

		assert false : "method not implemented";
		return false;
	}


	/**
	 * @see cerberus.manager.data.IGenomeIdManager#setMapByType(cerberus.data.mapping.GenomeMappingType, java.lang.Object)
	 * 
	 * @see cerberus.manager.data.genome.IGenomeIdMap
	 * @see cerberus.data.map.MultiHashArrayStringMap
	 * @see cerberus.data.map.MultiHashArrayIntegerMap
	 */
	public void setMapByType(final GenomeMappingType codingLutType, 
			Object map) {
		
		if (map.getClass().equals(MultiHashArrayIntegerMap.class)) {
			hashType2MultiMapInt.put(codingLutType, (MultiHashArrayIntegerMap)map);
			return;
		}
		
		if (map.getClass().equals(MultiHashArrayStringMap.class)) {
			hashType2MultiMapString.put(codingLutType, (MultiHashArrayStringMap)map);
			return;
		}
		
		try {
			hashType2Map.put(codingLutType, (IGenomeIdMap) map);
		}
		catch (NullPointerException npe) {
			throw new GeneViewRuntimeException("setMapByType(final GenomeMappingType codingLutType, Object map) unsupported object=" +
					map.getClass().toString(),
					GeneViewRuntimeExceptionType.DATAHANDLING);		
		}
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#removeMapByType(cerberus.data.mapping.GenomeMappingType)
	 */
	public void removeMapByType(final GenomeMappingType codingLutType) {
		
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

	public Collection<Integer> getIdIntListFromIdListByType(Collection<Integer> iIdList, GenomeMappingType type) {

		assert iIdList != null : "can not handle null pointer";
		
		HashMap <Integer,Integer> sortBuffer = new HashMap <Integer,Integer>(iSortBufferInitialsize);
		
		Integer constantValue = new Integer(-1);
		
		Iterator <Integer> iterInputList = iIdList.iterator();
		while ( iterInputList.hasNext() )
		{
			ArrayList<Integer> buffer = getIdIntListByType(
					iterInputList.next().intValue(),
					type);
			
			if (buffer!= null)
			{
				Iterator <Integer> iterInnerArrayList = buffer.iterator();
				while ( iterInnerArrayList.hasNext() )
				{
					sortBuffer.put(iterInnerArrayList.next(),constantValue);
				} //while ( iterInnerArrayList.hasNext() )
				
			} //if (buffer!= null))
			
		} //while ( iterInputList.hasNext() )
		
		return sortBuffer.keySet();
	}


	public Collection<String> getIdStringListFromIdListByType(Collection<String> sIdList, GenomeMappingType type) {

		assert sIdList != null : "can not handle null pointer";
		
		HashMap <String,String> sortBuffer = new HashMap <String,String>(iSortBufferInitialsize);
		
		String constantValue = "-1";
		
		Iterator <String> iterInputList = sIdList.iterator();
		while ( iterInputList.hasNext() )
		{
			ArrayList<String> buffer = getIdStringListByType(
					iterInputList.next(),
					type);
			
			if (buffer!= null)
			{
				Iterator <String> iterInnerArrayList = buffer.iterator();
				while ( iterInnerArrayList.hasNext() )
				{
					sortBuffer.put(iterInnerArrayList.next(),constantValue);
				} //while ( iterInnerArrayList.hasNext() )
				
			} //if (buffer!= null))
			
		} //while ( iterInputList.hasNext() )
		
		return sortBuffer.keySet();
	}


	private Set <Integer> getKeysFromExposedDataStructues(final GenomeMappingType type) {
		
		IGenomeIdMap bufferMap = this.hashType2Map.get( type );
		
		if ( bufferMap == null ) {
			return null;
		}
		Set <Integer> keyset = bufferMap.getKeysInteger();		
	
		assert keyset == null : "GenomeMappingType=[" + type + "] was not mapped to keys of type Integer";
		
		return keyset;			
	}
	
	/**
	 * Creates a HashMap from the the MultiMap requested using (GenomeMappingType) type; 
	 * < GenomeMappingType - Id , index from [0.. multiMap.keySet().size()-1) >
	 * 
	 * @param type
	 * @return HashMap with < GenomeMappingType - Id , index from [0.. multiMap.keySet().size()-1) > 
	 * 
	 * @see GenomeMappingType.NCBI_GENEID_2_GENE_SHORT_NAME
	 * @see GenomeMappingType."NCBI_GENEID_2_NCBI_GENEID_CODE REVERSE
	 */
	public HashMap<Integer,Integer> getAllKeysByGenomeIdTypeHashMap(final GenomeMappingType type) {
		
		try {
			Set <Integer> keyset = 
				getKeysFromExposedDataStructues(type);	
		
			if  (keyset == null ) {
				return null;
			}
			
			/* create result data structure.. */
			HashMap<Integer,Integer> resultHashMap =
				new HashMap<Integer,Integer> (keyset.size());	
			
			Iterator<Integer> iter = keyset.iterator();
			
			for ( int i=0;iter.hasNext(); i++) {
				resultHashMap.put( iter.next(), i );
			}
			
			return resultHashMap;
			
		} catch (OutOfMemoryError e) {
			System.err.println("Out of memroy while allocation of memory! " + e.getStackTrace());			
			throw e;
		}
	}
	
	/**
	 * 
	 * @see GenomeMappingType.NCBI_GENEID_2_GENE_SHORT_NAME
	 * @see GenomeMappingType."NCBI_GENEID_2_NCBI_GENEID_CODE REVERSE
	 */
	public int[] getAllKeysByGenomeIdType(GenomeMappingType type) {		
		
		try {
			Set <Integer> keyset =
				getKeysFromExposedDataStructues(type);	
			
			if  (keyset == null ) {
				return null;
			}
			
			/* create result data structure.. */
			int[] resultArray = new int[keyset.size()];
			
			Iterator<Integer> iter = keyset.iterator();
			
			for ( int i=0;iter.hasNext(); i++) {
				resultArray[i] = iter.next().intValue();
			}
			return resultArray;
			
		} catch (OutOfMemoryError e) {
			System.err.println("Out of memroy while allocation of memory! " + e.getStackTrace());			
			throw e;
		}
	}

}
