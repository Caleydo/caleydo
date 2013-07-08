/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.external;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class GroupLabelParseSpecification extends AExternalScoreParseSpecification {
	private String perspectiveKey;

	public GroupLabelParseSpecification() {
	}

	public GroupLabelParseSpecification(String dataSourcePath, String perspectiveKey) {
		super(dataSourcePath);
		this.perspectiveKey = perspectiveKey;
	}

	/**
	 * @return the perspectiveKey, see {@link #perspectiveKey}
	 */
	public String getPerspectiveKey() {
		return perspectiveKey;
	}

	/**
	 * @param perspectiveKey
	 *            the perspectiveKey to set
	 */
	public void setPerspectiveKey(String perspectiveKey) {
		this.perspectiveKey = perspectiveKey;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.view.tourguide.data.load.AExternalScoreParseSpecification#clone()
	 */
	@Override
	public GroupLabelParseSpecification clone() {
		return (GroupLabelParseSpecification) super.clone();
	}
}
