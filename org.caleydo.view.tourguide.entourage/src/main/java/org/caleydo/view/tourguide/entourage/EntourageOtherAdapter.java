/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.util.Collection;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.tourguide.api.model.AScoreRow;
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
		// FIXME
	}

	@Override
	public boolean canShowPreviews() {
		return true;
	}
}
