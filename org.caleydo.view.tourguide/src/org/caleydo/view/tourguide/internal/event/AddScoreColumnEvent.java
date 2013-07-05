/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import java.util.Arrays;
import java.util.Collection;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * event for adding scores to tour guide
 * 
 * @author Samuel Gratzl
 * 
 */
public class AddScoreColumnEvent extends ADirectedEvent {
	private final Collection<IScore> scores;

	public AddScoreColumnEvent(Collection<IScore> scores) {
		this.scores = scores;
	}

	public AddScoreColumnEvent(IScore... scores) {
		this(Arrays.asList(scores));
	}

	public Collection<IScore> getScores() {
		return scores;
	}

	@Override
	public boolean checkIntegrity() {
		return scores != null;
	}
}

