package org.caleydo.datadomain.pathway.manager;

import java.io.Serializable;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.specialized.Organism;

/**
 * Class that holds information about a specific pathway database.
 * 
 * @author Marc Streit
 */
public class PathwayDatabase implements Serializable {

	private static final long serialVersionUID = 1L;

	private EPathwayDatabaseType type;

	private String sXMLPath;

	private String sImagePath;

	private String sImageMapPath;

	/**
	 * Constructor.
	 */
	public PathwayDatabase(final EPathwayDatabaseType type, final String sXMLPath,
			final String sImagePath, final String sImageMapPath) {
		this.type = type;
		this.sXMLPath = sXMLPath;
		this.sImagePath = sImagePath;
		this.sImageMapPath = sImageMapPath;

		String sUserHomePath = System.getProperty(GeneralManager.USER_HOME_TEMPLATE);

		this.sXMLPath = sXMLPath
				.replace(GeneralManager.USER_HOME_TEMPLATE, sUserHomePath);
		this.sXMLPath = this.sXMLPath.replace(GeneralManager.CALEYDO_FOLDER_TEMPLATE,
				GeneralManager.CALEYDO_FOLDER);

		this.sImagePath = sImagePath.replace(GeneralManager.USER_HOME_TEMPLATE,
				sUserHomePath);
		this.sImagePath = this.sImagePath.replace(GeneralManager.CALEYDO_FOLDER_TEMPLATE,
				GeneralManager.CALEYDO_FOLDER);

		this.sImageMapPath = sImageMapPath.replace(GeneralManager.USER_HOME_TEMPLATE,
				sUserHomePath);
		this.sImageMapPath = this.sImageMapPath.replace(
				GeneralManager.CALEYDO_FOLDER_TEMPLATE, GeneralManager.CALEYDO_FOLDER);

		if (type == EPathwayDatabaseType.KEGG) {
			Organism eOrganism = GeneralManager.get().getOrganism();

			if (eOrganism == Organism.HOMO_SAPIENS) {
				this.sImagePath += "hsa/";
				this.sXMLPath += "hsa/";
			} else if (eOrganism == Organism.MUS_MUSCULUS) {
				this.sImagePath += "mmu/";
				this.sXMLPath += "mmu/";
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
