/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.caleydo.core.util.ClassUtils;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Utility class to hold a list of event listeners to register and remove them all in an convenient way.
 *
 * @author Samuel Gratzl
 *
 */
public class EventListenerManager implements DisposeListener {
	private final Set<AEventListener<?>> listeners = new HashSet<>();

	protected final IListenerOwner owner;
	// cache for classes (cannonical name) that has no relevant methods
	private final Set<String> nothingFounds = new HashSet<>();

	EventListenerManager(IListenerOwner owner) {
		this.owner = owner;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!listeners.isEmpty()) {
			Logger.create(EventListenerManager.class).error("not empty listeners during finalize - auto unregister");
			unregisterAll();
		}
		super.finalize();
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
		scan(listener, listener, eventSpace, scanWhile);
		return listener;
	}

	protected boolean scan(Object root, Object listener, String eventSpace, Predicate<? super Class<?>> scanWhile) {
		if (listener instanceof IRetrictedToEventSpace) {
			String _newEventSpace = ((IRetrictedToEventSpace) listener).getEventSpace();
			if (_newEventSpace != null)
				eventSpace = _newEventSpace;
		}
		final Class<?> clazz = listener.getClass();
		if (nothingFounds.contains(clazz.getCanonicalName())) // avoid scanning useless objects again
			return false;

		boolean scanAgain = false; // marker whether we have to scan this type in future runs?
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
			scanAgain = true;
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
			if (field == null) {
				scanAgain = true; // in a later run maybe not null anymore
				continue;
			}

			if (field instanceof Collection<?>) {
				@SuppressWarnings("unchecked")
				Collection<Object> r = (Collection<Object>) field;
				scanAll(root, r, eventSpace, scanWhile);
				scanAgain = true; // collections may change
			}
			if (field instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Map<?, Object> r = (Map<?, Object>) field;
				scanAll(root, r.values(), eventSpace, scanWhile);
				scanAgain = true; // collections may change
			}
			if (field instanceof Multimap<?, ?>) {
				@SuppressWarnings("unchecked")
				Multimap<?, Object> r = (Multimap<?, Object>) field;
				scanAll(root, r.values(), eventSpace, scanWhile);
				scanAgain = true; // collections may change
			}
			{ // primitive always scan
				boolean hasFieldOne = scan(root, field, eventSpace, scanWhile);
				scanAgain = scanAgain || hasFieldOne;
			}

		}
		if (!scanAgain)
			nothingFounds.add(clazz.getCanonicalName());
		return scanAgain;
	}

	private boolean scanAll(Object root, Iterable<Object> listeners, String eventSpace,
			Predicate<? super Class<?>> scanWhile) {
		if (listeners == null)
			return false;
		boolean hasOne = false;
		for (Object elem : listeners) {
			boolean hasFieldOne = scan(root, elem, eventSpace, scanWhile);
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

	@Override
	public void widgetDisposed(DisposeEvent e) {
		unregisterAll();
	}

	/**
	 * unregister all registered listeners for a given object
	 *
	 * @param listener
	 */
	public final <T> T unregister(T listener) {
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
		return listener;
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
	@Inherited
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

	/**
	 * interface to provide a event space for {@link ListenTo#restrictToEventSpace()}
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IRetrictedToEventSpace {
		/**
		 * return the event space to use
		 *
		 * @return
		 */
		String getEventSpace();
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

		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("ListenTo ").append(method.getDeclaringClass().getSimpleName()).append('.')
					.append(method.getName());
			b.append('(').append(method.getParameterTypes()[0].getSimpleName()).append(')');
			return b.toString();
		}
	}
}
