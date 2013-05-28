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
package org.caleydo.core.internal.startup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.caleydo.core.startup.IStartupAddon;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @author Samuel Gratzl
 *
 */
public class StartupAddons {
	private static final String EXTENSION_POINT = "org.caleydo.core.StartupAddon";

	public static Map<String, IStartupAddon> findAll() {
		List<StartupAddonDesc> tmp = new ArrayList<>();
		try {
			for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor(EXTENSION_POINT)) {
				final Object o = elem.createExecutableExtension("class");
				if (o instanceof IStartupAddon) {
					String orderS = elem.getAttribute("order");
					int order = Strings.isNullOrEmpty(orderS) ? 10 : Integer.parseInt(orderS);
					tmp.add(new StartupAddonDesc(elem.getAttribute("name"), (IStartupAddon) o, order));
				}
			}
		} catch (CoreException e) {
			System.err.println("can't find implementations of " + EXTENSION_POINT + " : " + "name");
			e.printStackTrace();
		}

		// sort by order
		Collections.sort(tmp);
		Map<String, IStartupAddon> factories = Maps.newLinkedHashMap();
		for(StartupAddonDesc desc : tmp)
			factories.put(desc.label, desc.addon);
		return Collections.unmodifiableMap(factories);
	}

	private static class StartupAddonDesc implements Comparable<StartupAddonDesc> {
		private final String label;
		private final IStartupAddon addon;
		private final int order;

		public StartupAddonDesc(String label, IStartupAddon addon, int order) {
			this.label = label;
			this.addon = addon;
			this.order = order;
		}

		@Override
		public int compareTo(StartupAddonDesc o) {
			return order - o.order;
		}
	}
}
