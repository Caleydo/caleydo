/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 *
 * @author Samuel Gratzl
 *
 */
public class DualCodeUpdateEvent extends ADirectedEvent {
	private String code;
	private String codeOrder;

	public DualCodeUpdateEvent(String code,String codeOrder) {
		this.code = code;
		this.codeOrder = codeOrder;
	}

	/**
	 * @return the code, see {@link #code}
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the codeOrder, see {@link #codeOrder}
	 */
	public String getCodeOrder() {
		return codeOrder;
	}

	@Override
	public boolean checkIntegrity() {
		return code != null;
	}

}
