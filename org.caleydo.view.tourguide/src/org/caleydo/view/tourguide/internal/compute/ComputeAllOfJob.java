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
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.internal.event.ScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;
import org.caleydo.view.tourguide.internal.view.model.ADataDomainQuery;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.google.common.base.Stopwatch;

/**
 * @author Samuel Gratzl
 *
 */
public class ComputeAllOfJob extends AComputeJob {
	private static final Logger log = Logger.create(ComputeAllOfJob.class);

	private final ADataDomainQuery query;

	public ComputeAllOfJob(ADataDomainQuery q, Collection<IScore> scores, Object receiver) {
		super(scores, receiver);
		this.query = q;
		this.receiver = receiver;
	}

	@Override
	public boolean hasThingsToDo() {
		if (!query.isInitialized())
			return true;
		return super.hasThingsToDo();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Stopwatch w = new Stopwatch().start();
		log.info("compute the data for datadomain: " + query.getDataDomain().getLabel());
		boolean creating = !query.isInitialized();
		List<PerspectiveRow> data = query.getOrCreate();
		BitSet mask = query.getMask();
		System.out.println("done in " + w);

		IStatus result = runImpl(monitor, data, mask);
		EventPublisher.trigger(new ScoreQueryReadyEvent(creating ? query : null).to(receiver));
		return result;
	}

}
