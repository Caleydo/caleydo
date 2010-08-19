package org.caleydo.view.bookmark;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.IDType;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Abstract base class for a single bookmark
 * 
 * @author Alexander Lex
 */
public abstract class ABookmark {

	protected Dimensions dimensions;

	protected IDType idType;
	protected int id;

	TextRenderer textRenderer;

	GLBookmarkView manager;

	/**
	 * The constructor takes a TextRenderer which is used to render all text
	 * 
	 * @param textRenderer
	 */
	public ABookmark(GLBookmarkView manager, IDType idType, TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		this.manager = manager;
		this.idType = idType;
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
