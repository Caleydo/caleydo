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
package org.caleydo.core.startup;


/**
 * Abstract startup procedure. Handling of view initialization and application init data.
 *
 * @author Marc Streit
 */
public interface IStartupProcedure {

	/**
	 * Initialization stuff that has to be done before the workbench opens
	 * (e.g., copying the workbench data from a serialized project).
	 */
	void preWorkbenchOpen();

	boolean run();

	/**
	 * Initialization stuff that has to be done after the workbench opened (e.g., making a specific view activate)
	 **/
	void postWorkbenchOpen();
}
