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
package org.caleydo.view.tourguide.api.score;

import java.awt.Color;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.spi.compute.IComputeScoreFilter;
import org.caleydo.view.tourguide.spi.compute.IComputedReferenceGroupScore;

public class DefaultComputedReferenceGroupScore extends AReferenceGroupScore implements IComputedReferenceGroupScore {
	private final IComputeScoreFilter filter;
	private final IGroupAlgorithm algorithm;

	public DefaultComputedReferenceGroupScore(String label, Perspective stratification, Group group,
			IGroupAlgorithm algorithm, IComputeScoreFilter filter, Color color, Color bgColor) {
		super(label, stratification, group, color, bgColor);
		this.filter = filter == null ? ComputeScoreFilters.SELF : filter;
		this.algorithm = algorithm;
	}


	@Override
	public void onRegistered() {

	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		return mode == EDataDomainQueryMode.TABLE_BASED;
	}

	@Override
	public IGroupAlgorithm getAlgorithm() {
		return algorithm;
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
