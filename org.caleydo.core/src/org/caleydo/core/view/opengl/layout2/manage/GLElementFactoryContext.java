/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.layout2.GLElement;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * the generic context to build {@link GLElement} using {@link GLElementFactories}
 *
 * @author Samuel Gratzl
 *
 */
public class GLElementFactoryContext {
	/**
	 * data to show
	 */
	private final ImmutableList<TablePerspective> datas;
	/**
	 * internal key prefix
	 */
	private final String prefix;
	/**
	 * generic arguments mappings
	 */
	private final ClassToInstanceMap<Object> objects;
	/**
	 * named arguments
	 */
	private final ImmutableMap<String, Object> namedObjects;

	private GLElementFactoryContext(ImmutableList<TablePerspective> datas, String prefix,
			ClassToInstanceMap<Object> objects, ImmutableMap<String, Object> namedObjects) {
		this.datas = datas;
		this.prefix = prefix;
		this.objects = objects;
		this.namedObjects = namedObjects;
	}

	/**
	 * returns a view on the current context with an addition prefix to all keyss
	 *
	 * @param prefix
	 * @return
	 */
	public GLElementFactoryContext sub(String prefix) {
		return new GLElementFactoryContext(datas, this.prefix + prefix, objects, namedObjects);
	}

	public TablePerspective getData() {
		return datas.size() > 0 ? datas.get(0) : null;
	}

	public List<TablePerspective> getDatas() {
		return datas;
	}

	/**
	 * returns a generic object, it it is set otherwise reutrn the given default value
	 *
	 * @param clazz
	 * @param default_
	 * @return
	 */
	public <T> T get(Class<T> clazz, T default_) {
		return get(null, clazz, default_);
	}

	/**
	 * similar to {@link #get(Class, Object)} but this time using a {@link Supplier} to avoid useless instantiations
	 *
	 * @param clazz
	 * @param default_
	 * @return
	 */
	public <T> T get(Class<T> clazz, Supplier<T> default_) {
		return get(null, clazz, default_);
	}

	/**
	 * returns a named object identified by its key rest see {@link #get(Class, Object)}
	 *
	 * @param key
	 * @param clazz
	 * @param default_
	 * @return
	 */
	public <T> T get(String key, Class<T> clazz, T default_) {
		return get(key, clazz, Suppliers.ofInstance(default_));
	}

	public <T> T get(String key, Class<T> clazz, Supplier<T> default_) {
		// unnamed
		if (StringUtils.isBlank(key)) {
			if (objects.containsKey(clazz))
				return objects.getInstance(clazz);
			return default_ == null ? null : default_.get();
		}

		// named
		key = prefix + key;
		if (namedObjects.containsKey(key)) {
			Object o = namedObjects.get(key);
			if (clazz.isInstance(o))
				return clazz.cast(o);
		}
		return default_ == null ? null : default_.get();
	}

	/**
	 * shortcut for setting if a named boolean argument is set
	 *
	 * @param key
	 * @return
	 */
	public boolean is(String key) {
		return is(key, false);
	}

	public boolean is(String key, boolean default_) {
		Boolean r = get(key, Boolean.class, default_);
		return r == null ? false : r.booleanValue();
	}

	public int getInt(String key, int default_) {
		Integer r = get(key, Integer.class, Integer.valueOf(default_));
		return r == null ? default_ : r.intValue();
	}

	public float getFloat(String key) {
		return getFloat(key, Float.NaN);
	}

	public float getFloat(String key, float default_) {
		Float r = get(key, Float.class, default_);
		return r == null ? default_ : r.floatValue();
	}

	/**
	 *
	 * @return
	 */
	public static Builder builder() {
		return new Builder();
	}


	public static class Builder {
		private final ImmutableList.Builder<TablePerspective> datas = ImmutableList.builder();
		private String prefix = "";
		private final ImmutableClassToInstanceMap.Builder<Object> objects = ImmutableClassToInstanceMap.builder();
		private final ImmutableMap.Builder<String, Object> namedObjects = ImmutableMap.builder();

		public Builder() {

		}

		public Builder usePrefix(String prefix) {
			this.prefix = StringUtils.trimToEmpty(prefix);
			return this;
		}

		public Builder withData(TablePerspective... data) {
			datas.add(data);
			return this;
		}

		public Builder withData(Iterable<TablePerspective> data) {
			datas.addAll(data);
			return this;
		}

		public <T> Builder put(Class<T> clazz, T value) {
			objects.put(clazz, value);
			return this;
		}

		public Builder put(String key, Object value) {
			namedObjects.put(key, value);
			return this;
		}

		public Builder set(String key) {
			return set(key, true);
		}

		public Builder set(String key, boolean value) {
			return put(key, Boolean.valueOf(value));
		}

		public Builder set(String key, int value) {
			return put(key, value);
		}

		public Builder set(String key, float value) {
			return put(key, value);
		}

		public GLElementFactoryContext build() {
			return new GLElementFactoryContext(datas.build(), prefix, objects.build(), namedObjects.build());
		}

	}
}