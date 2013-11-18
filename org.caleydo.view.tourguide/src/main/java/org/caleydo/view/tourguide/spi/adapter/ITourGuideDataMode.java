/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.spi.adapter;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.vis.lineup.model.RankTableModel;

import com.google.common.base.Predicate;

/**
 * @author Samuel Gratzl
 *
 */
public interface ITourGuideDataMode extends Predicate<IDataDomain> {
	/**
	 * @return
	 */
	Iterable<? extends ADataDomainQuery> createDataDomainQueries();

	/**
	 * @param dd
	 * @return
	 */
	Iterable<? extends ADataDomainQuery> createDataDomainQuery(IDataDomain dd);

	/**
	 * @return
	 */
	int getNumCategories();

	/**
	 * @param dataDomain
	 * @return
	 */
	int getCategory(IDataDomain dataDomain);

	void addDefaultColumns(RankTableModel table);
}

