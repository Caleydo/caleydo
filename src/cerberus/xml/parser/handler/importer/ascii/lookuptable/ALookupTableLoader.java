/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.util.HashMap;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class ALookupTableLoader 
//extends AbstractLoader 
implements ILookupTableLoader {

	protected final IGenomeIdManager refGenomeIdManager;
	
	protected String sFileName;
	
	protected GenomeMappingType genomeType;
	
	protected final IGeneralManager refGeneralManager;
	
	protected LookupTableLoaderProxy refLookupTableLoaderProxy;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public ALookupTableLoader( final IGeneralManager refGeneralManager,
			final String sFileName,
			final GenomeMappingType genomeType,
			final LookupTableLoaderProxy refLookupTableLoaderProxy ) {

		this.refLookupTableLoaderProxy = refLookupTableLoaderProxy;
		this.refGeneralManager = refGeneralManager;
		this.sFileName = sFileName;	
		this.genomeType = genomeType;
		
		refGenomeIdManager = 
			refGeneralManager.getSingelton().getGenomeIdManager();
		
		// Set proper map for writing the parsed data in it.
		setHashMap((HashMap)refGenomeIdManager.getMapByGenomeType(genomeType, false), 
				genomeType, false);	
		
		// Set reverse map
		setHashMap((HashMap)refGenomeIdManager.getMapByGenomeType(genomeType, true), 
				genomeType, true);	
		
	}

	public final void setHashMap(final HashMap refHashMap,
			final GenomeMappingType genomeMappingType, final boolean bIsReverse) {
		
		assert genomeMappingType == genomeType : "must use same type as in constructor!";
		
		// Check if map is a multi map
		if (genomeMappingType.isMultiMap())
		{
			setMultiHashMap((MultiHashArrayMap) refHashMap, bIsReverse);
			return;
		}
		
		/**
		 * GenomeMappingType.GenomeMappingDataType.*
		 */
		switch (genomeMappingType.getDataMapppingType()) {
		case INT2INT:
			setHashMap_IntegerInteger((HashMap <Integer,Integer>)refHashMap, bIsReverse);
			break;
			
		case STRING2INT:
			setHashMap_StringInteger(refHashMap, bIsReverse);
			break;
			
		case INT2STRING:
			setHashMap_IntegerString(refHashMap, bIsReverse);
			break;
			
		default:
			assert false : "not supported genome mapping type!";
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#setMultiHashMap(cerberus.base.map.MultiHashArrayMap, boolean)
	 */
	public void setMultiHashMap(MultiHashArrayMap setMultiHashMap, 
			final boolean bIsReverse) {
		
		assert false : "This methode must be overloaded by sub-class";
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#setHashMap_StringInteger(java.util.HashMap, boolean)
	 */
	public void setHashMap_StringInteger(HashMap  <String,Integer> setHashMap, 
			final boolean bIsReverse) {
		
		assert false : "This methode must be overloaded by sub-class";
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#setHashMap_IntegerString(java.util.HashMap, boolean)
	 */
	public void setHashMap_IntegerString(HashMap  <Integer,String> setHashMap, 
			final boolean bIsReverse) {
		
		assert false : "This methode must be overloaded by sub-class";
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#setHashMap_IntegerInteger(java.util.HashMap, boolean)
	 */
	public void setHashMap_IntegerInteger(HashMap  <Integer,Integer> setHashMap, 
			final boolean bIsReverse) {
		
		assert false : "This methode must be overloaded by sub-class";
	}
	
//	/* (non-Javadoc)
//	 * @see cerberus.xml.parser.handler.importer.ascii.AbstractLoader#loadDataParseFile(java.io.BufferedReader, int)
//	 */
//	public boolean loadDataParseFile(BufferedReader brFile,
//			final int iNumberOfLinesInFile) throws IOException {
//
////		/**
////		 * progress bar init
////		 */
////		progressBarSetStoreInitTitle("load LUT " + sFileName,
////				0,  // reset progress bar to 0
////				iNumberOfLinesInFile );
////		
//		
//		boolean bResultOnLoading = loadDataParseFileLUT( brFile, iNumberOfLinesInFile);	
//		
////		refGeneralManager.getSingelton().getLoggerManager().logMsg("  parsed #" + 
////				this.iLineInFile_CurrentDataIndex + "  [" + 			
////				this.iStartParsingAtLine + " -> " +
////				this.iStopParsingAtLine +  "] stoped at line #" +
////				(this.iLineInFile-1),
////				LoggerType.VERBOSE );				
////		
////		/**
////		 * reset progressbar...
////		 */
////		progressBarResetTitle();		
////		progressBarIncrement(5);
//		
//		return bResultOnLoading;
//	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.AbstractLoader#copyDataToInternalDataStructures()
	 */
	protected boolean copyDataToInternalDataStructures() {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoXML#setMementoXML_usingHandler(cerberus.xml.parser.ISaxParserHandler)
	 */
	public boolean setMementoXML_usingHandler(ISaxParserHandler refSaxHandler) {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#init()
	 */
	public void init() {
		assert false : "init() should not be called on this object!";
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#destroy()
	 */
	public void destroy() {
		assert false : "destroy() should not be called on this object!";
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#initLUT()
	 */
	public void initLUT() {

	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#destroyLUT()
	 */
	public void destroyLUT() {

	}

}
