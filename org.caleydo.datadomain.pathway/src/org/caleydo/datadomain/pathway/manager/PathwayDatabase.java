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
package org.caleydo.datadomain.pathway.manager;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.genetic.GeneticMetaData;
import org.caleydo.datadomain.genetic.Organism;

/**
 * Class that holds information about a specific pathway database.
 *
 * @author Marc Streit
 */
public final class PathwayDatabase {

	private final EPathwayDatabaseType type;

	private final String xmlPath;

	private final String imagePath;

	private final String imageMapPath;

	/**
	 * Constructor.
	 */
	public PathwayDatabase(EPathwayDatabaseType type, String xmlPath, String imagePath, String imageMapPath) {
		this.type = type;

		String userHomePath = System.getProperty(GeneralManager.USER_HOME);

		xmlPath = xmlPath.replace(GeneralManager.USER_HOME, userHomePath);
		xmlPath = xmlPath.replace(GeneralManager.CALEYDO_FOLDER_TEMPLATE,
				GeneralManager.CALEYDO_FOLDER);

		imagePath = imagePath.replace(GeneralManager.USER_HOME, userHomePath);
		imagePath = imagePath.replace(GeneralManager.CALEYDO_FOLDER_TEMPLATE,
				GeneralManager.CALEYDO_FOLDER);

		imageMapPath = imageMapPath.replace(GeneralManager.USER_HOME, userHomePath);
		this.imageMapPath = imageMapPath.replace(
				GeneralManager.CALEYDO_FOLDER_TEMPLATE, GeneralManager.CALEYDO_FOLDER);

		if (type == EPathwayDatabaseType.KEGG || type == EPathwayDatabaseType.WIKIPATHWAYS) {
			Organism eOrganism = GeneticMetaData.getOrganism();

			if (eOrganism == Organism.HOMO_SAPIENS) {
				imagePath += "hsa/";
				xmlPath += "hsa/";
			} else if (eOrganism == Organism.MUS_MUSCULUS) {
				imagePath += "mmu/";
				xmlPath += "mmu/";
			}
		}
		this.imagePath = imagePath;
		this.xmlPath = xmlPath;
	}

	public EPathwayDatabaseType getType() {
		return type;
	}

	public String getName() {
		return type.getName();
	}

	public String getURL() {
		return type.getURL();
	}

	public String getXMLPath() {
		assert xmlPath.length() != 0 : "Pathway XML path is not set!";

		return xmlPath;
	}

	public String getImagePath() {
		assert imagePath.length() != 0 : "Pathway image path is not set!";

		return imagePath;
	}

	public String getImageMapPath() {
		assert imageMapPath.length() != 0 : "Pathway imagemap path is not set!";

		return imageMapPath;
	}
}
