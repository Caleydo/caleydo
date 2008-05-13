package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.open.Vec2i;


public class GlyphGridPosition {
	private GlyphEntry glyph_;
	
	// position in relation to the grid - not the array!
	private Vec2i gridPosition_;
	
	public GlyphGridPosition(int x, int y) {
		gridPosition_ = new Vec2i();
		gridPosition_.setX(x+y - y/2);
		gridPosition_.setY(x-y+(y%2) + y/2 );
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
	
	
	

}
