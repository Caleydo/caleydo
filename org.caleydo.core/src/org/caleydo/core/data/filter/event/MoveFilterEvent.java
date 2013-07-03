/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter.event;


/**
 * @author Thomas Geymayer
 */
public class MoveFilterEvent extends FilterEvent {

	private int offset = 0;

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public boolean checkIntegrity() {
		if (!super.checkIntegrity() || offset == 0)
			return false;

		return true;
	}
}
