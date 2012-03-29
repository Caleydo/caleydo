package org.caleydo.core.view.opengl.miniview;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class GLParCoordsMiniView
	extends AGLMiniView {

	public GLParCoordsMiniView() {

		fWidth = 0.2f;
		fHeight = 0.2f;
	}

	@Override
	public void render(GL2 gl, float fXOrigin, float fYOrigin, float fZOrigin) {

		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(fXOrigin, fYOrigin, GeneralRenderStyle.MINIVEW_Z);
		gl.glVertex3f(fXOrigin, fYOrigin + fHeight, GeneralRenderStyle.MINIVEW_Z);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin + fHeight, GeneralRenderStyle.MINIVEW_Z);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin, GeneralRenderStyle.MINIVEW_Z);
		gl.glEnd();
		gl.glPopAttrib();
	}
}
