/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.util.ClassUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * space specific part of a picking manager see {@link PickingManager2}
 *
 * registration: {@link #addPickingListener(Object, int, IPickingListener)} or
 * {@link #addTypePickingListener(Object, IPickingListener)} or {@link #register(Object)} using annotation
 *
 * querying: {@link #getPickingID(Object, int)}
 *
 * usage: one {@link SpacePickingManager} per view
 *
 * @author Samuel Gratzl
 *
 */
public class SpacePickingManager {
	/**
	 * mapping from key: type,id to their state
	 */
	private Map<PickingKey, PickingManager2.PickingEntry> mapping = new HashMap<>();
	/**
	 * mapping for the generic version where just the type is queried
	 */
	private Map<Object, PickingListenerComposite> typeListeners = new HashMap<>();

	private final PickingManager2 parent;

	SpacePickingManager(PickingManager2 parent) {
		this.parent = parent;
	}

	/**
	 * returns the pickignID used for glPushName for a given key pair
	 *
	 * @param type
	 * @param objectId
	 * @return
	 */
	public int getPickingID(Object type, int objectId) {
		return get(type, objectId).pickingId;
	}

	private PickingManager2.PickingEntry get(Object type, int objectId) {
		PickingKey key = new PickingKey(type, objectId);
		PickingManager2.PickingEntry id = mapping.get(key);
		if (id == null) {
			id = parent.createEntry(objectId, getType(type));
			mapping.put(key, id);
		}
		return id;
	}

	private PickingListenerComposite getType(Object type) {
		PickingListenerComposite typeListener = typeListeners.get(type);
		if (typeListener == null) {
			typeListener = new PickingListenerComposite(1);
			typeListeners.put(type, typeListener);
		}
		return typeListener;
	}

	/**
	 * adds a picking listener for a given object type
	 *
	 * @param type
	 * @param l
	 */
	public void addTypePickingListener(Object type, IPickingListener l) {
		getType(type).add(l);
	}

	/**
	 * remove a picking listener for a given object type
	 *
	 * @param type
	 * @param l
	 */
	public boolean removeTypePickingListener(Object type, IPickingListener l) {
		return getType(type).remove(type);
	}

	/**
	 * removes all picking listeners for a given type
	 *
	 * @param type
	 * @param l
	 */
	public boolean removeTypePickingListeners(Object type) {
		return typeListeners.remove(type) != null;
	}

	/**
	 * adds an object id specific picking listener
	 *
	 * @param type
	 * @param id
	 * @param l
	 * @return the picking id of this key
	 */
	public int addPickingListener(Object type, int id, IPickingListener l) {
		PickingManager2.PickingEntry entry = get(type, id);
		entry.add(l);
		return entry.pickingId;
	}

	/**
	 * removes an object id specific picking listener
	 *
	 * @param type
	 * @param id
	 * @param l
	 * @return
	 */
	public boolean removePickingListener(Object type, int id, IPickingListener l) {
		PickingManager2.PickingEntry entry = get(type, id);
		boolean r = entry.remove(l);
		if (entry.isEmpty()) {
			removePickingListeners(type, id);
		}
		return r;
	}

	/**
	 * removes all picking listener for a given object id
	 *
	 * @param type
	 * @param id
	 * @param l
	 * @return
	 */
	public boolean removePickingListeners(Object type, int id) {
		PickingManager2.PickingEntry entry = mapping.remove(new PickingKey(type, id));
		if (entry != null) {
			parent.remove(entry);
			return true;
		}
		return false;
	}

	/**
	 * removes all type listeners and id type listeners for a given type
	 *
	 * @param type
	 */
	public void removeAllPickingListeners(Object type) {
		removeTypePickingListeners(type);
		// check all ids
		for (Iterator<Map.Entry<PickingKey, PickingManager2.PickingEntry>> it = mapping.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<PickingKey, PickingManager2.PickingEntry> entry = it.next();
			if (entry.getKey().type.equals(type)) {
				parent.remove(entry.getValue());
				it.remove();
			}
		}
	}

	/**
	 * scans the given object for {@link OnPick} annotation and register handler for it
	 *
	 * @param listener
	 */
	public void register(Object listener) {
		for (Method m : Iterables.filter(ClassUtils.findAllDeclaredMethods(listener.getClass()), matches)) {
			OnPick annotation = m.getAnnotation(OnPick.class);
			AnnotationBasedPickingListener l = new AnnotationBasedPickingListener(annotation.mode(), listener, m);
			if (annotation.id() == -1) {
				addTypePickingListener(annotation.value(), l);
			} else {
				addPickingListener(annotation.value(), annotation.id(), l);
			}
		}
	}

	/**
	 * reverse operation of {@link #register(Object)}
	 *
	 * @param listener
	 */
	public void unregister(Object listener) {
		for (PickingListenerComposite p : typeListeners.values()) {
			for (Iterator<IPickingListener> it = p.iterator(); it.hasNext();) {
				IPickingListener value = it.next();
				if (value instanceof AnnotationBasedPickingListener
						&& ((AnnotationBasedPickingListener) value).listener == listener) {
					it.remove();
				}
			}
		}
		for (PickingManager2.PickingEntry p : mapping.values()) {
			for (Iterator<IPickingListener> it = p.iterator(); it.hasNext();) {
				IPickingListener value = it.next();
				if (value instanceof AnnotationBasedPickingListener
						&& ((AnnotationBasedPickingListener) value).listener == listener)
					it.remove();
			}
		}
	}

	private static final Predicate<Method> matches = new Predicate<Method>() {
		@Override
		public boolean apply(Method m) {
			if (!m.isAnnotationPresent(OnPick.class) || m.getReturnType() != void.class)
				return false;
			Class<?>[] params = m.getParameterTypes();
			return params.length == 0 || (params.length == 1 && Pick.class.isAssignableFrom(params[0]));
		}
	};

	/**
	 * annotation to specify that the method should be called for a picking event on a defined element
	 *
	 * e.g. <code>
	 *
	 * @OnPick(value="ADD_TO_STRATOMEX",id=5,mode=PickingMode.CLICKED) private void onClick(Pick pick) { } </code>
	 *
	 * @author Samuel Gratzl
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Documented
	public @interface OnPick {
		/**
		 * the pickingType to listen to
		 *
		 * @return
		 */
		String value();

		/**
		 * the object id to restrict if the default -1 is used, a type listener will be created
		 *
		 * @return
		 */
		int id() default -1;

		/**
		 * the modes to listen to, zero = all, one ore more,
		 *
		 * @return
		 */
		PickingMode[] mode() default {};
	}

	private static class AnnotationBasedPickingListener implements IPickingListener {
		private final Set<PickingMode> modes;
		private final Object listener;
		private final Method method;
		private final boolean needsPickArg;

		public AnnotationBasedPickingListener(PickingMode[] modes, Object listener, Method method) {
			this.modes = modes.length == 0 ? EnumSet.noneOf(PickingMode.class) : EnumSet.of(modes[0], modes);
			this.listener = listener;
			this.method = method;
			this.needsPickArg = method.getParameterTypes().length == 1;
		}

		@Override
		public void pick(Pick pick) {
			if (!modes.isEmpty() && !modes.contains(pick.getPickingMode()))
				return; // skip uninteresting
			try {
				method.setAccessible(true);
				if (needsPickArg)
					method.invoke(listener, pick);
				else
					method.invoke(listener);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				System.err.println(e);
			}
		}
	}

	private static class PickingKey {
		private final Object type;
		private final int id;

		public PickingKey(Object type, int id) {
			this.type = type;
			this.id = id;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PickingKey other = (PickingKey) obj;
			if (id != other.id)
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}
}
