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
package org.caleydo.core.view.opengl.util.texture;

public enum EIconTextures {

	NO_ICON_AVAILABLE("resources/icons/general/no_icon_available.png"),

	ARROW_LEFT("resources/icons/view/remote/arrow-left.png"),
	ARROW_RIGHT("resources/icons/view/remote/arrow-right.png"),
	ARROW_UP("resources/icons/view/remote/arrow-up.png"),
	ARROW_DOWN("resources/icons/view/remote/arrow-down.png"),

	LOCK("resources/icons/view/remote/lock.png"),

	// Parallel Coordinates
	DROP_NORMAL("resources/icons/view/tablebased/parcoords/drop_normal.png"),
	DROP_DELETE("resources/icons/view/tablebased/parcoords/drop_delete.png"),
	DROP_DUPLICATE("resources/icons/view/tablebased/parcoords/drop_duplicate.png"),
	DROP_MOVE("resources/icons/view/tablebased/parcoords/drop_move.png"),
	SMALL_DROP("resources/icons/view/tablebased/parcoords/drop_small.png"),
	SMALL_DROP_ROTATED("resources/icons/view/tablebased/parcoords/drop_small_rotated.png"),
	ADD_GATE("resources/icons/view/tablebased/parcoords/add_gate.png"),
	NAN("resources/icons/view/tablebased/parcoords/nan.png"),

	GATE_BOTTOM("resources/icons/view/tablebased/parcoords/gate_bottom.png"),
	GATE_TOP("resources/icons/view/tablebased/parcoords/gate_top.png"),
	GATE_MENUE("resources/icons/view/tablebased/parcoords/gate_menue.png"),
	GATE_BODY("resources/icons/view/tablebased/parcoords/gate_body.png"),

	REMOVE("resources/icons/general/remove.png"),

	FILTER_PIPELINE_MENU_ITEM("resources/icons/view/filterpipeline/item_background.png"),
	FILTER_PIPELINE_EDIT("resources/icons/view/filterpipeline/edit.png"),
	FILTER_PIPELINE_DELETE("resources/icons/view/filterpipeline/delete.png"),

	// POLYLINE_TO_AXIS("resources/icons/general/no_icon_available.png"),
	// PREVENT_OCCLUSION("resources/icons/general/no_icon_available.png"),
	// RENDER_SELECTION("resources/icons/general/no_icon_available.png"),
	// RESET_SELECTIONS("resources/icons/general/no_icon_available.png"),
	// SAVE_SELECTIONS("resources/icons/general/no_icon_available.png"),
	// ANGULAR_BRUSHING("resources/icons/view/tablebased/parcoords/angular_brush.png"),

	// hierarchical heat map
	HEAT_MAP_SYMBOL("resources/icons/view/tablebased/heatmap/heatmap128x128.png"),
	HEAT_MAP_ICON("resources/icons/view/tablebased/heatmap/heatmap.png"),
	HEAT_MAP_ARROW("resources/icons/view/tablebased/heatmap/hm_arrow.png"),
	HEAT_MAP_GROUP_SELECTED("resources/icons/view/tablebased/heatmap/hm_group_selected.png"),
	HEAT_MAP_GROUP_NORMAL("resources/icons/view/tablebased/heatmap/hm_group_normal.png"),

	// dendrogram
	SLIDER_MIDDLE("resources/icons/view/tablebased/dendrogram/slider_middle.png"),
	SLIDER_ENDING("resources/icons/view/tablebased/dendrogram/slider_ending.png"),
	DENDROGRAM_HORIZONTAL_SYMBOL("resources/icons/view/tablebased/dendrogram/dendrogram_horizontal.png"),
	DENDROGRAM_VERTICAL_SYMBOL("resources/icons/view/tablebased/dendrogram/dendrogram_vertical.png"),

	PAR_COORDS_SYMBOL("resources/icons/view/tablebased/parcoords/parcoords128x128.png"),
	PAR_COORDS_ICON("resources/icons/view/tablebased/parcoords/parcoords.png"),

	PATHWAY_SYMBOL("resources/icons/view/pathway/pathway128x128.png"),
	PATHWAY_ICON("resources/icons/view/pathway/pathway.png"),

	RADIAL_SYMBOL("resources/icons/view/radial/radial.png"),

	BROWSER_REFRESH_IMAGE("resources/icons/view/browser/refresh.png"),
	BROWSER_BACK_IMAGE("resources/icons/view/browser/back.png"),
	BROWSER_STOP_IMAGE("resources/icons/view/browser/stop.png"),
	BROWSER_HOME_IMAGE("resources/icons/view/browser/home.png"),

	PANEL_SELECTION("resources/panel/selection_background.png"),

	NAVIGATION_REMOVE_VIEW("resources/icons/general/navigation_remove_view.png"),
	NAVIGATION_DRAG_VIEW("resources/icons/general/navigation_drag_view.png"),
	NAVIGATION_LOCK_VIEW("resources/icons/general/navigation_lock_view.png"),
	NAVIGATION_NEXT_BIG("resources/navigation/next_big.png"),
	NAVIGATION_NEXT_BIG_SIDE("resources/navigation/next_big_side.png"),
	NAVIGATION_NEXT_BIG_MIDDLE("resources/navigation/next_big_middle.png"),
	NAVIGATION_NEXT_SMALL("resources/navigation/next_small.png"),
	NAVIGATION_MASK_CURVE("resources/navigation/mask_curve.png"),
	NAVIGATION_MASK_CURVE_NEG("resources/navigation/mask_curve_neg.png"),
	NAVIGATION_MASK_CURVE_LIGHT("resources/navigation/mask_curve_light.png"),
	NAVIGATION_MASK_CURVE_NEG_LIGHT("resources/navigation/mask_curve_neg_light.png"),
	NAVIGATION_MASK_CURVE_WHITE("resources/navigation/mask_curve_white.png"),
	NAVIGATION_MASK_CURVE_NEG_WHITE("resources/navigation/mask_curve_neg_white.png"),

