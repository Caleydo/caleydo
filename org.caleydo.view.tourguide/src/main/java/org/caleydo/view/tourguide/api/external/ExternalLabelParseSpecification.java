/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.external;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class ExternalLabelParseSpecification extends AExternalScoreParseSpecification {
	private String dataDomainID;

	public ExternalLabelParseSpecification() {
	}

	public ExternalLabelParseSpecification(String dataSourcePath, String dataDomainID) {
		super(dataSourcePath);
		this.dataDomainID = dataDomainID;
	}

	/**
	 * @return the dataDomainID, see {@link #dataDomainID}
	 */
	public String getDataDomainID() {
		return dataDomainID;
	}

	/**
	 * @param dataDomainID
	 *            setter, see {@link dataDomainID}
	 */
	public void setDataDomainID(String dataDomainID) {
		this.dataDomainID = dataDomainID;
	}


	@Override
	public ExternalLabelParseSpecification clone() {
		return (ExternalLabelParseSpecification) super.clone();
	}
}
