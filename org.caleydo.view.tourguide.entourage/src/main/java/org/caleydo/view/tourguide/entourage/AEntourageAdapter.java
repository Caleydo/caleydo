/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.RcpGLSubGraphView;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapter;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Samuel Gratzl
 *
 */
abstract class AEntourageAdapter implements IViewAdapter {
	protected final GLEntourage entourage;
	protected final ITourGuideView vis;

	public AEntourageAdapter(GLEntourage entourage, ITourGuideView vis) {
		this.entourage = entourage;
		this.vis = vis;
	}

	@Override
	public void attach() {

	}

	@Override
	public void detach() {

	}


	@Override
	public void preDisplay() {

	}

	@Override
	public boolean canShowPreviews() {
		return true;
	}

	@Override
	public void onRowClick(RankTableModel table, PickingMode pickingMode, AScoreRow row, boolean isSelected) {
	}

	@Override
	public final boolean isRepresenting(IWorkbenchPart part) {
		if (part instanceof RcpGLSubGraphView)
			return ((RcpGLSubGraphView) part).getView() == entourage;
		return false;
	}
}
