/**
 * 
 */
package org.caleydo.core.manager.data.genome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
//import java.util.Iterator;
//import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

//import org.caleydo.core.base.map.MultiHashArrayMap;
//import org.caleydo.core.data.mapping.EGenomeIdType;
import org.caleydo.core.data.map.MultiHashArrayIntegerMap;
import org.caleydo.core.data.map.MultiHashArrayStringMap;
import org.caleydo.core.data.mapping.EGenomeMappingDataType;
import org.caleydo.core.data.mapping.EGenomeMappingType;
//import org.caleydo.core.base.map.MultiHashArrayStringMap;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.base.AAbstractManager;
import org.caleydo.core.manager.data.IGenomeIdManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.mapping.AGenomeMapper;
import org.caleydo.core.util.mapping.EGenomeMappingCascadeType;
import org.caleydo.core.util.mapping.GenomeMapper_ENZYME_2_NCBI_GENEID_2_ACCESSION_2_MICROARRAY_EXPRESSION_STORAGE_INDEX;


/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class DynamicGenomeIdManager
extends AAbstractManager
implements IGenomeIdManager {

	private static final int iSortBufferInitialsize = 200;
	
	private AtomicBoolean bHasMapActiveWriter = new AtomicBoolean(false); 
	
	private EGenomeMappingType currentEditingType;
	
	private IGenomeIdMap currentGenomeIdMap;
	
	protected HashMap<EGenomeMappingType, IGenomeIdMap> hashType2Map;
	
	protected HashMap<EGenomeMappingType, MultiHashArrayIntegerMap> hashType2MultiMapInt;
	
	protected HashMap<EGenomeMappingType, MultiHashArrayStringMap> hashType2MultiMapString;
	
	public static final int iInitialSizeHashMap = 1000;
	
	public static final int iInitialCountAllLookupTables = 10;
	
	public static final int iInitialCountMultiMapLookupTables = 4;
	
	/**
	 * Constructor.
	 * 
	 * @param setGeneralManager
	 */
	public DynamicGenomeIdManager(final IGeneralManager setGeneralManager) {

		super(setGeneralManager, 66, ManagerType.DATA_GENOME_ID );
		
		hashType2Map = new HashMap<EGenomeMappingType, IGenomeIdMap> (iInitialCountAllLookupTables);
		
		hashType2MultiMapInt = new  HashMap<EGenomeMappingType, MultiHashArrayIntegerMap> (iInitialCountMultiMapLookupTables);
		
		hashType2MultiMapString = new HashMap<EGenomeMappingType, MultiHashArrayStringMap> (iInitialCountMultiMapLookupTables);
	}

	
	public final boolean createMapByType( final EGenomeMappingType codingLutType, 
			final EGenomeMappingDataType dataType ) {
		
		return createMapByType(codingLutType, 
				dataType, 
				DynamicGenomeIdManager.iInitialSizeHashMap );
	}
	
	public boolean createMapByType( final EGenomeMappingType codingLutType, 
			final EGenomeMappingDataType dataType,
			final int iSetInitialSizeHashMap ) {
		
		/* conisitency check */
		int iCurrentInitialSizeHashMap = iSetInitialSizeHashMap;
		
		if ( hashType2Map.containsKey( codingLutType ) ) 
		{
			generalManager.getSingelton().logMsg(
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
		
		singelton.logMsg("createMapByType(" +
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
	
	public final IGenomeIdMap getMapByType( final EGenomeMappingType type ) {
		
		return hashType2Map.get( type );
	}
	
	public final MultiHashArrayIntegerMap getMultiMapIntegerByType( final EGenomeMappingType type ) {
		
		return hashType2MultiMapInt.get( type );
	}
	
	public final MultiHashArrayStringMap getMultiMapStringByType( final EGenomeMappingType type ) {
		
		return hashType2MultiMapString.get( type );
	}
	
	/**
	 * @see org.caleydo.core.manager.data.IGenomeIdManager#hasAnyMapByType(org.caleydo.core.data.mapping.EGenomeMappingType)
	 */
	public final boolean hasAnyMapByType( final EGenomeMappingType codingLutType ) {
		
		if (  hasMapByType( codingLutType ) ) {
			return true;
		}
		
		return hasMultiMapByType(codingLutType);
	}
	
	public final boolean hasMapByType( final EGenomeMappingType codingLutType ) {
		
		return hashType2Map.containsKey( codingLutType );
	}
	
	public final boolean hasMultiMapByType( final EGenomeMappingType codingLutType ) {
		
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
	 * @see org.caleydo.core.manager.data.IGenomeIdManager#buildLUT_startEditingSetTypes(org.caleydo.core.data.mapping.EGenomeIdType, org.caleydo.core.data.mapping.EGenomeIdType)
	 */
	public boolean buildLUT_startEditing( final EGenomeMappingType type ) {

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
				throw new CaleydoRuntimeException(
						"buildLUT_startEditingSetTypes(" + 
						type + ") is not allocated!",
						CaleydoRuntimeExceptionType.DATAHANDLING);
			}
			
			return true;
		} //if ( type.isMultiMap() )

	}

	


	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IGenomeIdManager#buildLUT_stopEditing()
	 */
	public boolean buildLUT_stopEditing( final EGenomeMappingType type ) {

		if ( ! bHasMapActiveWriter.compareAndSet(true,false) ) 
		{
			return false;
		}
		
		/* consistency check */
		
		if ( type.isMultiMap() )
		{
			//TODO: register multi hash map to!
			//assert false :  "TODO: register multi hash map to!";
			
			currentEditingType = EGenomeMappingType.NON_MAPPING;
			
			return true;
		} // if ( type.isMultiMap() ) ... else 
		
		if ( ! currentEditingType.equals( type ) ) 
		{
			throw new CaleydoRuntimeException("buildLUT_stopEditing(" + type +
					") differs from current type=[" +
					currentEditingType + "]");
		}

		currentEditingType = EGenomeMappingType.NON_MAPPING;
		
		return true;
	}

	
	public int getIdIntFromStringByMapping(
			final String sCaleydoId, 
			final EGenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer == null : "getIdFromStringByMapping(" + type +") type is not allocated";
		
		return buffer.getIntByStringChecked( sCaleydoId );
	}
	

	public int getIdIntFromIntByMapping(
			final int iUniqueId, 
			final EGenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer == null : "getIdFromIntByMapping(" + type +") type is not allocated";

		return buffer.getIntByIntChecked( iUniqueId );
	}
	
	public String getIdStringFromStringByMapping(
			final String sCaleydoId, 
			final EGenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer == null : "getStringIdFromStringByMapping(" + type +") type is not allocated";
		
		return buffer.getStringByStringChecked( sCaleydoId );
	}


	public String getIdStringFromIntByMapping(
			final int iUniqueId, 
			final EGenomeMappingType type) {

		IGenomeIdMap buffer = hashType2Map.get( type );
		
		assert buffer == null : "getStringIdFromIntByMapping(" + type +") type is not allocated";
		
		return buffer.getStringByIntChecked( iUniqueId );
	}

	//MARC: changed parameter from EGenomeIdType to EGenomeMappingType.
	// Because in the hashType2MultiMapInt the maps are stored with the EGenomeMappingType as key.
	public ArrayList<Integer> getIdIntListByType(int iId, EGenomeMappingType genomeMappingType) {

		return hashType2MultiMapInt.get(genomeMappingType).get(iId);
	}
	
	//MARC: changed parameter from EGenomeIdType to EGenomeMappingType.
	// Because in the hashType2MultiMapString the maps are stored with the EGenomeMappingType as key.	
	public ArrayList<String> getIdStringListByType(String sId, EGenomeMappingType genomeMappingType) {
		
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
	 * @see org.caleydo.core.manager.data.IGenomeIdManager#setMapByType(org.caleydo.core.data.mapping.EGenomeMappingType, java.lang.Object)
	 * 
	 * @see org.caleydo.core.manager.data.genome.IGenomeIdMap
	 * @see org.caleydo.core.data.map.MultiHashArrayStringMap
	 * @see org.caleydo.core.data.map.MultiHashArrayIntegerMap
	 */
	public void setMapByType(final EGenomeMappingType codingLutType, 
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
			throw new CaleydoRuntimeException("setMapByType(final EGenomeMappingType codingLutType, Object map) unsupported object=" +
					map.getClass().toString(),
					CaleydoRuntimeExceptionType.DATAHANDLING);		
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.IGenomeIdManager#removeMapByType(org.caleydo.core.data.mapping.EGenomeMappingType)
	 */
	public void removeMapByType(final EGenomeMappingType codingLutType) {
		
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

	public Collection<Integer> getIdIntListFromIdListByType(Collection<Integer> iIdList, EGenomeMappingType type) {

		assert iIdList == null : "can not handle null pointer";
		
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


	public Collection<String> getIdStringListFromIdListByType(Collection<String> sIdList, EGenomeMappingType type) {

		assert sIdList == null : "can not handle null pointer";
		
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


	private Set <Integer> getKeysFromExposedDataStructues(final EGenomeMappingType type) {
		
		IGenomeIdMap bufferMap = this.hashType2Map.get( type );
		
		if ( bufferMap == null ) {
			return null;
		}
		Set <Integer> keyset = bufferMap.getKeysInteger();		
	
		//assert keyset == null : "EGenomeMappingType=[" + type + "] was not mapped to keys of type Integer";
		
		return keyset;			
	}
	
	private Collection <Integer> getValuesFromExposedDataStructues(final EGenomeMappingType type) {
		
		IGenomeIdMap bufferMap = this.hashType2Map.get( type );
		
		if ( bufferMap == null ) {
			return null;
		}
		
		try {
			Collection <Integer> keyset = bufferMap.getValuesInteger();		
		
			assert keyset == null : "EGenomeMappingType=[" + type + "] was not mapped to keys of type Integer";
			
			return keyset;		
		} catch ( CaleydoRuntimeException gve ) {
			singelton.logMsg("getValuesFromExposedDataStructues( " + type.toString() +
					") failed, because String values could not be converted to Integer",
					LoggerType.MINOR_ERROR_XML );
			/* more details on exception .. */
			singelton.logMsg("getValuesFromExposedDataStructues( " + type.toString() + 
					") failed; Exception= " + gve.toString(),
					LoggerType.VERBOSE );
			
			/* return empty collection .. */
			return new ArrayList <Integer> ();
		}
	}
	
	/**
	 * Creates a HashMap from the the MultiMap requested using (EGenomeMappingType) type; 
	 * < EGenomeMappingType - Id , index from [0.. multiMap.keySet().size()-1) >
	 * 
	 * @param type
	 * @return HashMap with < EGenomeMappingType - Id , index from [0.. multiMap.keySet().size()-1) > 
	 * 
	 * @see EGenomeMappingType.NCBI_GENEID_2_GENE_SHORT_NAME
	 * @see EGenomeMappingType."NCBI_GENEID_2_NCBI_GENEID_CODE REVERSE
	 */
	public HashMap<Integer,Integer> getAllKeysByGenomeIdTypeHashMap(final EGenomeMappingType type) {
		
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
	 * @see EGenomeMappingType.NCBI_GENEID_2_GENE_SHORT_NAME
	 * @see EGenomeMappingType."NCBI_GENEID_2_NCBI_GENEID_CODE REVERSE
	 */
	public int[] getAllKeysByGenomeIdType(EGenomeMappingType type) {		
		
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


	@Override
	public HashMap<Integer, Integer> getAllValuesByGenomeIdTypeHashMap(
			EGenomeMappingType type) {

		try {
			Collection <Integer> keyset = 
				getValuesFromExposedDataStructues(type);	
		
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

	public AGenomeMapper getGenomeMapperByMappingCascadeType(
			final EGenomeMappingCascadeType mappingCascadeType) {
		
		if (mappingCascadeType.equals(EGenomeMappingCascadeType.ENZYME_2_NCBI_GENEID_2_ACCESSION_2_MICROARRAY_EXPRESSION_STORAGE_INDEX))
			return new GenomeMapper_ENZYME_2_NCBI_GENEID_2_ACCESSION_2_MICROARRAY_EXPRESSION_STORAGE_INDEX(generalManager);

		assert true : "No genome mapper is implemented for the requested mapping cascade!";
		return null;
	}
}
