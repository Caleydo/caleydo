/**
 * 
 */
package cerberus.xml.parser.parameter.data;

import java.util.Enumeration;
import java.util.Hashtable;

import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.parameter.IParameterKeyValuePair;

/**
 * Stores key-value pair and default-key,default-value in same place.
 * Attention: Default values mut be set before key-value pair are set, because
 * otherwise they would be overwritten!
 * 
 * @author java
 *
 */
public final class ParameterKeyValueDataNoDefault < T > implements IParameterKeyValuePair<T>
{

	/**
	 * flag to indicate, that default values are set.
	 * If this flag is true, data values are written and no more defautl values must be set,
	 * because that would cause the data valeus to be overwritten.
	 */
	private boolean bDefaultValuesSetFinished = false;
		
	private Hashtable < String, T > hashKey2Generic;
	
	/**
	 * 
	 */
	public ParameterKeyValueDataNoDefault()
	{
		hashKey2Generic = new Hashtable < String, T > ();
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#getValue(java.lang.String)
	 */
	public T getValue( final String key ) {
		return hashKey2Generic.get( key );
	}
	
	
	/**
	 * Returns the same value as getValue() since the default value is not stored in addition.
	 * 
	 * @see cerberus.xml.parser.parameter.data.ParameterKeyValueDataNoDefault#getValue(String)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#getDefaultValue(java.lang.String)
	 */
	public T getDefaultValue( final String key ) {
		return hashKey2Generic.get( key );
	}
	
	/**
	 * Returns the same value as getValue(String) since the default value is not stored in addition.
	 * 
	 * @see cerberus.xml.parser.parameter.data.ParameterKeyValueDataNoDefault#getValue(String)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#getValueOrDefault(java.lang.String)
	 */
	public T getValueOrDefault( final String key ) {
		return hashKey2Generic.get( key );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#setValue(java.lang.String, T)
	 */
	public void setValue( final String key,
			final T value ) {
		hashKey2Generic.put( key, value );
		
		/* set flag to avoid setting defautl values after calling setValue() .. */
		if ( ! bDefaultValuesSetFinished ) {
			bDefaultValuesSetFinished = true;
		}
	}
	
	/**
	 * Does the same value as setValue(String, T) since the default value is not stored in addition.
	 * 
	 * @see cerberus.xml.parser.parameter.data.ParameterKeyValueDataNoDefault#setValue(String, T)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValuePair#setValueAndDefaultValue(java.lang.String, null, null)
	 */
	public void setValueAndDefaultValue(final String key, 
			final T value,
			final T defaultValue) {
		setValue(key,value);
	}
	
	/**
	 * Does the same value as setValue(String, T) since the default value is not stored in addition.
	 * 
	 * @see cerberus.xml.parser.parameter.data.ParameterKeyValueDataNoDefault#setValue(String, T)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#setDefaultValue(java.lang.String, T)
	 */
	public void setDefaultValue( final String key,
			final T value ) {
		if ( bDefaultValuesSetFinished ) {
			throw new CerberusRuntimeException("setDefaultValue() is callled after writing data to this class! Once data valeus are written setDefaultValue() must not be called any more!");
		}
		hashKey2Generic.put( key, value );
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#clear()
	 */
	public void clear() {		
		synchronized( getClass() ) {
			hashKey2Generic.clear();
			bDefaultValuesSetFinished = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#containsValue(java.lang.String)
	 */
	public boolean containsValue( final String key ) {
		return hashKey2Generic.containsKey( key );
	}
	
	/**
	 * Returns the same value as containsValue(String) since the default value is not stored in addition.
	 * 
	 * @see cerberus.xml.parser.parameter.data.ParameterKeyValueDataNoDefault#containsValue(String)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#containsDefaultValue(java.lang.String)
	 */
	public boolean containsDefaultValue( final String key ) {
		return hashKey2Generic.containsKey( key );
	}
	
	/**
	 * Returns the same value as containsValue(String) since the default value is not stored in addition.	
	 * 
	 * @see cerberus.xml.parser.parameter.data.ParameterKeyValueDataNoDefault#containsValue(String)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#containsValueAndDefaultValue(java.lang.String)
	 */
	public boolean containsValueAndDefaultValue( final String key ) {
		return hashKey2Generic.containsKey( key );
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValuePair#size()
	 */
	public int size()
	{
		return this.hashKey2Generic.size();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValuePair#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this.hashKey2Generic.isEmpty();
	}
	
	
	public String toString() {
		
		if ( this.isEmpty() ) {
			return "-";
		}
		
		StringBuffer strBuffer = new StringBuffer();
		
		Enumeration <String> iter = hashKey2Generic.keys();
		
		/* special case for first element... */
		if ( iter.hasMoreElements() ) 
		{
			strBuffer.append( iter.nextElement() );
		}
		
		while ( iter.hasMoreElements() )
		{
			strBuffer.append( " " );
			strBuffer.append( iter.nextElement() );
		}
		
		return strBuffer.toString();
	}

}
