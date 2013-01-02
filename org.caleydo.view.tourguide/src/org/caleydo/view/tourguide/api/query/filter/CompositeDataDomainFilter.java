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

import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.spi.query.filter.IDataDomainFilter;

/**
 * @author Samuel Gratzl
 *
 */
public class CompositeDataDomainFilter extends HashSet<IDataDomainFilter> implements IDataDomainFilter {
	private static final long serialVersionUID = -8763101069844506294L;

	@Override
	public boolean apply(Pair<? extends ARecordPerspective, Group> pair) {
		for (IDataDomainFilter child : this)
			if (!child.apply(pair))
				return false;
		return true;
	}
}
