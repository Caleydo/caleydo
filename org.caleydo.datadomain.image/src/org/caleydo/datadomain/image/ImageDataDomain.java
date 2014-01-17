/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.image;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.util.color.Color;


@XmlType
@XmlRootElement
public class ImageDataDomain implements IDataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.image";

	@Override
	public int getDataAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLabel(String label) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getProviderName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataDomainID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataDomainID(String dataDomainType) {
		// TODO Auto-generated method stub

	}

	@Override
	public DataSetDescription getDataSetDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataSetDescription(DataSetDescription dataSetDescription) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDataDomainType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataDomainType(String dataDomainType) {
		// TODO Auto-generated method stub

	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IDCategory> getIDCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addIDCategory(IDCategory category) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSerializeable() {
		// TODO Auto-generated method stub
		return false;
	}

}
