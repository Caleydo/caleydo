package org.caleydo.core.view.opengl.miniview.slider;

import gleem.linalg.Vec4f;

import javax.media.opengl.GL2;

/**
 * OpenGL2 Slider Seperator
 * 
 * @author Stefan Sauer
 */
public class SliderSeperatorBond {
	private int iID = 0;
	private SliderSeperator seperator1 = null;
	private SliderSeperator seperator2 = null;

	public SliderSeperatorBond(int id, SliderSeperator seperator1, SliderSeperator seperator2) {
		iID = id;
		this.seperator1 = seperator1;
		this.seperator2 = seperator2;
	}

	public int getID() {
		return iID;
	}

	public void render(GL2 gl, float sizeX, float sizeY, Vec4f color) {
		float bottom = getBottom();
		float top = getTop();
		float height = top - bottom;

		gl.glPushMatrix();

		gl.glColor4f(color.get(0), color.get(1), color.get(2), color.get(3));
		gl.glTranslatef(0, bottom, -0.02f);
		// gl.glTranslatef(0, bottom, -0.5f);

		gl.glBegin(GL2.GL_QUADS);
		gl.glNormal3i(0, 1, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, height, 0);
		gl.glVertex3f(sizeX, height, 0);
		gl.glVertex3f(sizeX, 0, 0);
		gl.glEnd();

		gl.glPopMatrix();
	}

	public float getTop() {
		float p1 = seperator1.getPos();
		float p2 = seperator2.getPos();
		if (p1 < p2)
			return p2;
		else
			return p1;
	}

	public float getBottom() {
		float p1 = seperator1.getPos();
		float p2 = seperator2.getPos();
		if (p1 > p2)
			return p2;
		else
			return p1;
	}

}
