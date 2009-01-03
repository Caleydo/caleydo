package org.caleydo.core.view.opengl.util.texture;

public enum EIconTextures
{
	ARROW_LEFT("resources/icons/view/remote/arrow-left.png"),
	ARROW_RIGHT("resources/icons/view/remote/arrow-right.png"),
	ARROW_UP("resources/icons/view/remote/arrow-up.png"),
	ARROW_DOWN("resources/icons/view/remote/arrow-down.png"),
	REMOVE("resources/icons/view/storagebased/parcoords/axis_delete.png"),
	LOCK("resources/icons/view/remote/lock.png"),
	DUPLICATE("resources/icons/view/storagebased/parcoords/axis_copy.png"),
	POLYLINE_TO_AXIS("resources/icons/general/no_icon_available.png"),
	PREVENT_OCCLUSION("resources/icons/general/no_icon_available.png"),
	RENDER_SELECTION("resources/icons/general/no_icon_available.png"),
	RESET_SELECTIONS("resources/icons/general/no_icon_available.png"),
	SAVE_SELECTIONS("resources/icons/general/no_icon_available.png"),
	ANGULAR_BRUSHING("resources/icons/view/storagebased/parcoords/angular_brush.png"),
	HEAT_MAP_SYMBOL("resources/icons/view/storagebased/heatmap/heatmap128x128.png"),
	PAR_COORDS_SYMBOL("resources/icons/view/storagebased/parcoords/parcoords128x128.png"),
	PATHWAY_SYMBOL("resources/icons/view/pathway/pathway128x128.png"),
	GLYPH_SYMBOL("resources/icons/view/glyph/glyph128x128.png"),

	GLYPH_SORT_RANDOM("resources/icons/view/glyph/sort_random.png"),
	GLYPH_SORT_CIRCLE("resources/icons/view/glyph/sort_spirale.png"),
	GLYPH_SORT_RECTANGLE("resources/icons/view/glyph/sort_zickzack.png"),

	BROWSER_REFRESH_IMAGE("resources/icons/view/browser/refresh.png"),
	BROWSER_BACK_IMAGE("resources/icons/view/browser/back.png"),
	BROWSER_STOP_IMAGE("resources/icons/view/browser/stop.png"),
	BROWSER_HOME_IMAGE("resources/icons/view/browser/home.png"),

	PANEL_SELECTION("resources/panel/selection_background.png"),

	NAVIGATION_REMOVE_VIEW("resources/icons/general/navigation_remove_view.png"),
	NAVIGATION_DRAG_VIEW("resources/icons/general/navigation_drag_view.png"),
	NAVIGATION_LOCK_VIEW("resources/icons/general/navigation_lock_view.png"),	
	
	POOL_REMOVE_VIEW("resources/icons/general/pool_remove_view.png"),
	POOL_DRAG_VIEW("resources/icons/general/pool_drag_view.png"),
	POOL_VIEW_BACKGROUND("resources/navigation/pool_view_background.png"),
	POOL_VIEW_BACKGROUND_SELECTION("resources/navigation/pool_view_background_selection.png"),
	
	LOADING("resources/loading/loading_background.png"),
	LOADING_CIRCLE("resources/loading/loading_circle.png"), 
	
	CELL_MODEL("resources/models/cell.jpg");

	private String sFileName;

	EIconTextures(String sFileName)
	{

		this.sFileName = sFileName;

	}

	public String getFileName()
	{

		return sFileName;
	}
}