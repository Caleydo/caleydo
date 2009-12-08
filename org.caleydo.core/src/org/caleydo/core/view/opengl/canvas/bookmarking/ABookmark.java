package org.caleydo.core.view.opengl.canvas.bookmarking;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Abstract base class for a single bookmark
 * 
 * @author Alexander Lex
 */
public abstract class ABookmark {

	protected Dimensions dimensions;

	protected int id;

	TextRenderer textRenderer;

	/**
	 * The constructor takes a TextRenderer which is used to render all text
	 * 
	 * @param textRenderer
	 */
	public ABookmark(TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		dimensions = new Dimensions();
	}

	public abstract void render(GL gl);

	public Dimensions getDimensions() {
		return dimensions;
	}

	public int getID() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.hashCode() == hashCode())
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
