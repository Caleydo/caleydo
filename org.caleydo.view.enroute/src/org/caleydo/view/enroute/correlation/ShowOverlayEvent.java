/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.enroute.mappeddataview.overlay.IDataCellOverlayProvider;

/**
 * Tells a data cell to show the overlay that is used for correlation calculation.
 *
 * @author Christian
 *
 */
public class ShowOverlayEvent extends AEvent {

	protected final boolean isFirstCell;
	protected final DataCellInfo info;
	protected final IDataCellOverlayProvider overlay;

	/**
	 * @param dataCellID
	 * @param overlay
	 */
	public ShowOverlayEvent(DataCellInfo info, IDataCellOverlayProvider overlay, boolean isFirstCell) {
		this.info = info;
		this.overlay = overlay;
		this.isFirstCell = isFirstCell;
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
	 * @return the classifier, see {@link #classifier}
	 */
	public IDataCellOverlayProvider getOverlay() {
		return overlay;
	}

	/**
	 * @return the isFirstCell, see {@link #isFirstCell}
	 */
	public boolean isFirstCell() {
		return isFirstCell;
	}

}
