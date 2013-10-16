/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.dvi.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveCreator;

/**
 * @author Christian
 *
 */
public class CreateAndRenameTablePerspectiveEvent extends AEvent {

	private TablePerspectiveCreator creator;

	/**
	 *
	 */
	public CreateAndRenameTablePerspectiveEvent(TablePerspectiveCreator creator) {
		this.creator = creator;
	}

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
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

}
