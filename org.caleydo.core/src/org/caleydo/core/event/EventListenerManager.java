/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.caleydo.core.util.ClassUtils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Utility class to hold a list of event listeners to register and remove them all in an convenient way.
 *
 * @author Samuel Gratzl
 *
 */
public class EventListenerManager {
	private final Set<AEventListener<?>> listeners = new HashSet<>();

	protected final IListenerOwner owner;
	// cache for classes (cannoical name) that has no relevant methods
	private final Set<String> nothingFounds = new HashSet<>();

	EventListenerManager(IListenerOwner owner) {
		this.owner = owner;
	}

	/**
	 * Register the listener to the event type.
	 *
	 * @param event
	 *            class of event
	 * @param listener
	 */
	public void register(Class<? extends AEvent> event, AEventListener<?> listener) {
		assert listener.getHandler() == owner;
		listeners.add(listener);
		EventPublisher.INSTANCE.addListener(event, listener);
	}

	/**
	 * Filter all methods of the listener object for <code>
	 *
	 * @ListenTo void xxx(<? extends AEvent> event); </code>
	 *
	 *           and register an event listener for calling this method
	 *
	 * @param listener
	 */
	public final <T> T register(T listener) {
		return register(listener, null);
	}

	/**
	 * Filter all methods of the listener object for <code>
	 *
	 * @ListenTo void xxx(<? extends AEvent> event); </code>
	 *
	 *           and register an event listener for calling this method
	 *
	 * @param listener
	 * @param eventSpace
	 *            if {@link ListenTo#restrictToEventSpace()} or {@link ListenTo#restrictExclusiveToEventSpace()} is used
	 *            the eventSpace to set
	 */
	public final <T> T register(T listener, String eventSpace) {
		return register(listener, eventSpace, Predicates.alwaysTrue());
	}


	/**
	 * see {@link #register(Object, String)} but with an additional speedup criteria to support early stopping of
	 * scanning
	 *
	 * @param listener
	 * @param eventSpace
	 * @param stopAtClass
	 */
	public final <T> T register(T listener, String eventSpace, Predicate<? super Class<?>> scanWhile) {
		Class<?> clazz = listener.getClass();
		if (nothingFounds.contains(clazz.getCanonicalName())) // avoid scanning useless objects again
			return listener;
		boolean hasOne = scan(listener, listener, eventSpace, scanWhile);

		if (!hasOne)
			nothingFounds.add(clazz.getCanonicalName());

		return listener;
	}

	protected boolean scan(Object root, Object listener, String eventSpace, Predicate<? super Class<?>> scanWhile) {
		final Class<?> clazz = listener.getClass();
		boolean hasOne = false;
		// scan all methods
		for (Method m : Iterables.filter(ClassUtils.findAllDeclaredMethods(clazz, scanWhile), listenToMethod)) {
			Class<? extends AEvent> event = m.getParameterTypes()[0].asSubclass(AEvent.class);
			final ListenTo a = m.getAnnotation(ListenTo.class);
			boolean toMe = a.sendToMe() && ADirectedEvent.class.isAssignableFrom(event);

			final AnnotationBasedEventListener l = new AnnotationBasedEventListener(owner, root, listener, m, toMe);

			if (eventSpace != null && (a.restrictExclusiveToEventSpace() || a.restrictToEventSpace())) {
				if (a.restrictExclusiveToEventSpace())
					l.setExclusiveEventSpace(eventSpace);
				else
					l.setEventSpace(eventSpace);
			}

			register(event, l);
			hasOne = true;
		}
		// scan all fields for deep scans
		for (Field f : Iterables.filter(ClassUtils.findAllDeclaredFields(clazz, scanWhile), deepScanField)) {
			f.setAccessible(true);
			Object field;
			try {
				field = f.get(listener);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				System.err.println(e);
				continue;
			}
			if (field == null)
				continue;
			boolean hasFieldOne = scan(root, field, eventSpace, scanWhile);
			hasOne = hasOne || hasFieldOne;
		}
		return hasOne;
	}

	private static final Predicate<Method> listenToMethod = new Predicate<Method>() {
		@Override
		public boolean apply(Method m) {
			Class<?>[] p = m.getParameterTypes();
			return m.isAnnotationPresent(ListenTo.class) && m.getReturnType() == void.class && p.length == 1
					&& AEvent.class.isAssignableFrom(p[0]);
		}
	};
	private static final Predicate<Field> deepScanField = new Predicate<Field>() {
		@Override
		public boolean apply(Field m) {
			return m.isAnnotationPresent(DeepScan.class);
		}
	};

	/**
	 * unregister all registered listeners by this listener container
	 */
	public final void unregisterAll() {
		for (AEventListener<?> listener : listeners) {
			EventPublisher.INSTANCE.removeListener(listener);
		}
		listeners.clear();
	}

	/**
	 * unregister all registered listeners for a given object
	 *
	 * @param listener
	 */
	public final void unregister(Object listener) {
		for (Iterator<AEventListener<?>> it = listeners.iterator(); it.hasNext();) {
			AEventListener<?> e = it.next();
			if (e instanceof AnnotationBasedEventListener) {
				AnnotationBasedEventListener a = (AnnotationBasedEventListener) e;
				// the root or part of the root
				if (a.root == listener || a.listener == listener) {
					EventPublisher.INSTANCE.removeListener(e);
					it.remove();
				}
			}
		}
	}

	/**
	 * Marker annotation that the method is an event listener. No! DataDomain specific things are supported.
	 *
	 * @author Samuel Gratzl
	 *
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface ListenTo {
		/**
		 * determines if the {@link ADirectedEvent} has as its receiver our current listener object
		 *
		 * @return
		 */
		boolean sendToMe() default false;

		boolean restrictToEventSpace() default false;

		boolean restrictExclusiveToEventSpace() default false;
	}

	/**
	 * Marker annotation the annotated field should also be scanned for event listeners
	 *
	 * @author Samuel Gratzl
	 *
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface DeepScan {

	}

	private static class AnnotationBasedEventListener extends AEventListener<IListenerOwner> {

		private final Method method;
		private final Object root;
		private final Object listener;
		private final boolean checkSendToListener;

		public AnnotationBasedEventListener(IListenerOwner handler, Object root, Object listener, Method method,
				boolean checkSendToHandler) {
			this.method = method;
			this.root = root;
			this.listener = listener;
			this.checkSendToListener = checkSendToHandler;
			this.setHandler(handler);
		}

		@Override
		public void handleEvent(AEvent event) {
			if (checkSendToListener
					&& (!(event instanceof ADirectedEvent) || !((ADirectedEvent) event).sentTo(listener)))
				return;
			try {
				method.setAccessible(true);
				method.invoke(listener, event);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				System.err.println(e);
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(method, listener);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AnnotationBasedEventListener other = (AnnotationBasedEventListener) obj;
			return Objects.equals(listener, other.listener) && Objects.equals(method, other.method);
		}

	}
}