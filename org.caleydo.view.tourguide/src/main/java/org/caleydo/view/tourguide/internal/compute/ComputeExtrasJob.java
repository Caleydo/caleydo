/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.compute;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.internal.event.ExtraInitialScoreQueryReadyEvent;
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
