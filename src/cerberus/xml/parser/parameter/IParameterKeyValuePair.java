package cerberus.xml.parser.parameter;

public interface IParameterKeyValuePair<T>
{

	public abstract T getValue(final String key);

	public abstract T getDefaultValue(final String key);

	public abstract T getValueOrDefault(final String key);

	public abstract void setValue(final String key, final T value);
	
	public abstract void setValueAndDefaultValue(final String key, 
			final T value,
			final T defaultValue);

	public abstract void setDefaultValue(final String key, final T value);

	public abstract void clear();

	public abstract boolean containsValue(final String key);

	public abstract boolean containsDefaultValue(final String key);

	public abstract boolean containsValueAndDefaultValue(final String key);

	public int size();
	
	public boolean isEmpty();
}