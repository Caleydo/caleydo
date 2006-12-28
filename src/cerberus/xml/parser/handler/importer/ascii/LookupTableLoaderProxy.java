/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.collection.parser.ParserTokenHandler;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.xml.parser.IParserObject;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.xml.parser.handler.importer.ascii.AbstractLoader;
import cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader;
import cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableIntIntLoader;
import cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableIntIntMultiMapLoader;
import cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableStringIntLoader;



/**
 * @author Michael Kalkusch
 *
 */
public class LookupTableLoaderProxy extends AbstractLoader {
	
	private final ILoggerManager refLoggerManager;
	
	protected ILookupTableLoader refLookupTableLoader;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public LookupTableLoaderProxy(IGeneralManager setGeneralManager,
			final String setFileName,
			final GenomeMappingType genometype,
			final GenomeMappingDataType type ) {
		
		super(setGeneralManager,setFileName);
		
		refLoggerManager = setGeneralManager.getSingelton().getLoggerManager();
		
		switch ( type ) {
		
		case INT2INT:
			refLookupTableLoader = new LookupTableIntIntLoader(
					setGeneralManager,
					setFileName,
					genometype,
					this );
			break;
			
		case MULTI_INT2INT:
			refLookupTableLoader = new LookupTableIntIntMultiMapLoader(
					setGeneralManager,
					setFileName,
					genometype,
					this  );
			break;
			
		case STRING2INT:
			refLookupTableLoader = new LookupTableStringIntLoader(
					setGeneralManager,
					setFileName,
					genometype,
					this  );
			break;
			
		default:
			assert false : "unsupported type!";
		}
		
		refLookupTableLoader.initLUT();
	}
//
//	public void setMultiHashMap( final MultiHashArrayMap setMultiHashMap ) {
//		refLookupTableLoader.setMultiHashMap( setMultiHashMap );
//	}
	
	public void setHashMap( final HashMap setHashMap,
			final GenomeMappingType type) {
		
		refLoggerManager.logMsg(
				"setHashMap(" + setHashMap.toString() + " , " +
				type.toString() + ") called from outside!",
				LoggerType.VERBOSE );
		
		refLookupTableLoader.setHashMap( setHashMap, type);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.AbstractLoader#loadDataParseFile(java.io.BufferedReader, int)
	 */
	@Override
	protected boolean loadDataParseFile(BufferedReader brFile,
			final int iNumberOfLinesInFile) throws IOException {

		/**
		 * progress bar init
		 */
		progressBarSetStoreInitTitle("load LUT " + getFileName(),
				0,  // reset progress bar to 0
				iNumberOfLinesInFile );
		
		boolean bParsingResult = 
			refLookupTableLoader.loadDataParseFileLUT( 
				brFile, 
				iNumberOfLinesInFile );
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg("  parsed #" + 
				this.iLineInFile_CurrentDataIndex + "  [" + 			
				this.iStartParsingAtLine + " -> " +
				this.iStopParsingAtLine +  "] stoped at line #" +
				(this.iLineInFile-1),
				LoggerType.VERBOSE );	
		
		/**
		 * reset progressbar...
		 */
		progressBarResetTitle();		
		progressBarIncrement(5);
		
		
		return bParsingResult;
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.AbstractLoader#copyDataToInternalDataStructures()
	 */
	@Override
	protected boolean copyDataToInternalDataStructures() {

		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoXML#setMementoXML_usingHandler(cerberus.xml.parser.ISaxParserHandler)
	 */
	public boolean setMementoXML_usingHandler(ISaxParserHandler refSaxHandler) {

		return false;
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
		refLookupTableLoader.destroyLUT();
	}

}
