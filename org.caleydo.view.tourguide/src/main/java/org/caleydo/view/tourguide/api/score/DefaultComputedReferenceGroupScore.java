/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import java.util.Collection;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.adapter.DataDomainModes;
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IComputeScoreFilter;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.spi.compute.IComputedReferenceGroupScore;
import org.caleydo.view.tourguide.spi.score.IGroupSelector;

import com.google.common.base.Objects;

public class DefaultComputedReferenceGroupScore extends AReferenceGroupScore implements IComputedReferenceGroupScore {
	private final IComputeScoreFilter filter;
	private final IGroupAlgorithm algorithm;
	private final IGroupSelector selector;

	public DefaultComputedReferenceGroupScore(String label, Perspective stratification, Group group,
			IGroupAlgorithm algorithm, IComputeScoreFilter filter, IGroupSelector selector, Color color, Color bgColor) {
		super(label, stratification, group, color, bgColor);
		this.filter = filter == null ? ComputeScoreFilters.SELF : filter;
		this.algorithm = algorithm;
		this.selector = Objects.firstNonNull(selector, GroupSelectors.MAX);
	}


	@Override
	public void onRegistered() {

	}

	@Override
	public boolean supports(ITourGuideDataMode mode) {
		return DataDomainModes.areStratificatins(mode);
	}

	@Override
	public IGroupAlgorithm getAlgorithm() {
		return algorithm;
	}

	@Override
	public Group select(IComputeElement elem, Collection<Group> groups) {
		return selector.select(this, elem, groups);
	}

	@Override
	public IComputeScoreFilter getFilter() {
		return filter;
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultComputedReferenceGroupScore other = (DefaultComputedReferenceGroupScore) obj;
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
