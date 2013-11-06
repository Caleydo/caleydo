/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.pathway;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.base.IAction;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Place where common pathway actions for context menus are collected.
 *
 * @author Christian
 *
 */
public final class PathwayActions {
	private final static String EXTENSION_POINT = "org.caleydo.datadomain.pathway.PathwayAction";
	private final static Collection<IPathwayActionFactory> factories = ExtensionUtils.findImplementation(
			EXTENSION_POINT, "class", IPathwayActionFactory.class);

	private PathwayActions() {
	}

	public static Collection<Pair<String, ? extends IAction>> getPathwayActions(PathwayGraph pathway, Object sender) {
		Collection<Pair<String, ? extends IAction>> events = new ArrayList<>();
		for (IPathwayActionFactory factory : factories) {
			events.addAll(factory.create(pathway, sender));
		}
		return events;
	}

	public static boolean addToContextMenu(ContextMenuCreator creator, PathwayGraph pathway, Object sender,
			boolean separate) {
		boolean first = separate;
		boolean added = false;
		for (IPathwayActionFactory factory : factories) {
			Collection<Pair<String, ? extends IAction>> actions = factory.create(pathway, sender);
			if (actions.isEmpty())
				continue;
			if (!first) {
				creator.addSeparator();
			}
			first = false;
			added = true;
			for (Pair<String, ? extends IAction> action : actions) {
				creator.add(new ActionBasedContextMenuItem(action.getFirst(), action.getSecond()));
			}
		}
		return added;
	}

	public interface IPathwayActionFactory {

		/**
		 * Creates a pair of a string describing the action, which will appear in a context menu, and an event that
		 * should be triggered when clicking that context menu item.
		 *
		 * @param dataDomain
		 * @param sender
		 * @return
		 */
		public Collection<Pair<String, ? extends IAction>> create(PathwayGraph pathway, Object sender);
	}
}
