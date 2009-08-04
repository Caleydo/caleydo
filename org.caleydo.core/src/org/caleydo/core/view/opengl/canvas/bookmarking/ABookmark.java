package org.caleydo.core.view.opengl.canvas.bookmarking;

import java.awt.Font;

import javax.media.opengl.GL;

import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class ABookmark {

	protected Dimensions dimensions;

	protected TextRenderer textRenderer;

	public ABookmark() {
		dimensions = new Dimensions();
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), false);
	}

	public abstract void render(GL gl);

	public Dimensions getDimensions() {
		return dimensions;
	}

	protected void renderCaption(GL gl, String sLabel, float fXOrigin, float fYOrigin, float fFontScaling) {

		if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
			sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
			sLabel = sLabel + "..";
		}

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sLabel, fXOrigin, fYOrigin, 0, fFontScaling);
		textRenderer.end3DRendering();
		gl.glPopAttrib();
	}


}
