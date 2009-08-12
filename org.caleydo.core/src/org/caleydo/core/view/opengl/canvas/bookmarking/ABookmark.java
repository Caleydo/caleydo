package org.caleydo.core.view.opengl.canvas.bookmarking;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

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

	protected void renderCaption(GL gl, String sLabel, float fXOrigin, float fYOrigin, float fFontScaling) {

		if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
			sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
			sLabel = sLabel + "..";
		}

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sLabel, fXOrigin, fYOrigin, 0, fFontScaling);
		textRenderer.end3DRendering();
		gl.glPopAttrib();
	}

	
	@Override
	public boolean equals(Object obj) {
		if(obj.hashCode() == hashCode())
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
