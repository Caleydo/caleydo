package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.open.Vec2i;

import java.util.Vector;

import javax.media.opengl.GL;


public class GlyphEntry {
	private int id_;
	
	private Vec2i pos_; 
	private Vec2i posGoTo_; 
	
	private boolean selected_ = true;
	
	Vector<Integer> parameter_ = null;
	
	private GLCanvasGlyphGenerator generator_ = null;
	
	int glList_ = 0;
	int glListSelected_ = 0;
	
	
	public GlyphEntry(int id, GLCanvasGlyphGenerator generator) {
		id_ = id;
		generator_ = generator;
		parameter_ = new Vector<Integer>();

		posGoTo_ = new Vec2i();
		posGoTo_.setXY(0, 0);
		pos_ = new Vec2i();
		pos_.setXY(0, 0);
	}
	
	public int getID() { return id_; }
	public int getX() { return pos_.x(); }
	public int getY() { return pos_.y(); }
	public Vec2i getXY() { return pos_; }
	
	public void setPosition(int x, int y) {
		pos_.setXY(x, y);
	}
	
	public void select() {
		selected_ = true;
	}
	public void deSelect() {
		selected_ = false;
	}
	

	public int getGlList(GL gl) {
		if(selected_) {
			return glListSelected_;
		}
		
		return glList_;
	}
	
	public void addParameter(int value) {
		parameter_.add(value);
	}
	
	public int getParameter(int index) {
		if(parameter_.size() <= index)
			return -1;
		return parameter_.get(index);
	}

	public void generateGLLists(GL gl) {
		glListSelected_ = generator_.generateGlyph(gl, this, true);
		glList_ = generator_.generateGlyph(gl, this, false);
	}
	
	
}
