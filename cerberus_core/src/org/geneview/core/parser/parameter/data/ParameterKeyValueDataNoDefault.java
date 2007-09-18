/**
 * 
 */
package org.geneview.core.parser.parameter.data;

import java.util.Enumeration;
import java.util.Hashtable;

import org.geneview.core.parser.parameter.IParameterKeyValuePair;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Stores key-value pair and default-key,default-value in same place.
 * Attention: Default values mut be set before key-value pair are set, because
 * otherwise they would be overwritten!
 * 
 * @author Michael Kalkusch
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
	 * @see cerberus.parser.parameter.IParameterKeyValueDefaultvalue#getValue(Stringt)
	 */
	public T getValue( final String key ) {
		return hashKey2Generic.get( key );
	}
	
	
	/**
	 * Returns the same value as getValue() since the default value is not stored in addition.
	 * 
	 * @see cerberus.parser.parameter.data.ParameterKeyValueDataNoDefault#getValue(String)
	 * @see cerberus.parser.parameter.IParameterKeyValueDefaultvalue#getDefaultValue(Stringt)
	 */
	public T getDefaultValue( final String key ) {
		return hashKey2Generic.get( key );
	}
	
	/**
	 * Returns the same value as getValue(String) since the default value is not stored in addition.
	 * 
	 * @see cerberus.parser.parameter.data.ParameterKeyValueDataNoDefault#getValue(String)
	 * @see cerberus.parser.parameter.IParameterKeyValueDefaultvalue#getValueOrDefault(Stringt)
	 */
	public T getValueOrDefault( final String key ) {
		return hashKey2Generic.get( key );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.parser.parameter.IParameterKeyValueDefaultvalue#setValue(Stringt, T)
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
	 * @see cerberus.parser.parameter.data.ParameterKeyValueDataNoDefault#setValue(String, T)
	 * @see cerberus.parser.parameter.IParameterKeyValuePair#setValueAndDefaultValue(Stringt, null, null)
	 */
	public void setValueAndDefaultValue(final String key, 
			final T value,
			final T defaultValue) {
		setValue(key,value);
	}
	
	/**
	 * Does the same value as setValue(String, T) since the default value is not stored in addition.
	 * 
	 * @see cerberus.parser.parameter.data.ParameterKeyValueDataNoDefault#setValue(String, T)
	 * @see cerberus.parser.parameter.IParameterKeyValueDefaultvalue#setDefaultValue(Stringt, T)
	 */
	public void setDefaultValue( final String key,
			final T value ) {
		if ( bDefaultValuesSetFinished ) {
			throw new GeneViewRuntimeException("setDefaultValue() is callled after writing data to this class! Once data valeus are written setDefaultValue() must not be called any more!");
		}
		hashKey2Generic.put( key, value );
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.parser.parameter.IParameterKeyValueDefaultvalue#clear()
	 */
	public void clear() {		
		synchronized( getClass() ) {
			hashKey2Generic.clear();
			bDefaultValuesSetFinished = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see cerberus.parser.parameter.IParameterKeyValueDefaultvalue#containsValue(Stringt)
	 */
	public boolean containsValue( final String key ) {
		return hashKey2Generic.containsKey( key );
	}
	
	/**
	 * Returns the same value as containsValue(String) since the default value is not stored in addition.
	 * 
	 * @see cerberus.parser.parameter.data.ParameterKeyValueDataNoDefault#containsValue(String)
	 * @see cerberus.parser.parameter.IParameterKeyValueDefaultvalue#containsDefaultValue(Stringt)
	 */
	public boolean containsDefaultValue( final String key ) {
		return hashKey2Generic.containsKey( key );
	}
	
	/**
	 * Returns the same value as containsValue(String) since the default value is not stored in addition.	
	 * 
	 * @see cerberus.parser.parameter.data.ParameterKeyValueDataNoDefault#containsValue(String)
	 * @see cerberus.parser.parameter.IParameterKeyValueDefaultvalue#containsValueAndDefaultValue(Stringt)
	 */
	public boolean containsValueAndDefaultValue( final String key ) {
		return hashKey2Generic.containsKey( key );
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.parser.parameter.IParameterKeyValuePair#size()
	 */
	public int size()
	{
		return this.hashKey2Generic.size();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.parser.parameter.IParameterKeyValuePair#isEmpty()
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
		
		String sBuffer;
		
		/* special case for first element... */
		if ( iter.hasMoreElements() ) 
		{
			sBuffer = iter.nextElement();
			strBuffer.append( sBuffer );
			strBuffer.append( "->" );
			strBuffer.append( hashKey2Generic.get( sBuffer ) );
		}
		
		while ( iter.hasMoreElements() )
		{
			sBuffer = iter.nextElement();
			strBuffer.append( " " );
			strBuffer.append( sBuffer );
			strBuffer.append( "->" );
			strBuffer.append( hashKey2Generic.get( sBuffer ) );
		}
		
		return strBuffer.toString();
	}

}
