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
 * descriptor, what data domains should be shown within TourGuide
 * 
 * @author Samuel Gratzl
 * 
 */
public interface ITourGuideDataMode extends Predicate<IDataDomain> {
	/**
	 * @return a list of {@link ADataDomainQuery} elements for all the matching data domains
	 */
	Iterable<? extends ADataDomainQuery> createDataDomainQueries();

	/**
	 * create {@link ADataDomainQuery} elements for the specific data domain
	 * 
	 * @param dd
	 * @return
	 */
	Iterable<? extends ADataDomainQuery> createDataDomainQuery(IDataDomain dd);

	/**
	 * @return the number of categories to split up the data domains in vertical mode
	 */
	int getNumCategories();

	/**
	 * @return the category of a specific data domain
	 */
	int getCategory(IDataDomain dataDomain);

	/**
	 * add default columns to the {@link RankTableModel} for this data domain mode, e.g. label, metrics,...
	 * 
	 * @param table
	 */
	void addDefaultColumns(RankTableModel table);
}

