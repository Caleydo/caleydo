/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.external;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class ScoreParseSpecification extends AExternalScoreParseSpecification {
	public ScoreParseSpecification() {
	}

	public ScoreParseSpecification(String dataSourcePath) {
		super(dataSourcePath);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ScoreParseSpecification clone() {
		return (ScoreParseSpecification) super.clone();
	}
}
