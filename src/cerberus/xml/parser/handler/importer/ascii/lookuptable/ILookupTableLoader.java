package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeMappingType;


public interface ILookupTableLoader {

	public boolean loadDataParseFileLUT(BufferedReader brFile,
			final int iNumberOfLinesInFile ) throws IOException;
	
	public void setMultiHashMap(MultiHashArrayMap refMultiHashMap,
			final boolean bIsReverse);
	
	public void setHashMap_StringInteger(HashMap <String,Integer> refHashMap,
			final boolean bIsReverse);
	
	public void setHashMap_IntegerString(HashMap <Integer,String> refHashMap,
			final boolean bIsReverse);
	
	public void setHashMap_IntegerInteger(HashMap <Integer,Integer> refHashMap,
			final boolean bIsReverse);
	
	public void setHashMap( final HashMap refHashMap,
			final GenomeMappingType type, final boolean bIsReverse);
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#init()
	 */
	public void initLUT();
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#destroy()
	 */
	public void destroyLUT();
}
