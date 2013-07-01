/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.AEvent;

/**
 * This Event should be triggered when the table perspectives of a data domain change.
 * 
 * @author Christian Partl
 */
public class DataDomainUpdateEvent
	extends AEvent {

	private IDataDomain dataDomain;

	public DataDomainUpdateEvent(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		return dataDomain;
	}

}
