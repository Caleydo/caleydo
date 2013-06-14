package org.caleydo.core.view.opengl.util.text;
/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/



import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.eclipse.swt.widgets.Shell;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

/**
 * a {@link ITextRenderer} based on a glyph texture cache
 *
 * @author Samuel Gratzl
 *
 */
public final class BitmapTextRenderer extends ABitmapTextRenderer implements ITextRenderer {
	/**
	 * padding around every glyph for rendering to avoid precision problems
	 */
	private static final int PADDING = 2;

	private TextureRenderer texture;
	/**
	 * the text color to use
	 */
	private Color color = Color.BLACK;


	public BitmapTextRenderer(Font base) {
		super(base);
	}

	@Override
	protected Pair<Graphics2D, Dimension> createGraphics(Rectangle maxBounds) {
		int w = maxBounds.width;
		int h = maxBounds.height;
		int baseline = -maxBounds.y;

		GL gl = GLContext.getCurrentGL();
		final boolean nonPowerOf2 = gl.isExtensionAvailable("GL_ARB_texture_non_power_of_two");

		int[] result = new int[1];
		gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, result, 0);
		int maxTexSize = result[0];

		// TODO define a good size
		System.out.printf("w%d h%d baseline %d nonPpowerof %s max tex size: %d\n", w, h, baseline, nonPowerOf2,
				maxTexSize);

		Dimension size = new Dimension(1024, 1024);
		texture = TextureRenderer.createAlphaOnlyRenderer(size.width, size.height, true);
		Graphics2D g = texture.createGraphics();
		g.setBackground(Color.BLACK.getAWTColor());
		g.setColor(Color.WHITE.getAWTColor());
		return Pair.make(g, size);
	}

	@Override
	protected void markDirty(Rectangle bounds) {
		texture.markDirty(bounds.x, bounds.y, bounds.width, bounds.height); // mark that region dirty
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}



	@Override
	public void renderTextInBounds(GL2 gl, String text, float x, float y, float z, float w, float h) {
		GlyphVector glyphVector = get(text);
		if (glyphVector == null)
			return;

		upload(text);

		gl.glPushAttrib(GL2.GL_TEXTURE_BIT | GL.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL.GL_BLEND);
		Texture tex = texture.getTexture();
		tex.enable(gl);
		tex.bind(gl);
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);

		gl.glColor4fv(color.getRGBA(), 0); // set our color

		// TODO idea is now to avoid rasterization effects, that as we are in a pixel space
		// to align all quads to pixel borders

		double s = scale(h);
		w /= s; // to the other space
		gl.glScaled(s, s, s);

		gl.glTranslatef(0, baseLine, 0);

		gl.glBegin(GL2.GL_QUADS);


		final int p = PADDING;

		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			if (filterChar(c))
				continue;
			final CharacterInfo info = chars.get(c);

			Rectangle2D bounds = info.getBounds();
			Rectangle2D texCoords = toTextureCoordinates(bounds);

			Rectangle2D target = glyphVector.getGlyphVisualBounds(i).getBounds2D();

			if ((target.getMaxX() >= w)) { // within bounds
				break;
			}

			gl.glTexCoord2d(texCoords.getX(), texCoords.getY());
			gl.glVertex2d(target.getX() - p, target.getY() - p);

			gl.glTexCoord2d(texCoords.getMaxX(), texCoords.getY());
			gl.glVertex2d(target.getMaxX() + p, target.getY() - p);

			gl.glTexCoord2d(texCoords.getMaxX(), texCoords.getMaxY());
			gl.glVertex2d(target.getMaxX() + p, target.getMaxY() + p);

			gl.glTexCoord2d(texCoords.getX(), texCoords.getMaxY());
			gl.glVertex2d(target.getX() - p, target.getMaxY() + p);
		}
		gl.glEnd();


		gl.glPopMatrix();
		gl.glPopAttrib();
	}

	@Override
	public boolean isOriginTopLeft() {
		return true;
	}

	/**
	 * @param bounds
	 * @return
	 */
	private Rectangle2D toTextureCoordinates(Rectangle2D bounds) {
		double iw = 1. / texture.getWidth();
		double ih = 1. / texture.getHeight();
		int p = PADDING;
		return new Rectangle2D.Double((bounds.getX() - p) * iw, (bounds.getY() - p) * ih, (bounds.getWidth() + p * 2)
				* iw, (bounds.getHeight() + p * 2) * ih);
	}

	public static void main(String[] args) {
		GLSandBox.main(args, Root.class);
	}

	public static class Root extends GLSandBox {

		public Root(Shell parentShell) {
			super(parentShell, "text test", new GLElement() {
				@Override
				protected void renderImpl(GLGraphics g, float w, float h) {
					g.color(Color.RED).fillRect(10, 10, 500, 18);
					g.drawText("This is a test VAV", 10, 10, 500, 12);
				}
			}, GLPadding.ZERO, new Dimension(500, 500));
		}

		@Override
		protected ITextRenderer createTextRenderer(ETextStyle style) {
			return new BitmapTextRenderer(new Font(Font.SANS_SERIF, style.toAWTFontStyle(), 12));
		}
	}

}
