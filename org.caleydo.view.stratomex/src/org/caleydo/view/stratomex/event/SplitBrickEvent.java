/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
