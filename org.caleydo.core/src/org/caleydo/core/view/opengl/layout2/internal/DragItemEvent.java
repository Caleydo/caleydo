/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDnDItem;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragGLSource;

/**
 * @author Samuel Gratzl
 *
 */
public class DragItemEvent extends ADirectedEvent {

	private final IDnDItem item;
	private final IDragGLSource source;
	private final boolean finished;
	/**
	 * @param active
	 */
	public DragItemEvent(IDnDItem item, IDragGLSource source, boolean finished) {
		this.item = item;
		this.source = source;
		this.finished = finished;
	}

	/**
	 * @return the finished, see {@link #finished}
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * @return the item, see {@link #item}
	 */
	public IDnDItem getItem() {
		return item;
	}

	/**
	 * @return the source, see {@link #source}
	 */
	public IDragGLSource getSource() {
		return source;
	}

}
