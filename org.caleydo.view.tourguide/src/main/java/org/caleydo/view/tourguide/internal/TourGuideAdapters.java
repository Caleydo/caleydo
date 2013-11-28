/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.internal;

import java.util.List;
import java.util.Objects;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapterFactory;

/**
 * @author Samuel Gratzl
 *
 */
public class TourGuideAdapters {
	private static final List<ITourGuideAdapterFactory> factories = ExtensionUtils.findImplementation(
			"org.caleydo.view.tourguide.adapter", "class", ITourGuideAdapterFactory.class);

	public static List<ITourGuideAdapterFactory> get() {
		return factories;
	}

	/**
	 * @param secondaryId
	 * @return
	 */
	public static ITourGuideAdapter createFrom(String secondaryId) {
		for (ITourGuideAdapterFactory f : factories) {
			if (Objects.equals(secondaryId, f.getSecondaryID()))
				return f.create();
		}
		return null;
	}
}

