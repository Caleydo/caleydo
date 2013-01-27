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
package org.caleydo.core.view.opengl.picking;

/**
 * List of all possible pickable elements. Every type of element which should be pickable must be registered here.
 *
 * @author Alexander Lex
 * @deprecated replace with strings
 */
@Deprecated
public enum PickingType {

	// parallel coordinates
	POLYLINE_SELECTION,
	X_AXIS_SELECTION,
	Y_AXIS_SELECTION,
	GATE_TIP_SELECTION,
	GATE_BODY_SELECTION,
	GATE_BOTTOM_SELECTION,
	ADD_GATE,
	ADD_MASTER_GATE,
	REMOVE_GATE,
	PC_ICON_SELECTION,
	MOVE_AXIS,
	REMOVE_AXIS,
	DUPLICATE_AXIS,
	ANGULAR_UPPER,
	ANGULAR_LOWER,
	/** Type for selection of views in the parallel coordinates, currently the heat map */
	PCS_VIEW_SELECTION,
	REMOVE_NAN,

	// heat map
	HEAT_MAP_RECORD_SELECTION,
	HEAT_MAP_DIMENSION_SELECTION,

	// heat map detail button
	HEAT_MAP_HIDE_HIDDEN_ELEMENTS,
	HEAT_MAP_SHOW_CAPTIONS,

	// bookmark
	BOOKMARK_CONTAINER_HEADING,
	BOOKMARK_ELEMENT,

	// hierarchical heat map
	HIER_HEAT_MAP_FIELD_SELECTION,
	HEAT_MAP_TEXTURE_SELECTION,
	/** Button that triggers whether level 2 is large or small */
	HIER_HEAT_MAP_INFOCUS_SELECTION,
	HIER_HEAT_MAP_ACTIVATE_HORIZONTAL_DENDROGRAM,
	/**
	 * Button that triggers wheter dimension dendrogram shows the whole tree, or only the tree till the cut-off
	 */
	HIER_HEAT_MAP_ACTIVATE_STORAGE_DENDROGRAM,
	HIER_HEAT_MAP_CURSOR_LEVEL1,
	HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL1,
	HIER_HEAT_MAP_CURSOR_LEVEL2,
	HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL2,
	HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION,
	HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION,
	HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION,
	HIER_HEAT_MAP_TEXTURE_CURSOR,
	HEAT_MAP_RECORD_GROUP,
	HIER_HEAT_MAP_DIMENSION_GROUP,

	// dendrogram
	DENDROGRAM_GENE_LEAF_SELECTION,
	DENDROGRAM_GENE_NODE_SELECTION,
	DENDROGRAM_EXPERIMENT_LEAF_SELECTION,
	DENDROGRAM_EXPERIMENT_NODE_SELECTION,
	DENDROGRAM_CUT_SELECTION,

	// radial hierarchy
	RAD_HIERARCHY_PDISC_SELECTION,
	RAD_HIERARCHY_SLIDER_SELECTION,
	RAD_HIERARCHY_SLIDER_BODY_SELECTION,
	RAD_HIERARCHY_SLIDER_BUTTON_SELECTION,

	// grouper
	GROUPER_GROUP_SELECTION,
	GROUPER_VA_ELEMENT_SELECTION,
	GROUPER_BACKGROUND_SELECTION,
	GROUPER_COLLAPSE_BUTTON_SELECTION,

	// treemap
	TREEMAP_ELEMENT_SELECTED,
	TREEMAP_THUMBNAILVIEW_SELECTED,

	// filterpipeline
	FILTERPIPE_BACKGROUND,
	FILTERPIPE_FILTER,
	FILTERPIPE_SUB_FILTER,
	FILTERPIPE_START_ARROW,

	ZOOM_SCROLLBAR;
}
