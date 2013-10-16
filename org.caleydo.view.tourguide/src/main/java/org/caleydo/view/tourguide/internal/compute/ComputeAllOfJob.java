/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.compute;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.internal.event.InitialScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.event.ScoreQueryReadyEvent;
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
		progress(0.0f, "Preparing Data");
		boolean creating = !query.isInitialized();
		List<AScoreRow> data = query.getOrCreate();
		BitSet mask = query.getRawMask();
		System.out.println("done in " + w);
		progress(0.0f, "Computing Scores");
		IStatus result = runImpl(monitor, data, mask);
		if (creating)
			EventPublisher.trigger(new InitialScoreQueryReadyEvent(query).to(receiver));
		else
			EventPublisher.trigger(new ScoreQueryReadyEvent(null).to(receiver));
		return result;
	}

}
