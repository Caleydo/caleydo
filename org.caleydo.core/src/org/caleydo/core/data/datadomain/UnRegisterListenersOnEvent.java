/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;

/**
 * utility event listener, which invoke to unregister all events listenes on its handler on an event
 * 
 * @author Samuel Gratzl
 * 
 */
public class UnRegisterListenersOnEvent extends AEventListener<IListenerOwner> {
	public UnRegisterListenersOnEvent(IListenerOwner handler, IDataDomain dataDomain) {
		setHandler(handler);
		setExclusiveEventSpace(dataDomain.getDataDomainID());
	}
	@Override
	public void handleEvent(AEvent event) {
		handler.unregisterEventListeners();
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
