/*
 * @(#)Translator.java	1.0 23/01/02
 *
 * Copyright (C) 2003 Sven Luzar
 *
 * 6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because nobody changed the file significantly since the last
 * 3.0 version of GPGraphpad that was LGPL. By significantly, I mean: 
 *  - less than 3 instructions changes could honnestly have been done from an old fork,
 *  - license or copyright changes in the header don't count
 *  - automaticaly updating imports don't count,
 *  - updating systematically 2 instructions to a library specification update don't count.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jgraph.pad.resources;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.Vector;

/**
 * Contains ResourceBundle objects. The first (deepest) bundle is the Graphpad
 * bundle. If a user wants to use Graphpad as a framework he can push his own
 * bundle with the <tt>pushBundle</tt> method. Requests will procedure with
 * the following logic: The translator asks the highest bundle for a localized
 * text string. If the bundle has no entry for the specified key. The next
 * bundle will ask for the key. If the deepest bundle hasn't a value for the key
 * the key will return.
 */

public final class Translator {

	/**
	 * Container for the registered LocaleChangeListener.
	 * 
	 * @see LocaleChangeListener
	 */
	private static ArrayList listeners = new ArrayList();

	/**
	 * The translator creates outputs on the System.err if a resource wasn't
	 * found and this boolean is <tt>true</tt>
	 */
	private static boolean logNotFoundResources = false;

	/**
	 * Contains ResourceBundle objects. The first bundle is the Graphpad bundle.
	 * If a user wants to use Graphpad as a framework he can push his own
	 * bundle.
	 */
	private static Stack bundles = new Stack();

	/**
	 * Contains ResourceBundle names The first bundlename is the Graphpad
	 * bundle. If a user wants to use Graphpad as a framework he can push his
	 * own bundle.
	 */
	private static Stack bundleNames = new Stack();

	/**
	 * Resouce Bundle with proper names.
	 */
	private static DefaultResourceBundle defaultBundle = new DefaultResourceBundle();

	/**
	 * Returns the resouce bundle with the proper names.
	 * 
	 */
	public static DefaultResourceBundle getDefaultResourceBundle() {
		return defaultBundle;
	}

	/**
	 * Returns the localized String for the key. If the String wasn't found the
	 * method will return the Key but not null.
	 * 
	 * @param sKey
	 *            Key for the localized String.
	 * 
	 */
	public static String getString(final String sKey) {
		return getString(bundles.size() - 1, sKey);
	}

	/**
	 * Returns the localized String for the key. If the String wasn't found the
	 * method will return null.
	 * 
	 * @param bundleIndex
	 *            The bundle index for the request. The method requests the
	 *            bundle at the specified position and at all deeper positions.
	 * 
	 * @param sKey
	 *            Key for the localized String.
	 * 
	 */
	public static String getString(final int bundleIndex, final String sKey) {
		if (bundleIndex < bundles.size() && bundleIndex >= 0) {
			final ResourceBundle bundle = (ResourceBundle) bundles.get(bundleIndex);
			try {
				return bundle.getString(sKey);
			} catch (MissingResourceException mrex) {
				return getString(bundleIndex - 1, sKey);
			}
		}
		try {
			return defaultBundle.getString(sKey);
		} catch (MissingResourceException mrex) {
			if (logNotFoundResources)
				System.err.println("Resource for the following key not found:"
						+ sKey);
			return null;
		}
	}

	/**
	 * Returns the localized String for the key. If the String wasn't found the
	 * method will return the key but not null.
	 * 
	 * @param sKey
	 *            Key for the localized String.
	 * @param values
	 *            Object array for placeholders.
	 * 
	 * @see MessageFormat#format(String, Object[])
	 */
	public static String getString(final String sKey, final Object[] values) {
		return getString(bundles.size() - 1, sKey, values);
	}

