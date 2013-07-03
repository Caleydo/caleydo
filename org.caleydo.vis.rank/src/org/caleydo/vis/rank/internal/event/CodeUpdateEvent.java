/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 *
 * @author Samuel Gratzl
 * 
 */
public class CodeUpdateEvent extends ADirectedEvent {
	private String code;

	public CodeUpdateEvent(String code) {
		this.code = code;
	}

	/**
	 * @return the code, see {@link #code}
	 */
	public String getCode() {
		return code;
	}

	@Override
	public boolean checkIntegrity() {
		return code != null;
	}

}
