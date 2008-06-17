package org.caleydo.core.view.opengl.canvas.glyph;


import gleem.linalg.Vec4f;
import gleem.linalg.open.Vec2i;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;

import com.sun.opengl.util.j2d.TextRenderer;



public class GLCanvasGlyphGrid {
	private IGeneralManager generalManager;

	private Vector<Vector<GlyphGridPosition>> glyphMap_;
	private HashMap<Integer, HashMap<Integer, Vec2i>> scatterpointmap;
	
	private HashMap<Integer, GlyphEntry> glyphs_ = null;
	private GlyphDataLoader glyphDataLoader = null;

	private int GLGridList_ = -1;
	private int iScatterPlotGrid = -1;
	private int iPositionType = EIconIDs.DISPLAY_RECTANGLE.ordinal();
	private Vec4f gridColor_ = new Vec4f( 0.2f, 0.2f, 0.2f, 1f );
	private Vec2i worldLimit_ = new Vec2i(); 
	
	protected TextRenderer textRenderer;

	
	public GLCanvasGlyphGrid(final IGeneralManager generalManager) {
		this.generalManager = generalManager;
		textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 16), false);

		glyphMap_ = new Vector<Vector<GlyphGridPosition>>();
		
		setGridSize(50,100);
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
	
	public int getGridLayout(boolean isLocal)
	{
		if(iPositionType == EIconIDs.DISPLAY_SCATTERPLOT.ordinal())
			return iScatterPlotGrid;
		if(!isLocal)
			return -1;
		return GLGridList_;
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
	
	public GlyphDataLoader getDataLoader() {
		return glyphDataLoader;
	}
	
	
	public ArrayList<Integer> deSelectAll() {
		int ssi = glyphDataLoader.getSendParameter();
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
		int ssi = glyphDataLoader.getSendParameter();
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
		if(x >= worldLimit_.x() || y >= worldLimit_.y()) {
			generalManager.getLogger().log(Level.WARNING, "Someone wanted a Glyph outside the grid! (" + x + ", " + y + ") WorldLimit (" + worldLimit_.x() + "," + worldLimit_.y() + ")");
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

	
	
	public void loadData(GL gl,ISet glyphMapping, ISet glyphData, ISet glyphDictionary) {
		glyphDataLoader = new GlyphDataLoader(generalManager);

		glyphDataLoader.setupGlyphDictionary(glyphDictionary);

		glyphs_ = glyphDataLoader.loadGlyphs(glyphData);
		
		glyphDataLoader.setupGlyphGenerator(glyphMapping);
		
		setGlyphPositions(iPositionType);
		//setGlyphPositionsRectangle();
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
	
	
	public void buildScatterplotGrid(GL gl) {
		textRenderer.setColor(0, 0, 0, 1);
		
		//int maxx = 40;
		//int maxy = 40;
		int maxx = this.worldLimit_.x() - (this.worldLimit_.x()/5);
		int maxy = maxx;
		//get axix information
		int scatterParamX = this.glyphDataLoader.getScatterPlotAxis().x();
		int scatterParamY = this.glyphDataLoader.getScatterPlotAxis().y();
		GlyphAttributeType xdata = this.glyphDataLoader.getGlphAttributeType(scatterParamX);
		GlyphAttributeType ydata = this.glyphDataLoader.getGlphAttributeType(scatterParamY);

		
		if(xdata == null || ydata == null)
		{
			generalManager.getLogger().log(Level.WARNING, "Scatterplot axix definition corrupt!");
			return;
		}
		
		String[] xaxisdescription = xdata.getAttributeNames();
		String[] yaxisdescription = ydata.getAttributeNames();
		
		float incx = maxx / xaxisdescription.length;
		float incy = maxy / yaxisdescription.length;
		float linex = yaxisdescription.length * incy;
		float liney = xaxisdescription.length * incx;
		
		
		int drawLabelEveryLineX = 1;
		int drawLabelEveryLineY = 1;
		
		if(incy<2) drawLabelEveryLineY = 3;
		
		
		ArrayList<Float> pointsX = new ArrayList<Float>();
		ArrayList<Float> pointsY = new ArrayList<Float>();
		
		
		iScatterPlotGrid = gl.glGenLists(1);
		gl.glNewList(iScatterPlotGrid, GL.GL_COMPILE);
		gl.glLineWidth(1);
		
		gl.glRotatef( -45f, 0,0,1 );

		gl.glBegin(GL.GL_LINES);
		gl.glColor4f(gridColor_.get(0),gridColor_.get(1),gridColor_.get(2),gridColor_.get(3));
		gl.glVertex3f(0,  0,  0);
		gl.glVertex3f(0,   linex,  0);
		gl.glEnd();
		
		
		for(int i=0;i<xaxisdescription.length;++i)
		{
    		pointsX.add( incx * i + incx /2.0f );
			gl.glTranslatef(incx, 0f, 0f);
		
			gl.glBegin(GL.GL_LINES);
    		gl.glColor4f(gridColor_.get(0),gridColor_.get(1),gridColor_.get(2),gridColor_.get(3));
    		gl.glVertex3f(0,  0,  0);
    		gl.glVertex3f(0, linex,  0);
    		gl.glEnd();
    		
    		if(i%drawLabelEveryLineX==0) {
    			gl.glTranslatef(-incx/2.0f, -2.0f, 0f);
				textRenderer.begin3DRendering();	
				textRenderer.draw3D(xaxisdescription[i], 0, 0, 0, 0.1f);
				textRenderer.end3DRendering();
				gl.glTranslatef( incx/2.0f, +2.0f, 0f);
    		}
			
		}
		//spare point for non valid data
  		pointsX.add( incx * (xaxisdescription.length+2) );
    	
		gl.glTranslatef(+0.0f, -4.0f, 0f);
		textRenderer.begin3DRendering();	
		textRenderer.draw3D(xdata.getName() , 0, 0, 0, 0.1f);
		textRenderer.end3DRendering();
		gl.glTranslatef(-0.0f, +4.0f, 0f);

		gl.glTranslatef(-xaxisdescription.length * incx, 0f, 0f);
		
		
		gl.glRotatef( -90f, 0,0,1 );

		gl.glBegin(GL.GL_LINES);
		gl.glColor4f(gridColor_.get(0),gridColor_.get(1),gridColor_.get(2),gridColor_.get(3));
		gl.glVertex3f(0,  0,  0);
		gl.glVertex3f(0,   liney,  0);
		gl.glEnd();
		
		for(int i=0;i<yaxisdescription.length;++i)
		{
    		pointsY.add( incy * i + incy /2.0f );
			gl.glTranslatef(-incy, 0f, 0f);

			gl.glBegin(GL.GL_LINES);
    		gl.glColor4f(gridColor_.get(0),gridColor_.get(1),gridColor_.get(2),gridColor_.get(3));
    		gl.glVertex3f(0,  0,  0);
    		gl.glVertex3f(0,   liney,  0);
    		gl.glEnd();
    		
    		if(i%drawLabelEveryLineY==0) {
    			gl.glTranslatef(+incy/2.0f, -2.0f, 0f);
				textRenderer.begin3DRendering();	
				textRenderer.draw3D(yaxisdescription[i], 0, 0, 0, 0.1f);
				textRenderer.end3DRendering();
				gl.glTranslatef(-incy/2.0f, +2.0f, 0f);
    		}

		}
		gl.glTranslatef(-0.0f, -4.0f, 0f);
		textRenderer.begin3DRendering();	
		textRenderer.draw3D(ydata.getName() , 0, 0, 0, 0.1f);
		textRenderer.end3DRendering();
		gl.glTranslatef(+0.0f, +4.0f, 0f);

		
		gl.glTranslatef(yaxisdescription.length * incy, 0f, 0f);
		//spare point for non valid data
  		pointsY.add( incy * (yaxisdescription.length+5) );
		
		gl.glRotatef( 135f, 0,0,1 );
		
		scatterpointmap = new HashMap<Integer, HashMap<Integer, Vec2i>>();
		
		for(int i=0;i<pointsX.size();++i)
		{
			HashMap<Integer, Vec2i> temp = new HashMap<Integer, Vec2i>();
			scatterpointmap.put(i, temp);
			for(int j=0;j<pointsY.size();++j)
			{
				Vec2i temp2 = new Vec2i();
				
				//transform point
				float y1 = pointsX.get(i);
				float x1 = pointsY.get(j);

				double a1 = java.lang.Math.atan(y1/x1);
				double c = y1 / java.lang.Math.sin(a1);
				double a2 = -(java.lang.Math.PI/4.0 - a1);
				
				float x1t = (float)(java.lang.Math.cos(a2) * c);
				float y1t = (float)(java.lang.Math.sin(a2) * c);
				
				
				/*
				gl.glTranslatef(x1t, y1t, 0f);
				GLSharedObjects.drawAxis(gl);
				gl.glTranslatef(-x1t, -y1t, 0f);
				
				System.out.println(x1 + "          " + y1 + "          " + a1 + "          " + a2 + "          " + c + "          " + x1t + "          " + y1t);
				*/
				
				
				
				double dist = 10000000000000.0;
				// this needs a rework
				Iterator<Vector<GlyphGridPosition>> it1 = glyphMap_.iterator();
				Iterator<GlyphGridPosition> it2;
				while(it1.hasNext() )
				{
					Vector<GlyphGridPosition> vggp = it1.next();
					it2 = vggp.iterator();
					
					while(it2.hasNext())
					{
						GlyphGridPosition ggp = it2.next();
						Vec2i pos = ggp.getGridPosition();
						
						int x2 = pos.x();
						int y2 = pos.y();
						
						double dist2 = java.lang.Math.sqrt( (x1t-x2)*(x1t-x2) + (y1t-y2)*(y1t-y2) );
						if(dist2 < dist)
						{
							dist = dist2;
							temp2 = ggp.getPosition();
						}
					}
				}
				temp.put(j, temp2);
			}
		}
		/*
		Iterator<HashMap<Integer, Vec2i>> it1 = scatterpointmap.values().iterator();
		Iterator<Vec2i> it2;
		while(it1.hasNext() )
		{
			HashMap<Integer, Vec2i> iv = it1.next();
			it2 = iv.values().iterator();
			
			while(it2.hasNext())
			{
				Vec2i pos1 = it2.next();
				Vec2i pos = glyphMap_.get(pos1.x()).get(pos1.y()).getGridPosition();
				
				
				gl.glTranslatef(pos.x(), pos.y(), 0f);
				GLSharedObjects.drawAxis(gl);
				gl.glTranslatef(-pos.x(), -pos.y(), 0f);
				
			}
		}*/
		
		gl.glEndList();
		//iScatterPlotGrid = -1;
	}
	
	
	
	
	public void setGlyphPositions(int iTyp) {
		iPositionType = iTyp;
		
		if(iTyp == EIconIDs.DISPLAY_RECTANGLE.ordinal() )
			setGlyphPositionsRectangle();

		if(iTyp == EIconIDs.DISPLAY_CIRCLE.ordinal() )
			setGlyphPositionsCenter();

		if(iTyp == EIconIDs.DISPLAY_RANDOM.ordinal() )
			setGlyphPositionsRandom();
		
		if(iTyp == EIconIDs.DISPLAY_SCATTERPLOT.ordinal() )
			setGlyphPositionsScatterplot();
		
	}
	
	
	private void setGlyphPositionsRectangle() {
		clearGlyphMap();
		ArrayList<GlyphEntry> gg = sortGlyphs(glyphs_.values());
		
		int num = gg.size();
		int x_max = (int) java.lang.Math.sqrt(num);
		
		if(x_max > worldLimit_.x())
			x_max = worldLimit_.x();
		
		int i=0,j=0;
		for(GlyphEntry g : gg) {

			g.setPosition(i, j);
			glyphMap_.get(i).get(j).setGlyph(g);
			
			//System.out.println(i + " " + j + " (" + worldLimit_.x() + "," + worldLimit_.y() );
			
			++i;
			
			if(i>=x_max) {
				i=0;
				++j;
			}

		}
	}
	
	private void setGlyphPositionsCenter() {
		clearGlyphMap();
		ArrayList<GlyphEntry> gg = sortGlyphs(glyphs_.values());
		setGlyphPositionsCenter((worldLimit_.x()-2)/2,(worldLimit_.y()-2)/2, gg);
	}

	private void setGlyphPositionsCenter(int centerX, int centerY, ArrayList<GlyphEntry> gg) {
		
		if(centerX == 0 && centerY == 0) {
			int num = gg.size();
			int x_max = (int) java.lang.Math.sqrt(num);
			centerX = x_max/2+1;
			centerY = x_max/2+1;
		}

		int k =  0;
		int d  = 0;
		int cm = 1;
		int c  = 0;
		int x = centerX;
		int y = centerY;
		
		for(GlyphEntry g : gg) {
			boolean isfree = false;
			
			if(x >= 0 && y >= 0 && x < this.worldLimit_.x() && y < this.worldLimit_.y() )
				isfree = glyphMap_.get(x).get(y).isPositionFree();
			
			while(!isfree) {
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
				if(x<0) x = 0;
				if(y<0) y = 0;
				
				if(x<0) x = 0; if(x>=worldLimit_.x()) x = worldLimit_.x()-1;
				if(y<0) y = 0; if(y>=worldLimit_.y()) y = worldLimit_.y()-1;
				isfree = glyphMap_.get(x).get(y).isPositionFree();
			}
			g.setPosition(x, y);
			glyphMap_.get(x).get(y).setGlyph(g);
			
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

	

	private void setGlyphPositionsScatterplot()
	{
		clearGlyphMap();
		ArrayList<GlyphEntry> gg = sortGlyphs(glyphs_.values());
		
		int scatterParamX = this.glyphDataLoader.getScatterPlotAxis().x();
		int scatterParamY = this.glyphDataLoader.getScatterPlotAxis().y();
		GlyphAttributeType tx = this.glyphDataLoader.getGlphAttributeType(scatterParamX);
		GlyphAttributeType ty = this.glyphDataLoader.getGlphAttributeType(scatterParamY);
		
		if(tx == null || ty == null)
		{
			generalManager.getLogger().log(Level.WARNING, "Scatterplot axix definition corrupt!");
			return;
		}

		int maxX = tx.getMaxIndex();
		int maxY = ty.getMaxIndex();
		
		for(GlyphEntry g : gg)
		{
			ArrayList<GlyphEntry> alge = new ArrayList<GlyphEntry>();
			int xp = g.getParameter(scatterParamX);
			int yp = g.getParameter(scatterParamY);
			
			if(xp < 0) xp = maxX+1;
			if(yp < 0) yp = maxY+1;
			
			alge.add(g);
			Vec2i pos = scatterpointmap.get(xp).get(yp);
			
			setGlyphPositionsCenter(pos.x(),pos.y(), alge);
//			alge.remove(g);
			//System.out.println(g.getParameter(0) + "    " + xp + "     " + yp);
			
		}
	}

	
	
	private ArrayList<GlyphEntry> sortGlyphs( Collection<GlyphEntry> unsorted) {
		//ArrayList<GlyphEntry> temp2 = new ArrayList<GlyphEntry>();
		//temp2.addAll(unsorted);
		//return temp2;
		return sortGlyphsRecursive(unsorted,0);
	}
	
	
	
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

	
	
	public boolean isFree(int x, int y) {
		if(glyphMap_.contains(x))
			if(glyphMap_.get(x).contains(y))
				if(glyphMap_.get(x).get(y).getGlyph() != null)
					return false;

		return true;
	}
	
	

}
