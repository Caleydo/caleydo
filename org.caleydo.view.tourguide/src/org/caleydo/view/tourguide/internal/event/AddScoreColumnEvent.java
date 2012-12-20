/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public class AddScoreColumnEvent extends AEvent {
	private IScore[] scores;

	public AddScoreColumnEvent() {

	}

	public AddScoreColumnEvent(IScore score, ScoreQueryUI sender) {
		this(sender, score);
	}

	public AddScoreColumnEvent(ScoreQueryUI sender, IScore... scores) {
		this.scores = scores;
		this.setSender(sender);
	}

	public IScore[] getScores() {
		return scores;
	}

	@Override
	public boolean checkIntegrity() {
		return scores != null;
	}
}

