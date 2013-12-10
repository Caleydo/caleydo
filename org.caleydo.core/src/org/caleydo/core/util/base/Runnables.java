/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.base;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class Runnables {
	public static Runnable withinSWTThread(final Runnable run) {
		return new Runnable() {
			@Override
			public void run() {
				Display.getDefault().asyncExec(run);
			}
		};
	}

	public static Runnable sendEvent(final AEvent event) {
		return new Runnable() {
			@Override
			public void run() {
				EventPublisher.trigger(event);
			}
		};
	}

	public static final Function<Pair<String, ? extends AEvent>, Pair<String, Runnable>> SEND_EVENTS = new Function<Pair<String, ? extends AEvent>, Pair<String, Runnable>>() {
		@Override
		public Pair<String, Runnable> apply(Pair<String, ? extends AEvent> input) {
			assert input != null;
			return Pair.make(input.getFirst(), sendEvent(input.getSecond()));
		}
	};

}
