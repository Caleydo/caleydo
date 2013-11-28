/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.model;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.vis.lineup.model.ARow;

/**
 * @author Samuel Gratzl
 *
 */
public class GroupRow extends ARow implements ILabeled {
	private final Group group;

	public GroupRow(Group group) {
		this.group = group;
	}

	@Override
	public String getLabel() {
		return group.getLabel();
	}

	/**
	 * @return the group, see {@link #group}
	 */
	public Group getGroup() {
		return group;
	}
}
