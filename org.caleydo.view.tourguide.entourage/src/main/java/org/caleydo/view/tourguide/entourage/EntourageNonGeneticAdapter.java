/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.util.Collection;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.view.entourage.datamapping.DataMappingState;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class EntourageNonGeneticAdapter extends AEntourageAdapter {
	private final NonGeneticDataMode mode = new NonGeneticDataMode();
	public EntourageNonGeneticAdapter() {
		super();
	}

	@Override
	public String getSecondaryID() {
		return EntourageNonGeneticAdapterFactory.SECONDARY_ID;
	}

	@Override
	public String getPartName() {
		return "Non-Genetic";
	}

	@Override
	public void addDefaultColumns(RankTableModel table) {
		asMode().addDefaultColumns(table);
	}

	@Override
	public ITourGuideDataMode asMode() {
		return mode;
	}

	@Override
	public boolean filterBoundView(IDataDomain dataDomain) {
		if (entourage == null)
			return true;
		final IDCategory target = entourage.getDataMappingState().getExperimentalDataIDCategory();
		assert dataDomain instanceof ATableBasedDataDomain;
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;
		return d.getRecordIDCategory().equals(target) || d.getDimensionIDCategory().equals(target);
	}

	@Override
	protected void loadViewState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanup() {
		super.cleanup();
	}

	@Override
	public boolean isPreviewing(AScoreRow row) {
		return false; // just a single selection
	}

	@Override
	public boolean isVisible(AScoreRow row) {
		// FIXME
		return false;
	}

	@Override
	public void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleScores, IScore sortedByScore) {
		if (entourage == null)
			return;
		assert old == null || old instanceof ITablePerspectiveScoreRow;
		assert new_ == null || new_ instanceof ITablePerspectiveScoreRow;

		DataMappingState dmState = entourage.getDataMappingState();
		TablePerspective tp = ((ITablePerspectiveScoreRow) new_).asTablePerspective();
		if (tp.getRecordPerspective().getIdType().getIDCategory() == dmState.getExperimentalDataIDCategory()) {
			dmState.addContextualTablePerspective(tp.getDataDomain(), tp.getDimensionPerspective());
		} else if (tp.getDimensionPerspective().getIdType().getIDCategory() == dmState.getExperimentalDataIDCategory()) {
			dmState.addContextualTablePerspective(tp.getDataDomain(), tp.getRecordPerspective());
		}
	}

	@Override
	public boolean canShowPreviews() {
		return true;
	}
}
