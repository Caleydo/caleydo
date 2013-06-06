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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.internal.event.ExtraInitialScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.model.AScoreRow;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * special job for computing a bunch of entries for a given {@link ADataDomainQuery}
 * 
 * @author Samuel Gratzl
 * 
 */
public class ComputeExtrasJob extends AComputeJob {
	private static final Logger log = Logger.create(ComputeExtrasJob.class);

	private List<Pair<ADataDomainQuery, List<AScoreRow>>> extras;

	public ComputeExtrasJob(List<Pair<ADataDomainQuery, List<AScoreRow>>> extras, Collection<IScore> scores,
			Object receiver) {
		super(scores, receiver);
		this.extras = extras;
	}

	@Override
	public boolean hasThingsToDo() {
		return super.hasThingsToDo();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		log.info("compute new data for");
		progress(0.0f, "Preparing Data");

		List<AScoreRow> data = new ArrayList<>();
		for (Pair<ADataDomainQuery, List<AScoreRow>> pair : extras) {
			data.addAll(pair.getSecond());
		}
		BitSet mask = new BitSet(data.size());
		mask.set(0, data.size()); // set all

		progress(0.0f, "Computing Scores");
		IStatus result = runImpl(monitor, data, mask);
		EventPublisher.trigger(new ExtraInitialScoreQueryReadyEvent(extras).to(receiver));
		return result;
	}

}
