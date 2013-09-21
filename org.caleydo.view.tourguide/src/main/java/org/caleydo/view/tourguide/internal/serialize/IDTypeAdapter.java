/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.serialize;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDType;

/**
 * @author Samuel Gratzl
 *
 */
public class IDTypeAdapter extends XmlAdapter<String, IDType> {

	@Override
	public IDType unmarshal(String v) throws Exception {
		if (v == null)
			return null;
		IDType idType = IDType.getIDType(v);
		if (idType != null)
			return idType;
		// check if we can reproduce the datadomain specific id type
		if (v.startsWith("record_")) {
			ATableBasedDataDomain d = findDataDomain(v, "record_");
			return d == null ? null : d.getRecordIDType();
		} else if (v.startsWith("group_record_")) {
			ATableBasedDataDomain d = findDataDomain(v, "group_record_");
			return d == null ? null : d.getRecordGroupIDType();
		} else if (v.startsWith("dimension_")) {
			ATableBasedDataDomain d = findDataDomain(v, "dimension_");
			return d == null ? null : d.getDimensionIDType();
		} else if (v.startsWith("group_dimension_")) {
			ATableBasedDataDomain d = findDataDomain(v, "group_dimension_");
			return d == null ? null : d.getDimensionGroupIDType();
		}
		return null;
	}

	private static ATableBasedDataDomain findDataDomain(String type, String prefix) {
		type = type.substring(prefix.length());
		String dataDomainID = type.substring(0, type.lastIndexOf('_'));
		return (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(dataDomainID);
	}

	@Override
	public String marshal(IDType v) throws Exception {
		return v == null ? null : v.getTypeName();
	}

}
