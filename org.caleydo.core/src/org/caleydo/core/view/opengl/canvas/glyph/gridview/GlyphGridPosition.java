package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.open.Vec2i;

public class GlyphGridPosition {

	private GlyphEntry glyph_;

	// position in relation to the grid - not the array!
	private Vec2i gridPosition_;

	private Vec2i position_;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            X Grid Position
	 * @param y
	 *            Y Grid Position
	 */
	public GlyphGridPosition(int x, int y) {

		gridPosition_ = new Vec2i();
		gridPosition_.setX(x + y - y / 2);
		gridPosition_.setY(x - y + y % 2 + y / 2);

		position_ = new Vec2i();
		position_.setX(x);
		position_.setY(y);
	}

	/**
	 * Returns the Grid Position
	 * 
	 * @return
	 */
	public Vec2i getPosition() {

		return new Vec2i(position_);
	}

	/**
	 * Returns the Position in the Grid in real World Coordinates
	 * 
	 * @return
	 */
	public Vec2i getGridPosition() {

		return new Vec2i(gridPosition_);
	}

	/**
	 * Returns the Glyph of this Position
	 * 
	 * @return
	 */
	public GlyphEntry getGlyph() {

		return glyph_;
	}

	/**
	 * Sets a Glyph to this Position
	 * 
	 * @param glyph
	 *            The Glyph
	 */
	public void setGlyph(GlyphEntry glyph) {

		glyph_ = glyph;
	}

	/**
	 * Returns if the position is free
	 * 
	 * @return true if free, false if filled
	 */
	public boolean isPositionFree() {

		if (glyph_ == null)
			return true;
		return false;
	}

}
