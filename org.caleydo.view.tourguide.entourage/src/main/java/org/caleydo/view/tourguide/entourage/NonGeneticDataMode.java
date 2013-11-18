/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.api.adapter.ATourGuideDataMode;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class NonGeneticDataMode extends ATourGuideDataMode {

	@Override
	public boolean apply(IDataDomain input) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return null;
	}
}
