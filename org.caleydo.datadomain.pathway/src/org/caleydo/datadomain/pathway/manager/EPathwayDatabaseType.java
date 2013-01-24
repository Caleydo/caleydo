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

/**
 * Class holds all supported pathway database types.
 *
 * @author Marc Streit
 */
public enum EPathwayDatabaseType {

	KEGG("KEGG", "www.genome.jp/kegg"), BIOCARTA("BioCarta", "www.biocarta.com"), WIKIPATHWAYS(
			"Wikipathways",
			"www.wikipathways.org");

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
