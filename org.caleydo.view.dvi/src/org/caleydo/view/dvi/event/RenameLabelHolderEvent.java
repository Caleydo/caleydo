/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.view.dvi.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.base.ILabelHolder;

/**
 * Event that triggers an input dialog in order to rename a {@link ILabelHolder}
 * .
 * 
 * @author Christian Partl
 * 
 */
public class RenameLabelHolderEvent extends AEvent {

	private ILabelHolder labelHolder;

	public RenameLabelHolderEvent() {
	}

	public RenameLabelHolderEvent(ILabelHolder labelHolder) {
		this.labelHolder = labelHolder;
	}

	@Override
	public boolean checkIntegrity() {
		return labelHolder != null;
	}

	/**
	 * @param labelHolder
	 *            setter, see {@link #labelHolder}
	 */
	public void setLabelHolder(ILabelHolder labelHolder) {
		this.labelHolder = labelHolder;
	}

	/**
	 * @return the labelHolder, see {@link #labelHolder}
	 */
	public ILabelHolder getLabelHolder() {
		return labelHolder;
	}

}
