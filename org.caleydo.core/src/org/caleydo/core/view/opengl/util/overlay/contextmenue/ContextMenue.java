package org.caleydo.core.view.opengl.util.overlay.contextmenue;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.renderstyle.InfoAreaRenderStyle;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.AOverlayManager;

public class ContextMenue
	extends AOverlayManager {

	String sData = "test";
	private float xOrigin;
	private float yOrigin;

	public ContextMenue() {
		super();
	}

	public void setData() {

	}

	public void render(GL gl) {
		if (!isEnabled)
			return;
		if (isFirstTime) {

			float[] fArWorldCoords =
				GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x, pickedPoint.y);
			xOrigin = fArWorldCoords[0];
			yOrigin = fArWorldCoords[1];

			// fXElementOrigin = fXOrigin + 0.2f;
			// fYElementOrigin = fYOrigin + 0.2f;
			// vecLowerLeft.set(xOrigin, yOrigin, 0);

		}

		gl.glColor3fv(InfoAreaRenderStyle.INFO_AREA_COLOR, 0);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(xOrigin, yOrigin, 4);
		gl.glVertex3f(xOrigin, yOrigin + 2, 4);
		gl.glVertex3f(xOrigin + 2, yOrigin + 2, 4);
		gl.glVertex3f(xOrigin + 2, yOrigin, 4);
		gl.glEnd();

		// infoArea.renderInfoArea(gl, vecLowerLeft, bFirstTime, 4);
		// bFirstTime = false;
	}
}
