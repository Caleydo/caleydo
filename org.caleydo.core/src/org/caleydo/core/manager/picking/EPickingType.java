package org.caleydo.core.manager.picking;

import org.caleydo.core.manager.id.EManagedObjectType;

public enum EPickingType {
	// bucket
	BUCKET_MOVE_IN_ICON_SELECTION(EManagedObjectType.GL_REMOTE_RENDERING, true),
	BUCKET_MOVE_OUT_ICON_SELECTION(EManagedObjectType.GL_REMOTE_RENDERING, true),
	BUCKET_MOVE_LEFT_ICON_SELECTION(EManagedObjectType.GL_REMOTE_RENDERING, true),
	BUCKET_MOVE_RIGHT_ICON_SELECTION(EManagedObjectType.GL_REMOTE_RENDERING, true),
	BUCKET_LOCK_ICON_SELECTION(EManagedObjectType.GL_REMOTE_RENDERING, false),
	BUCKET_REMOVE_ICON_SELECTION(EManagedObjectType.GL_REMOTE_RENDERING, false),
	BUCKET_DRAG_ICON_SELECTION(EManagedObjectType.GL_REMOTE_RENDERING, false),
	// BUCKET_SEARCH_PATHWAY,
	VIEW_SELECTION(EManagedObjectType.GL_REMOTE_RENDERING, true),
	/**
	 * A remote level element is the place-holder for a view, basically the wall behind a view
	 */
	REMOTE_LEVEL_ELEMENT(EManagedObjectType.GL_REMOTE_RENDERING, false),
	MEMO_PAD_SELECTION(EManagedObjectType.GL_REMOTE_RENDERING, false),

	// selection panel
	SELECTION_PANEL_ITEM(EManagedObjectType.GL_SELECTION_PANEL, false),

	// parallel coordinates
	POLYLINE_SELECTION(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	X_AXIS_SELECTION(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	Y_AXIS_SELECTION(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	GATE_TIP_SELECTION(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	GATE_BODY_SELECTION(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	GATE_BOTTOM_SELECTION(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	ADD_GATE(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	ADD_MASTER_GATE(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	REMOVE_GATE(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	PC_ICON_SELECTION(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	MOVE_AXIS(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	REMOVE_AXIS(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	DUPLICATE_AXIS(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	ANGULAR_UPPER(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	ANGULAR_LOWER(EManagedObjectType.GL_PARALLEL_COORDINATES, false),
	PCS_VIEW_SELECTION(EManagedObjectType.GL_PARALLEL_COORDINATES, true),
	REMOVE_NAN(EManagedObjectType.GL_PARALLEL_COORDINATES, false),

	// pathway manager
	PATHWAY_ELEMENT_SELECTION(EManagedObjectType.GL_PATHWAY, false),
	PATHWAY_TEXTURE_SELECTION(EManagedObjectType.GL_PATHWAY, false),

	// heat map
	HEAT_MAP_LINE_SELECTION(EManagedObjectType.GL_HEAT_MAP, false),
	HEAT_MAP_STORAGE_SELECTION(EManagedObjectType.GL_HEAT_MAP, false),

	// hierarchical heat map
	HIER_HEAT_MAP_FIELD_SELECTION(EManagedObjectType.GL_HIER_HEAT_MAP, false),
	HIER_HEAT_MAP_TEXTURE_SELECTION(EManagedObjectType.GL_HIER_HEAT_MAP, false),
	HIER_HEAT_MAP_INFOCUS_SELECTION(EManagedObjectType.GL_HIER_HEAT_MAP, false),
	HIER_HEAT_MAP_CURSOR(EManagedObjectType.GL_HIER_HEAT_MAP, false),
	HIER_HEAT_MAP_VIEW_SELECTION(EManagedObjectType.GL_HIER_HEAT_MAP, true),
	HIER_HEAT_MAP_TEXTURE_CURSOR(EManagedObjectType.GL_HIER_HEAT_MAP, false),
	HIER_HEAT_MAP_GENES_GROUP(EManagedObjectType.GL_HIER_HEAT_MAP, false),
	HIER_HEAT_MAP_EXPERIMENTS_GROUP(EManagedObjectType.GL_HIER_HEAT_MAP, false),

	// dendrogram
	DENDROGRAM_SELECTION(EManagedObjectType.GL_HIER_HEAT_MAP, false),

	// glyph
	GLYPH_FIELD_SELECTION(EManagedObjectType.GL_GLYPH, false),
	// TODO: works only for glyph sliders now, new solution?
	SLIDER_SELECTION(EManagedObjectType.GL_GLYPH_SLIDER, false),

	// radial hierarchy
	RAD_HIERARCHY_PDISC_SELECTION(EManagedObjectType.GL_RADIAL_HIERARCHY, false),

	// histogram
	HISTOGRAM_COLOR_LINE(EManagedObjectType.GL_HISTOGRAM, false);

	private EManagedObjectType viewType;

	private boolean bCanContainOtherPicks;

	private EPickingType(EManagedObjectType viewType, boolean bCanContainOtherPicks) {
		this.viewType = viewType;
		this.bCanContainOtherPicks = bCanContainOtherPicks;
	}

	/**
	 * Returns the view type associated with the Picking Type
	 * 
	 * @return
	 */
	public EManagedObjectType getViewType() {
		return viewType;
	}

	public boolean canContainOtherPicks() {
		return bCanContainOtherPicks;
	}

	public EPickingType get(int iOrdinal) {
		return values()[iOrdinal];
	}
}
