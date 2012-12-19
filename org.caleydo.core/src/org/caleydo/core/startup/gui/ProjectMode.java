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
package org.caleydo.core.startup.gui;

import java.util.Set;

import org.caleydo.core.data.virtualarray.VirtualArray;

public enum ProjectMode {

	GENE_EXPRESSION_NEW_DATA,

	/** specifies to load an entire sample project */
	SAMPLE_PROJECT,

	GENE_EXPRESSION_SAMPLE_DATA,
	UNSPECIFIED_NEW_DATA,

	/**
	 * Needed for starting caleydo without loading any data. For example needed for eye tracker test setup.
	 */
	NO_DATA,

	/**
	 * specifies that the UseCase (including {@link Set} and {@link VirtualArray}) is loaded from the file
	 * system
	 */
	LOAD_PROJECT;

}
