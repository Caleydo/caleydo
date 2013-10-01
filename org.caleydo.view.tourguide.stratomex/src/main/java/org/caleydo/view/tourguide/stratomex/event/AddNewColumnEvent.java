/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.event;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.tourguide.stratomex.s.EWizardMode;

/**
 * @author Samuel Gratzl
 *
 */
public class AddNewColumnEvent extends ADirectedEvent {
	private final int objectId;
	private final EWizardMode mode;

	/**
	 * @param objectID
	 */
	public AddNewColumnEvent(int objectID) {
		this(objectID, EWizardMode.GLOBAL);
	}

	/**
	 * @param objectID2
	 * @param b
	 */
	public AddNewColumnEvent(int objectID, EWizardMode mode) {
		this.objectId = objectID;
		this.mode = mode;
	}

	/**
	 * @return the mode, see {@link #mode}
	 */
	public EWizardMode getMode() {
		return mode;
	}
	/**
	 * @return the objectId, see {@link #objectId}
	 */
	public int getObjectId() {
		return objectId;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