	POOL_REMOVE_VIEW("resources/icons/general/pool_remove_view.png"),
	POOL_DRAG_VIEW("resources/icons/general/pool_drag_view.png"),
	POOL_VIEW_BACKGROUND("resources/navigation/pool_view_background.png"),
	POOL_VIEW_BACKGROUND_SELECTION("resources/navigation/pool_view_background_selection.png"),

	DATA_FLIPPER_CONNECTION_STRAIGHT("resources/icons/view/dataflipper/connection_straight.png"),
	DATA_FLIPPER_CONNECTION_CORNER("resources/icons/view/dataflipper/connection_corner.png"),
	DATA_FLIPPER_GUIDANCE_CONNECTION_STRAIGHT("resources/icons/view/dataflipper/guidance_connection_straight.png"),
	DATA_FLIPPER_GUIDANCE_CONNECTION_STRAIGHT_HIGHLIGHT(
			"resources/icons/view/dataflipper/guidance_connection_straight_highlight.png"),
	DATA_FLIPPER_DATA_ICON_BACKGROUND("resources/icons/view/dataflipper/data_icon_background.png"),
	DATA_FLIPPER_DATA_ICON_BACKGROUND_ROUND("resources/icons/view/dataflipper/data_icon_background_round.png"),
	DATA_FLIPPER_DATA_ICON_BACKGROUND_ROUND_HIGHLIGHTED(
			"resources/icons/view/dataflipper/data_icon_background_round_highlighted.png"),
	DATA_FLIPPER_VIEW_ICON_BACKGROUND_ROUNDED("resources/icons/view/dataflipper/view_icon_background_rounded.png"),
	DATA_FLIPPER_VIEW_ICON_BACKGROUND_SQUARE("resources/icons/view/dataflipper/view_icon_background_square.png"),
	DATA_FLIPPER_EXCLAMATION_MARK("resources/icons/view/dataflipper/exclamation_mark.png"),
	DATA_FLIPPER_TASK("resources/icons/view/dataflipper/task.png"),
	DATA_FLIPPER_TASK_BACKGROUND("resources/icons/view/dataflipper/task_background.png"),

	DATA_DOMAIN_CLINICAL("resources/icons/view/dataflipper/patient.png"),
	DATA_DOMAIN_PATHWAY("resources/icons/view/dataflipper/pathway.png"),
	DATA_DOMAIN_GENETIC("resources/icons/view/dataflipper/gene_expression.png"),
	DATA_DOMAIN_TISSUE("resources/icons/view/dataflipper/tissue.png"),
	DATA_DOMAIN_ORGAN("resources/icons/view/dataflipper/organ.png"),

	LOADING("resources/loading/loading_background.png"),
	LOADING_CIRCLE("resources/loading/loading_circle.png"),

	CELL_MODEL("resources/models/cell.jpg"),
	TISSUE_SAMPLE("resources/icons/view/tissue/tissue.png"),

	// context menu elements
	CM_LOAD_DEPENDING_PATHWAYS("/resources/context_menu/load_depending_pathways.png"),
	CM_DEPENDING_PATHWAYS("/resources/context_menu/depending_pathways.png"),
	CM_BOOKMARK("/resources/context_menu/bookmark.png"),
	CM_CORNER_BLACK("/resources/context_menu/corner_black.png"),
	CM_EDGE_BLACK("/resources/context_menu/edge_black.png"),
	CM_SELECTION_SIDE_BLACK("/resources/context_menu/selection_side_black.png"),
	CM_SELECTION_RIGHT_EXTENSIBLE_BLACK("/resources/context_menu/submenue_black.png"),
	CM_SELECTION_LINES_BLACK("/resources/context_menu/selection_lines_black.png"),
	CM_BIOCARTA("/resources/context_menu/biocarta.png"),
	CM_KEGG("/resources/context_menu/kegg.png"),
	CM_SCROLL_BUTTON("/resources/context_menu/scroll_button.png"),
	CM_SCROLL_BUTTON_OVER("/resources/context_menu/scroll_button_over.png"),

	COMPARER_SHOW_HIDDEN("/resources/icons/view/compare/show_hidden_elements.png"),
	COMPARER_SHOW_CAPTIONS("/resources/icons/view/compare/show_captions.png"),

	GROUPER_COLLAPSE_PLUS("resources/icons/general/plus.png"),
	GROUPER_COLLAPSE_MINUS("resources/icons/general/minus.png"),

	CLUSTER_ICON("resources/icons/view/tablebased/clustering.png"),

	// MOVE_ICON("resources/icons/view/histogram/move_icon.png"),

	HISTOGRAM_ICON("resources/icons/view/histogram/histogram.png"),

	/** Linearized Pathway */
	ABSTRACT_BAR_ICON("resources/icons/view/enroute/abstract_mode.png"),

	TAGCLOUD_ICON("resources/icons/view/tagclouds/icon.png"),
	NAVIGATION_BACKGROUND("resources/icons/general/navigation.png"),
	PIN("resources/icons/general/pin.png");

	private String sFileName;

	EIconTextures(String sFileName) {

		this.sFileName = sFileName;

	}

	public String getFileName() {

		return sFileName;
	}
}