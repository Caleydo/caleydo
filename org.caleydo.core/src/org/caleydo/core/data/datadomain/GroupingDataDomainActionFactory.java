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
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.datadomain.DataDomainActions.IDataDomainActionFactory;
import org.caleydo.core.data.datadomain.event.CreateClusteringEvent;
import org.caleydo.core.data.datadomain.event.LoadGroupingEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.collection.Pair;

/**
 * @author Samuel Gratzl
 *
 */
public class GroupingDataDomainActionFactory implements IDataDomainActionFactory {
	@Override
	public Collection<Pair<String, ? extends AEvent>> create(IDataDomain dataDomain, Object sender) {
		Collection<Pair<String, ? extends AEvent>> r = new ArrayList<>(4);
		if (dataDomain instanceof ATableBasedDataDomain) {
			ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;

			r.add(Pair.make("Load grouping for " + d.getDimensionIDCategory().getCategoryName(), new LoadGroupingEvent(
					d, d.getDimensionIDCategory()).from(sender)));
			r.add(Pair.make("Load grouping for " + d.getRecordIDCategory().getCategoryName(), new LoadGroupingEvent(d,
					d.getRecordIDCategory()).from(sender)));
			r.add(Pair.make(
					"Create grouping for " + d.getDimensionIDCategory().getCategoryName() + " using Clustering",
					new CreateClusteringEvent(d, true).from(sender)));
			r.add(Pair.make("Create grouping for " + d.getRecordIDCategory().getCategoryName() + " using Clustering",
					new CreateClusteringEvent(d, false).from(sender)));
		}
		return r;
	}
}
