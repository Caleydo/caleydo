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
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.collection.Pair;
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


	public static Collection<Pair<String, ? extends AEvent>> create(IDataDomain dataDomain, Object sender) {
		Collection<Pair<String, ? extends AEvent>> events = new ArrayList<>();
		for (IDataDomainActionFactory factory : factories) {
			events.addAll(factory.create(dataDomain, sender));
		}
		return events;
	}

	public static boolean add(ContextMenuCreator creator, IDataDomain dataDomain, Object sender, boolean separate) {
		boolean first = separate;
		boolean added = false;
		for (IDataDomainActionFactory factory : factories) {
			Collection<Pair<String, ? extends AEvent>> create = factory.create(dataDomain, sender);
			if (create.isEmpty())
				continue;
			if (!first) {
				creator.addSeparator();
			}
			first = false;
			added = true;
			creator.addAll(create);
		}
		return added;
	}

	public interface IDataDomainActionFactory {
		public Collection<Pair<String, ? extends AEvent>> create(IDataDomain dataDomain, Object sender);
	}

}
