/**
 * 
 */
package org.caleydo.core.parser.parameter;

import java.util.Enumeration;
import java.util.Hashtable;


/**
 * @author Michael Kalkusch
 */
public final class ParameterKeyValueDataAndDefault<T>
	implements IParameterKeyValuePair<T> {

	private Hashtable<String, T> hashKey2Generic;

	private Hashtable<String, T> hashKey2DefaultValue;

	/**
	 * 
	 */
	public ParameterKeyValueDataAndDefault() {

		hashKey2Generic = new Hashtable<String, T>();

		hashKey2DefaultValue = new Hashtable<String, T>();
	}

	@Override
	public T getValue(final String key) {

		return hashKey2Generic.get(key);
	}

	@Override
	public T getDefaultValue(final String key) {

		return hashKey2DefaultValue.get(key);
	}

	@Override
	public T getValueOrDefault(final String key) {

		T buffer = hashKey2Generic.get(key);

		if (buffer == null)
			return hashKey2DefaultValue.get(key);

		return buffer;
	}

	@Override
	public void setValue(final String key, final T value) {

		hashKey2Generic.put(key, value);
	}

	public void setValueAndDefaultValue(final String key, final T value, final T defaultValue) {

		hashKey2Generic.put(key, value);
		hashKey2DefaultValue.put(key, defaultValue);
	}

	@Override
	public void setDefaultValue(final String key, final T value) {

		hashKey2DefaultValue.put(key, value);
		hashKey2Generic.put(key, value);
	}

	@Override
	public void clear() {

		synchronized (getClass()) {
			hashKey2DefaultValue.clear();
			hashKey2Generic.clear();
		}
	}

	@Override
	public boolean containsValue(final String key) {

		return hashKey2Generic.containsKey(key);
	}

	@Override
	public boolean containsDefaultValue(final String key) {

		return hashKey2DefaultValue.containsKey(key);
	}

	@Override
	public boolean containsValueAndDefaultValue(final String key) {

		if (hashKey2Generic.containsKey(key) && hashKey2DefaultValue.containsKey(key))
			return true;
		return false;
	}

	@Override
	public int size() {

		return this.hashKey2Generic.size();
	}

	@Override
	public boolean isEmpty() {

		return this.hashKey2Generic.isEmpty();
	}

	@Override
	public String toString() {

		if (this.isEmpty())
			return "-";

		StringBuffer strBuffer = new StringBuffer();

		Enumeration<String> iter = hashKey2Generic.keys();

		String sBuffer;
		/* special case for first element... */
		if (iter.hasMoreElements()) {
			sBuffer = iter.nextElement();
			strBuffer.append(sBuffer);
			strBuffer.append("->");
			strBuffer.append(hashKey2Generic.get(sBuffer));
		}

		while (iter.hasMoreElements()) {
			sBuffer = iter.nextElement();
			strBuffer.append(" ");
			strBuffer.append(sBuffer);
			strBuffer.append("->");
			strBuffer.append(hashKey2Generic.get(sBuffer));
		}

		return strBuffer.toString();
	}

}
