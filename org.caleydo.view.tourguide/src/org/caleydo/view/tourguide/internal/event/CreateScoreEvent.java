/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * triggers to create a new score given by name
 * 
 * @author Samuel Gratzl
 * 
 */
public class CreateScoreEvent extends ADirectedEvent {

	private final String score;

	public CreateScoreEvent(String name) {
		this.score = name;
	}

	/**
	 * @return the score, see {@link #score}
	 */
	public String getScore() {
		return score;
	}

	@Override
	public boolean checkIntegrity() {
		return score != null;
	}

}

