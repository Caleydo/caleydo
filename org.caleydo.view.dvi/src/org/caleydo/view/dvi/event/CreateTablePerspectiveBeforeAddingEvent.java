/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveCreator;

public class CreateTablePerspectiveBeforeAddingEvent extends AEvent {

	private TablePerspectiveCreator creator;
	/** The view which is the receiver of the data containers */
	private ITablePerspectiveBasedView receiver;

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
		this.receiver = receiver;
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	public ITablePerspectiveBasedView getReceiver() {
		return receiver;
	}

}
