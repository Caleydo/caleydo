package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.Vec4f;

import java.awt.Font;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Render styles for the Glyph View
 * 
 * @author Sauer Stefan
 */

public class GlyphRenderStyle
	extends GeneralRenderStyle {

	/**
	 * Constructor
	 * 
	 * @param viewFrustum
	 */
	public GlyphRenderStyle(ViewFrustum viewFrustum) {

		super(viewFrustum);
	}

	/**
	 * Returns the used Text Renderer
	 * 
	 * @return
	 */
	public TextRenderer getScatterplotTextRenderer() {

		TextRenderer textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 16), false);
		textRenderer.setColor(0, 0, 0, 1);
		return textRenderer;
	}

	/**
	 * Returns the color of the grid
	 * 
	 * @return a color (red,green,blue,alpha)
	 */
	public Vec4f getGridColor() {
		return new Vec4f(0.2f, 0.2f, 0.2f, 1f);
	}

}
