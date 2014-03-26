/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.ExtensionUtils.IExtensionLoader;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.info.dataset.spi.IDataSetItem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class DataSetItems {
	private DataSetItems() {

	}

	public static Collection<IDataSetItem> create() {
		List<Pair<IDataSetItem, Integer>> items = ExtensionUtils.loadExtensions("org.caleydo.view.info.DataSetItem",
				new ExtensionLoader());
		items = new ArrayList<>(items); // mutable copy
		Collections.sort(items, Pair.<Integer> compareSecond());
		return Lists.transform(items, Pair.<IDataSetItem, Integer> mapFirst());
	}

	private static class ExtensionLoader implements IExtensionLoader<Pair<IDataSetItem, Integer>> {

		@Override
		public Pair<IDataSetItem, Integer> load(IConfigurationElement elem) throws CoreException {
			Object t = elem.createExecutableExtension("class");
			if (!(t instanceof IDataSetItem))
				return null;
			String order = elem.getAttribute("order");
			if (!StringUtils.isNumeric(order))
				order = "10";
			int order_i = Integer.parseInt(order);
			return Pair.make((IDataSetItem) t, order_i);
		}

	}
}
