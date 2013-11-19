/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.model;

import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.view.tourguide.api.model.ASingleIDDataDomainQuery;
import org.caleydo.view.tourguide.api.model.GroupInfo;
import org.caleydo.vis.lineup.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class SingleIDDataDomainQuery extends ASingleIDDataDomainQuery {
	public SingleIDDataDomainQuery(ATableBasedDataDomain dataDomain, EDimension dim) {
		super(dataDomain, dim);
	}

	@Override
	protected VirtualArray createVirtualArrayImpl(String label, Integer id) {
		TablePerspective t = getDataDomain().getDefaultTablePerspective();
		return dim.opposite().select(t.getDimensionPerspective(), t.getRecordPerspective()).getVirtualArray();
	}

	@Override
	public Collection<GroupInfo> getGroupInfos(Integer id) {
		return Collections.emptyList();
	}

	@Override
	public int getGroupSize(Integer id) {
		return 0;
	}

	@Override
	protected boolean hasFilterImpl() {
		return false;
	}

	@Override
	public void createSpecificColumns(RankTableModel table) {

	}

	@Override
	public void removeSpecificColumns(RankTableModel table) {

	}

	@Override
	public void updateSpecificColumns(RankTableModel table) {

	}

}

