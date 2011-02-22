package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.text.MinSizeTextRenderer;

class RenderingHelpers {

	static void renderText(GL2 gl, MinSizeTextRenderer textRenderer, String sLabel,
			float fXOrigin, float fYOrigin, float fFontScaling) {

		textRenderer.setColor(0, 0, 0, 1);

//		if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
//			sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
//			sLabel = sLabel + "..";
//		}

		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
		textRenderer.renderText(gl, sLabel, fXOrigin, fYOrigin, 0);
		gl.glPopAttrib();

	}

}
