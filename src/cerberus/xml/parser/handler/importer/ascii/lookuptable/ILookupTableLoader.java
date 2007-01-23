package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
//import java.util.HashMap;

//import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.data.genome.IGenomeIdMap;

public interface ILookupTableLoader {

	public boolean loadDataParseFileLUT(BufferedReader brFile,
			final int iNumberOfLinesInFile ) throws IOException;
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#init()
	 */
	public void initLUT();
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#destroy()
	 */
	public void destroyLUT();
	
	public void setHashMap( final IGenomeIdMap setHashMap,
			final GenomeMappingType type);
}
