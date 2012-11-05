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

/**
 * 
 * Known source ID formats covered by correctly applying these expressions:
 * 
 * Known source ID formats not covered correctly:
 * 
 * <li>OV_20_0990 - known expression: "^[a-z]+\\-",
 * setReplacementExpression("\\_", "-");</li>
 * 
 * @author Alexander Lex
 * 
 */
public class TCGADefinitions {

	public static final String[] KNOWN_ID_EXAMPLES = { "TCGA-06-0171-02",
			"tcga-06-0125-02","TCGA-02-0003-01A-01R-0177-01", "TCGA-02-0004-01A-21-1898-20", "OV_20_0990" };

	// tcga\\-|TCGA\\-|^[a-zA-Z]|\\-..\\z
	public static final String TCGA_ID_SUBSTRING_REGEX = "^[a-zA-Z]*\\-|\\-..\\z|\\-...\\-";
	public static final String[] TCGA_REPLACING_EXPRESSIONS = {"\\.","\\_"};
	public static final String TCGA_REPLACEMENT_STRING = "-";

}
