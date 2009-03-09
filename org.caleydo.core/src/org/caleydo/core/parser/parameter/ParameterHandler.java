/**
 * 
 */
package org.caleydo.core.parser.parameter;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.data.ParameterKeyValueDataAndDefault;

/**
 * Handles attributes from XML file used to create objects.
 * 
 * @author Michael Kalkusch
 */
public final class ParameterHandler
	extends AParameterHandler {

	private Hashtable<String, ParameterHandlerType> hashPrimarySwitch;

	private IParameterKeyValuePair<Integer> hashKey2Integer;

	private IParameterKeyValuePair<Float> hashKey2Float;

	private IParameterKeyValuePair<String> hashKey2String;

	private IParameterKeyValuePair<Boolean> hashKey2Boolean;

	/**
	 * 
	 */
	public ParameterHandler() {

		hashPrimarySwitch = new Hashtable<String, ParameterHandlerType>();

		hashKey2Integer = new ParameterKeyValueDataAndDefault<Integer>();
		hashKey2Float = new ParameterKeyValueDataAndDefault<Float>();
		hashKey2String = new ParameterKeyValueDataAndDefault<String>();
		hashKey2Boolean = new ParameterKeyValueDataAndDefault<Boolean>();
	}

	public void clear() {

		synchronized (getClass()) {
			hashPrimarySwitch.clear();
			hashKey2Integer.clear();
			hashKey2Float.clear();
			hashKey2String.clear();
			hashKey2Boolean.clear();
		}
	}

	@Override
	public Object getValue(final String key) {

		ParameterHandlerType type = hashPrimarySwitch.get(key);

		switch (type) {
			case BOOL:
				return getValueBoolean(key);
			case FLOAT:
				return getValueFloat(key);
			case INT:
				return getValueInt(key);
			case STRING:
				return getValueString(key);
			default:
				throw new IllegalArgumentException("ParameterHandler.getValue(" + key
					+ ") uses unregistered enumeration!");
		}
	}

	@Override
	public ParameterHandlerType getValueType(final String key) {

		return hashPrimarySwitch.get(key);
	}

	@Override
	public int getValueInt(final String key) {
		return hashKey2Integer.getValue(key);
	}

	@Override
	public Vec3f getValueVec3f(final String key) {

		if (key == null) {
			return new Vec3f();
		}

		return new Vec3f(hashKey2Float.getValue(key + "_GL0"), hashKey2Float.getValue(key + "_GL1"),
			hashKey2Float.getValue(key + "_GL2"));

	}

	@Override
	public Vec4f getValueVec4f(final String key) {

		if (key == null) {
			return new Vec4f();
		}

		return new Vec4f(hashKey2Float.getValue(key + "_GLROT0"), hashKey2Float.getValue(key + "_GLROT1"),
			hashKey2Float.getValue(key + "_GLROT2"), hashKey2Float.getValue(key + "_GLROT3"));

	}

	@Override
	public float getValueFloat(final String key) {

		return hashKey2Float.getValue(key);
	}

	@Override
	public String getValueString(final String key) {

		return hashKey2String.getValue(key);
	}

	@Override
	public boolean getValueBoolean(final String key) {

		return hashKey2Boolean.getValue(key);
	}

	@Override
	public void setValue(final String key, final String value) {

		ParameterHandlerType currentType = hashPrimarySwitch.get(key);

		if (currentType == null) {
			throw new IllegalArgumentException("ParameterHandler.setValue(" + key
				+ " , * ) key was not registered as type!");
		}

		try {
			switch (currentType) {
				case BOOL:
					hashKey2Boolean.setValue(key, Boolean.valueOf(value));
					break;
				case FLOAT:
					hashKey2Float.setValue(key, Float.valueOf(value));
					break;
				case INT:
					hashKey2Integer.setValue(key, Integer.valueOf(value));
					break;
				case STRING:
					hashKey2String.setValue(key, value);
					break;

				default:
					throw new IllegalArgumentException("ParameterHandler.setValue(" + key
						+ ",*) uses unregistered enumeration!");
			}

		}
		catch (NumberFormatException nfe) {
			new IllegalStateException("ParameterHandler.setValue(" + key + "," + value
				+ ") value was not valid due to enumeration type=" + currentType.toString() + " !");

		}
	}

	@Override
	public void setValueAndType(final String key, final String value, final ParameterHandlerType type) {

		try {
			switch (type) {
				case BOOL:
					hashKey2Boolean.setValue(key, Boolean.valueOf(value));
					break;
				case FLOAT:
					hashKey2Float.setValue(key, Float.valueOf(value));
					break;
				case INT:
					hashKey2Integer.setValue(key, Integer.valueOf(value));
					break;
				case STRING:
					hashKey2String.setValue(key, value);
					break;

				default:
					throw new IllegalArgumentException("ParameterHandler.setValueAndType(" + key
						+ ") uses unregistered enumeration!");
			}
		}
		catch (NumberFormatException nfe) {
			new IllegalArgumentException("ParameterHandler.setValueAndType(" + key + "," + value
				+ ") value was not valid due to enumeration type=" + type.toString() + " !");

		}

		hashPrimarySwitch.put(key, type);

	}

	@Override
	public void setValueAndTypeAndDefault(final String key, final String value,
		final ParameterHandlerType type, final String defaultValue) {

		try {
			switch (type) {
				case BOOL:
					try {
						hashKey2Boolean.setValueAndDefaultValue(key, Boolean.valueOf(value), Boolean
							.valueOf(defaultValue));
					}
					catch (NumberFormatException nfe) {
						hashKey2Boolean.setValueAndDefaultValue(key, Boolean.valueOf(defaultValue), Boolean
							.valueOf(defaultValue));
					}
					break;

				case FLOAT:
					try {
						hashKey2Float.setValueAndDefaultValue(key, Float.valueOf(value), Float.valueOf(defaultValue));
					}
					catch (NumberFormatException nfe) {
						hashKey2Float.setValueAndDefaultValue(key, Float.valueOf(defaultValue), Float
							.valueOf(defaultValue));
					}
					break;

				case INT:
					try {
						hashKey2Integer.setValueAndDefaultValue(key, Integer.valueOf(value), Integer
							.valueOf(defaultValue));
					}
					catch (NumberFormatException nfe) {
						hashKey2Integer.setValueAndDefaultValue(key, Integer.valueOf(defaultValue), Integer
							.valueOf(defaultValue));
					}
					break;

				case STRING:
					if (key.length() > 0) {
						hashKey2String.setValueAndDefaultValue(key, value, defaultValue);
					}
					else {
						hashKey2String.setValueAndDefaultValue(key, defaultValue, defaultValue);
					}
					break;

				case VEC3F: {
					StringTokenizer tokenizer = new StringTokenizer(value, IGeneralManager.sDelimiter_Parser_DataItems);

					if (tokenizer.countTokens() != 3) {
						throw new IllegalArgumentException("Error in parameter " + key + "=[" + value
							+ "] needs three float values!");
					} // if

					for (int i = 0; tokenizer.hasMoreTokens(); i++) {
						hashKey2Float.setValue(key + "_GL" + i, Float.valueOf(tokenizer.nextToken()));
					} // for

				} // case
					break;

				case VEC4F: {
					StringTokenizer tokenizer = new StringTokenizer(value, IGeneralManager.sDelimiter_Parser_DataItems);

					if (tokenizer.countTokens() != 4) {
						throw new IllegalArgumentException("Error in parameter " + key + "=[" + value
							+ "] needs four float values!");
					} // if

					for (int i = 0; tokenizer.hasMoreTokens(); i++) {
						hashKey2Float.setValue(key + "_GLROT" + i, Float.valueOf(tokenizer.nextToken()));
					} // for

				} // case
					break;

				default:
					throw new IllegalStateException("ParameterHandler.setValueAndType(" + key
						+ ") uses unregistered enumeration!");
			}
		}
		catch (NumberFormatException nfe) {
			new IllegalStateException("ParameterHandler.setValueAndTypeAndDefault(" + key + "," + defaultValue
				+ ") defaultValue was not valid due to enumeration type=" + type.toString() + " !");

		}

		hashPrimarySwitch.put(key, type);
	}

	@Override
	public void setDefaultValueAnyType(final String key, final String value, final ParameterHandlerType type) {

		try {
			switch (type) {
				case BOOL:
					hashKey2Boolean.setDefaultValue(key, Boolean.valueOf(value));
					break;
				case FLOAT:
					hashKey2Float.setDefaultValue(key, Float.valueOf(value));
					break;
				case INT:
					hashKey2Integer.setDefaultValue(key, Integer.valueOf(value));
					break;
				case STRING:
					hashKey2String.setDefaultValue(key, value);
					break;

				default:
					throw new IllegalArgumentException("ParameterHandler.setValueAndType(" + key
						+ ") uses unregistered enumeration!");
			}
		}
		catch (NumberFormatException nfe) {
			new IllegalStateException("ParameterHandler.setValueAndType(" + key + "," + value
				+ ") value was not valid due to enumeration type=" + type.toString() + " !");

		}

		hashPrimarySwitch.put(key, type);

	}

	@Override
	public void setDefaultType(final String key, final ParameterHandlerType type) {

		hashPrimarySwitch.put(key, type);
	}

	@Override
	public String toString() {

		StringBuffer strBuffer = new StringBuffer();

		strBuffer.append("B:");
		strBuffer.append(hashKey2Boolean.toString());
		strBuffer.append("\n");

		strBuffer.append(" I:");
		strBuffer.append(hashKey2Integer.toString());
		strBuffer.append("\n");

		strBuffer.append(" F:");
		strBuffer.append(hashKey2Float.toString());
		strBuffer.append("\n");

		strBuffer.append(" S:");
		strBuffer.append(hashKey2String.toString());

		return strBuffer.toString();
	}
}
