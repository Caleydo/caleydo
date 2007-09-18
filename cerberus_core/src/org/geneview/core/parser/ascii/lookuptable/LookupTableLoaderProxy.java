/**
 * 
 */
package org.geneview.core.parser.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
//import java.util.HashMap;
//import java.util.ListIterator;
//import java.util.NoSuchElementException;
//import java.util.StringTokenizer;

//import org.geneview.core.base.map.MultiHashArrayMap;
//import org.geneview.core.base.map.MultiHashArrayStringMap;
//import org.geneview.core.data.collection.parser.ParserTokenHandler;
import org.geneview.core.data.map.MultiHashArrayIntegerMap;
import org.geneview.core.data.map.MultiHashArrayStringMap;
import org.geneview.core.data.mapping.GenomeMappingType;
import org.geneview.core.data.mapping.GenomeMappingDataType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
//import org.geneview.core.manager.data.genome.AGenomeIdMap;
import org.geneview.core.manager.data.genome.DynamicGenomeIdManager;
import org.geneview.core.manager.data.genome.IGenomeIdMap;
import org.geneview.core.parser.ascii.AbstractLoader;
import org.geneview.core.parser.xml.sax.ISaxParserHandler;

/**
 * @author Michael Kalkusch
 *
 * @see cerberus.parser.ascii.IParserObject
 */
