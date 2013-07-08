/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
