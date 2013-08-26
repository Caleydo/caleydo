/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import java.util.List;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.internal.compute.ComputeExtrasJob;
import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.model.AScoreRow;

/**
 * response of the {@link ComputeExtrasJob} which triggers to add extra lines a a set of given {@link ADataDomainQuery}
 * 
 * @author Samuel Gratzl
 * 
 */
public class ExtraInitialScoreQueryReadyEvent extends ADirectedEvent {

	private final List<Pair<ADataDomainQuery, List<AScoreRow>>> extras;

	public ExtraInitialScoreQueryReadyEvent(List<Pair<ADataDomainQuery,List<AScoreRow>>> extras) {
		this.extras = extras;
	}

	/**
	 * @return the extras, see {@link #extras}
	 */
	public List<Pair<ADataDomainQuery, List<AScoreRow>>> getExtras() {
		return extras;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}

