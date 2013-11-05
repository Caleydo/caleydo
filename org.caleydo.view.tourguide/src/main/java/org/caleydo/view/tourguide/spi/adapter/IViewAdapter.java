/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.spi.adapter;

import java.util.Collection;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Samuel Gratzl
 *
 */
public interface IViewAdapter {

	/**
	 * attach the adapter the view
	 */
	void attach();

	/**
	 * detach the adapter from the view
	 */
	void detach();

	/**
	 * last detach
	 */
	void cleanup(GLElementContainer lineUp);

	/**
	 * first attach version
	 */
	void setup(GLElementContainer lineUp);

	/**
	 * is the given row currently previewed?
	 *
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

	/**
	 * @param table
	 * @param pickingMode
	 * @param row
	 * @param isSelected
	 */
	void onRowClick(RankTableModel table, PickingMode pickingMode, AScoreRow row, boolean isSelected,
			IGLElementContext context);
}
