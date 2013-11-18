/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.mode;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.api.adapter.ATourGuideDataMode;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.InhomogenousDataDomainQuery;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;

import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class VariableDataMode extends ATourGuideDataMode {

	@Override
	public boolean apply(IDataDomain dataDomain) {
		return (dataDomain instanceof ATableBasedDataDomain && !((ATableBasedDataDomain) dataDomain).getTable()
				.isDataHomogeneous())
				&& InhomogenousDataDomainQuery.hasOne(dataDomain,
						Sets.immutableEnumSet(EDataClass.NATURAL_NUMBER, EDataClass.REAL_NUMBER));
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
		return new InhomogenousDataDomainQuery((ATableBasedDataDomain) dd, Sets.immutableEnumSet(
				EDataClass.NATURAL_NUMBER, EDataClass.REAL_NUMBER));
	}
}
