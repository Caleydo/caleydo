package org.geneview.core.parser.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
//import java.util.HashMap;
//import org.geneview.core.base.map.MultiHashArrayMap;
import org.geneview.core.data.map.MultiHashArrayIntegerMap;
import org.geneview.core.data.map.MultiHashArrayStringMap;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.data.genome.IGenomeIdMap;

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
	 * @see org.geneview.core.parser.ascii.IParserObject#init()
	 */
	public void initLUT();
	
	/**
	 * Destroy and cleanup stored variables of the lookup table if neccessary.
	 * 
	 * @see org.geneview.core.parser.ascii.IParserObject#destroy()
	 */
	public void destroyLUT();
	
	/**
	 * Write data back to IGenomeIdManager.
	 * This mehtode is called by LookupTableLoaderProxy#copyDataToInternalDataStructures(), which
	 * is derived from AbstractLoader#copyDataToInternalDataStructures().
	 * 
	 * @see org.geneview.core.parser.ascii.lookuptable.LookupTableLoaderProxy
	 * @see org.geneview.core.parser.ascii.lookuptable.LookupTableLoaderProxy#copyDataToInternalDataStructures()
	 * @see org.geneview.core.parser.ascii.AbstractLoader#copyDataToInternalDataStructures()
	 * @see org.geneview.core.manager.data.IGenomeIdManager
	 * 
	 * @return TRUE on success.
	 */
	public abstract void wirteBackMapToGenomeIdManager();
	
	/**
	 * set the reference to the hashmap
	 * 
	 * @param setHashMap
	 * @param type specify the type of mapping
	 */
	public void setHashMap( final IGenomeIdMap setHashMap,
			final EGenomeMappingType type);
	
	/**
	 * set the reference to the hashmap
	 * 
	 * @param setHashMap
	 * @param type specify the type of mapping
	 */
	public void setMultiMapInteger( final MultiHashArrayIntegerMap setHashMap,
			final EGenomeMappingType type);
	
	public void setMultiMapString( final MultiHashArrayStringMap setHashMap,
			final EGenomeMappingType type);
	
	/**
	 * Define initial size. 
	 * Must be called before initLUT() is called!
	 * 
	 * @param iInitialSizeHashMap
	 */
	public void setInitialSizeHashMap( final int iInitialSizeHashMap );
		
}
