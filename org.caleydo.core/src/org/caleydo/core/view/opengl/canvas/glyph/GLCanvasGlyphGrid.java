package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.Vec4f;
import gleem.linalg.open.Vec2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import javax.media.opengl.GL;

import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.PickingManager;


public class GLCanvasGlyphGrid {
	
	public enum POSITIONTYPES { rectangle , center }; 
	
	//Integer[][] gridArray_;
	Vector<Vector<GlyphGridPosition>> glyphMap_;
	
	HashMap<Integer, GlyphEntry> glyphs_ = null;

	int GLGridList_ = 0;
	int GLGlyphList_ = 0;
	private Vec4f gridColor_ = new Vec4f( 0.2f, 0.2f, 0.2f, 1f );
	
	
	
	public GLCanvasGlyphGrid() {
		//gridArray_ = new Integer[1000][1000];
		
		glyphMap_ = new Vector<Vector<GlyphGridPosition>>();
		
		//build grid
		for(int i=0;i<1000;++i) {
			Vector<GlyphGridPosition> t = new Vector<GlyphGridPosition>();
			for(int j=0;j<1000;++j)
				t.add(j, new GlyphGridPosition(i,j));
			glyphMap_.add(i,  t);
		}
	}
	
	public int getXMax() {
		return glyphMap_.size();
	}
	
	public int getYMax() {
		if(getXMax() > 0)
			return glyphMap_.get(0).size();
		return 0;
	}
	
	public void deSelectAll() {
		for(GlyphEntry g : glyphs_.values())
			g.deSelect();
	}
	
	public int getGlyphGLList(GL gl, int x, int y) {
		if(glyphMap_.get(x) != null)
			if(glyphMap_.get(x).get(y) != null)
				if(glyphMap_.get(x).get(y).getGlyph() != null)
					return glyphMap_.get(x).get(y).getGlyph().getGlList(gl);
		return -1;
	}

	public int getGlyphID(int x, int y) {
		if(glyphMap_.get(x) != null)
			if(glyphMap_.get(x).get(y) != null)
				if(glyphMap_.get(x).get(y).getGlyph() != null)
				return glyphMap_.get(x).get(y).getGlyph().getID();
    	return -1;

	}
	public Vec2i getGridPosition(int x, int y) {
		return glyphMap_.get(x).get(y).getGridPosition();
	}
	
    public GlyphEntry getGlyph(int id) {
    	if(!glyphs_.containsKey(id))
    		return null;
    	return glyphs_.get(id);
    }
    
    
    public HashMap<Integer, GlyphEntry> getGlyphList() {
    	return glyphs_;
    }
	

	
	private void clearGlyphMap() {
		for(Vector<GlyphGridPosition> v1 : glyphMap_)
			for(GlyphGridPosition v2 : v1)
				v2.setGlyph(null);
	}

	
	
