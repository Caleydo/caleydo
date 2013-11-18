/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.view.tourguide.api.adapter.ATourGuideDataMode;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.api.model.InhomogenousDataDomainQuery;
import org.caleydo.view.tourguide.api.model.StratificationDataDomainQuery;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;

import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class NonGeneticDataMode extends ATourGuideDataMode {
	private final IDCategory gene = IDCategory.getIDCategory(EGeneIDTypes.GENE.name());

	@Override
	public boolean apply(IDataDomain input) {
		if (!(input instanceof ATableBasedDataDomain))
			return false;
		ATableBasedDataDomain d = (ATableBasedDataDomain)input;
		if (!DataSupportDefinitions.homogenousTables.apply(d))
			return !d.getRecordIDCategory().equals(gene);

		// FIXME
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
	protected ADataDomainQuery createFor(IDataDomain dd) {
		if (!DataSupportDefinitions.homogenousTables.apply(dd))
			return new InhomogenousDataDomainQuery((ATableBasedDataDomain) dd, Sets.immutableEnumSet(
					EDataClass.NATURAL_NUMBER, EDataClass.REAL_NUMBER));
		if (DataSupportDefinitions.categoricalTables.apply(dd))
			return new CategoricalDataDomainQuery((ATableBasedDataDomain) dd, geneDimension((ATableBasedDataDomain) dd)
					.opposite());
		return new StratificationDataDomainQuery((ATableBasedDataDomain) dd, geneDimension((ATableBasedDataDomain) dd)
				.opposite());
	}

	/**
	 * @param dd
	 * @return
	 */
	private EDimension geneDimension(ATableBasedDataDomain dd) {
		return EDimension.get(dd.getDimensionIDCategory().equals(gene));
	}
}
