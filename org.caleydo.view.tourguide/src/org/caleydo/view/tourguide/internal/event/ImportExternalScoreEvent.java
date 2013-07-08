/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;

/**
 * triggers the import dialog of an external score
 * 
 * @author Samuel Gratzl
 * 
 */
public class ImportExternalScoreEvent extends ADirectedEvent {
	private ATableBasedDataDomain dataDomain;
	private boolean inDimensionDirection;
	private Class<? extends ISerializeableScore> type;

	public ImportExternalScoreEvent(ATableBasedDataDomain dataDomain, boolean inDimensionDirection,
			Class<? extends ISerializeableScore> type) {
		super();
		this.dataDomain = dataDomain;
		this.inDimensionDirection = inDimensionDirection;
		this.type = type;
	}

	@Override
	public boolean checkIntegrity() {
		return dataDomain != null && type != null;
	}

	/**
	 * @return the type, see {@link #type}
	 */
	public Class<? extends ISerializeableScore> getType() {
		return type;
	}

	/**
	 * @return the inDimensionDirection, see {@link #inDimensionDirection}
	 */
	public boolean isInDimensionDirection() {
		return inDimensionDirection;
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

}

