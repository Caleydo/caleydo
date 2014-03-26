/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.adapter.TourGuideDataModes;
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.algorithm.IComputeScoreFilter;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.caleydo.view.tourguide.spi.compute.IComputedReferenceStratificationScore;

/**
 * @author Samuel Gratzl
 *
 */
public class DefaultComputedReferenceStratificationScore extends AReferenceStratificationScore implements
		IComputedReferenceStratificationScore {
	private final IStratificationAlgorithm algorithm;
	private final IComputeScoreFilter filter;

	public DefaultComputedReferenceStratificationScore(String label, Perspective reference,
			IStratificationAlgorithm algorithm, IComputeScoreFilter filter, Color color, Color bgColor) {
		super(label, reference, color, bgColor);
		this.algorithm = algorithm;
		this.filter = filter == null ? ComputeScoreFilters.SELF : filter;
	}

	@Override
	public void onRegistered() {

	}

	@Override
	public boolean supports(ITourGuideDataMode mode) {
		return TourGuideDataModes.areStratificatins(mode);
	}

	@Override
	public IStratificationAlgorithm getAlgorithm() {
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
		DefaultComputedReferenceStratificationScore other = (DefaultComputedReferenceStratificationScore) obj;
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
