/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.listener;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;

/**
 * Event that triggers the removal of a data container with the specified tablePerspectiveID.
 *
 * @author Alexander Lex
 *
 */
public class RemoveTablePerspectiveEvent extends ADirectedEvent {

	/** The ID of the table perspective to be removed */
	private TablePerspective tablePerspective;

	public RemoveTablePerspectiveEvent() {
	}

	public RemoveTablePerspectiveEvent(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	public RemoveTablePerspectiveEvent(TablePerspective tablePerspective, IMultiTablePerspectiveBasedView receiver) {
		this.tablePerspective = tablePerspective;
		to(receiver);
	}

	/**
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	/**
	 * @param tablePerspective
	 *            setter, see {@link tablePerspective}
	 */
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public boolean checkIntegrity() {
		return tablePerspective != null;
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	@Override
	public IMultiTablePerspectiveBasedView getReceiver() {
		return (IMultiTablePerspectiveBasedView) super.getReceiver();
	}

}
