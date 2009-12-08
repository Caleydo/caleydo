package org.caleydo.core.view.opengl.canvas.bookmarking;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

import com.sun.opengl.util.j2d.TextRenderer;

class RenderingHelpers {

	static void renderText(GL gl, TextRenderer textRenderer, String sLabel, float fXOrigin, float fYOrigin,
		float fFontScaling) {

		textRenderer.setColor(0, 0, 0, 1);

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
