package cerberus.manager.data.genome;

//import cerberus.data.mapping.GenomeMappingDataType;


public interface IGenomeIdMap {

//	public Object getObjectByType( final Object byObject, GenomeMappingDataType type );
	
	public int getIntByInt( final int key);
	
	public int getIntByString( final String key);
	
	public String getStringByInt( final int key);
	
	public String getStringByString( final String key);

	/**
	 * Adds a key-value pair. 
	 * Each implementation has to cast to the propper types.
	 * 
	 * @param key
	 * @param value
	 */
	public void put( final String key, 
			final String value);
	
//	// Object cast to int or String
//	public int getInt( final Object byObject);
//	
//	public String getString( final Object byObject);
	
}
