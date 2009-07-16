package org.caleydo.core.view.opengl.canvas.storagebased;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class BookmarkRenderStyle
	extends GeneralRenderStyle {

	private static final float LIST_SELECTED_FIELD_WIDTH = 0.3f;

	public static final float LIST_SPACING = 0.05f;

	public static final int NUMBER_OF_LIST_ITEMS_PER_PAGE = 30;
	
	public BookmarkRenderStyle(IViewFrustum viewFrustum) {
		super(viewFrustum);
		// TODO Auto-generated constructor stub
	}

}
