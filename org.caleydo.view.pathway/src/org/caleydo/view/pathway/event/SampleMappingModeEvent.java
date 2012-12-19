/**
 * 
 */
package org.caleydo.view.pathway.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.pathway.ESampleMappingMode;

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
