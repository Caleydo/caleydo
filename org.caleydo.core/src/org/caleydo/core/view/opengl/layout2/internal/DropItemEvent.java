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
public class DropItemEvent extends ADirectedEvent {

	private final IDnDItem item;
	private final IDropGLTarget target;
	private final boolean dropping;

	/**
	 * @param active
	 */
	public DropItemEvent(IDnDItem item, IDropGLTarget target, boolean dropping) {
		this.item = item;
		this.target = target;
		this.dropping = dropping;
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
	 * @return
	 */
	public boolean isDropping() {
		return dropping;
	}

}
