/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.datadomain.genetic.internal.BasicInformation;


/**
 * the new {@link BasicInformation} holder using the {@link ProjectMetaData}
 * 
 * @author Samuel Gratzl
 * 
 */
public class GeneticMetaData {
	private static final String META_DATA_KEY_ORGANISM = "Organism";

	/**
	 * @return the organism, see {@link #organism}
	 */
	public static Organism getOrganism() {
		ProjectMetaData metaData = GeneralManager.get().getMetaData();
		if (metaData.contains(META_DATA_KEY_ORGANISM)) {
			return Organism.valueOf(metaData.get(META_DATA_KEY_ORGANISM));
		}
		return Organism.HOMO_SAPIENS;
	}

	static void setOrganism(Organism organism) {
		GeneralManager.get().getMetaData().set(META_DATA_KEY_ORGANISM, organism.name());
	}
}
