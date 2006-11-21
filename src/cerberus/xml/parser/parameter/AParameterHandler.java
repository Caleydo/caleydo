/**
 * 
 */
package cerberus.xml.parser.parameter;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.xml.sax.Attributes;

import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.IParameterKeyValuePair;
import cerberus.xml.parser.parameter.IParameterHandler.ParameterHandlerType;
import cerberus.xml.parser.parameter.data.ParameterKeyValueDataAndDefault;

/**
 * Handles attributes from XML file used to create objects.
 * 
 * @author java
 *
 */
public abstract class AParameterHandler 
implements IParameterHandler {

	/**
	 * 
	 */
	protected AParameterHandler()
	{
		
	}

	
	public final void setDefaultValue( final String key, 
			final String value, 
			final String type ) {
		try 
		{
			setDefaultValueAnyType( key, value, ParameterHandlerType.valueOf( type ) );
		} 
		catch ( NumberFormatException nfe ) 
		{
			new CerberusRuntimeException("ParameterHandler.setDefaultTypeAsString("+ key + "," + type + 
					") no valid enumeration type!");
			
		}
	}
	
	public final void setDefaultTypeAsString( final String key, 
			final String type ) {
		
		try 
		{
			setDefaultType( key, ParameterHandlerType.valueOf( type ) );
		} 
		catch ( NumberFormatException nfe ) 
		{
			new CerberusRuntimeException("ParameterHandler.setDefaultTypeAsString("+ key + "," + type + 
					") no valid enumeration type!");
			
		}
	}
	
	public final void setDefaultTypeByArray( final String[] keys, 
			final String[] defaultVales, 
			final String[] types) {
		
		
		
	}
	
	public final void setDefaultTypeByVector( final Vector <String> keys, 
			final Vector <String> defaultVales, 
			final Vector <String> types) {
		
		int iMinSize = keys.size();
		
		if ( defaultVales.size() < iMinSize ) 
		{
			iMinSize = defaultVales.size();
		}
		if ( types.size() < iMinSize ) 
		{
			iMinSize = types.size();
		}
		
		Iterator <String> iterKey = keys.iterator();
		Iterator <String> iterDefaultValue = defaultVales.iterator();
		Iterator <String> iterType = types.iterator();
		
		
		for (int iIndex = 0; iIndex < iMinSize; iIndex++ ) 
		{
			setDefaultValue( iterKey.next(),
					iterDefaultValue.next(),
					iterType.next() );
		}
	}
	
		
	public final void setValueBySaxAttributes( final Attributes attrs,
			final String key,
			final String sDefaultValue,
			final ParameterHandlerType type	) {
		
		assert sDefaultValue != null : "default value must not be null!";
		
		String value = attrs.getValue( key );
		
		if ( value == null  ) {
			setValueAndTypeAndDefault( key, sDefaultValue, type, sDefaultValue );
			return;
		}
		
		setValueAndTypeAndDefault( key, value, type, sDefaultValue );
	}
	
}
