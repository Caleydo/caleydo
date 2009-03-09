package org.caleydo.core.view.opengl.renderstyle.border;

import javax.media.opengl.GL;

public class BorderRenderStyleLineSolid
	extends BorderRenderStyle {
	private float fHeight = 1.0f;
	private float fWidth = 1.0f;

	@Override
	public void init(GL gl) {
		if (glList >= 0)
			gl.glDeleteLists(glList, 1);

		glList = gl.glGenLists(1);
		gl.glNewList(glList, GL.GL_COMPILE);
		draw(gl);
		gl.glEndList();
	}

	@Override
	public void display(GL gl) {
		if (glList < 0)
			draw(gl);
		else
			gl.glCallList(glList);
	}

	private void draw(GL gl) {
		gl.glPushMatrix();
		gl.glLineWidth(iBorderWidth);

		if (bBorderLeft) {
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2), vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, fHeight, 0);
			gl.glEnd();
		}

		gl.glTranslatef(0f, fHeight, 0f);

		if (bBorderTop) {
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2), vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(fWidth, 0, 0);
			gl.glEnd();
		}

		gl.glTranslatef(fWidth, 0f, 0f);

		if (bBorderRight) {
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2), vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, -fHeight, 0);
			gl.glEnd();
		}

		gl.glTranslatef(0f, -fHeight, 0f);

		if (bBorderBottom) {
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2), vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(-fWidth, 0, 0);
			gl.glEnd();
		}

		gl.glLineWidth(1);
		gl.glPopMatrix();
	}

}
