/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDnDItem;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDropGLTarget;

/**
 * @author Samuel Gratzl
 *
 */
public class DropEnterLeaveItemEvent extends ADirectedEvent {

	private final IDnDItem item;
	private final IDropGLTarget target;
	private final boolean entering;

	/**
	 * @param active
	 */
	public DropEnterLeaveItemEvent(IDnDItem item, IDropGLTarget target, boolean entering) {
		this.item = item;
		this.target = target;
		this.entering = entering;
	}

	/**
	 * @return the item, see {@link #item}
	 */
	public IDnDItem getItem() {
		return item;
	}

	/**
	 * @return the target, see {@link #target}
	 */
	public IDropGLTarget getTarget() {
		return target;
	}

	/**
	 * @return the entering, see {@link #entering}
	 */
	public boolean isEntering() {
		return entering;
	}

}
