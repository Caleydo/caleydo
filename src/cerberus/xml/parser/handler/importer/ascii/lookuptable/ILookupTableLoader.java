package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
//import java.util.HashMap;
import cerberus.base.map.MultiHashArrayIntegerMap;
//import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.data.genome.IGenomeIdMap;

/**
 * Interface for ASCII file loader.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ILookupTableLoader {

	/**
	 * load and parse a file.
	 * 
	 * @param brFile handle to the file
	 * @param iNumberOfLinesInFile total number of lines in file or Interger.MAX
	 * @return real line red or -1 in case of an error
	 * @throws IOException if somthing went wrong during file access
	 */
	public int loadDataParseFileLUT(BufferedReader brFile,
			final int iNumberOfLinesInFile ) throws IOException;
	
	/**
	 * initialize the lookup table if neccessary.
	 * 
	 * @see cerberus.xml.parser.IParserObject#init()
	 */
	public void initLUT();
	
	/**
	 * Destroy and cleanup stored variables of the lookup table if neccessary.
	 * 
	 * @see cerberus.xml.parser.IParserObject#destroy()
	 */
	public void destroyLUT();
	
	/**
	 * set the reference to teh hashmap
	 * 
	 * @param setHashMap
	 * @param type specify the type of mapping
	 */
	public void setHashMap( final IGenomeIdMap setHashMap,
			final GenomeMappingType type);
	
	/**
	 * set the reference to teh hashmap
	 * 
	 * @param setHashMap
	 * @param type specify the type of mapping
	 */
	public void setMultiMap( final MultiHashArrayIntegerMap setHashMap,
			final GenomeMappingType type);
	
	/**
	 * Define initial size. 
	 * Must be called before initLUT() is called!
	 * 
	 * @param iInitialSizeHashMap
	 */
	public void setInitialSizeHashMap( final int iInitialSizeHashMap );
		
}
