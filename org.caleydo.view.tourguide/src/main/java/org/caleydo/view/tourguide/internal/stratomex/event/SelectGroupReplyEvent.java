/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.stratomex.event;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.ADirectedEvent;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectGroupReplyEvent extends ADirectedEvent {
	private final TablePerspective tablePerspective;
	private final Group group;

	public SelectGroupReplyEvent(TablePerspective tablePerspective, Group group) {
		this.tablePerspective = tablePerspective;
		this.group = group;
	}

	/**
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	/**
	 * @return the group, see {@link #group}
	 */
	public Group getGroup() {
		return group;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
