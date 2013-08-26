/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * event for editing annotations
 * 
 * @author Samuel Gratzl
 * 
 */
public class AnnotationEditEvent extends ADirectedEvent {
	private String title;
	private String description;

	public AnnotationEditEvent(String title, String description) {
		super();
		this.title = title;
		this.description = description;
	}

	/**
	 * @return the title, see {@link #title}
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the description, see {@link #description}
	 */
	public String getDescription() {
		return description;
	}


	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
