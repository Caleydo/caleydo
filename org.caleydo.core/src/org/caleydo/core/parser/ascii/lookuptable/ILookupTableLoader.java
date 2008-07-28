package org.caleydo.core.parser.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
//import java.util.HashMap;
//import org.caleydo.core.base.map.MultiHashArrayMap;
import org.caleydo.core.data.map.MultiHashArrayIntegerMap;
import org.caleydo.core.data.map.MultiHashArrayStringMap;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;

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
	 * @param iNumberOfLinesInFile total number of lines in file or Integer.MAX
	 * @return real line red or -1 in case of an error
	 * @throws IOException if something went wrong during file access
	 */
	public int loadDataParseFileLUT(BufferedReader brFile,
			final int iNumberOfLinesInFile ) throws IOException;
	
	/**
	 * initialize the lookup table if necessary.
	 * 
	 * @see org.caleydo.core.parser.ascii.IParserObject#init()
	 */
	public void initLUT();
	
	/**
	 * Destroy and cleanup stored variables of the lookup table if necessary.
	 * 
	 * @see org.caleydo.core.parser.ascii.IParserObject#destroy()
	 */
	public void destroyLUT();
	
	/**
	 * Write data back to IGenomeIdManager.
	 * This mehtode is called by LookupTableLoaderProxy#copyDataToInternalDataStructures(), which
	 * is derived from AbstractLoader#copyDataToInternalDataStructures().
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.LookupTableLoaderProxy
	 * @see org.caleydo.core.parser.ascii.lookuptable.LookupTableLoaderProxy#setArraysToStorages()
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#setArraysToStorages()
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdManager
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
