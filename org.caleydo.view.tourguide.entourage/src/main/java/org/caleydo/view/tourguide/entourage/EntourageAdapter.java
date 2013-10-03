/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.util.Collection;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.RcpGLSubGraphView;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.entourage.ui.DataDomainElements;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapter;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Samuel Gratzl
 *
 */
public class EntourageAdapter implements IViewAdapter, ISelectionCallback {

	private final GLEntourage entourage;
	private final ITourGuideView vis;

	private final DataDomainElements dataDomains = new DataDomainElements();

	/**
	 * @param entourage
	 * @param vis
	 */
	public EntourageAdapter(GLEntourage entourage, ITourGuideView vis) {
		this.entourage = entourage;
		this.vis = vis;
		this.dataDomains.setCallback(this);

		for (GeneticDataDomain d : DataDomainManager.get().getDataDomainsByType(GeneticDataDomain.class)) {
			this.dataDomains.addDataDomain(d);
		}
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
	public void cleanup(GLElementContainer lineUp) {
		lineUp.remove(0);

	}

	@Override
	public void setup(GLElementContainer lineUp) {
		lineUp.add(0, this.dataDomains);
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
	public void onSelectionChanged(GLButton button, boolean selected) {
		final IDataDomain dataDomain = button.getLayoutDataAs(IDataDomain.class, null);
		assert dataDomain != null;

		// TODO dataDomain selection update
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
