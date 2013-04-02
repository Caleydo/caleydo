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
package org.caleydo.view.tourguide.internal.compute;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.tourguide.internal.event.ScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Samuel Gratzl
 *
 */
public class ComputeForScoreJob extends AComputeJob {
	private final Collection<IScore> scores;
	private final List<PerspectiveRow> data;
	private final BitSet mask;

	@SuppressWarnings("unchecked")
	public ComputeForScoreJob(Collection<IScore> scores, List<?> data, BitSet mask, Object receiver) {
		super(scores, receiver);
		this.data = (List<PerspectiveRow>) data;
		this.mask = mask;
		this.scores = scores;
	}

	@Override
	public boolean hasThingsToDo() {
		if (mask.isEmpty())
			return false;
		return super.hasThingsToDo();
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		IStatus result = runImpl(monitor, data, mask);
		EventPublisher.trigger(new ScoreQueryReadyEvent(scores).to(receiver));
		return result;
	}
}
