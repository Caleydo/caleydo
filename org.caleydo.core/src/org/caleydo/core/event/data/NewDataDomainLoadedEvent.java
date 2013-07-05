/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.AEvent;

/**
 * triggered after a data domain was successfully loaded
 * 
 * @author Samuel Gratzl
 * 
 */
public class NewDataDomainLoadedEvent
	extends AEvent {

	private IDataDomain dataDomain;

	public NewDataDomainLoadedEvent(IDataDomain dataDomain) {
		this.setDataDomain(dataDomain);
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		return dataDomain;
	}

}
