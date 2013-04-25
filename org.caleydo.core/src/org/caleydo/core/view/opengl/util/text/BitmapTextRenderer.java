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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.eclipse.swt.widgets.Shell;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

/**
 * @author Samuel Gratzl
 *
 */
public final class BitmapTextRenderer extends ABitmapTextRenderer implements ITextRenderer {
	private TextureRenderer texture;
	private float[] color = Colors.BLACK.getRGBA();

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

		// TODO
		System.out.printf("w%d h%d baseline %d nonPpowerof %s max tex size: %d\n", w, h, baseline, nonPowerOf2,
				maxTexSize);

		Dimension size = new Dimension(1024, 1024);
		texture = TextureRenderer.createAlphaOnlyRenderer(size.width, size.height);
		Graphics2D g = texture.createGraphics();
		g.setBackground(Color.BLACK);
		g.setColor(Color.WHITE);
		return Pair.make(g, size);
	}

	@Override
	protected void markDirty(Rectangle bounds) {
		texture.markDirty(bounds.x, bounds.y, bounds.width, bounds.height); // mark that region dirty
	}

	@Override
	public void setColor(Color color) {
		this.color = color.getRGBComponents(null);
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		this.color = new float[] { r, g, b, a };
	}

	@Override
	public void renderTextInBounds(GL2 gl, String text, float x, float y, float z, float w, float h) {
		GlyphVector glyphVector = get(text);
		if (glyphVector == null)
			return;

		upload(text);

		Texture tex = texture.getTexture();
		tex.enable(gl);
		tex.bind(gl);
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		float s = scale(h);
		w /= s; // to the other space
		gl.glScalef(s, s, 1);

		GLGraphics.checkError(gl);

		gl.glBegin(GL2.GL_QUADS);

		gl.glColor4fv(color, 0); // set our color

		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			if (filterChar(c))
				continue;
			final CharacterInfo info = chars.get(c);

			Rect bounds = info.getBounds();
			Rect texCoords = toTextureCoordinates(bounds);

			Point2D pos = glyphVector.getGlyphPosition(i);

			if ((pos.getX() + bounds.width() >= w)) { // within bounds
				break;
			}

			float xo = (float) pos.getX();
			float yo = (float) pos.getY();

			gl.glTexCoord2f(texCoords.x(), texCoords.y());
			gl.glVertex2f(xo, yo);

			gl.glTexCoord2f(texCoords.x2(), texCoords.y());
			gl.glVertex2f(xo + bounds.width(), yo);

			gl.glTexCoord2f(texCoords.x2(), texCoords.y2());
			gl.glVertex2f(xo + bounds.width(), yo + bounds.height());

			gl.glTexCoord2f(texCoords.x(), texCoords.y2());
			gl.glVertex2f(xo, yo + bounds.height());
		}
		gl.glEnd();

		gl.glPopMatrix();

		tex.disable(gl);
	}

	@Override
	public boolean isOriginTopLeft() {
		return true;
	}

	/**
	 * @param bounds
	 * @return
	 */
	private Rect toTextureCoordinates(Rect bounds) {
		float iw = 1.f / texture.getWidth();
		float ih = 1.f / texture.getHeight();
		return new Rect(bounds.x() * iw, bounds.y() * ih, bounds.width() * iw, bounds.height() * ih);
	}

	public static void main(String[] args) {
		GLSandBox.main(args, Root.class);
	}

	public static class Root extends GLSandBox {

		public Root(Shell parentShell) {
			super(parentShell, "text test", new GLElement() {
				@Override
				protected void renderImpl(GLGraphics g, float w, float h) {
					g.color(Color.RED).fillRect(0, 0, 200, 200);
					g.drawText("This is a test", 10, 10, 100, 40);
				}
			}, GLPadding.ZERO, new Dimension(500, 500));
		}

		@Override
		protected ITextRenderer createTextRenderer() {
			return new BitmapTextRenderer(new Font("Arial", Font.PLAIN, 40));
		}
	}

}
