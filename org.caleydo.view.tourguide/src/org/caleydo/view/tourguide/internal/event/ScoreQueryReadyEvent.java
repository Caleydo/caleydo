/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import java.util.Collection;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * marker event that the scores for a given set of scores are computed
 * 
 * @author Samuel Gratzl
 * 
 */
public class ScoreQueryReadyEvent extends ADirectedEvent {
	private final Collection<IScore> scores;

	public ScoreQueryReadyEvent(Collection<IScore> scores) {
		this.scores = scores;
	}
	/**
	 * @return the scores, see {@link #scores}
	 */
	public Collection<IScore> getScores() {
		return scores;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}

