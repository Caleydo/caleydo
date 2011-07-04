package org.caleydo.core.parser.parameter;

import java.util.Hashtable;

/**
 * @author Michael Kalkusch
 */
public final class ParameterKeyValueDataAndDefault<T> {

	private Hashtable<String, T> hashKey2Generic;

	private Hashtable<String, T> hashKey2DefaultValue;

	public ParameterKeyValueDataAndDefault() {

		hashKey2Generic = new Hashtable<String, T>();

		hashKey2DefaultValue = new Hashtable<String, T>();
	}

	public T getValue(final String key) {

		return hashKey2Generic.get(key);
	}

	public void setValueAndDefaultValue(final String key, final T value, final T defaultValue) {

		hashKey2Generic.put(key, value);
		hashKey2DefaultValue.put(key, defaultValue);
	}

	public void setDefaultValue(final String key, final T value) {

		hashKey2DefaultValue.put(key, value);
		hashKey2Generic.put(key, value);
	}
}
