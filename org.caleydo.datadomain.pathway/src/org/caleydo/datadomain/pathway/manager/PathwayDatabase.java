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

import java.io.Serializable;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.specialized.Organism;

/**
 * Class that holds information about a specific pathway database.
 * 
 * @author Marc Streit
 */
public class PathwayDatabase implements Serializable {

	private static final long serialVersionUID = 1L;

	private PathwayDatabaseType type;

	private String sXMLPath;

	private String sImagePath;

	private String sImageMapPath;

	/**
	 * Constructor.
	 */
	public PathwayDatabase(final PathwayDatabaseType type, final String sXMLPath,
			final String sImagePath, final String sImageMapPath) {
		this.type = type;
		this.sXMLPath = sXMLPath;
		this.sImagePath = sImagePath;
		this.sImageMapPath = sImageMapPath;

		String sUserHomePath = System.getProperty(GeneralManager.USER_HOME);

		this.sXMLPath = sXMLPath.replace(GeneralManager.USER_HOME, sUserHomePath);
		this.sXMLPath = this.sXMLPath.replace(GeneralManager.CALEYDO_FOLDER_TEMPLATE,
				GeneralManager.CALEYDO_FOLDER);

		this.sImagePath = sImagePath.replace(GeneralManager.USER_HOME, sUserHomePath);
		this.sImagePath = this.sImagePath.replace(GeneralManager.CALEYDO_FOLDER_TEMPLATE,
				GeneralManager.CALEYDO_FOLDER);

		this.sImageMapPath = sImageMapPath.replace(GeneralManager.USER_HOME,
				sUserHomePath);
		this.sImageMapPath = this.sImageMapPath.replace(
				GeneralManager.CALEYDO_FOLDER_TEMPLATE, GeneralManager.CALEYDO_FOLDER);

		if (type == PathwayDatabaseType.KEGG) {
			Organism eOrganism = GeneralManager.get().getBasicInfo().getOrganism();

			if (eOrganism == Organism.HOMO_SAPIENS) {
				this.sImagePath += "hsa/";
				this.sXMLPath += "hsa/";
			} else if (eOrganism == Organism.MUS_MUSCULUS) {
				this.sImagePath += "mmu/";
				this.sXMLPath += "mmu/";
			}
		}
	}

	public final PathwayDatabaseType getType() {
		return type;
	}

	public final String getName() {
		return type.getName();
	}

	public final String getURL() {
		return type.getURL();
	}

	public final String getXMLPath() {
		assert sXMLPath.length() != 0 : "Pathway XML path is not set!";

		return sXMLPath;
	}

	public final String getImagePath() {
		assert sImagePath.length() != 0 : "Pathway image path is not set!";

		return sImagePath;
	}

	public final String getImageMapPath() {
		assert sImageMapPath.length() != 0 : "Pathway imagemap path is not set!";

		return sImageMapPath;
	}
}
