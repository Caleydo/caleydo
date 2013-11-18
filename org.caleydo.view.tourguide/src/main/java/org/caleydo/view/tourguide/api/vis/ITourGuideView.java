/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.api.vis;

import java.util.List;

import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public interface ITourGuideView {

	/**
	 * removes all leading score columns, e.g. to cleanup between states
	 */
	void removeLeadingScoreColumns();

	/**
	 * adds some columns to tour guide
	 */
	void addColumns(IScore... scores);

	/**
	 * clear selection
	 */
	void clearSelection();

	void removeAllSimpleFilter();

	/**
	 * returns the currently selected row or null if none is selected
	 *
	 * @return
	 */
	AScoreRow getSelection();

	void setSelection(AScoreRow row);

	List<ADataDomainQuery> getQueries();

	void updateQueryUIStates();

}
