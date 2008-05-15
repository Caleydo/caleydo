package org.caleydo.core.view.opengl.canvas.glyph;

import java.util.HashMap;
import java.util.Random;

import javax.media.opengl.GL;


public class GLCanvasGlyphGenerator {
	int indexTopColor_ = -1;
	int indexBoxColor_ = -1;
	int indexHeight_ = -1;
	
	public GLCanvasGlyphGenerator() {
		indexTopColor_ = 2;
		indexBoxColor_ = 0;
		indexHeight_ = 1;
	}
	
	public HashMap<Integer, GlyphEntry> loadGlyphs(GL gl) {
		HashMap<Integer, GlyphEntry> glyphs = new HashMap<Integer, GlyphEntry>();
		
		Random rand = new Random();
		
		int counter=1;
		for(int i=0;i<1000;++i) {
				GlyphEntry g = new GlyphEntry(counter, this);
				g.addParameter(rand.nextInt(5));  // boxcolor
				g.addParameter(rand.nextInt(10)); // height
				g.addParameter(rand.nextInt(2));  // topcolor
				glyphs.put(counter, g);
				++counter;
		}
		 
		return glyphs;
	}
	
	
	public int generateGlyph(GL gl, GlyphEntry glyph, boolean selected) {
		return generateSingleObject(gl, glyph, selected);
	}

	
	private int generateSingleObject(GL gl, GlyphEntry glyph, boolean selected) {

		float xmin = 0f;
		float ymin = 0f;
		float zmin = 0.0f;
		float xmax = 0.75f;
		float ymax = 0.75f;
		float zmax = 1.0f;
		float box_h = (glyph.getParameter(indexHeight_)%10);
		float sockel_h = 0.15f;

		int dltemp = gl.glGenLists(1);
		gl.glNewList(dltemp, GL.GL_COMPILE);
		
		

		gl.glEnable(GL.GL_BLEND);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);

		float[] mat_ambient = {1.0f, 1.0f, 1.0f, 1.0f };
		float[] mat_diffuse = {1.0f, 1.0f, 1.0f, 1.0f };
		
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse,0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient,0);

	      gl.glEnable    ( GL.GL_AUTO_NORMAL);
	      gl.glEnable    ( GL.GL_NORMALIZE);

	      gl.glEnable    ( GL.GL_DEPTH_TEST);
//	      gl.glEnable    ( GL.GL_CULL_FACE);
//	      gl.glBlendFunc ( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	      gl.glEnable    ( GL.GL_LINE_SMOOTH);

