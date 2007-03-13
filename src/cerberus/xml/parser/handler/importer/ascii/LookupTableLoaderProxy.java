/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
//import java.util.HashMap;
//import java.util.ListIterator;
//import java.util.NoSuchElementException;
//import java.util.StringTokenizer;

//import cerberus.base.map.MultiHashArrayMap;
//import cerberus.base.map.MultiHashArrayStringMap;
//import cerberus.data.collection.parser.ParserTokenHandler;
import cerberus.base.map.MultiHashArrayIntegerMap;
import cerberus.base.map.MultiHashArrayStringMap;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.genome.DynamicGenomeIdManager;
import cerberus.manager.data.genome.IGenomeIdMap;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.xml.parser.handler.importer.ascii.AbstractLoader;
import cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader;
import cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableHashMapLoader;
import cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableMultiMapIntLoader;
import cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableMultiMapStringLoader;




/**
 * @author Michael Kalkusch
 *
 * @see cerberus.xml.parser.IParserObject
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
			final GenomeMappingType genometype,
			final GenomeMappingDataType type,
			final GenomeMappingType genometype_optionlalTarget,
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
					genometype,
					this );
			dgi_mng.createMapByType( genometype, genometype.getDataMapppingType() );
			
			IGenomeIdMap setCurrentMap = dgi_mng.getMapByType( genometype );
			
			refProxyLookupTableLoader.setHashMap( setCurrentMap, genometype );
			
			break;
			
		case MULTI_INT2INT:
			refProxyLookupTableLoader = new LookupTableMultiMapIntLoader(
					setGeneralManager,
					setFileName,
					genometype,
					this  );
			
			dgi_mng.createMapByType( genometype, genometype.getDataMapppingType() );
			
			MultiHashArrayIntegerMap setCurrentMultiMap = 
				dgi_mng.getMultiMapByType( genometype );
			
			refProxyLookupTableLoader.setMultiMapInteger( setCurrentMultiMap, genometype );
			break;
			
		case MULTI_STRING2STRING_USE_LUT:
			refProxyLookupTableLoader = new LookupTableMultiMapStringLoader(
					setGeneralManager,
					setFileName,
					genometype,
					this  );
			refProxyLookupTableLoader.setInitialSizeHashMap( 1000 );
			
			dgi_mng.createMapByType( genometype_optionlalTarget, 
					genometype_optionlalTarget.getDataMapppingType() );
			
			MultiHashArrayIntegerMap mha_IntegerMap = 
				dgi_mng.getMultiMapByType( genometype_optionlalTarget );
			
			refProxyLookupTableLoader.setMultiMapInteger( mha_IntegerMap, 
					genometype_optionlalTarget );
			
			break;
			
		case MULTI_STRING2STRING:
			refProxyLookupTableLoader = new LookupTableMultiMapStringLoader(
					setGeneralManager,
					setFileName,
					genometype,
					this  );
			refProxyLookupTableLoader.setInitialSizeHashMap( 1000 );
			
			dgi_mng.createMapByType( genometype_optionlalTarget, 
					genometype_optionlalTarget.getDataMapppingType() );
			
			MultiHashArrayStringMap mha_StringMap = 
				dgi_mng.getMultiMapStringByType( genometype_optionlalTarget );
			
			refProxyLookupTableLoader.setMultiMapString( mha_StringMap, 
					genometype_optionlalTarget );
			
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
	 * @see cerberus.xml.parser.handler.importer.ascii.AbstractLoader#loadDataParseFile(java.io.BufferedReader, int)
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

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.AbstractLoader#copyDataToInternalDataStructures()
	 */
	@Override
	protected boolean copyDataToInternalDataStructures() {

		return true;
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
			dgi_mng.getMultiMapByType(originMultiMapType);
		
		dgi_mng.createMapByType(targetMultiMapType, 
				targetMultiMapType.getDataMapppingType(), 
				refIntMultiMapOrigin.size());
		
		MultiHashArrayIntegerMap refIntMultiMapTarget = 
			dgi_mng.getMultiMapByType(targetMultiMapType);

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
	
}
