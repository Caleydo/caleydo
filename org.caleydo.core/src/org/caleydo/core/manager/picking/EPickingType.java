package org.caleydo.core.manager.picking;


/**
 * List of all possible pickable elements. Every type of element which should be pickable must be registered
 * here.
 * 
 * @author Alexander Lex
 */
public enum EPickingType {
	// bucket
	BUCKET_MOVE_IN_ICON_SELECTION,
	BUCKET_MOVE_OUT_ICON_SELECTION,
	BUCKET_MOVE_LEFT_ICON_SELECTION,
	BUCKET_MOVE_RIGHT_ICON_SELECTION,
	BUCKET_LOCK_ICON_SELECTION,
	BUCKET_REMOVE_ICON_SELECTION,
	BUCKET_DRAG_ICON_SELECTION,
	// BUCKET_SEARCH_PATHWAY,
	VIEW_SELECTION,
	/** A remote level element is the place-holder for a view, basically the wall behind a view */
	REMOTE_LEVEL_ELEMENT,
	MEMO_PAD_SELECTION,

	// selection panel
	SELECTION_PANEL_ITEM,

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

	// pathway manager
	PATHWAY_ELEMENT_SELECTION,
	PATHWAY_TEXTURE_SELECTION,

	// heat map
	HEAT_MAP_LINE_SELECTION,
	HEAT_MAP_STORAGE_SELECTION,
	
	// list heat map
	LIST_HEAT_MAP_CLEAR_ALL,

	// hierarchical heat map
	HIER_HEAT_MAP_FIELD_SELECTION,
	HIER_HEAT_MAP_TEXTURE_SELECTION,
	HIER_HEAT_MAP_INFOCUS_SELECTION,
	HIER_HEAT_MAP_CURSOR,
	HIER_HEAT_MAP_BLOCK_CURSOR,
	HIER_HEAT_MAP_VIEW_SELECTION,
	HIER_HEAT_MAP_TEXTURE_CURSOR,
	HIER_HEAT_MAP_GENES_GROUP,
	HIER_HEAT_MAP_EXPERIMENTS_GROUP,
	
	

	// dendrogram
	DENDROGRAM_HORIZONTAL_SELECTION,
	DENDROGRAM_VERTICAL_SELECTION,
	DENDROGRAM_CUT_SELECTION,

	// glyph
	GLYPH_FIELD_SELECTION,
	// TODO: works only for glyph sliders now, new solution?
	SLIDER_SELECTION,

	// radial hierarchy
	RAD_HIERARCHY_PDISC_SELECTION,
	RAD_HIERARCHY_SLIDER_SELECTION,
	RAD_HIERARCHY_SLIDER_BUTTON_SELECTION,

	// histogram
	HISTOGRAM_COLOR_LINE,
	HISTOGRAM_LEFT_SPREAD_COLOR_LINE,
	HISTOGRAM_RIGHT_SPREAD_COLOR_LINE,

	CONTEXT_MENU_SELECTION;

}
