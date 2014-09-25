/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.datadomain.DataDomainActions.IDataDomainActionFactory;
import org.caleydo.core.data.datadomain.event.CreateClusteringEvent;
import org.caleydo.core.data.datadomain.event.LoadGroupingEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.core.util.collection.Pair;

import com.google.common.collect.Collections2;

/**
 * @author Samuel Gratzl
 *
 */
public class GroupingDataDomainActionFactory implements IDataDomainActionFactory {
	@Override
	public Collection<Pair<String, Runnable>> create(IDataDomain dataDomain, Object sender) {
		Collection<Pair<String, ? extends AEvent>> r = new ArrayList<>(4);
		if (dataDomain instanceof ATableBasedDataDomain) {
			ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;

			r.add(Pair.make("Load Grouping for " + d.getDimensionIDCategory().getCategoryName(), new LoadGroupingEvent(
					d, d.getDimensionIDCategory()).from(sender)));
			r.add(Pair.make("Load Grouping for " + d.getRecordIDCategory().getCategoryName(), new LoadGroupingEvent(d,
					d.getRecordIDCategory()).from(sender)));
			r.add(Pair.make(
					"Create Grouping for " + d.getDimensionIDCategory().getCategoryName() + " using Clustering",
					new CreateClusteringEvent(d, true).from(sender)));
			r.add(Pair.make("Create Grouping for " + d.getRecordIDCategory().getCategoryName() + " using Clustering",
					new CreateClusteringEvent(d, false).from(sender)));
		}
		return Collections2.transform(r, Runnables.SEND_EVENTS);
	}
}
