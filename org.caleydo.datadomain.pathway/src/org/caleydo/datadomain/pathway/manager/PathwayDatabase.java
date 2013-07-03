/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
