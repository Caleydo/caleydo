package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeMappingType;


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
	
	public void setMultiHashMap( MultiHashArrayMap setMultiHashMap );
	
	public void setHashMap_StringInteger( HashMap <String,Integer> setHashMap );
	
	/**
	 * Also called reverse HashMap in cerberus.
	 * 
	 * @param setHashMap
	 */
	public void setHashMap_IntegerString( HashMap <Integer,String> setHashMap );
	
	public void setHashMap_IntegerInteger( HashMap <Integer,Integer> setHashMap );
	
	public void setHashMap( final HashMap setHashMap,
			final GenomeMappingType type);
}
