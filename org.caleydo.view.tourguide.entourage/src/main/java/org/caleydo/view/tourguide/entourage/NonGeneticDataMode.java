/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.util.Arrays;
import java.util.Collections;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.tourguide.api.adapter.ATourGuideDataMode;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.api.model.InhomogenousDataDomainQuery;
import org.caleydo.view.tourguide.entourage.model.SingleIDDataDomainQuery;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;

import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class NonGeneticDataMode extends ATourGuideDataMode {
	@Override
	public boolean apply(IDataDomain input) {
		if (!(input instanceof ATableBasedDataDomain))
			return false;
		if (input instanceof GeneticDataDomain)
			return false;
		return true;
	}

	@Override
	public void addDefaultColumns(RankTableModel table) {
		addDataDomainRankColumn(table);
		final StringRankColumnModel base = new StringRankColumnModel(GLRenderers.drawText("Name"),
				StringRankColumnModel.DEFAULT);
		table.add(base);
		base.setWidth(150);
		base.orderByMe();
	}

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQuery(IDataDomain dd) {
		assert dd instanceof ATableBasedDataDomain;
		ATableBasedDataDomain d = (ATableBasedDataDomain) dd;
		if (!DataSupportDefinitions.homogenousTables.apply(dd))
			return Collections.singleton(new InhomogenousDataDomainQuery(d, Sets.immutableEnumSet(
					EDataClass.NATURAL_NUMBER, EDataClass.REAL_NUMBER, EDataClass.CATEGORICAL)));
		if (DataSupportDefinitions.categoricalTables.apply(dd)) {
			return Arrays.asList(new CategoricalDataDomainQuery(d, EDimension.DIMENSION),
					new CategoricalDataDomainQuery(d, EDimension.RECORD));
		} else {
			return Arrays.asList(new SingleIDDataDomainQuery(d, EDimension.DIMENSION), new SingleIDDataDomainQuery(d,
					EDimension.RECORD));
		}
	}
}
