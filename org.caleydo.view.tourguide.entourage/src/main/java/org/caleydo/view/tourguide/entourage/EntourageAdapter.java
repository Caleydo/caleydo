/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.util.Collection;

import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.RcpGLSubGraphView;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapter;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Samuel Gratzl
 *
 */
public class EntourageAdapter implements IViewAdapter {

	private final GLEntourage entourage;
	private final ITourGuideView vis;

	/**
	 * @param entourage
	 * @param vis
	 */
	public EntourageAdapter(GLEntourage entourage, ITourGuideView vis) {
		this.entourage = entourage;
		this.vis = vis;
	}

	@Override
	public void attach() {
		// TODO Auto-generated method stub

	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPreviewing(AScoreRow row) {
		assert row instanceof ITablePerspectiveScoreRow;
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVisible(AScoreRow row) {
		assert row instanceof ITablePerspectiveScoreRow;
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleScores, EDataDomainQueryMode mode,
			IScore sortedByScore) {
		if (mode != EDataDomainQueryMode.STRATIFICATIONS)
			return;
		assert old == null || old instanceof ITablePerspectiveScoreRow;
		assert new_ == null || new_ instanceof ITablePerspectiveScoreRow;
		// TODO Auto-generated method stub

	}

	@Override
	public void preDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canShowPreviews() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRepresenting(IWorkbenchPart part) {
		if (part instanceof RcpGLSubGraphView)
			return ((RcpGLSubGraphView) part).getView() == entourage;
		return false;
	}
}
