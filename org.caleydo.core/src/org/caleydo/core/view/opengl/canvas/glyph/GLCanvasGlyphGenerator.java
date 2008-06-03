package org.caleydo.core.view.opengl.canvas.glyph;

import javax.media.opengl.GL;

public class GLCanvasGlyphGenerator {
	int indexTopColor_ = -1;
	int indexBoxColor_ = -1;
	int indexHeight_ = -1;
	int iMaxHeight = 1;
	
	int iTowerBox = -1;
	int iTowerBoxBlackTop = -1;
	int iBaseBox = -1;
	int iBaseBoxSelected = -1;
	
	float sockel_h = 0.15f;
	
	
	public GLCanvasGlyphGenerator() {
		
	}
	
	public void setIndexTopColor(int index) {
		indexTopColor_ = index;
	}
	
	public void setIndexBoxColor(int index) {
		indexBoxColor_ = index;
	}
	
	public void setIndexHeight(int index) {
		indexHeight_ = index;
	}
	public int getIndexHeight() {
		return indexHeight_;
	}
	
	public void setMaxHeight(int height) {
		iMaxHeight = height;
	}
	
	
	public int generateGlyph(GL gl, GlyphEntry glyph, boolean selected) {
	    if(iBaseBox < 0) {
	    	iBaseBox = generateBase (gl, false);
	    	iBaseBoxSelected = generateBase (gl, true);
	    	iTowerBox = generateBox(gl, false);
	    	iTowerBoxBlackTop = generateBox(gl, true);
		}
	    
		return generateSingleObject(gl, glyph, selected);
	}

	
	private int generateSingleObject(GL gl, GlyphEntry glyph, boolean selected) {

		float box_h = glyph.getParameter(indexHeight_);
		// float sockel_h = 0.15f;

		if (box_h < 0.1f)
			box_h = 1.0f;

		box_h = box_h / iMaxHeight;

		int dltemp = gl.glGenLists(1);
		gl.glNewList(dltemp, GL.GL_COMPILE);

		gl.glPushMatrix();

		// gl.glEnable(GL.GL_BLEND);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);

		float[] mat_ambient = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] mat_diffuse = { 1.0f, 1.0f, 1.0f, 1.0f };

		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient, 0);

		gl.glEnable(GL.GL_AUTO_NORMAL);
		gl.glEnable(GL.GL_NORMALIZE);

		gl.glEnable(GL.GL_DEPTH_TEST);
		// gl.glEnable ( GL.GL_CULL_FACE);
		// gl.glBlendFunc ( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_LINE_SMOOTH);

		// gl.glEnable ( GL.GL_COLOR_MATERIAL ) ;
		// gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );

		// light

		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_LIGHT1);
		gl.glEnable(GL.GL_LIGHT2);

		float lc = 0.1f;

		float[] diffuse_light0 =
		{ 5 * lc, 5 * lc, 5 * lc, 1.0f };
		float[] position_light0 =
		{ 0.0f, 4.0f, 0.0f, 1.0f };
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse_light0, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position_light0, 0);

		float[] diffuse_light1 =
		{ 3 * lc, 3 * lc, 3 * lc, 1.0f };
		float[] position_light1 =
		{ -3.0f, 0.0f, 2.0f, 1.0f };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, diffuse_light1, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position_light1, 0);

		float[] diffuse_light2 =
		{ 2 * lc, 2 * lc, 2 * lc, 1.0f };
		float[] position_light2 =
		{ 5.5f, 0.0f, 2.0f, 1.0f };
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, diffuse_light2, 0);
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position_light2, 0);

		float[] ambient_lightModel =
		{ 5 * lc, 5 * lc, 5 * lc, 1.0f };
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, ambient_lightModel, 0);

		
		//draw base
		if (selected)
			gl.glCallList(iBaseBoxSelected);
		else
			gl.glCallList(iBaseBox);

		gl.glTranslatef(0.125f, 0.125f, sockel_h + 0.01f);

		// boxcolor
		int n = glyph.getParameter(indexBoxColor_) % 5;

		// TODO: add "standartverteilung" eg. double value of a glyph (lookup
		// dictionary)
		float nv = 0.5f;

		if (!selected)
			nv = 0.0f;

		switch (n)
		{
		case 0:
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // white
			break;
		case 1:
			gl.glColor4f(0.0f, 0.4f + nv, 0.0f, 1.0f); // green
			break;
		case 2:
			gl.glColor4f(0.3f, 0.4f + nv, 0.4f + nv, 1.0f); // gray -->
															// blue/green
			break;
		case 3:
			gl.glColor4f(0.4f + nv, 0.4f + nv, 0.0f, 1.0f); // gray --> gold
			break;
		case 4:
			gl.glColor4f(0.4f + nv, 0.0f, 0.0f, 1.0f); // red
			break;
		default:
			gl.glColor4f(0.1f + nv, 0.1f + nv, 0.1f + nv, 1.0f); // gray
			break;
		}

		gl.glScalef(1.0f, 1.0f, box_h);

		int tc = glyph.getParameter(indexTopColor_);
		if (tc % 2 == 0)
			gl.glCallList(iTowerBoxBlackTop);
		else
			gl.glCallList(iTowerBox);

		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_LIGHT0);
		gl.glDisable(GL.GL_LIGHT1);
		gl.glDisable(GL.GL_LIGHT2);

		gl.glPopMatrix();

		gl.glEndList();

		return dltemp;
	}
	
	
	private int generateBase(GL gl, boolean borderHighlight) {

		int dltemp = gl.glGenLists(1);
		gl.glNewList(dltemp, GL.GL_COMPILE);

		float height = sockel_h;

		float xmin = 0.0f;
		float ymin = 0.0f;
		float zmin = 0.0f;
		float xmax = 1.0f;
		float ymax = 1.0f;
		float zmax = height;

		if (borderHighlight)
			gl.glColor4f(0.1f, 0.6f, 0.1f, 1.0f);
		else
			gl.glColor4f(0.1f, 0.35f, 0.1f, 1.0f);

		// front left
		gl.glBegin(GL.GL_QUADS);

		gl.glNormal3i(0, -1, 0);
		gl.glVertex3f(xmin, ymin, zmin);
		gl.glVertex3f(xmax, ymin, zmin);
		gl.glVertex3f(xmax, ymin, zmax);
		gl.glVertex3f(xmin, ymin, zmax);
		gl.glEnd();

		// front right
		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3i(-1, 0, 0);
		gl.glVertex3f(xmin, ymax, zmax);
		gl.glVertex3f(xmin, ymax, zmin);
		gl.glVertex3f(xmin, ymin, zmin);
		gl.glVertex3f(xmin, ymin, zmax);
		gl.glEnd();

		// top
		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3i(0, 0, 1);
		gl.glTexCoord2d(0, 0);
		gl.glVertex3f(xmin, ymin, zmax);
		gl.glTexCoord2d(1, 0);
		gl.glVertex3f(xmax, ymin, zmax);
		gl.glTexCoord2d(1, 1);
		gl.glVertex3f(xmax, ymax, zmax);
		gl.glTexCoord2d(0, 1);
		gl.glVertex3f(xmin, ymax, zmax);
		gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);

		gl.glEndList();

		return dltemp;
	}
	
	
	private int generateBox(GL gl, boolean topcolorblack) {
		float xmin = 0f;
		float ymin = 0f;
		float zmin = 0.0f;
		float xmax = 0.75f;
		float ymax = 0.75f;
		float zmax = 1.0f;
		
		int dltemp = gl.glGenLists(1);
		gl.glNewList(dltemp, GL.GL_COMPILE);

		// der Boden
		/*
		gl.glBegin(GL.GL_QUADS); 
		gl.glNormal3i (0, 0, -1);
		gl.glVertex3f(xmin, ymax, zmin);    
		gl.glVertex3f(xmax, ymax, zmin);
		gl.glVertex3f(xmax, ymin, zmin);    
		gl.glVertex3f(xmin, ymin, zmin);
		gl.glEnd( );
*/
		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3i (0, 1, 0);
		gl.glVertex3f(xmin, ymax, zmin);
		gl.glVertex3f(xmin, ymax, zmax);
		gl.glVertex3f(xmax, ymax, zmax);
		gl.glVertex3f(xmax, ymax, zmin);
		gl.glEnd( );

		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3i (1, 0, 0);    
		gl.glVertex3f(xmax, ymin, zmax);
		gl.glVertex3f(xmax, ymin, zmin);
		gl.glVertex3f(xmax, ymax, zmin);    
		gl.glVertex3f(xmax, ymax, zmax);
		gl.glEnd( );


//front right
		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3i (0, -1, 0);
		gl.glVertex3f(xmin, ymin, zmin);
		gl.glVertex3f(xmax, ymin, zmin);
		gl.glVertex3f(xmax, ymin, zmax);    
		gl.glVertex3f(xmin, ymin, zmax);
		gl.glEnd( );

//front left
		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3i (-1, 0, 0);
		gl.glVertex3f(xmin, ymax, zmax);
		gl.glVertex3f(xmin, ymax, zmin);
		gl.glVertex3f(xmin, ymin, zmin);
		gl.glVertex3f(xmin, ymin, zmax);
		gl.glEnd( );

//top
		if (topcolorblack)
			gl.glColor4d     ( 0.0, 0.0, 0.0, 1.0);
		
		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3i (0, 0, 1);
		gl.glVertex3f(xmin, ymin, zmax);
		gl.glVertex3f(xmax, ymin, zmax);
		gl.glVertex3f(xmax, ymax, zmax);
		gl.glVertex3f(xmin, ymax, zmax);
		gl.glEnd( );
		
		gl.glEndList();
		return dltemp;
	}



}
