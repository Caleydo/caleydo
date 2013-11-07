/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.util.Collection;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.datamapping.DataMappingState;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public class EntourageOtherAdapter extends AEntourageAdapter {

	public EntourageOtherAdapter(GLEntourage entourage, ITourGuideView vis) {
		super(entourage, vis);
	}

	@Override
	public void setup(GLElementContainer lineUp) {

	}

	@Override
	public void cleanup(GLElementContainer lineUp) {

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
	public void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleScores, EDataDomainQueryMode mode,
			IScore sortedByScore) {
		if (mode != EDataDomainQueryMode.OTHER)
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
