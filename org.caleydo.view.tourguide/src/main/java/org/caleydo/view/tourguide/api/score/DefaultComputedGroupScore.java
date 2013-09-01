/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import java.util.Collection;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.spi.compute.IComputeScoreFilter;
import org.caleydo.view.tourguide.spi.compute.IComputedGroupScore;
import org.caleydo.view.tourguide.spi.score.IGroupSelector;

public class DefaultComputedGroupScore extends AComputedGroupScore implements IComputedGroupScore {
	private final IComputeScoreFilter filter;
	private final IGroupAlgorithm algorithm;
	private final IGroupSelector selector;

	public DefaultComputedGroupScore(String label, IGroupAlgorithm algorithm, IComputeScoreFilter filter,
			IGroupSelector selector, Color color,
			Color bgColor) {
		super(label, color, bgColor);
		this.filter = filter == null ? ComputeScoreFilters.SELF : filter;
		this.algorithm = algorithm;
		this.selector = selector;
	}

	@Override
	public void onRegistered() {

	}

	@Override
	public Group select(IComputeElement elem, Collection<Group> groups) {
		return selector.select(this, elem, groups);
	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		return mode == EDataDomainQueryMode.STRATIFICATIONS;
	}

	@Override
	public IGroupAlgorithm getAlgorithm() {
		return algorithm;
	}

	@Override
	public String getAbbreviation() {
		return algorithm.getAbbreviation();
	}

	@Override
	public String getDescription() {
		return algorithm.getDescription() + getLabel();
	}

	@Override
	public IComputeScoreFilter getFilter() {
		return filter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultComputedGroupScore other = (DefaultComputedGroupScore) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		return true;
	}

}
