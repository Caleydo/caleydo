/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;

/**
 * common actions for table perspectives that can be performed
 *
 * @author Samuel Gratzl
 *
 */
public class TablePerspectiveActions {
	private final static String EXTENSION_POINT = "org.caleydo.datadomain.TablePerspectiveActions";
	private final static Collection<ITablePerspectiveFactory> factories = ExtensionUtils.findImplementation(
			EXTENSION_POINT, "class", ITablePerspectiveFactory.class);


	public static Collection<Pair<String, Runnable>> create(TablePerspective tablePerspective, Object sender) {
		Collection<Pair<String, Runnable>> events = new ArrayList<>();
		for (ITablePerspectiveFactory factory : factories) {
			events.addAll(factory.create(tablePerspective, sender));
		}
		return events;
	}

	public static boolean add(ContextMenuCreator creator, TablePerspective tablePerspective, Object sender,
			boolean separate) {
		return DataDomainActions.add(creator, map(tablePerspective, sender), separate);
	}

	private static Iterable<Collection<Pair<String, Runnable>>> map(TablePerspective tablePerspective, Object sender) {
		Collection<Collection<Pair<String, Runnable>>> r = new ArrayList<>(factories.size());
		for (ITablePerspectiveFactory factory : factories) {
			r.add(factory.create(tablePerspective, sender));
		}
		return r;
	}

	public interface ITablePerspectiveFactory {
		public Collection<Pair<String, Runnable>> create(TablePerspective tablePerspective, Object sender);
	}

}