public final class LookupTableLoaderProxy 
extends AbstractLoader {
	
	private final ILoggerManager refLoggerManager;
	
	private ILookupTableLoader refProxyLookupTableLoader;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public LookupTableLoaderProxy(final IGeneralManager setGeneralManager,
			final String setFileName,
			final GenomeMappingType genomeIdType,
			final GenomeMappingDataType type,
			//final GenomeMappingType genomeIdType_optionalTarget,
			final boolean enableMultipeThreads) {
		
		super(setGeneralManager,
				setFileName,
				enableMultipeThreads);

		refLoggerManager = setGeneralManager.getSingelton().getLoggerManager();
		
		bRequiredSizeOfReadableLines = true;
		
		DynamicGenomeIdManager dgi_mng = 
			(DynamicGenomeIdManager) refGeneralManager.getSingelton().getGenomeIdManager();
		
		switch ( type ) {
		
		case INT2INT:
		case INT2STRING:
		case STRING2INT:
		case STRING2STRING:
			refProxyLookupTableLoader = new LookupTableHashMapLoader(
					setGeneralManager,
					setFileName,
					genomeIdType,
					this );
			
			//dgi_mng.createMapByType( genomeIdType, genomeIdType.getDataMapppingType() );
			dgi_mng.createMapByType( genomeIdType, type );
			
			IGenomeIdMap setCurrentMap = dgi_mng.getMapByType( genomeIdType );
			
			refProxyLookupTableLoader.setHashMap( setCurrentMap, genomeIdType );
			
			break;
			
		case MULTI_INT2INT:
			refProxyLookupTableLoader = new LookupTableMultiMapIntLoader(
					setGeneralManager,
					setFileName,
					genomeIdType,
					this  );
			
			dgi_mng.createMapByType( genomeIdType, type);// genomeIdType.getDataMapppingType() );
			
			MultiHashArrayIntegerMap setCurrentMultiMap = 
				dgi_mng.getMultiMapIntegerByType( genomeIdType );
			
			refProxyLookupTableLoader.setMultiMapInteger( setCurrentMultiMap, genomeIdType );
			break;
			
//		case MULTI_STRING2STRING_USE_LUT:
//			refProxyLookupTableLoader = new LookupTableMultiMapStringLoader(
//					setGeneralManager,
//					setFileName,
//					genomeIdType,
//					this  );
//			refProxyLookupTableLoader.setInitialSizeHashMap( 1000 );
//			
//			dgi_mng.createMapByType( genomeIdType, 
//					genomeIdType.getDataMapppingType() );
//			
//			MultiHashArrayIntegerMap mha_IntegerMap = 
//				dgi_mng.getMultiMapIntegerByType( genomeIdType_optionalTarget );
//			
//			refProxyLookupTableLoader.setMultiMapInteger( mha_IntegerMap, 
//					genomeIdType_optionalTarget );
//			
//			break;
			
		case MULTI_STRING2STRING:
			refProxyLookupTableLoader = new LookupTableMultiMapStringLoader(
					setGeneralManager,
					setFileName,
					genomeIdType,
					this  );
			refProxyLookupTableLoader.setInitialSizeHashMap( 1000 );
			
			dgi_mng.createMapByType( genomeIdType, type);//genomeIdType.getDataMapppingType() );
			
			MultiHashArrayStringMap mha_StringMap = 
				dgi_mng.getMultiMapStringByType( genomeIdType );
			
			refProxyLookupTableLoader.setMultiMapString( mha_StringMap, genomeIdType );
			
			break;
			
			
		default:
			assert false : "unsupported type! " + type;
		}
		
		refProxyLookupTableLoader.initLUT();
	}
//
//	public void setMultiHashMap( final MultiHashArrayMap setMultiHashMap ) {
//		refProxyLookupTableLoader.setMultiHashMap( setMultiHashMap );
//	}
	
	public void setHashMap( final IGenomeIdMap setHashMap,
			final GenomeMappingType type) {
		
		refLoggerManager.logMsg(
				"setHashMap(" + setHashMap.toString() + " , " +
				type.toString() + ") called from outside!",
				LoggerType.VERBOSE );
		
		refProxyLookupTableLoader.setHashMap( setHashMap, type);
	}
	
	public void setMultiMap( final MultiHashArrayIntegerMap setMultiMap,
			final GenomeMappingType type) {
		
		refProxyLookupTableLoader.setMultiMapInteger( setMultiMap, type );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.parser.handler.importer.ascii.AbstractLoader#loadDataParseFile(java.io.BufferedReader, int)
	 */
	@Override
	protected int loadDataParseFile(BufferedReader brFile,
			final int iNumberOfLinesInFile) throws IOException {

		/**
		 * progress bar init
		 */
		progressBarSetStoreInitTitle("load LUT " + getFileName(),
				0,  // reset progress bar to 0
				iNumberOfLinesInFile );
		
		int iTotalNumerOfLinesRed = 
			refProxyLookupTableLoader.loadDataParseFileLUT( 
				brFile, 
				iNumberOfLinesInFile );
		
		refGeneralManager.getSingelton().logMsg("  parsed #" + 
				this.iLineInFile_CurrentDataIndex + "  [" + 			
				this.iStartParsingAtLine + " -> " +
				this.iStopParsingAtLine +  "] stoped at line #" +
				iTotalNumerOfLinesRed,
				LoggerType.VERBOSE );	
		
		/**
		 * reset progressbar...
		 */
		progressBarResetTitle();		
		progressBarIncrement(5);
				
		return iTotalNumerOfLinesRed;
	}

	/**
	 * Writes back Map to IGenomeIdManager by calling cerberus.parser.ascii.lookuptable.ILookupTableLoader#wirteBackMapToGenomeManager()
	 * 
	 * @see cerberus.parser.ascii.lookuptable.ILookupTableLoader#wirteBackMapToGenomeManager()
	 * @see cerberus.parser.ascii.AbstractLoader#copyDataToInternalDataStructures()
	 * @see cerberus.manager.data.IGenomeIdManager
	 */
	@Override
	protected boolean copyDataToInternalDataStructures() {

		try {
			refProxyLookupTableLoader.wirteBackMapToGenomeIdManager();
			return true;
		}
		catch (Exception e) {

			refLoggerManager.logMsg("copyDataToInternalDataStructures() calling wirteBackMapToGenomeIdManager() failed!\n  error=" + 
					e.toString(), 
					LoggerType.ERROR_ONLY);
			
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoXML#setMementoXML_usingHandler(cerberus.xml.parser.ISaxParserHandler)
	 */
	public boolean setMementoXML_usingHandler(ISaxParserHandler refSaxHandler) {

		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#init()
	 */
	public void init() {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#destroy()
	 */
	public void destroy() {
		refProxyLookupTableLoader.destroyLUT();
	}
	
	public static final IGenomeIdMap createReverseMapFromMap( 
			final IGeneralManager refGeneralManager,
			final GenomeMappingType originMultiMapType,
			final GenomeMappingType targetMultiMapType) {
	
		/* consistency check */
		if ((originMultiMapType.getTypeOrigin() != targetMultiMapType.getTypeTarget()) ||
			 (originMultiMapType.getTypeTarget() != targetMultiMapType.getTypeOrigin()))
		{
			assert false : "Can not create reverse multimap, because originMapType and targetMapType do not match!";
			return null;
		}
		
		DynamicGenomeIdManager dgi_mng = 
			(DynamicGenomeIdManager) refGeneralManager.getSingelton().getGenomeIdManager();
		
		IGenomeIdMap refOrigionMap = dgi_mng.getMapByType(originMultiMapType);
		
		IGenomeIdMap refTargetMap = refOrigionMap.getReversedMap();
		
		dgi_mng.setMapByType(targetMultiMapType, refTargetMap);
		
		return refTargetMap;
	}

	/**
	 * Creates a new MultiHashArrayIntegerMap inside the DynamicGenomeIdManager.
	 * Fills the new MultiMap with data from originMultiMapType.
	 * 
	 * @param refGeneralManager
	 * @param originMultiMapType
	 * @param targetMultiMapType
	 * @return new MultiHashArrayIntegerMap
	 */
	public static final MultiHashArrayIntegerMap createReverseMultiMapFromMultiMapInt( 
			final IGeneralManager refGeneralManager,
			final GenomeMappingType originMultiMapType,
			final GenomeMappingType targetMultiMapType) {
		
		/* consistency check */
		if ((originMultiMapType.getTypeOrigin() != targetMultiMapType.getTypeTarget())||
			 (originMultiMapType.getTypeTarget() != targetMultiMapType.getTypeOrigin())||
			 (originMultiMapType.getDataMapppingType() != GenomeMappingDataType.MULTI_INT2INT))
		{
			assert false : "Can not create reverse multimap, because originMultMapType and targetMultMapType do not match!";
			return null;
		}
		
		DynamicGenomeIdManager dgi_mng = 
			(DynamicGenomeIdManager) refGeneralManager.getSingelton().getGenomeIdManager();
		
		MultiHashArrayIntegerMap refIntMultiMapOrigin = 
			dgi_mng.getMultiMapIntegerByType(originMultiMapType);
		
		dgi_mng.createMapByType(targetMultiMapType, 
				targetMultiMapType.getDataMapppingType(), 
				refIntMultiMapOrigin.size());
		
		MultiHashArrayIntegerMap refIntMultiMapTarget = 
			dgi_mng.getMultiMapIntegerByType(targetMultiMapType);

		Set<Integer> setKeysOrigin = refIntMultiMapOrigin.keySet();
		
		Iterator <Integer> iterKeysOrigin = setKeysOrigin.iterator();
		while ( iterKeysOrigin.hasNext())
		{
			int iKeyOrigin = iterKeysOrigin.next().intValue();
			
			ArrayList<Integer> buffer = refIntMultiMapOrigin.get(iKeyOrigin);			
			Iterator <Integer> iterValues = buffer.iterator();				
			//Iterator <Integer> iterValues = refIntMultiMapOrigin.get(iKeyOrigin).iterator();		
			while (iterValues.hasNext())
			{				
				refIntMultiMapTarget.put(
						iterValues.next().intValue(), 
						iKeyOrigin);
				
			} //while (iterValues.hasNext())
			
		} //while ( iterKeysOrigin.hasNext())
		
		return refIntMultiMapTarget;
	}
	
	/**
	 * Creates a new MultiHashArrayStringMap inside the DynamicGenomeIdManager.
	 * Fills the new MultiMap with data from originMultiMapType.
	 * 
	 * @param refGeneralManager
	 * @param originMultiMapType
	 * @param targetMultiMapType
	 * @return new MultiHashArrayStringMap
	 */
	public static final MultiHashArrayStringMap createReverseMultiMapFromMultiMapString( 
			final IGeneralManager refGeneralManager,
			final GenomeMappingType originMultiMapType,
			final GenomeMappingType targetMultiMapType) {
		
		/* consistency check */
		if ((originMultiMapType.getTypeOrigin() != targetMultiMapType.getTypeTarget())||
			 (originMultiMapType.getTypeTarget() != targetMultiMapType.getTypeOrigin())||
			 (originMultiMapType.getDataMapppingType() != GenomeMappingDataType.MULTI_INT2INT))
		{
			assert false : "Can not create reverse multimap, because originMultMapType and targetMultMapType do not match!";
			return null;
		}
		
		DynamicGenomeIdManager dgi_mng = 
			(DynamicGenomeIdManager) refGeneralManager.getSingelton().getGenomeIdManager();
		
		MultiHashArrayStringMap refStringMultiMapOrigin = 
			dgi_mng.getMultiMapStringByType(originMultiMapType);
		
		dgi_mng.createMapByType(targetMultiMapType, 
				targetMultiMapType.getDataMapppingType(), 
				refStringMultiMapOrigin.size());
		
		MultiHashArrayStringMap refStringMultiMapTarget = 
			dgi_mng.getMultiMapStringByType(targetMultiMapType);

		Set<String> setKeysOrigin = refStringMultiMapOrigin.keySet();
		
		Iterator <String> iterKeysOrigin = setKeysOrigin.iterator();
		while ( iterKeysOrigin.hasNext())
		{
			String sKeyOrigin = iterKeysOrigin.next();
			
			ArrayList<String> buffer = refStringMultiMapOrigin.get(sKeyOrigin);			
			Iterator <String> iterValues = buffer.iterator();
			//Iterator <String> iterValues = refIntMultiMapOrigin.get(sKeyOrigin).iterator();
			while (iterValues.hasNext())
			{				
				refStringMultiMapTarget.put(iterValues.next(), 
						sKeyOrigin);
				
			} //while (iterValues.hasNext())
			
		} //while ( iterKeysOrigin.hasNext())
		
		return refStringMultiMapTarget;
	}
	
	public static final IGenomeIdMap createCodeResolvedMapFromMap( 
			final IGeneralManager refGeneralManager,		
			GenomeMappingType originMapMappingType,
			GenomeMappingType genomeMappingLUT_1,
			GenomeMappingType genomeMappingLUT_2,
			GenomeMappingDataType sourceMapMappingType) {
	
		DynamicGenomeIdManager dgi_mng = 
			(DynamicGenomeIdManager) refGeneralManager.getSingelton().getGenomeIdManager();
		
		IGenomeIdMap refMapToConvert = dgi_mng.getMapByType(originMapMappingType);
		
		IGenomeIdMap refTargetMap = refMapToConvert.getCodeResolvedMap(
				dgi_mng, genomeMappingLUT_1, 
				genomeMappingLUT_2,
				originMapMappingType.getDataMapppingType(),
				sourceMapMappingType);
		
		// Removes old map that contains the codes instead of the IDs
		dgi_mng.removeMapByType(originMapMappingType);
		
		dgi_mng.setMapByType(originMapMappingType, refTargetMap);
		
		return refTargetMap;
	}
	
	public static final MultiHashArrayIntegerMap createCodeResolvedMultiMapFromMultiMapString( 
			final IGeneralManager refGeneralManager,		
			GenomeMappingType originMapMappingType,
			GenomeMappingType genomeMappingLUT_1,
			GenomeMappingType genomeMappingLUT_2) {
	
		DynamicGenomeIdManager dgi_mng = 
			(DynamicGenomeIdManager) refGeneralManager.getSingelton().getGenomeIdManager();
		
		MultiHashArrayStringMap refMapToConvert = dgi_mng.getMultiMapStringByType(originMapMappingType);
		
		MultiHashArrayIntegerMap refTargetMap = refMapToConvert.getCodeResolvedMap(
				dgi_mng, genomeMappingLUT_1, 
				genomeMappingLUT_2);
		
		// Removes old map that contains the codes instead of the IDs
		dgi_mng.removeMapByType(originMapMappingType);
		
		dgi_mng.setMapByType(originMapMappingType, refTargetMap);
		
		return refTargetMap;
	}
}
