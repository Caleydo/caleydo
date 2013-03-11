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
/**
 *
 */
package org.caleydo.view.enroute;

/**
 * Picking types for the linearized pathway view.
 *
 * @author Christian
 *
 */
public enum EPickingType {
	BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON,
	BRANCH_SUMMARY_NODE,
	GENE_NODE,
	COMPOUND_NODE,
	LINEARIZABLE_NODE,
	REMOVABLE_NODE,
	REMOVE_NODE_BUTTON,


	/** Picking type for samples */
	SAMPLE,
	/** Picking type for sample groups */
	SAMPLE_GROUP,
	/** The view mode for the sample group */
	SAMPLE_GROUP_VIEW_MODE,
	/** Picking type for abstract bars */
	SAMPLE_GROUP_RENDERER,
	/** A bar in a histogram which can be resolved to samples */
	HISTOGRAM_BAR
}
