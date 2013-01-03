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
package org.caleydo.view.tourguide.api.query.filter;

import java.util.HashSet;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.spi.query.filter.IDataDomainFilter;

/**
 * @author Samuel Gratzl
 *
 */
public class SpecificDataDomainFilter extends HashSet<IDataDomainFilter> implements IDataDomainFilter {
	private static final long serialVersionUID = -8763101069844506294L;
	private final IDataDomain dataDomain;

	/**
	 * @param dataDomain
	 */
	public SpecificDataDomainFilter(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public SpecificDataDomainFilter clone() {
		SpecificDataDomainFilter clone = new SpecificDataDomainFilter(dataDomain);
		for (IDataDomainFilter f : this)
			clone.add(f.clone());
		return clone;
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public boolean apply(Pair<? extends ARecordPerspective, Group> pair) {
		if (!dataDomain.equals(pair.getFirst().getDataDomain()))
			return true;
		for (IDataDomainFilter child : this)
			if (!child.apply(pair))
				return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = ((dataDomain == null) ? 0 : dataDomain.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		// no super call!
		SpecificDataDomainFilter other = (SpecificDataDomainFilter) obj;
		if (dataDomain == null) {
			if (other.dataDomain != null)
				return false;
		} else if (!dataDomain.equals(other.dataDomain))
			return false;
		return true;
	}
}
