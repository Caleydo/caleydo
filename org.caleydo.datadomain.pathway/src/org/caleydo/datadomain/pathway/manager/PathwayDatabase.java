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
import org.caleydo.core.specialized.Organism;

/**
 * Class that holds information about a specific pathway database.
 * 
 * @author Marc Streit
 */
public class PathwayDatabase {

	private EPathwayDatabaseType type;

	private String xmlPath;

	private String imagePath;

	private String imageMapPath;

	/**
	 * Constructor.
	 */
	public PathwayDatabase(final EPathwayDatabaseType type, final String xmlPath,
			final String imagePath, final String imageMapPath) {
		this.type = type;
		this.xmlPath = xmlPath;
		this.imagePath = imagePath;
		this.imageMapPath = imageMapPath;

		String userHomePath = System.getProperty(GeneralManager.USER_HOME);

		this.xmlPath = xmlPath.replace(GeneralManager.USER_HOME, userHomePath);
		this.xmlPath = this.xmlPath.replace(GeneralManager.CALEYDO_FOLDER_TEMPLATE,
				GeneralManager.CALEYDO_FOLDER);

		this.imagePath = imagePath.replace(GeneralManager.USER_HOME, userHomePath);
		this.imagePath = this.imagePath.replace(GeneralManager.CALEYDO_FOLDER_TEMPLATE,
				GeneralManager.CALEYDO_FOLDER);

		this.imageMapPath = imageMapPath.replace(GeneralManager.USER_HOME, userHomePath);
		this.imageMapPath = this.imageMapPath.replace(
				GeneralManager.CALEYDO_FOLDER_TEMPLATE, GeneralManager.CALEYDO_FOLDER);

		if (type == EPathwayDatabaseType.KEGG) {
			Organism eOrganism = GeneralManager.get().getBasicInfo().getOrganism();

			if (eOrganism == Organism.HOMO_SAPIENS) {
				this.imagePath += "hsa/";
				this.xmlPath += "hsa/";
			} else if (eOrganism == Organism.MUS_MUSCULUS) {
				this.imagePath += "mmu/";
				this.xmlPath += "mmu/";
			}
		}
	}

	public final EPathwayDatabaseType getType() {
		return type;
	}

	public final String getName() {
		return type.getName();
	}

	public final String getURL() {
		return type.getURL();
	}

	public final String getXMLPath() {
		assert xmlPath.length() != 0 : "Pathway XML path is not set!";

		return xmlPath;
	}

	public final String getImagePath() {
		assert imagePath.length() != 0 : "Pathway image path is not set!";

		return imagePath;
	}

	public final String getImageMapPath() {
		assert imageMapPath.length() != 0 : "Pathway imagemap path is not set!";

		return imageMapPath;
	}
}
