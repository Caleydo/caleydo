/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.genetic;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;

/**
 * @author Christian
 *
 */
public final class GeneActions {
	
	private final static String EXTENSION_POINT = "org.caleydo.datadomain.genetic.GeneAction";
	private final static Collection<IGeneActionFactory> factories = ExtensionUtils.findImplementation(
			EXTENSION_POINT, "class", IGeneActionFactory.class);
	
	private GeneActions() {
		
	}
	
	public static Collection<Pair<String, ? extends Runnable>> getPathwayActions(Object id, IDType idType, Object sender) {
		Collection<Pair<String, ? extends Runnable>> events = new ArrayList<>();
		for (IGeneActionFactory factory : factories) {
			events.addAll(factory.create(id, idType, sender));
		}
		return events;
	}

	public static boolean addToContextMenu(ContextMenuCreator creator, Object id, IDType idType, Object sender,
			boolean separate) {
		boolean first = separate;
		boolean added = false;
		for (IGeneActionFactory factory : factories) {
			Collection<Pair<String, ? extends Runnable>> actions = factory.create(id, idType, sender);
			if (actions.isEmpty())
				continue;
			if (!first) {
				creator.addSeparator();
			}
			first = false;
			added = true;
			for (Pair<String, ? extends Runnable> action : actions) {
				creator.add(new ActionBasedContextMenuItem(action.getFirst(), action.getSecond()));
			}
		}
		return added;
	}

	public interface IGeneActionFactory {

		/**
		 * Creates a pair of a string describing the action, which will appear in a context menu, and an event that
		 * should be triggered when clicking that context menu item.
		 *
		 * @param dataDomain
		 * @param sender
		 * @return
		 */
		public Collection<Pair<String, ? extends Runnable>> create(Object id, IDType idType, Object sender);
	}

}
