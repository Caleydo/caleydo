/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Organism on which an analysis bases. Currently we support homo sapiens (human) and mus musculus (mouse).
 * FIXME: organism should be moved to the datadomain.genetic plugin. however, we have a dependency problem.
 *
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public enum Organism {

	/**
	 * Human
	 */
	HOMO_SAPIENS,

	/**
	 * Mouse
	 */
	MUS_MUSCULUS;

	public String getLabel() {
		switch(this) {
		case HOMO_SAPIENS:
			return "Human (homo sapiens)";
		case MUS_MUSCULUS:
			return "Mouse (mus musculus)";
		}
		throw new IllegalStateException("unknown me");
	}
}
