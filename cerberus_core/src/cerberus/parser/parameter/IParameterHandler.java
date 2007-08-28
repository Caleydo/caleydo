package cerberus.parser.parameter;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.util.Vector;

import org.xml.sax.Attributes;

public interface IParameterHandler {

	public enum ParameterHandlerType {
		
		BOOL(),
		INT(),
		FLOAT(),
		STRING(),
		VEC3F(),
		VEC4F();
		
		private ParameterHandlerType() {
			
		}
	}

	public abstract Object getValue(final String key);

	public abstract ParameterHandlerType getValueType(final String key);

	public abstract int getValueInt(final String key);

	public abstract float getValueFloat(final String key);

	public abstract String getValueString(final String key);

	public abstract boolean getValueBoolean(final String key);
	
	public Vec3f getValueVec3f( final String key );

	public Vec4f getValueVec4f( final String key );

	public abstract void setValue(final String key, final String value);

	public abstract void setValueAndType(final String key, final String value,
			final ParameterHandlerType type);

	/**
	 * Set key, value its type and a default value.
	 * If value could not be converted to type the default value is assigned.
	 * some impelmentations store the defautl value in addition to the 'data' value.
	 * 
	 * @param key of the pair (key,value)
	 * @param value  of the pair (key,value)
	 * @param type of of the data
	 * @param defaultValue default value in case the value can not be converted to the requested type
	 */
	public void setValueAndTypeAndDefault( final String key, 
			final String value, 
			final ParameterHandlerType type,
			final String defaultValue ) ;
	
	public void setValueBySaxAttributes( final Attributes attrs,
			final String key,
			final String sDefaultValue,
			final ParameterHandlerType type );
	
	public abstract void setDefaultValueAnyType(final String key, final String value,
			final ParameterHandlerType type);

	public abstract void setDefaultType(final String key,
			final ParameterHandlerType type);
	
	
	public void setDefaultValue(final String key, final String value,
			final String type);

	public void setDefaultTypeAsString(final String key,
			final String type);
	
	
	/**
	 * register keys, defaultValues and thier types.
	 * 
	 * @param keys array of keys
	 * @param defaultVales array of default values bound to the keys
	 * @param types array of types for each key
	 */
	public void setDefaultTypeByArray( final String[] keys, 
			final String[] defaultVales, 
			final String[] types);
	
	public void setDefaultTypeByVector( final Vector <String> keys, 
			final Vector <String> defaultVales, 
			final Vector <String> types);
	
	public void clear();

}