/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
