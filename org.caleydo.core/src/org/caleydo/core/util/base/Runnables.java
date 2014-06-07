/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.base;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.internal.cmd.AOpenViewHandler;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class Runnables {
	public static Runnable withinSWTThread(final Runnable run) {
		if (run instanceof WithinSWT) // already wrapped
			return run;
		return new WithinSWT(run);
	}

	public static Runnable sendEvent(final AEvent event) {
		return new Runnable() {
			@Override
			public void run() {
				EventPublisher.trigger(event);
			}
		};
	}

	public static Runnable show(String viewType, TablePerspective... tablePerspective) {
		return show(viewType, null, tablePerspective);
	}

	public static Runnable show(String viewType, String secondaryId, ASerializedView serializationData) {
		return withinSWTThread(new ShowAndAddToViewAction(viewType, secondaryId, serializationData));
	}

	public static Runnable show(String viewType, String secondaryId, TablePerspective... tablePerspective) {
		return withinSWTThread(new ShowAndAddToViewAction(viewType, secondaryId, tablePerspective));
	}

	public static Runnable showMultiple(String viewType, TablePerspective... tablePerspective) {
		return show(viewType, AOpenViewHandler.createSecondaryID(), tablePerspective);
	}

	public static Runnable showMultiple(String viewType, ASerializedView data) {
		return show(viewType, AOpenViewHandler.createSecondaryID(), data);
	}

	public static final Function<Pair<String, ? extends AEvent>, Pair<String, Runnable>> SEND_EVENTS = new Function<Pair<String, ? extends AEvent>, Pair<String, Runnable>>() {
		@Override
		public Pair<String, Runnable> apply(Pair<String, ? extends AEvent> input) {
			assert input != null;
			return Pair.make(input.getFirst(), sendEvent(input.getSecond()));
		}
	};

	private static class WithinSWT implements Runnable {
		private final Runnable wrappee;

		public WithinSWT(Runnable wrappee) {
			this.wrappee = wrappee;
		}

		@Override
		public void run() {
			Display.getDefault().asyncExec(wrappee);
		}
	}

}
