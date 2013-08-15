/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.view.IView;

/**
 * Event signaling that an old {@link TablePerspective} should be replaced with
 * a new one in a view identified through it's id.
 *
 * @author Alexander Lex
 *
 */
public class ReplaceTablePerspectiveEvent extends ADirectedEvent {

	private TablePerspective oldPerspective;
	private TablePerspective newPerspective;

	public ReplaceTablePerspectiveEvent() {

	}

	public ReplaceTablePerspectiveEvent(IView viewID, TablePerspective newPerspective,
			TablePerspective oldPerspective) {
		to(viewID);
		this.oldPerspective = oldPerspective;
		this.newPerspective = newPerspective;
	}

	/**
	 * @param oldPerspective
	 *            setter, see {@link #oldPerspective}
	 */
	public void setOldPerspective(TablePerspective oldPerspective) {
		this.oldPerspective = oldPerspective;
	}

	/**
	 * @return the oldPerspective, see {@link #oldPerspective}
	 */
	public TablePerspective getOldPerspective() {
		return oldPerspective;
	}

	/**
	 * @param newPerspective
	 *            setter, see {@link #newPerspective}
	 */
	public void setNewPerspective(TablePerspective newPerspective) {
		this.newPerspective = newPerspective;
	}

	/**
	 * @return the newPerspective, see {@link #newPerspective}
	 */
	public TablePerspective getNewPerspective() {
		return newPerspective;
	}

	@Override
	public boolean checkIntegrity() {
		return oldPerspective != null && newPerspective != null;
	}

}
