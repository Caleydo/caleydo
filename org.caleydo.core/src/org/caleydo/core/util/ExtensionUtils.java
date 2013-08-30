/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.osgi.framework.Bundle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Maps;

/**
 *
 * @author Samuel Gratzl
 *
 */
public final class ExtensionUtils {
	private static final Logger log = Logger.create(ExtensionUtils.class);

	private ExtensionUtils() {

	}

	/**
	 * simple wrapper for creating all implementation instances of a given extension point
	 *
	 * @param extensionPoint
	 * @param property
	 *            the property which holds the class name, e.g. class
	 * @param type
	 *            the expected class type
	 * @return
	 */
	public static <T> List<T> findImplementation(String extensionPoint, String property, Class<T> type) {
		if (noRegistry())
			return ImmutableList.of();
		Builder<T> factories = ImmutableList.builder();
		try {

			for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor(extensionPoint)) {
				final Object o = elem.createExecutableExtension(property);
				if (type.isInstance(o))
					factories.add(type.cast(o));
			}
		} catch (CoreException e) {
			log.error("can't find implementations of " + extensionPoint + " : " + property, e);
		}
		return factories.build();
	}

	/**
	 * @return
	 */
	private static boolean noRegistry() {
		return RegistryFactory.getRegistry() == null;
	}

	/**
	 * custom loader script for loading extension
	 *
	 * @param extensionPoint
	 * @param loader
	 * @return
	 */
	public static <T> List<T> loadExtensions(String extensionPoint, IExtensionLoader<T> loader) {
		if (noRegistry())
			return ImmutableList.of();
		Builder<T> factories = ImmutableList.builder();
		try {

			for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor(extensionPoint)) {
				T obj = loader.load(elem);
				if (obj != null)
					factories.add(obj);
			}
		} catch (CoreException e) {
			log.error("can't find load plugins of " + extensionPoint, e);
		}
		return factories.build();
	}

	public interface IExtensionLoader<T> {
		T load(IConfigurationElement elem) throws CoreException;
	}

	public static <T> T findFirstImplementation(String extensionPoint, String property, Class<T> type) {
		if (RegistryFactory.getRegistry() == null)
			return null;
		try {
			for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor(extensionPoint)) {
				final Object o = elem.createExecutableExtension(property);
				if (type.isInstance(o))
					return type.cast(o);
			}
		} catch (CoreException e) {
			log.error("can't find implementations of " + extensionPoint + " : " + property, e);
		}
		return null;
	}

	/**
	 * see {@link #findImplementation(String, String, Class)} but this time a label and a class
	 *
	 * @param extensionPoint
	 * @param labelAttr
	 *            the attribute holding the label
	 * @param classAttr
	 *            the attribute holding the classname
	 * @param type
	 *            expected type
	 * @return
	 */
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

	/**
	 * returns the url for the resource reference in an attribute of the given {@link IConfigurationElement}
	 *
	 * @param elem
	 * @param attribute
	 * @return
	 */
	public static URL getResource(IConfigurationElement elem, String attribute) {
		String v = elem.getAttribute(attribute);
		if (v == null || v.trim().isEmpty()) {
			return null;
		}
		Bundle bundle = Platform.getBundle(elem.getContributor().getName());
		if (bundle == null)
			return null;

		URL url = bundle.getEntry(v);
		try {
			return FileLocator.toFileURL(url);
		} catch (IOException e) {
			return null;
		}
	}
}
