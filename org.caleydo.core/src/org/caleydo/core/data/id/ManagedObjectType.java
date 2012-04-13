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
package org.caleydo.core.data.id;

/**
 * Types of managed objects
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum ManagedObjectType {
	DIMENSION(10),
	COLUMN_NUMERICAL(11),
	COLUMN_NOMINAL(12),

	VIRTUAL_ARRAY(13),
	SELECTION(14),
	DATA_TABLE(15),
	GROUP(16),

	VIEW(20),
	VIEW_GL_CANVAS(21),
	VIEW_SWT_BROWSER_GENERAL(26),
	VIEW_SWT_BROWSER_GENOME(23),
	VIEW_SWT_TABULAR_DATA_VIEWER(99),
	VIEW_SWT_JOGL_CONTAINER(99),
	VIEW_SWT_COLLAB(99),

	GL_CANVAS(28),
	GL_VIEW(29),

	GL_CONTEXT_MENUE(49),

	GUI_SWT_WINDOW(50),
	GUI_SWT_NATIVE_WIDGET(51),
	GUI_SWT_EMBEDDED_JOGL_WIDGET(52),

	CMD_QUEUE(54),
	COMMAND(56),

	PATHWAY(58),
	PATHWAY_ELEMENT(59),
	PATHWAY_VERTEX(60),
	PATHWAY_VERTEX_REP(61),
	PATHWAY_EDGE(62),
	PATHWAY_EDGE_REP(63),

	EVENT_PUBLISHER(64),
	EVENT_MEDIATOR(65),

	REMOTE_LEVEL_ELEMENT(66),

	GRAPH(67),
	GRAPH_ITEM(68),

	DIMENSION_GROUP(69),
	DIMENSION_GROUP_SPACER(70),

	/**
	 * Type for ids used to connect different elements that have different ids.
	 */
	CONNECTION(85),

	HIERARCHYGRAPH(90);

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
