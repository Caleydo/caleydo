/**
 * 
 */
package cerberus.xml.parser.parameter;

import java.util.Hashtable;
import java.util.StringTokenizer;

//import org.xml.sax.Attributes;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.exception.CerberusExceptionType;
//import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.AParameterHandler;
import cerberus.xml.parser.parameter.IParameterKeyValuePair;
//import cerberus.xml.parser.parameter.IParameterHandler.ParameterHandlerType;
import cerberus.xml.parser.parameter.data.ParameterKeyValueDataAndDefault;

/**
 * Handles attributes from XML file used to create objects.
 * 
 * @author Michael Kalkusch
 *
 */
public final class ParameterHandler
extends AParameterHandler
implements IParameterHandler
{

	private Hashtable <String, ParameterHandlerType > hashPrimarySwitch;
	
	
	private IParameterKeyValuePair <Integer> hashKey2Integer;
	
	private IParameterKeyValuePair <Float> hashKey2Float;
	
	private IParameterKeyValuePair <String> hashKey2String;
	
	private IParameterKeyValuePair <Boolean> hashKey2Boolean;
	
	
	/**
	 * 
	 */
	public ParameterHandler()
	{
		hashPrimarySwitch = new Hashtable <String, ParameterHandlerType > ();
		
		hashKey2Integer = new ParameterKeyValueDataAndDefault <Integer> ();
		hashKey2Float = new ParameterKeyValueDataAndDefault <Float> ();
		hashKey2String = new ParameterKeyValueDataAndDefault <String> ();
		hashKey2Boolean = new ParameterKeyValueDataAndDefault <Boolean> ();
	}

	
	public void clear() {
		
		synchronized( getClass() ) {
			hashPrimarySwitch.clear();
			hashKey2Integer.clear();
			hashKey2Float.clear();
			hashKey2String.clear();
			hashKey2Boolean.clear();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#getValue(java.lang.String)
	 */
	public Object getValue( final String key ) {
		
		ParameterHandlerType type = hashPrimarySwitch.get( key );
		
		switch ( type ) {
			case BOOL:
				return getValueBoolean(key);
			case FLOAT:
				return getValueFloat(key);
			case INT:
				return getValueInt(key);
			case STRING:
				return getValueString(key);
			
			default:
				throw new CerberusRuntimeException("ParameterHandler.getValue("+ key + ") uses unregistered enumeration!");
		}
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#getValueType(java.lang.String)
	 */
	public ParameterHandlerType getValueType( final String key ) {		
		return hashPrimarySwitch.get( key );
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#getValueInt(java.lang.String)
	 */
	public int getValueInt( final String key ) {
		try {
			return hashKey2Integer.getValue(key);
		}
		catch ( NullPointerException npe) 
		{
			throw new CerberusRuntimeException("getValueInt("+ key + ") failed, because key was not registered!");
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#getValueInt(java.lang.String)
	 */
	public Vec3f getValueVec3f( final String key ) {
		try {
			return new Vec3f( hashKey2Float.getValue(key+"_GL0"),
					hashKey2Float.getValue(key+"_GL1"),
					hashKey2Float.getValue(key+"_GL2") );
		}
		catch ( NullPointerException npe) 
		{
			throw new CerberusRuntimeException("getValueInt("+ key + ") failed, because key was not registered!");
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#getValueInt(java.lang.String)
	 */
	public Vec4f getValueVec4f( final String key ) {
		try {
			return new Vec4f( 
					hashKey2Float.getValue(key+"_GLROT0"),
					hashKey2Float.getValue(key+"_GLROT1"),
					hashKey2Float.getValue(key+"_GLROT2"),
					hashKey2Float.getValue(key+"_GLROT3") );
		}
		catch ( NullPointerException npe) 
		{
			throw new CerberusRuntimeException("getValueInt("+ key + ") failed, because key was not registered!");
		}
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#getValueFloat(java.lang.String)
	 */
	public float getValueFloat( final String key ) {
		return hashKey2Float.getValue(key);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#getValueString(java.lang.String)
	 */
	public String getValueString( final String key ) {
		return hashKey2String.getValue(key);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#getValueBoolean(java.lang.String)
	 */
	public boolean getValueBoolean( final String key ) {		
		return hashKey2Boolean.getValue(key);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#setValue(java.lang.String, java.lang.String)
	 */
	public void setValue( final String key, 
			final String value ) {
		
		ParameterHandlerType currentType = 
			hashPrimarySwitch.get( key );
		
		if ( currentType== null ) {
			throw new CerberusRuntimeException("ParameterHandler.setValue("+ key + " , * ) key was not registered as type!");			
		}
		
		try 
		{
			switch ( currentType ) {
				case BOOL:
					hashKey2Boolean.setValue(key, Boolean.valueOf(value) );break;
				case FLOAT:
					hashKey2Float.setValue(key, Float.valueOf(value) );break;
				case INT:
					hashKey2Integer.setValue(key, Integer.valueOf(value) );break;
				case STRING:
					hashKey2String.setValue(key, value );break;
				
				default:
					throw new CerberusRuntimeException("ParameterHandler.setValue("+ key + ",*) uses unregistered enumeration!");
			}
			
		} 
		catch ( NumberFormatException nfe ) 
		{
			new CerberusRuntimeException("ParameterHandler.setValue("+ key + "," + value + 
					") value was not valid due to enumeration type=" + 
					currentType.toString() + " !");
			
		}
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#setValueAndType(java.lang.String, java.lang.String, cerberus.xml.parser.parameter.ParameterHandler.ParameterHandlerType)
	 */
	public void setValueAndType( final String key, 
			final String value, 
			final ParameterHandlerType type ) {

		try {
			switch ( type ) {
				case BOOL:
					hashKey2Boolean.setValue(key, Boolean.valueOf(value) );break;
				case FLOAT:
					hashKey2Float.setValue(key, Float.valueOf(value) );break;
				case INT:
					hashKey2Integer.setValue(key, Integer.valueOf(value) );break;
				case STRING:
					hashKey2String.setValue(key, value );break;
				
				default:
					throw new CerberusRuntimeException("ParameterHandler.setValueAndType("+ key + ") uses unregistered enumeration!");
			}
		} 
		catch ( NumberFormatException nfe ) 
		{
			new CerberusRuntimeException("ParameterHandler.setValueAndType("+ key + "," + value + 
					") value was not valid due to enumeration type=" + 
					type.toString() + " !");
			
		}
		
		hashPrimarySwitch.put( key, type );
		
		
	}

	
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#setValueAndType(java.lang.String, java.lang.String, cerberus.xml.parser.parameter.ParameterHandler.ParameterHandlerType)
	 */
	public void setValueAndTypeAndDefault( final String key, 
			final String value, 
			final ParameterHandlerType type,
			final String defaultValue ) {

		try {
			switch ( type ) {
				case BOOL:
					try {
						hashKey2Boolean.setValueAndDefaultValue(
								key, 
								Boolean.valueOf(value),
								Boolean.valueOf(defaultValue) );
					} 
					catch ( NumberFormatException nfe ) 
					{
						hashKey2Boolean.setValueAndDefaultValue(
								key, 
								Boolean.valueOf(defaultValue),
								Boolean.valueOf(defaultValue) );
					}
					break;
					
					
				case FLOAT:
					try {
						hashKey2Float.setValueAndDefaultValue(key,
								Float.valueOf(value),
								Float.valueOf(defaultValue) );
					} 
					catch ( NumberFormatException nfe ) 
					{
						hashKey2Float.setValueAndDefaultValue(key,
								Float.valueOf(defaultValue),
								Float.valueOf(defaultValue) );
					}
					break;

					
				case INT:
					try {
						hashKey2Integer.setValueAndDefaultValue(key, 
								Integer.valueOf(value),
								Integer.valueOf(defaultValue) );
					} 
					catch ( NumberFormatException nfe ) 
					{
						hashKey2Integer.setValueAndDefaultValue(key, 
								Integer.valueOf(defaultValue),
								Integer.valueOf(defaultValue) );
					}
					break;
					
					
				case STRING:
					if ( key.length() > 0 ) {
						hashKey2String.setValueAndDefaultValue(key, 
								value,
								defaultValue );
					}
					else 
					{
						hashKey2String.setValueAndDefaultValue(key, 
								defaultValue,
								defaultValue );
					}
					break;
				
				case VEC3F:
				{
					StringTokenizer tokenizer = new StringTokenizer( value,
							IGeneralManager.sDelimiter_Parser_DataItems);
					
					if ( tokenizer.countTokens() != 3 ) {						
						throw new CerberusRuntimeException( 
								"Error in parameter " +
								key +
								"=[" + value + 
								"] needs three float values!",
								CerberusExceptionType.CONVERSION );
					} // if
					
					for ( int i=0; tokenizer.hasMoreTokens(); i++ ) 
					{
						hashKey2Float.setValue(key+"_GL" + i, 
								Float.valueOf(tokenizer.nextToken()) );
					} //for
					
				} // case
					break;
					
				case VEC4F:
				{
					StringTokenizer tokenizer = new StringTokenizer( value,
							IGeneralManager.sDelimiter_Parser_DataItems);
					
					if ( tokenizer.countTokens() != 4 ) {						
						throw new CerberusRuntimeException( 
								"Error in parameter " +
								key +
								"=[" + value + 
								"] needs four float values!",
								CerberusExceptionType.CONVERSION );
					} // if
					
					for ( int i=0; tokenizer.hasMoreTokens(); i++ ) 
					{
						hashKey2Float.setValue(key+"_GLROT" + i, 
								Float.valueOf(tokenizer.nextToken()) );
					} //for
					
				} // case
					break;
					
				default:
					throw new CerberusRuntimeException("ParameterHandler.setValueAndType("+ key + ") uses unregistered enumeration!");
			}
		} 
		catch ( NumberFormatException nfe ) 
		{
			new CerberusRuntimeException("ParameterHandler.setValueAndTypeAndDefault("+ key + "," + defaultValue + 
					") defaultValue was not valid due to enumeration type=" + 
					type.toString() + " !");
			
		}
		
		hashPrimarySwitch.put( key, type );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#setDefaultValue(java.lang.String, java.lang.String, cerberus.xml.parser.parameter.ParameterHandler.ParameterHandlerType)
	 */
	public void setDefaultValueAnyType( final String key, 
			final String value, 
			final ParameterHandlerType type ) {
		
		try {
			switch ( type ) {
				case BOOL:
					hashKey2Boolean.setDefaultValue(key, Boolean.valueOf(value) );break;
				case FLOAT:
					hashKey2Float.setDefaultValue(key, Float.valueOf(value) );break;
				case INT:
					hashKey2Integer.setDefaultValue(key, Integer.valueOf(value) );break;
				case STRING:
					hashKey2String.setDefaultValue(key, value );break;
				
				default:
					throw new CerberusRuntimeException("ParameterHandler.setValueAndType("+ key + ") uses unregistered enumeration!");
			}
		} 
		catch ( NumberFormatException nfe ) 
		{
			new CerberusRuntimeException("ParameterHandler.setValueAndType("+ key + "," + value + 
					") value was not valid due to enumeration type=" + 
					type.toString() + " !");
			
		}
		
		hashPrimarySwitch.put( key, type );
			
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.parameter.IParameterHandler#setDefaultType(java.lang.String, cerberus.xml.parser.parameter.ParameterHandler.ParameterHandlerType)
	 */
	public void setDefaultType( final String key, 
			final ParameterHandlerType type ) {
		
		hashPrimarySwitch.put( key, type );
	}
	
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		
		strBuffer.append("B:");
		strBuffer.append( hashKey2Boolean.toString() );
		strBuffer.append( "\n" );
		
		strBuffer.append(" I:");
		strBuffer.append( hashKey2Integer.toString() );
		strBuffer.append( "\n" );
		
		strBuffer.append(" F:");
		strBuffer.append( hashKey2Float.toString() );
		strBuffer.append( "\n" );
		
		strBuffer.append(" S:");
		strBuffer.append( hashKey2String.toString() );
		
		return strBuffer.toString();
	}
}
