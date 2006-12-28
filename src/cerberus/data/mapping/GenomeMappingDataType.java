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
	
	MULTI_INT2INT ( true ),
	
	NONE( false );
	
	private boolean bUseMultiMap;
	
	private GenomeMappingDataType( boolean bEnableUseMultiMap ) {
		this.bUseMultiMap = bEnableUseMultiMap;
	}
	
	public boolean isMultiMapUsed() {
		return bUseMultiMap;
	}
}
