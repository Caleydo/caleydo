/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.spi.adapter;

import java.util.Collection;

import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.model.AScoreRow;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Samuel Gratzl
 *
 */
public interface IViewAdapter {

	/**
	 *
	 */
	void attach();

	/**
	 *
	 */
	void detach();

	/**
	 *
	 */
	void cleanUp();

	/**
	 *
	 */
	void setup();

	/**
	 * @param row
	 * @return
	 */
	boolean isPreviewing(AScoreRow row);

	/**
	 * @param row
	 * @return
	 */

	boolean isVisible(AScoreRow row);

	/**
	 * @param old
	 * @param new_
	 * @param visibleScores
	 * @param mode
	 * @param sortedByScore
	 */
	void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleScores, EDataDomainQueryMode mode,
			IScore sortedByScore);

	/**
	 *
	 */
	void preDisplay();

	/**
	 * @return
	 */
	boolean canShowPreviews();

	/**
	 * @param part
	 * @return
	 */
	boolean isRepresenting(IWorkbenchPart part);
}
