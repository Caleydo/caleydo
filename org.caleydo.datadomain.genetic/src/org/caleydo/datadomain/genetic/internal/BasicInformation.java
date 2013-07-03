/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic.internal;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.datadomain.genetic.Organism;

@XmlRootElement
@XmlType
public class BasicInformation {

	private Organism organism = Organism.HOMO_SAPIENS;

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public Organism getOrganism() {

		return organism;
	}
}
