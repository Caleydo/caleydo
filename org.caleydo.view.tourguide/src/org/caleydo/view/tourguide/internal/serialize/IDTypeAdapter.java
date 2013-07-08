/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.serialize;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.caleydo.core.id.IDType;

/**
 * @author Samuel Gratzl
 *
 */
public class IDTypeAdapter extends XmlAdapter<String, IDType> {

	@Override
	public IDType unmarshal(String v) throws Exception {
		return v == null ? null : IDType.getIDType(v);
	}

	@Override
	public String marshal(IDType v) throws Exception {
		return v == null ? null : v.getTypeName();
	}

}