	/**
	 * Returns the localized String for the key. If the String wasn't found the
	 * method will return an empty String but not null and will log on the
	 * System.err the key String, if logNotFoundResources is true.
	 * 
	 * @param sKey
	 *            Key for the localized String.
	 * @param oValues
	 *            Object array for placeholders.
	 * 
	 * @see MessageFormat#format(String, Object[])
	 */
	public static String getString(final int bundleIndex, final String sKey,
			final Object[] oValues) {

		if (bundleIndex < bundles.size() && bundleIndex >= 0) {
			final ResourceBundle bundle = (ResourceBundle) bundles.get(bundleIndex);
			try {
				return MessageFormat.format(bundle.getString(sKey), oValues);
			} catch (MissingResourceException mrex) {
				return getString(bundleIndex - 1, sKey, oValues);
			}
		}
		try {
			return defaultBundle.getString(sKey);
		} catch (MissingResourceException mrex) {
			if (logNotFoundResources)
				System.err.println("Resource for the following key not found:"
						+ sKey);
			return null;
		}

	}

	/**
	 * Adds a locale change listener to this Translator. If the local changes,
	 * this translator will fire locale change events.
	 * 
	 */
	public static void addLocaleChangeListener(final LocaleChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the registered Listener
	 * 
	 */
	public static void removeLocaleChangeListener(final LocaleChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Returns the current locale
	 * 
	 */
	public static Locale getLocale() {
		return Locale.getDefault();
	}

	/**
	 * Sets the new locale and fires events to the locale change listener.
	 * 
	 */
	public static void setLocale(final Locale locale) {
		// change the locale
		final Locale oldLocale = Locale.getDefault();
		Locale.setDefault(locale);

		// reload the bundles
		reloadBundles();

		// update listeners
		Vector cpyListeners;
		synchronized (listeners) {
			cpyListeners = (Vector) listeners.clone();
		}

		Enumeration oEnum = cpyListeners.elements();
		while (oEnum.hasMoreElements()) {
			LocaleChangeListener listener = (LocaleChangeListener) oEnum
					.nextElement();
			listener.localeChanged(new LocaleChangeEvent(oldLocale, locale));
		}
	}

	/**
	 * 
	 * Reloads the bundles at the stack by using the default locale.
	 */
	public static void reloadBundles() {
		// update the bundles at the stack
		for (int i = 0; i < bundleNames.size(); i++) {
			final ResourceBundle resourcebundle = PropertyResourceBundle
					.getBundle((String) bundleNames.get(i));
			bundles.set(i, resourcebundle);
		}
	}

	/**
	 * Pushes the specified bundle on the stack. An example for a bundle file
	 * name is 'org.jgraph.pad.resources.Graphpad'. Don't add the suffix of the
	 * filename, the language or country code.
	 */
	public static void pushBundle (final String filename) throws MissingResourceException {
		final ResourceBundle resourcebundle = PropertyResourceBundle
				.getBundle(filename);
		bundles.push(resourcebundle);
		bundleNames.push(filename);
	}

	/**
	 * Pops the highest bundle on the stack
	 */
	public static void popBundle() {
		bundles.pop();
		bundleNames.pop();
	}

	/**
	 * removes the specified bundle
	 */
	public static void removeBundle(final int index) {
		bundles.remove(index);
		bundleNames.remove(index);
	}

	/**
	 * Returns the logNotFoundResources.
	 * 
	 * @return boolean
	 */
	public static boolean isLogNotFoundResources() {
		return logNotFoundResources;
	}

	/**
	 * Sets the logNotFoundResources.
	 * 
	 * @param logNotFoundResources
	 *            The logNotFoundResources to set
	 */
	public static void setLogNotFoundResources(final boolean logNotFoundResources) {
		Translator.logNotFoundResources = logNotFoundResources;
	}

	public static Stack getBundles() {
		return bundles;
	}

	public static void setBundles(Stack bundles) {
		Translator.bundles = bundles;
	}

	public static Stack getBundleNames() {
		return bundleNames;
	}

	public static void setBundleNames(Stack bundleNames) {
		Translator.bundleNames = bundleNames;
	}
}