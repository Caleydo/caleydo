package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.Vec4f;
import gleem.linalg.open.Vec2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;


public class GLCanvasGlyphGrid {
	private IGeneralManager generalManager;

	private Vector<Vector<GlyphGridPosition>> glyphMap_;
	
	private HashMap<Integer, GlyphEntry> glyphs_ = null;
	private GlyphDataLoader glyphDataLoader = null;

	int GLGridList_ = 0;
	int GLGlyphList_ = 0;
	private Vec4f gridColor_ = new Vec4f( 0.2f, 0.2f, 0.2f, 1f );
	private Vec2i worldLimit_ = new Vec2i(); 
	
	
	
	public GLCanvasGlyphGrid(final IGeneralManager generalManager) {
		this.generalManager = generalManager;
		worldLimit_.setXY(50, 100);
		
		glyphMap_ = new Vector<Vector<GlyphGridPosition>>();
		
		//build grid
		for(int i=0;i<worldLimit_.x();++i) {
			Vector<GlyphGridPosition> t = new Vector<GlyphGridPosition>();
			for(int j=0;j<worldLimit_.y();++j)
				t.add(j, new GlyphGridPosition(i,j));
			glyphMap_.add(i,  t);
		}
	}
	
	/*
	 * World boundary. Only for random positioning  (yet)
	 * DONT CHANGE THIS @ RUNNING TIME (only directly after "new ....")
	 * otherwise you need to reload everything
	 */
	public void setGridSize(int x, int y) {
		worldLimit_.setXY(x, y);
		
		for(int i=0;i<worldLimit_.x();++i) {
			Vector<GlyphGridPosition> t = new Vector<GlyphGridPosition>();
			for(int j=0;j<worldLimit_.y();++j)
				t.add(j, new GlyphGridPosition(i,j));
			glyphMap_.add(i,  t);
		}
	}
	
	
	public int getXMax() {
		//return  glyphMap_.size();
		return worldLimit_.x();
	}
	
	public int getYMax() {
//		if(getXMax() > 0)
//			return glyphMap_.get(0).size();
//		return 0;
		return worldLimit_.y();
	}
	/*
	public void setSelectReturnParameter(int num) {
		iSelectReturnParameter = num;		
	}
	*/
	
	public ArrayList<Integer> deSelectAll() {
		int ssi = glyphDataLoader.getSelectionSubmitIndex();
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(GlyphEntry g : glyphs_.values()) {
			if(g.isSelected()) {
				temp.add(g.getParameter(ssi));
				g.deSelect();
			}
		}
		return temp;
	}
	
	public ArrayList<Integer> selectAll() {
		int ssi = glyphDataLoader.getSelectionSubmitIndex();
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(GlyphEntry g : glyphs_.values()) {
			if(!g.isSelected()) {
				temp.add(g.getParameter(ssi));
				g.select();
			}
		}
		return temp;
	}
	
	public int getNumOfSelected() {
		int c = 0;
		for(GlyphEntry g : glyphs_.values())
			if(g.isSelected())
				++c;
		return c;
	}
	
	public int getNumOfDeSelected() {
		int c = 0;
		for(GlyphEntry g : glyphs_.values())
			if(!g.isSelected())
				++c;
		return c;
	}
	
	
	
	
	public int getGlyphGLList(GL gl, int x, int y) {
		if(glyphMap_.contains(x))
			if(glyphMap_.get(x).contains(y))
				if(glyphMap_.get(x).get(y).getGlyph() != null)
					return glyphMap_.get(x).get(y).getGlyph().getGlList(gl);
		
		generalManager.getLogger().log(Level.WARNING, "Someone wanted a Glyph GL List on the grid position " + x + ", " + y + ", but there is nothing");
		return -1;
	}

	public int getGlyphID(int x, int y) {
		if(glyphMap_.contains(x))
			if(glyphMap_.get(x).contains(y))
				if(glyphMap_.get(x).get(y).getGlyph() != null)
				return glyphMap_.get(x).get(y).getGlyph().getID();
		
		generalManager.getLogger().log(Level.WARNING, "Someone wanted a Glyph on the grid position " + x + ", " + y + ", but there is nothing");
    	return -1;
	}
	
