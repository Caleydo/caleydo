/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.AEvent;

public class RemoveDataDomainEvent extends AEvent {
	public RemoveDataDomainEvent(Object sender, IDataDomain dataDomain) {
		this.setSender(sender);
		this.setEventSpace(dataDomain.getDataDomainID());
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
