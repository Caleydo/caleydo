package org.caleydo.view.bookmark;

import javax.media.opengl.GL2;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Abstract base class for a single bookmark
 * 
 * @author Alexander Lex
 */
public abstract class ABookmark {

	/** The bookmarkDimensions of an individual bookmark */
	protected Dimensions bookmarkDimensions;

	protected IDType idType;
	protected int id;

	CaleydoTextRenderer textRenderer;

	GLBookmarkView manager;

	/**
	 * The constructor takes a TextRenderer which is used to render all text
	 * 
	 * @param textRenderer
	 */
	public ABookmark(GLBookmarkView manager, IDType idType,
			CaleydoTextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		this.manager = manager;
		this.idType = idType;
		bookmarkDimensions = new Dimensions();
		float height = (float) (textRenderer.getBounds("Text").getHeight())
				* GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;
		bookmarkDimensions.setHeight(height * 2);
	}

	public abstract void render(GL2 gl);

	public Dimensions getDimensions() {
		return bookmarkDimensions;
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
