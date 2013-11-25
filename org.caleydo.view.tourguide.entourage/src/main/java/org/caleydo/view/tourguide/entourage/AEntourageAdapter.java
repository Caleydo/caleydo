/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.net.URL;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.RcpGLSubGraphView;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Samuel Gratzl
 *
 */
abstract class AEntourageAdapter implements ITourGuideAdapter {
	protected GLEntourage entourage;
	protected ITourGuideView vis;

	public AEntourageAdapter() {

	}

	@Override
	public void setup(ITourGuideView vis, GLElementContainer lineUp) {
		this.vis = vis;
	}

	@Override
	public void cleanup() {
		this.vis = null;
	}

	@Override
	public final String getLabel() {
		return entourage == null ? "<none>" : entourage.getLabel();
	}

	@Override
	public URL getIcon() {
		return AEntourageAdapter.class.getResource("icon.png");
	}

	@Override
	public void preDisplay() {

	}

	@Override
	public boolean canShowPreviews() {
		return true;
	}

	@Override
	public void onRowClick(RankTableModel table, PickingMode pickingMode, AScoreRow row, boolean isSelected,
			IGLElementContext context) {
	}

	@Override
	public boolean filterBoundView(ADataDomainQuery query) {
		return true;
	}

	@Override
	public final boolean isRepresenting(IWorkbenchPart part, boolean isBoundTo) {
		if (part instanceof RcpGLSubGraphView)
			return !isBoundTo || ((RcpGLSubGraphView) part).getView() == entourage;
		return false;
	}

	@Override
	public final void bindTo(IViewPart part) {
		if (part instanceof RcpGLSubGraphView) {
			this.entourage = ((RcpGLSubGraphView) part).getView();
		} else
			this.entourage = null;
		if (vis != null) {
			vis.updateBound2ViewState();
			loadViewState();
		}
	}

	@Override
	public final boolean ignoreActive(IViewPart part) {
		return false;
	}

	@Override
	public final boolean isBound2View() {
		return entourage != null;
	}

	protected abstract void loadViewState();
}
