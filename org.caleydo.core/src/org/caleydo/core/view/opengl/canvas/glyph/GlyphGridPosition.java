package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.open.Vec2i;


public class GlyphGridPosition {
	private GlyphEntry glyph_;
	
	// position in relation to the grid - not the array!
	private Vec2i gridPosition_;
	private Vec2i position_;
	
	public GlyphGridPosition(int x, int y) {
		gridPosition_ = new Vec2i();
		gridPosition_.setX(x+y - y/2);
		gridPosition_.setY(x-y+(y%2) + y/2 );
		
		position_ = new Vec2i();
		position_.setX(x);
		position_.setY(y);
	}
	
	public Vec2i getPosition() {
		return position_;
	}

	public Vec2i getGridPosition() {
		return gridPosition_;
	}

	public GlyphEntry getGlyph() {
	   return glyph_;
	}
	public void setGlyph(GlyphEntry glyph) {
		glyph_ = glyph;
	}
	public boolean isPositionFree() {
		if(glyph_ == null)
			return true;
		return false;
	}
	
	
	

}
