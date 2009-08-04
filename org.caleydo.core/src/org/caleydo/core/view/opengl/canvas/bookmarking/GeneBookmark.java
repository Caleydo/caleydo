package org.caleydo.core.view.opengl.canvas.bookmarking;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class GeneBookmark
	extends ABookmark {

	Integer id;
	public GeneBookmark(Integer id) {
		this.id = id;
		dimensions.setHeight(0.1f);
	}

	@Override
	public void render(GL gl) {
		
		renderCaption(gl, id.toString(), dimensions.getXOrigin(), dimensions.getYOrigin(),
			GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);

	}

}
