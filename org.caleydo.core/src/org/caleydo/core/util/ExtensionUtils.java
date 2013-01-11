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
package org.caleydo.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;

import com.google.common.collect.Maps;

/**
 * @author Samuel Gratzl
 *
 */
public class ExtensionUtils {
	private static final Logger log = Logger.create(ExtensionUtils.class);

	public static <T> Collection<T> findImplementation(String extensionPoint, String property, Class<T> type) {
		Collection<T> factories = new ArrayList<>();
		try {
			for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor(extensionPoint)) {
				final Object o = elem.createExecutableExtension(property);
				if (type.isInstance(o))
					factories.add(type.cast(o));
			}
		} catch (CoreException e) {
			log.error("can't find implementations of " + extensionPoint + " : " + property, e);
		}
		return Collections.unmodifiableCollection(factories);
	}

	public static <T> Map<String, T> findImplementation(String extensionPoint, String labelAttr, String classAttr,
			Class<T> type) {
		Map<String, T> factories = Maps.newLinkedHashMap();
		try {
			for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor(extensionPoint)) {
				final Object o = elem.createExecutableExtension(classAttr);
				if (type.isInstance(o))
					factories.put(elem.getAttribute(labelAttr), type.cast(o));
			}
		} catch (CoreException e) {
			log.error("can't find implementations of " + extensionPoint + " : " + classAttr, e);
		}
		return Collections.unmodifiableMap(factories);
	}
}
