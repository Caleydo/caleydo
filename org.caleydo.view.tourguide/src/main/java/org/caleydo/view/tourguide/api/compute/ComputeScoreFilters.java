/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.compute;

import java.util.Objects;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.api.prefs.MyPreferences;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.compute.IComputeScoreFilter;

/**
 * factory class for filters that check whether the score of two {@link IComputeElement} should be computed or not
 *
 * @author Samuel Gratzl
 *
 */
public final class ComputeScoreFilters {
	public static IComputeScoreFilter ALL = new IComputeScoreFilter() {
		@Override
		public boolean doCompute(IComputeElement a, Group ag, IComputeElement b, Group bg) {
			return true;
		}
	};

	public static IComputeScoreFilter SELF = new IComputeScoreFilter() {
		@Override
		public boolean doCompute(IComputeElement a, Group ag, IComputeElement b, Group bg) {
			if (!Objects.equals(ag, bg)) // not the same group
				return true;
			if (ag == null && !Objects.equals(a, b)) // no groups and not the same stratification
				return true;
			return false;
		}
	};

	public static IComputeScoreFilter TOO_SMALL = new IComputeScoreFilter() {
		@Override
		public boolean doCompute(IComputeElement a, Group ag, IComputeElement b, Group bg) {
			if (Objects.equals(ag, bg) && Objects.equals(a, b))
				return true;
			final int size = ag.getSize();
			final int minSize = MyPreferences.getMinClusterSize();
			return size > minSize;
		}
	};

	public static IComputeScoreFilter and(final IComputeScoreFilter... elems) {
		if (elems.length == 1)
			return elems[0];
		return new IComputeScoreFilter() {
			@Override
			public boolean doCompute(IComputeElement a, Group ag, IComputeElement b, Group bg) {
				for (IComputeScoreFilter elem : elems)
					if (!elem.doCompute(a, ag, b, bg))
						return false;
				return true;
			}
		};
	}

	public static IComputeScoreFilter or(final IComputeScoreFilter... elems) {
		if (elems.length == 1)
			return elems[0];
		return new IComputeScoreFilter() {
			@Override
			public boolean doCompute(IComputeElement a, Group ag, IComputeElement b, Group bg) {
				for (IComputeScoreFilter elem : elems)
					if (elem.doCompute(a, ag, b, bg))
						return true;
				return false;
			}
		};
	}
}
