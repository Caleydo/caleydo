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
package org.caleydo.core.id.object;

/**
 * Types of managed objects
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum ManagedObjectType {
	COLUMN_NUMERICAL(11),
	COLUMN_NOMINAL(12),

	VIRTUAL_ARRAY(13),
	DATA_TABLE(15),
	GROUP(16),

	VIEW(20),
	VIEW_SWT_BROWSER_GENERAL(26),
	VIEW_SWT_BROWSER_GENOME(23),
	VIEW_SWT_TABULAR_DATA_VIEWER(99),

	GL_CANVAS(28),
	GL_VIEW(29),

	PATHWAY(58),
	PATHWAY_VERTEX(60),
	PATHWAY_VERTEX_REP(61),
	
	REMOTE_LEVEL_ELEMENT(66),

	DIMENSION_GROUP(69),
	DIMENSION_GROUP_SPACER(70),

	/**
	 * Type for ids used to connect different elements that have different ids.
	 */
	CONNECTION(85);

	private int iIdPrefix;

	/**
	 * Constructor.
	 */
	private ManagedObjectType(final int iIdPrefix) {
		this.iIdPrefix = iIdPrefix;
	}

	public int getIdPrefix() {
		return iIdPrefix;
	}
}
