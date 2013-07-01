/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.event;

import java.util.List;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Event for merging bricks of a column.
 *
 * @author Christian Partl
 *
 */
public class MergeBricksEvent extends ADirectedEvent {

	private List<GLBrick> bricks;

	public MergeBricksEvent(List<GLBrick> bricks) {
		this.bricks = bricks;
	}

	@Override
	public boolean checkIntegrity() {
		return bricks != null;
	}

	/**
	 * @param bricks
	 *            setter, see {@link bricks}
	 */
	public void setBricks(List<GLBrick> bricks) {
		this.bricks = bricks;
	}

	/**
	 * @return the bricks, see {@link #bricks}
	 */
	public List<GLBrick> getBricks() {
		return bricks;
	}

}
