/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
