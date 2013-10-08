/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.internal;

import java.util.List;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapterFactory;

/**
 * @author Samuel Gratzl
 *
 */
public class ViewAdapters {

	private static final List<IViewAdapterFactory> factories = ExtensionUtils.findImplementation(
			"org.caleydo.view.tourguide.adapter", "class", IViewAdapterFactory.class);

	public static List<IViewAdapterFactory> get() {
		return factories;
	}
}
