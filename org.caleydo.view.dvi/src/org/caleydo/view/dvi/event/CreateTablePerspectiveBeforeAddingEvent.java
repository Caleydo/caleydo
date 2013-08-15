/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.event;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveCreator;

public class CreateTablePerspectiveBeforeAddingEvent extends ADirectedEvent {

	private TablePerspectiveCreator creator;

	public CreateTablePerspectiveBeforeAddingEvent(TablePerspectiveCreator creator) {
		this.creator = creator;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param creator
	 *            setter, see {@link creator}
	 */
	public void setCreator(TablePerspectiveCreator creator) {
		this.creator = creator;
	}

	/**
	 * @return the creator, see {@link #creator}
	 */
	public TablePerspectiveCreator getCreator() {
		return creator;
	}

	/**
	 * @param receiver
	 *            setter, see {@link receiver}
	 */
	public void setReceiver(ITablePerspectiveBasedView receiver) {
		to(receiver);
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	@Override
	public ITablePerspectiveBasedView getReceiver() {
		return (ITablePerspectiveBasedView) super.getReceiver();
	}

}
