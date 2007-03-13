package cerberus.data.mapping;

/**
 * @author Michael Kalkusch
 *
 */
public enum GenomeMappingDataType
{
	INT2INT ( false ),
	INT2STRING ( false ),
	STRING2INT ( false ),
	STRING2STRING ( false ),
	
	MULTI_INT2INT ( true ),
	/**
	 * Reads a Integer-2-Integer Multimap and uses a 
	 * lookup table to create the final Integer Multimap.
	 */
	MULTI_INT2INT_USE_LUT ( true ),
	
	MULTI_STRING2STRING ( true ),
	
	/**
	 * Reads a String-2-String Multimap and uses a 
	 * lookup table to create the final String Multimap.
	 */
	MULTI_STRING2STRING_USE_LUT ( true ),
	
	MULTI_STRING2STRING_CREATE_REVERSE ( true ),
	
	NONE( false );
	
	private boolean bUseMultiMap;
	
	private GenomeMappingDataType( boolean bEnableUseMultiMap ) {
		this.bUseMultiMap = bEnableUseMultiMap;
	}
	
	public boolean isMultiMapUsed() {
		return bUseMultiMap;
	}
}
