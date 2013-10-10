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
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.internal.event.ScoreQueryReadyEvent;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Samuel Gratzl
 *
 */
public class ComputeForScoreJob extends AComputeJob {
	private final Collection<IScore> scores;
	private final List<AScoreRow> data;
	private final BitSet mask;

	@SuppressWarnings("unchecked")
	public ComputeForScoreJob(Collection<IScore> scores, List<?> data, BitSet mask, Object receiver) {
		super(scores, receiver);
		this.data = (List<AScoreRow>) data;
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
