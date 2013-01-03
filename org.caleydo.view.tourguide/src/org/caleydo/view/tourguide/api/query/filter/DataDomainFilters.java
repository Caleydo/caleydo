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

import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.spi.query.filter.IDataDomainFilter;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainFilters {
	public static final IDataDomainFilter EMPTY_GROUP = new IDataDomainFilter() {
		@Override
		public boolean apply(Pair<? extends ARecordPerspective, Group> pair) {
			return pair.getSecond() == null || pair.getSecond().getSize() > 0;
		}

		@Override
		public IDataDomainFilter clone() {
			return this;
		}
	};

	public static final IDataDomainFilter DEFAULT_GROUP = new IDataDomainFilter() {
		@Override
		public boolean apply(Pair<? extends ARecordPerspective, Group> pair) {
			return !pair.getFirst().isDefault();
		}

		@Override
		public IDataDomainFilter clone() {
			return this;
		}
	};
}
