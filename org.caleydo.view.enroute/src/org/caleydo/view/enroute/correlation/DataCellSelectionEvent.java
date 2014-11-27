/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import org.caleydo.core.event.AEvent;

/**
 * @author Christian
 *
 */
public class DataCellSelectionEvent extends AEvent {

	protected DataCellInfo info;

	public DataCellSelectionEvent(DataCellInfo info) {
		this.info = info;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the info, see {@link #info}
	 */
	public DataCellInfo getInfo() {
		return info;
	}

	/**
	 * @param info
	 *            setter, see {@link info}
	 */
	public void setInfo(DataCellInfo info) {
		this.info = info;
	}

}
