/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.ranking;

import org.caleydo.core.event.AEvent;

/**
 * @author Christian
 *
 */
public class RankPathwaysEvent extends AEvent {

	private final IPathwayRanking ranking;

	public RankPathwaysEvent(IPathwayRanking ranking) {
		this.ranking = ranking;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the ranking, see {@link #ranking}
	 */
	public IPathwayRanking getRanking() {
		return ranking;
	}

}
