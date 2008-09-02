package org.caleydo.core.view.opengl.util;

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

	GLYPH_SORT_RANDOM("resources/icons/view/glyph/sort_random.png"),
	GLYPH_SORT_CIRCLE("resources/icons/view/glyph/sort_spirale.png"),
	GLYPH_SORT_RECTANGLE("resources/icons/view/glyph/sort_zickzack.png");

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