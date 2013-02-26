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
package org.caleydo.view.tourguide.internal.view.model;

import static org.caleydo.view.tourguide.v3.model.StringRankColumnModel.starToRegex;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class PathwayDataDomainQuery extends ADataDomainQuery {
	private String matches = null;

	public PathwayDataDomainQuery(EDataDomainQueryMode mode, PathwayDataDomain dataDomain) {
		super(mode, dataDomain);
	}

	@Override
	public PathwayDataDomain getDataDomain() {
		return (PathwayDataDomain) super.getDataDomain();
	}

	@Override
	public void cloneFrom(ADataDomainQuery clone) {
		super.cloneFrom(clone);
		this.matches = ((PathwayDataDomainQuery) clone).matches;
	}

	@Override
	public boolean include(Perspective perspective, Group group) {
		assert perspective.getDataDomain() == dataDomain;
		if (matches == null)
			return true;
		return Pattern.matches(starToRegex(matches), perspective.getLabel());
	}

	@Override
	public List<PerspectiveRow> getAll() {
		PathwayDataDomain p = (PathwayDataDomain) dataDomain;
		List<PerspectiveRow> r = Lists.newArrayList();
		for (Perspective per : p.getPathwayRecordPerspectives()) {
			r.add(new PerspectiveRow(per, null));
		}
		return r;
	}

	public void setMatches(String matches) {
		if (Objects.equals(matches, this.matches))
			return;
		this.matches = matches;
		updateFilter();
	}

	/**
	 * @return the matches, see {@link #matches}
	 */
	public String getMatches() {
		return matches;
	}

	@Override
	public boolean hasFilter() {
		return this.matches != null;
	}
}
