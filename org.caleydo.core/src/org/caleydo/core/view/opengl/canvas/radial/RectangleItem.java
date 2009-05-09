package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;

public class RectangleItem
	extends ALabelItem {
	
	private float[] fArColor;
	private float fWidthToHeightRatio;

	public RectangleItem(float[] fArColor, float fWidthToHeightRatio) {
		if(fArColor.length >= 3) {
			this.fArColor = fArColor;
		}
		else {
			fArColor = new float[3];
		}
		this.fWidthToHeightRatio = fWidthToHeightRatio;
	}
	
	@Override
	public void draw(GL gl) {
		
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		
		gl.glColor3fv(fArColor, 0);
		
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - 0.01f, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - 0.01f, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() + fHeight - 0.01f, 0);
		gl.glVertex3f(vecPosition.x(), vecPosition.y() + fHeight - 0.01f, 0);
		gl.glEnd();
		
		gl.glPopAttrib();

	}
	
	public void setColor(float[] fArColor) {
		if(fArColor.length >= 3) {
			this.fArColor = fArColor;
		}
	}
	
	@Override
	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
		fWidth = fHeight * fWidthToHeightRatio;
	}

}