//	      gl.glEnable     ( GL.GL_COLOR_MATERIAL ) ;
//	      gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );



		
		//light
		
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_LIGHT1);
		gl.glEnable(GL.GL_LIGHT2);

	      float lc = 0.1f;

	      float[] diffuse_light0  = { 5*lc, 5*lc, 5*lc, 1.0f };
	      float[] position_light0 = { 0.0f, 4.0f, 0.0f, 1.0f };
	      gl.glLightfv( GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse_light0, 0 );
	      gl.glLightfv( GL.GL_LIGHT0, GL.GL_POSITION, position_light0, 0 );


	      float[] diffuse_light1  =  { 3*lc,3*lc,3*lc, 1.0f };
	      float[] position_light1 = { -3.0f, 0.0f, 2.0f, 1.0f };
	      gl.glLightfv( GL.GL_LIGHT1, GL.GL_DIFFUSE, diffuse_light1, 0 );
	      gl.glLightfv( GL.GL_LIGHT1, GL.GL_POSITION, position_light1, 0 );

	      float[] diffuse_light2  =  { 2*lc,2*lc,2*lc, 1.0f };
	      float[] position_light2 = { 5.5f, 0.0f, 2.0f, 1.0f };
	      gl.glLightfv( GL.GL_LIGHT2, GL.GL_DIFFUSE, diffuse_light2, 0 );
	      gl.glLightfv( GL.GL_LIGHT2, GL.GL_POSITION, position_light2, 0 );
	      
	      float[] ambient_lightModel =  { 5*lc,5*lc,5*lc, 1.0f };
	      gl.glLightModelfv( GL.GL_LIGHT_MODEL_AMBIENT, ambient_lightModel,0 );

		
		
		//float xmin, float ymin, float zmin, float xmax, float ymax, float zmax, boolean topcolorblack
		
    	generateBase (gl, sockel_h, selected);

		gl.glTranslatef(0.125f, 0.125f, sockel_h+0.01f);
		
		
		//boxcolor
	    int n = glyph.getParameter(indexBoxColor_)%5;
	    //TODO: add "standartverteilung" eg. double value of a glyph (lookup dictionary)
	    float nv = 0.5f;
	    
	    if(!selected)
	    	nv = 0.0f;
	    
	    
	    switch(n) {
	    	case 0:
	    		gl.glColor4f(0.8f+nv, 0.8f+nv, 0.8f+nv, 1.0f);     // gray
	    		break;
	    	case 1:
	    		gl.glColor4f( 0.0f, 0.4f + nv, 0.0f, 1.0f); // green
	    		break;
	    	case 2:
	    		gl.glColor4f(0.3f, 0.4f + nv, 0.4f + nv, 1.0f); //gray --> blue/green
	    		break;
	    	case 3:
	    		gl.glColor4f(0.4f + nv, 0.4f + nv, 0.0f, 1.0f); //gray --> gold
	    		break;
	    	case 4:
	    		gl.glColor4f( 0.4f + nv, 0.0f, 0.0f, 1.0f); // red
	    		break;
	    }
	    

	    int tc = glyph.getParameter(indexTopColor_);
	    if(tc%2 == 0)
	    	generateBox(gl, xmin, ymin, zmin, xmax, ymax, box_h/10, true);
	    else
	    	generateBox(gl, xmin, ymin, zmin, xmax, ymax, box_h/10, false);
	    
		gl.glTranslatef(-0.125f, -0.125f, -sockel_h-0.01f);

		gl.glEndList();

		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_LIGHT0);
		gl.glDisable(GL.GL_LIGHT1);
		gl.glDisable(GL.GL_LIGHT2);
		
		return dltemp;
	}
	
	
	private void generateBase (GL gl, float height, boolean borderHighlight) 
	{

	  float xmin = 0.0f; float ymin = 0.0f; float zmin = 0.0f;
	  float xmax = 1.0f; float ymax = 1.0f; float zmax = height;

	  if (borderHighlight)
			gl.glColor4f(0.1f, 0.6f, 0.1f, 1.0f);
	  else
		    gl.glColor4f(0.1f, 0.35f, 0.1f, 1.0f);


	  //front left
	  gl.glBegin(GL.GL_QUADS);
	  
	  gl.glNormal3i (0, -1, 0);
	  gl.glVertex3f(xmin, ymin, zmin);
	  gl.glVertex3f(xmax, ymin, zmin);
	  gl.glVertex3f(xmax, ymin, zmax);
	  gl.glVertex3f(xmin, ymin, zmax);
	  gl.glEnd( );

//front right
	  gl.glBegin(GL.GL_QUADS);
	  gl.glNormal3i (-1, 0, 0);
	  gl.glVertex3f(xmin, ymax, zmax);
	  gl.glVertex3f(xmin, ymax, zmin);
	  gl.glVertex3f(xmin, ymin, zmin);
	  gl.glVertex3f(xmin, ymin, zmax);
	  gl.glEnd( );

//top
	  gl.glBegin(GL.GL_QUADS);
	  gl.glNormal3i (0, 0, 1);
	  gl.glTexCoord2d (0,  0);
	  gl.glVertex3f(xmin, ymin, zmax);
	  gl.glTexCoord2d (1,  0);
	  gl.glVertex3f(xmax, ymin, zmax);
	  gl.glTexCoord2d (1,  1);
	  gl.glVertex3f(xmax, ymax, zmax);
	  gl.glTexCoord2d (0,  1);
	  gl.glVertex3f(xmin, ymax, zmax);
	  gl.glEnd( );
	  gl.glDisable( GL.GL_TEXTURE_2D);

	}
	
	
	private void generateBox(GL gl, float xmin, float ymin, float zmin, float xmax, float ymax, float zmax, boolean topcolorblack) {
/*
		// der Boden
		gl.glBegin(GL.GL_QUADS); 
		gl.glNormal3i (0, 0, -1);
		gl.glVertex3f(xmin, ymax, zmin);    
		gl.glVertex3f(xmax, ymax, zmin);
		gl.glVertex3f(xmax, ymin, zmin);    
		gl.glVertex3f(xmin, ymin, zmin);
		gl.glEnd( );
*/
/*
		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3i (0, 1, 0);
		gl.glVertex3f(xmin, ymax, zmin);
		gl.glVertex3f(xmin, ymax, zmax);
		gl.glVertex3f(xmax, ymax, zmax);
		gl.glVertex3f(xmax, ymax, zmin);
		gl.glEnd( );
*/
/*
		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3i (1, 0, 0);    
		gl.glVertex3f(xmax, ymin, zmax);
		gl.glVertex3f(xmax, ymin, zmin);
		gl.glVertex3f(xmax, ymax, zmin);    
		gl.glVertex3f(xmax, ymax, zmax);
		gl.glEnd( );
*/

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
	}



}
