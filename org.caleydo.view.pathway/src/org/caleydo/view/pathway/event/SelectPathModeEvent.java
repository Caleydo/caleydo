/**
 * 
 */
package org.caleydo.view.pathway.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.pathway.GLPathway;

/**
 * Event that tells whether path selection mode should be enabled.
 * 
 * @author Christian Partl
 * 
 */
public class SelectPathModeEvent extends AEvent {

	/**
	 * Determines whether the path selection mode should be enabled in a
	 * {@link GLPathway} view.
	 */
	private boolean isPathSelectionMode = false;

	public SelectPathModeEvent(boolean isPathSelectionMode) {
		this.isPathSelectionMode = isPathSelectionMode;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param isPathSelectionMode
	 *            setter, see {@link #isPathSelectionMode}
	 */
	public void setPathSelectionMode(boolean isPathSelectionMode) {
		this.isPathSelectionMode = isPathSelectionMode;
	}

	/**
	 * @return the isPathSelectionMode, see {@link #isPathSelectionMode}
	 */
	public boolean isPathSelectionMode() {
		return isPathSelectionMode;
	}

}
