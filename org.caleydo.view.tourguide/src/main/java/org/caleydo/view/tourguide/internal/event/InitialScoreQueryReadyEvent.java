/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.compute.ComputeAllOfJob;

/**
 * result of a {@link ComputeAllOfJob} where all scores for the newly initialized {@link ADataDomainQuery} were computed
 * 
 * @author Samuel Gratzl
 * 
 */
public class InitialScoreQueryReadyEvent extends ADirectedEvent {
	private final ADataDomainQuery newQuery;

	public InitialScoreQueryReadyEvent(ADataDomainQuery newQuery) {
		this.newQuery = newQuery;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the newQuery, see {@link #newQuery}
	 */
	public ADataDomainQuery getNewQuery() {
		return newQuery;
	}
}

