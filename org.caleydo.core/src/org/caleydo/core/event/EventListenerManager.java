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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.caleydo.core.manager.GeneralManager;

/**
 * Utility class to hold a list of event listeners to register and remove them all in an convenient way.
 * 
 * @author Samuel Gratzl
 * 
 */
public class EventListenerManager {
	private static final EventPublisher EVENT_PUBLISHER = GeneralManager.get().getEventPublisher();

	private final Set<AEventListener<?>> listeners = new HashSet<>();

	protected final IListenerOwner owner;

	EventListenerManager(IListenerOwner owner) {
		this.owner = owner;
	}

	public void register(Class<? extends AEvent> event, AEventListener<?> listener) {
		assert listener.getHandler() == owner;
		listeners.add(listener);
		EVENT_PUBLISHER.addListener(event, listener);
	}

	/**
	 * filter all methods of the listener object for <code>
	 *
	 * @ListenTo void xxx(<? extends AEvent> event); </code>
	 *
	 *           and register an event listener for calling this method
	 *
	 * @param listener
	 */
	public final void register(Object listener) {
		register(listener, null);
	}

	/**
	 * filter all methods of the listener object for <code>
	 *
	 * @ListenTo void xxx(<? extends AEvent> event); </code>
	 *
	 *           and register an event listener for calling this method
	 *
	 * @param listener
	 * @param dataDomainID
	 *            if {@link ListenTo#restrictToDataDomain()} or {@link ListenTo#restrictExclusiveToDataDomain()} is used
	 *            the dataDomainID to set
	 */
	public final void register(Object listener, String dataDomainID) {
		Class<?> clazz = listener.getClass();
		while (clazz != null) {
			for (Method m : clazz.getDeclaredMethods()) {
				if (!matches(m))
					continue;
				Class<? extends AEvent> event = m.getParameterTypes()[0].asSubclass(AEvent.class);
				final ListenTo a = m.getAnnotation(ListenTo.class);
				boolean toMe = a.sendToMe()
						&& ADirectedEvent.class.isAssignableFrom(event);

				final AnnotationBasedEventListener l = new AnnotationBasedEventListener(owner, listener, m, toMe);

				if (dataDomainID != null && (a.restrictExclusiveToDataDomain() || a.restrictToDataDomain())) {
					if (a.restrictExclusiveToDataDomain())
						l.setExclusiveDataDomainID(dataDomainID);
					else
						l.setDataDomainID(dataDomainID);
				}

				register(event, l);
			}
			clazz = clazz.getSuperclass();
		}
	}

	private boolean matches(Method m) {
		return m.isAnnotationPresent(ListenTo.class) && m.getParameterTypes().length == 1
				&& AEvent.class.isAssignableFrom(m.getParameterTypes()[0]) && m.getReturnType() == void.class;
	}

	/**
	 * unregister all registered listeners by this listener container
	 */
	public final void unregisterAll() {
		for (AEventListener<?> listener : listeners) {
			EVENT_PUBLISHER.removeListener(listener);
		}
		listeners.clear();
	}

	/**
	 * marker annotation that the method is an event listener, !no DataDomain specific things are supported
	 *
	 * @author Samuel Gratzl
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface ListenTo {
		/**
		 * determines if the {@link ADirectedEvent} has as its receiver our current listener object
		 *
		 * @return
		 */
		boolean sendToMe() default false;

		boolean restrictToDataDomain() default false;

		boolean restrictExclusiveToDataDomain() default false;
	}

	private static class AnnotationBasedEventListener extends AEventListener<IListenerOwner> {

		private final Method method;
		private final Object listener;
		private final boolean checkSendToListener;

		public AnnotationBasedEventListener(IListenerOwner handler, Object listener, Method method,
				boolean checkSendToHandler) {
			this.method = method;
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