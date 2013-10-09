/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals which of the {@link ESampleMappingMode}s should be used in
 * the pathway.
 * 
 * @author Alexander Lex
 * 
 */
public class SampleMappingModeEvent
	extends AEvent {

	private ESampleMappingMode sampleMappingMode = null;

	/**
	 * 
	 */
	public SampleMappingModeEvent() {
	}

	public SampleMappingModeEvent(ESampleMappingMode sampleMappingMode) {
		this.sampleMappingMode = sampleMappingMode;
	}

	/**
	 * @param sampleMappingMode setter, see {@link #sampleMappingMode}
	 */
	public void setSampleMappingMode(ESampleMappingMode sampleMappingMode) {
		this.sampleMappingMode = sampleMappingMode;
	}

	/**
	 * @return the sampleMappingMode, see {@link #sampleMappingMode}
	 */
	public ESampleMappingMode getSampleMappingMode() {
		return sampleMappingMode;
	}

	@Override
	public boolean checkIntegrity() {
		if (sampleMappingMode == null)
			return false;

		return true;
	}

}
