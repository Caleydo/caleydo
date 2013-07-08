/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.view.stratomex.event;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals that a brick should be split based on a band.
 * 
 * @author Alexander Lex
 */
public class SplitBrickEvent extends AEvent {

	/** The id of the band which should be used for splitting */
	private Integer connectionBandID = null;
	/**
	 * Flag telling whether the left brick of the band (true) or the right brick
	 * (false) should be split
	 */
	private Boolean splitLeftBrick = null;

	/**
	 * 
	 */
	public SplitBrickEvent() {
	}

	public SplitBrickEvent(Integer connectionBandID, Boolean splitLeftBrick) {
		this.splitLeftBrick = splitLeftBrick;
		this.connectionBandID = connectionBandID;
	}

	/**
	 * @param connectionBandID
	 *            setter, see {@link #connectionBandID}
	 */
	public void setConnectionBandID(Integer connectionBandID) {
		this.connectionBandID = connectionBandID;
	}

	/**
	 * @return the connectionBandID, see {@link #connectionBandID}
	 */
	public Integer getConnectionBandID() {
		return connectionBandID;
	}

	/**
	 * @param splitLeftBrick
	 *            setter, see {@link #splitLeftBrick}
	 */
	public void setSplitLeftBrick(Boolean splitLeftBrick) {
		this.splitLeftBrick = splitLeftBrick;
	}

	/**
	 * @return the spliteLeftBrick, see {@link #splitLeftBrick}
	 */
	public Boolean isSplitLeftBrick() {
		return splitLeftBrick;
	}

	@Override
	public boolean checkIntegrity() {
		if (splitLeftBrick == null || connectionBandID == null)
			return false;

		return true;
	}
}