	public Vec2i getGridPosition(int x, int y) {
		if(worldLimit_.x() < x || worldLimit_.y() < y) {
			generalManager.getLogger().log(Level.WARNING, "Someone wanted a Glyph outside the grid! (" + x + ", " + y + ")");
			return null;
		}
		
		return glyphMap_.get(x).get(y).getGridPosition();
	}
	
	
    public GlyphEntry getGlyph(int id) {
    	if(!glyphs_.containsKey(id)) {
    		generalManager.getLogger().log(Level.WARNING, "Someone wanted a Glyph with the id " + id + " but it doesn't exist.");
    		return null;
    	}
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

	
	
	public void loadGlyphs(GL gl,ISet glyphMapping, ISet glyphData, ISet glyphDictionary) {
		glyphDataLoader = new GlyphDataLoader(generalManager);
		
		glyphDataLoader.setupGlyphGenerator(glyphMapping);
		glyphDataLoader.setupGlyphDictionary(glyphDictionary);

		glyphs_ = glyphDataLoader.loadGlyphs(gl, glyphData);
		
		
		setGlyphPositionsRectangle();
		//setGlyphPositionsCenter();
		//setGlyphPositionsRandom();
		
	}

	
	
	public void buildGrid(GL gl) {
		
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
	    	gl.glVertex3f( 100,  0,  0);
	    	gl.glEnd();
		}
		
		gl.glTranslatef(-100f,-100f, 0f);
		
		for(int i=0;i<200;++i) {
			gl.glTranslatef(1f, 0f, 0f);
			gl.glBegin(GL.GL_LINES);
	    	gl.glColor4f(gridColor_.get(0),gridColor_.get(1),gridColor_.get(2),gridColor_.get(3));
	    	gl.glVertex3f(0,  -100,  0);
	    	gl.glVertex3f(0,   100,  0);
	    	gl.glEnd();
		}

		gl.glTranslatef(-100f,0f, 0f);
		
		gl.glEndList();
	}
	
	
	public void setGlyphPositions(int iTyp) {
		if(iTyp == EIconIDs.DISPLAY_RECTANGLE.ordinal() )
			setGlyphPositionsRectangle();

		if(iTyp == EIconIDs.DISPLAY_CIRCLE.ordinal() )
			setGlyphPositionsCenter();

		if(iTyp == EIconIDs.DISPLAY_RANDOM.ordinal() )
			setGlyphPositionsRandom();
		
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
		
		int center = x_max/2+1;

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
	

	private void setGlyphPositionsRandom() {
		clearGlyphMap();
		ArrayList<GlyphEntry> gg = sortGlyphs(glyphs_.values());
		Random rand = new Random();
		
		for(GlyphEntry g : gg) {
			boolean bFoundplace = false;
			int x;
			int y;
			int counter = 0;
			do {
				x = rand.nextInt(worldLimit_.x());
				y = rand.nextInt(worldLimit_.y());
			
				bFoundplace = isFree(x,y);
				++counter;
				
			} while(!bFoundplace && counter < 10000000);

			if(counter >= 10000000)
				System.err.println("no place for glyph " + g.getID() + " found");
			
			if(bFoundplace) {
				g.setPosition(x, y);
				glyphMap_.get(x).get(y).setGlyph(g);
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
		
		Integer[] sortOrder = glyphDataLoader.getSortOrder();
		
		if(sortOrder == null) {
			generalManager.getLogger().log(Level.WARNING, "No Glyph Sort index set!");

			ArrayList<GlyphEntry> t = new ArrayList<GlyphEntry>();
			t.addAll(unsorted);
			return t;
		}
		
		if(sortOrder.length <= parameterindex) {
			ArrayList<GlyphEntry> t = new ArrayList<GlyphEntry>();
			t.addAll(unsorted);
			return t;
		}
		
		int sortIndex = sortOrder[parameterindex];
		

		for(GlyphEntry g : unsorted) {
			int p = g.getParameter(sortIndex);

			if(!temp.containsKey(p))
				temp.put(p, new ArrayList<GlyphEntry>());
			
			temp.get(p).add(g);
			if(p > maxp)
				maxp = p;
		}
		
		ArrayList<GlyphEntry> temp2 = new ArrayList<GlyphEntry>();
		
		for( int i : temp.keySet() ) {
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
		if(glyphMap_.contains(x))
			if(glyphMap_.get(x).contains(y))
				if(glyphMap_.get(x).get(y).getGlyph() != null)
					return false;

		return true;
	}
	
	

}
