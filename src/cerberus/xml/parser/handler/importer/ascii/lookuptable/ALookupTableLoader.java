/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.collection.parser.ParserTokenHandler;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.xml.parser.handler.importer.ascii.AbstractLoader;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;


/**
 * @author Michael Kalkusch
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
	public ALookupTableLoader( final IGeneralManager setGeneralManager,
			final String setFileName,
			final GenomeMappingType genometype,
			final LookupTableLoaderProxy setLookupTableLoaderProxy ) {

		refLookupTableLoaderProxy = setLookupTableLoaderProxy;
		refGeneralManager = setGeneralManager;
		sFileName = setFileName;
		
//		super(setGeneralManager, setFileName);
//		
//		super.bRequiredSizeOfReadableLines = true;		
		
	
		genomeType = genometype;
		
		refGenomeIdManager = 
			refGeneralManager.getSingelton().getGenomeIdManager();
		
		

		HashMap bufferMap = (HashMap) refGenomeIdManager.getMapByGenomeType( genomeType );
		
		setHashMap( bufferMap, genomeType );
		
	}

	public final void setHashMap( final HashMap setHashMap,
			final GenomeMappingType type) {
		
		assert type == genomeType : "must use same type as in constructor!";
		
		if ( type.isMultiMap() )
		{
			setMultiHashMap( (MultiHashArrayMap) setHashMap );
			return;
		}
		
		/**
		 * GenomeMappingType.GenomeMappingDataType.*
		 */
		switch ( type.getDataMapppingType() ) {
		case INT2INT:
			setHashMap_IntegerInteger( (HashMap <Integer,Integer>) setHashMap );
			break;
			
		case STRING2INT:
			setHashMap_StringInteger( setHashMap );
			break;
			
		case INT2STRING:
			setHashMap_IntegerString( setHashMap );
			break;
			
		default:
			assert false : "not supported type!";
		}
	}
	
	public void setMultiHashMap( MultiHashArrayMap setMultiHashMap ) {
		assert false : "This methode must be overloaded by sub-class";
	}
	
	public void setHashMap_StringInteger( HashMap  <String,Integer> setHashMap ) {
		assert false : "This methode must be overloaded by sub-class";
	}
	
	public void setHashMap_IntegerString( HashMap  <Integer,String> setHashMap ) {
		assert false : "This methode must be overloaded by sub-class";
	}
	
	public void setHashMap_IntegerInteger( HashMap  <Integer,Integer> setHashMap ) {
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
