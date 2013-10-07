/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.serialize;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainAdapter extends XmlAdapter<String, ATableBasedDataDomain> {

	@Override
	public ATableBasedDataDomain unmarshal(String v) throws Exception {
		if (v == null)
			return null;
		return (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(v);
	}

	@Override
	public String marshal(ATableBasedDataDomain v) throws Exception {
		return v == null ? null : v.getDataDomainID();
	}

}
