/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.specific;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.vis.lineup.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public interface IDataDomainQueryModeSpecfics {

	/**
	 * @return
	 */
	Iterable<? extends ADataDomainQuery> createDataDomainQueries();

	/**
	 * @param table
	 * @param glTourGuideView
	 */
	void addDefaultColumns(RankTableModel table);

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
}

