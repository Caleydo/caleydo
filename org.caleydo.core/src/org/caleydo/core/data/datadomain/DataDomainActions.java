/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;

/**
 * common actions for a data domain, that can be performed
 *
 * @author Samuel Gratzl
 *
 */
public class DataDomainActions {
	private final static String EXTENSION_POINT = "org.caleydo.datadomain.DataDomainActions";
	private final static Collection<IDataDomainActionFactory> factories = ExtensionUtils.findImplementation(
			EXTENSION_POINT, "class", IDataDomainActionFactory.class);


	public static Collection<Pair<String, Runnable>> create(IDataDomain dataDomain, Object sender) {
		Collection<Pair<String, Runnable>> events = new ArrayList<>();
		for (IDataDomainActionFactory factory : factories) {
			events.addAll(factory.create(dataDomain, sender));
		}
		return events;
	}

	public static boolean add(ContextMenuCreator creator, IDataDomain dataDomain, Object sender, boolean separate) {
		boolean first = separate;
		boolean added = false;
		for (IDataDomainActionFactory factory : factories) {
			Collection<Pair<String, Runnable>> create = factory.create(dataDomain, sender);
			if (create.isEmpty())
				continue;
			if (!first) {
				creator.addSeparator();
			}
			first = false;
			added = true;
			for (Pair<String, Runnable> p : create)
				creator.add(new ActionBasedContextMenuItem(p.getFirst(), p.getSecond()));
		}
		return added;
	}

	public interface IDataDomainActionFactory {
		public Collection<Pair<String, Runnable>> create(IDataDomain dataDomain, Object sender);
	}

}
