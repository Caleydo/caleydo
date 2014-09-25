/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;

/**
 * common actions for ids that can be performed
 *
 * @author Samuel Gratzl
 *
 */
public class TypedIDActions {
	private final static String EXTENSION_POINT = "org.caleydo.datadomain.TypedIDActions";
	private final static Collection<ITypedIDActionFactory> factories = ExtensionUtils.findImplementation(
			EXTENSION_POINT, "class", ITypedIDActionFactory.class);


	public static Collection<Pair<String, Runnable>> create(Integer id, IDType idType,
			ATableBasedDataDomain dataDomain, Object sender) {
		Collection<Pair<String, Runnable>> events = new ArrayList<>();
		for (ITypedIDActionFactory factory : factories) {
			events.addAll(factory.create(id, idType, dataDomain, sender));
		}
		return events;
	}

	public static boolean add(ContextMenuCreator creator, Integer id, IDType idType, ATableBasedDataDomain dataDomain,
			Object sender, boolean separate) {
		return DataDomainActions.add(creator, map(id, idType, dataDomain, sender), separate);
	}

	private static Iterable<Collection<Pair<String, Runnable>>> map(Integer id, IDType idType,
			ATableBasedDataDomain dataDomain, Object sender) {
		Collection<Collection<Pair<String, Runnable>>> r = new ArrayList<>(factories.size());
		for (ITypedIDActionFactory factory : factories) {
			r.add(factory.create(id, idType, dataDomain, sender));
		}
		return r;
	}

	public interface ITypedIDActionFactory {
		public Collection<Pair<String, Runnable>> create(Integer id, IDType idType, ATableBasedDataDomain dataDomain,
				Object sender);
	}

}
