/**
 * 
 */
package cerberus.xml.parser.parameter.data;

import java.util.Enumeration;
import java.util.Hashtable;

import cerberus.xml.parser.parameter.IParameterKeyValuePair;

/**
 * @author Michael Kalkusch
 *
 */
public final class ParameterKeyValueDataAndDefault < T > implements IParameterKeyValuePair<T>
{

	private Hashtable < String, T > hashKey2Generic;
	
	private Hashtable < String, T > hashKey2DefaultValue;
	
	/**
	 * 
	 */
	public ParameterKeyValueDataAndDefault()
	{
		hashKey2Generic = new Hashtable < String, T > ();
		
		hashKey2DefaultValue = new Hashtable < String, T > ();
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#getValue(Stringt)
	 */
	public T getValue( final String key ) {
		return hashKey2Generic.get( key );
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#getDefaultValue(Stringt)
	 */
	public T getDefaultValue( final String key ) {
		return hashKey2DefaultValue.get( key );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#getValueOrDefault(Stringt)
	 */
	public T getValueOrDefault( final String key ) {
		T buffer = hashKey2Generic.get( key );
		
		if ( buffer == null ) 
		{
			return hashKey2DefaultValue.get( key );
		}
		
		return buffer;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#setValue(Stringt, T)
	 */
	public void setValue( final String key,
			final T value ) {
		hashKey2Generic.put( key, value );
	}
	
	public void setValueAndDefaultValue(final String key, 
			final T value,
			final T defaultValue) {
		hashKey2Generic.put( key, value );
		hashKey2DefaultValue.put( key, defaultValue );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#setDefaultValue(Stringt, T)
	 */
	public void setDefaultValue( final String key,
			final T value ) {
		hashKey2DefaultValue.put( key, value );
		hashKey2Generic.put( key, value );
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#clear()
	 */
	public void clear() {		
		synchronized( getClass() ) {
			hashKey2DefaultValue.clear();
			hashKey2Generic.clear();
		}
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#containsValue(Stringt)
	 */
	public boolean containsValue( final String key ) {
		return hashKey2Generic.containsKey( key );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#containsDefaultValue(Stringt)
	 */
	public boolean containsDefaultValue( final String key ) {
		return hashKey2DefaultValue.containsKey( key );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterKeyValueDefaultvalue#containsValueAndDefaultValue(Stringt)
	 */
	public boolean containsValueAndDefaultValue( final String key ) {
		if ( (hashKey2Generic.containsKey( key ))&&
				(hashKey2DefaultValue.containsKey( key )) ) {
			return true;
		}
		return false;
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
		
		String sBuffer;
		/* special case for first element... */
		if ( iter.hasMoreElements() ) 
		{
			sBuffer = iter.nextElement();
			strBuffer.append( sBuffer );
			strBuffer.append( "->");
			strBuffer.append( hashKey2Generic.get( sBuffer ) );
		}
		
		while ( iter.hasMoreElements() )
		{
			sBuffer = iter.nextElement();
			strBuffer.append( " " );
			strBuffer.append( sBuffer );
			strBuffer.append( "->");
			strBuffer.append( hashKey2Generic.get( sBuffer ) );
		}
		
		return strBuffer.toString();
	}

}
