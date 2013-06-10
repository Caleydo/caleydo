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
package org.caleydo.view.tourguide.internal.event;

import org.caleydo.core.event.ADirectedEvent;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * event that updates the progress of the state using Events instead of {@link IProgressMonitor}
 * 
 * @author Samuel Gratzl
 * 
 */
public class JobStateProgressEvent extends ADirectedEvent {
	/**
	 * text to show
	 */
	private final String text;
	/**
	 * completion state in percent
	 */
	private final float completed;
	/**
	 * is this an erroneous progress update, i.e the text describes an error
	 */
	private final boolean erroneous;

	public JobStateProgressEvent(String text, float completed, boolean erroneous) {
		this.text = text;
		this.completed = completed;
		this.erroneous = erroneous;
	}

	/**
	 * @return the completed, see {@link #completed}
	 */
	public float getCompleted() {
		return completed;
	}

	/**
	 * @return the erroneous, see {@link #erroneous}
	 */
	public boolean isErroneous() {
		return erroneous;
	}

	/**
	 * @return the text, see {@link #text}
	 */
	public String getText() {
		return text;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
