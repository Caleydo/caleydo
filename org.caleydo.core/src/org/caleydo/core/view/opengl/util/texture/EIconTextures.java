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

@Deprecated
public enum EIconTextures {

	NO_ICON_AVAILABLE("resources/icons/general/no_icon_available.png"),

	LOCK("resources/icons/view/remote/lock.png"),

	REMOVE("resources/icons/general/remove.png"),

	FILTER_PIPELINE_MENU_ITEM("resources/icons/view/filterpipeline/item_background.png"),
	FILTER_PIPELINE_EDIT("resources/icons/view/filterpipeline/edit.png"),
	FILTER_PIPELINE_DELETE("resources/icons/view/filterpipeline/delete.png"),

	// hierarchical heat map
	HEAT_MAP_SYMBOL("resources/icons/view/tablebased/heatmap/heatmap128x128.png"),
	HEAT_MAP_ICON("resources/icons/view/tablebased/heatmap/heatmap.png"),
	HEAT_MAP_ARROW("resources/icons/view/tablebased/heatmap/hm_arrow.png"),
	HEAT_MAP_GROUP_SELECTED("resources/icons/view/tablebased/heatmap/hm_group_selected.png"),
	HEAT_MAP_GROUP_NORMAL("resources/icons/view/tablebased/heatmap/hm_group_normal.png"),

	RADIAL_SYMBOL("resources/icons/view/radial/radial.png"),

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

	DATA_DOMAIN_CLINICAL("resources/icons/view/dataflipper/patient.png"),
	DATA_DOMAIN_PATHWAY("resources/icons/view/dataflipper/pathway.png"),
	DATA_DOMAIN_GENETIC("resources/icons/view/dataflipper/gene_expression.png"),

	LOADING("resources/loading/loading_background.png"),
	LOADING_CIRCLE("resources/loading/loading_circle.png"),

	CM_SELECTION_RIGHT_EXTENSIBLE_BLACK("/resources/context_menu/submenue_black.png"),
	CM_KEGG("/resources/context_menu/kegg.png"),


	COMPARER_SHOW_HIDDEN("/resources/icons/view/compare/show_hidden_elements.png"),
	COMPARER_SHOW_CAPTIONS("/resources/icons/view/compare/show_captions.png"),

	GROUPER_COLLAPSE_PLUS("resources/icons/general/plus.png"),
	GROUPER_COLLAPSE_MINUS("resources/icons/general/minus.png"),

	CLUSTER_ICON("resources/icons/view/tablebased/clustering.png"),

	// MOVE_ICON("resources/icons/view/histogram/move_icon.png"),

	/** Linearized Pathway */
	ABSTRACT_BAR_ICON("resources/icons/view/enroute/abstract_mode.png"),

	NAVIGATION_BACKGROUND("resources/icons/general/navigation.png"),
	PIN("resources/icons/general/pin.png");

	private final String sFileName;

	EIconTextures(String sFileName) {

		this.sFileName = sFileName;

	}

	public String getFileName() {

		return sFileName;
	}
}