/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.manager;

/**
 * Class holds all supported pathway database types.
 *
 * @author Marc Streit
 */
public enum EPathwayDatabaseType {

	KEGG("KEGG", "www.genome.jp/kegg"), WIKIPATHWAYS("Wikipathways", "www.wikipathways.org");

	private final String name;

	private final String url;

	/**
	 * Constructor.
	 *
	 * @param name
	 * @param url
	 */
	private EPathwayDatabaseType(String name, String url) {

		this.name = name;
		this.url = url;
	}

	public String getName() {

		return name;
	}

	public String getURL() {

		return url;
	}
}
