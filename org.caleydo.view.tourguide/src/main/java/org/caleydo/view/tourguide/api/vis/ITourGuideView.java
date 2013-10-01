/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.api.vis;

import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public interface ITourGuideView {

	/**
	 *
	 */
	void removeLeadingScoreColumns();

	/**
	 * @param scores
	 */
	void addColumns(IScore... scores);

	/**
	 * @param object
	 */
	void clearSelection();

}
