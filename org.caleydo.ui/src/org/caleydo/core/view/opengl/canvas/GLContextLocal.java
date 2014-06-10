/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.media.opengl.GLContext;

import org.caleydo.core.util.execution.SafeCallable;

import com.google.common.base.Supplier;
import com.google.common.collect.MapMaker;

/**
 * a local variable that depends on the current active {@link GLContext} using {@link GLContext#getCurrent()}, similar
 * to {@link ThreadLocal}
 *
 * @author Samuel Gratzl
 *
 */
public class GLContextLocal<T> implements Supplier<T> {
	private static final String ATTACH_KEY = "contextlocals";
	/**
	 * special map if no context is available
	 */
	private static final Map<GLContextLocal<?>, Object> noContext = new WeakHashMap<>(5);

	/**
	 * shared area with soft references to {@link GLContextLocal} locals
	 */
	private static final ConcurrentMap<String, GLContextLocal<Object>> shared = new MapMaker().softValues().makeMap();

	private final SafeCallable<T> initializer;

	public GLContextLocal() {
		this(null);
	}
	/**
	 * Creates a thread local variable.
	 */
	public GLContextLocal(SafeCallable<T> initializer) {
		this.initializer = initializer;
	}

	/**
	 * returns a weakly shared {@link GLContextLocal} variable, that means if none has a reference on this
	 * {@link GLContextLocal} instance it may be garbage collected
	 *
	 * @param key
	 * @param initializer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> GLContextLocal<T> getOrCreateShared(String key, SafeCallable<T> initializer) {
		// create one or use the old one
		shared.putIfAbsent(key, (GLContextLocal<Object>) new GLContextLocal<>(initializer));
		return (GLContextLocal<T>) shared.get(key);
	}

	/**
	 * override in sub class to provide an initial value that should be used;
	 *
	 * @return
	 */
	protected T initialValue() {
		return initializer != null ? initializer.call() : null;
	}

	/**
	 * returns the context local version, initialized using {@link #initialValue()}
	 *
	 * @return
	 */
	@Override
	public final T get() {
		Map<GLContextLocal<T>, T> map = getMap();
		T r;
		if (map != null && map.containsKey(this)) {
			r = map.get(this);
		} else {
			r = setInitialValue();
		}
		return r;
	}

	private T setInitialValue() {
		T value = initialValue();
		Map<GLContextLocal<T>, T> map = getMap();
		if (map != null)
			map.put(this, value);
		else
			createMap(value);
		return value;
	}

	/**
	 * sets the context local value
	 *
	 * @param value
	 */
	public final void set(T value) {
		Map<GLContextLocal<T>, T> map = getMap();
		if (map != null)
			map.put(this, value);
		else
			createMap(value);
	}

	/**
	 * removes the value
	 */
	public final void remove() {
		Map<GLContextLocal<T>, T> m = getMap();
		if (m != null)
			m.remove(this);
	}

	@SuppressWarnings("unchecked")
	private Map<GLContextLocal<T>, T> getMap() {
		GLContext current = GLContext.getCurrent();
		Map<?, ?> r;
		if (current == null)
			r = noContext;
		else
			r = (Map<?, ?>) current.getAttachedObject(ATTACH_KEY);
		return (Map<GLContextLocal<T>, T>) r;
	}

	private void createMap(T firstValue) {
		GLContext current = GLContext.getCurrent();
		if (current == null)
			noContext.put(this, firstValue);
		else {
			Map<GLContextLocal<T>, T> map = new WeakHashMap<>(5);
			current.attachObject(ATTACH_KEY, map);
			map.put(this, firstValue);
		}
	}
}