	public void buildGrid(GL gl) {
		//load & set glyphs
		GLCanvasGlyphGenerator gen = new GLCanvasGlyphGenerator();

		glyphs_ = gen.loadGlyphs(gl);
		//setGlyphPositionsRectangle();
		setGlyphPositionsCenter();
		
		
		
		//draw grid
		GLGridList_ = gl.glGenLists(1);
		gl.glNewList(GLGridList_, GL.GL_COMPILE);
		
		gl.glLineWidth(1);
		
		gl.glTranslatef(0f, -100f, 0f);
		
		for(int i=0;i<200;++i) {
			gl.glTranslatef(0f, 1f, 0f);
			gl.glBegin(GL.GL_LINES);
	    	gl.glColor4f(gridColor_.get(0),gridColor_.get(1),gridColor_.get(2),gridColor_.get(3));
	    	gl.glVertex3f(-100,  0,  0);
	    	gl.glVertex3f(100,  0,  0);
	    	gl.glEnd();
		}
		
		gl.glTranslatef(-100f,-100f, 0f);
		
		for(int i=0;i<200;++i) {
			gl.glTranslatef(1f, 0f, 0f);
			gl.glBegin(GL.GL_LINES);
	    	gl.glColor4f(gridColor_.get(0),gridColor_.get(1),gridColor_.get(2),gridColor_.get(3));
	    	gl.glVertex3f(0,  -100,  0);
	    	gl.glVertex3f(0,  100,  0);
	    	gl.glEnd();
		}

		gl.glTranslatef(-100f,0f, 0f);
		
		gl.glEndList();
	}
	
	
	public void setGlyphPositions(POSITIONTYPES typ) {
		switch(typ) {
			case rectangle :
				setGlyphPositionsRectangle();
				break;
			case center :
				setGlyphPositionsCenter();
				break;
		}
	}
	
	
	private void setGlyphPositionsRectangle() {
		clearGlyphMap();
		ArrayList<GlyphEntry> gg = sortGlyphs(glyphs_.values());
		
		int num = gg.size();
		int x_max = (int) java.lang.Math.sqrt(num);
		
		int i=0,j=0;
		for(GlyphEntry g : gg) {

			g.setPosition(i, j);
			glyphMap_.get(i).get(j).setGlyph(g);
			
			++i;
			
			if(i>=x_max) {
				i=0;
				++j;
			}

		}
	}
	
	private void setGlyphPositionsCenter() {
		ArrayList<GlyphEntry> gg = sortGlyphs(glyphs_.values());
		
		int num = gg.size();
		int x_max = (int) java.lang.Math.sqrt(num);
		
		int center = x_max/2 + 1;

		int k =  0;
		int d  = 0;
		int cm = 1;
		int c  = 0;
		int x = center;
		int y = center;
		
		for(GlyphEntry g : gg) {
			g.setPosition(x, y);
			glyphMap_.get(x).get(y).setGlyph(g);
			
			switch(d%4) {
				case 0: ++x; break;
				case 1: --y; break;
				case 2: --x; break;
				case 3: ++y; break;
			}
			++c;
			if(c == cm) {
				++d;
				c=0;
				++k;
				if(k==2) {
					++cm;
					k=0;
				}
			}
			
		}
	}
	

	
	
	
	
	
	private ArrayList<GlyphEntry> sortGlyphs( Collection<GlyphEntry> unsorted) {
		//ArrayList<GlyphEntry> temp2 = new ArrayList<GlyphEntry>();
		//temp2.addAll(unsorted);
		//return temp2;
		return sortGlyphsRecursive(unsorted,0);
	}
	
	
	
	/*
	 * TODO: change parameterindex to a queue type thing
	 * (so it can be sorted not depending on the parameterindex)  
	 */
	private ArrayList<GlyphEntry> sortGlyphsRecursive( Collection<GlyphEntry> unsorted, int parameterindex) {
		HashMap<Integer, ArrayList<GlyphEntry>> temp = new HashMap<Integer, ArrayList<GlyphEntry>>(); 
		int maxp = 0;

		for(final GlyphEntry g : unsorted) {
			int p = g.getParameter(parameterindex);

			if(p < 0) { //ups - no more parameter to sort after
				ArrayList<GlyphEntry> t = new ArrayList<GlyphEntry>();
				t.addAll(unsorted);
				return t;
			}
			
			if(!temp.containsKey(p))
				temp.put(p, new ArrayList<GlyphEntry>());
			
			temp.get(p).add(g);
			if(p > maxp)
				maxp = p;
		}
		
		ArrayList<GlyphEntry> temp2 = new ArrayList<GlyphEntry>();
		for(int i=0;i<=maxp; ++i) {
			if(!temp.containsKey(i))
				continue;
			
			ArrayList<GlyphEntry> gs = sortGlyphsRecursive(temp.get(i), parameterindex+1);
			temp2.addAll(gs);
		}
		
		return temp2;
	}

	public int getGridLayout() {
	  return GLGridList_;
	}

	public boolean isFree(int x, int y) {
		if(glyphMap_.get(x).get(y).getGlyph() == null)
			return true;

		return false;
	}
	
	

}
