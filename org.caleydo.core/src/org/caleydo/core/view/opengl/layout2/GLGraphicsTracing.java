package org.caleydo.core.view.opengl.layout2;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.media.opengl.GL2;

import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.util.text.ITextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

/**
 * debugging version of {@link GLGraphics}
 *
 * @author Samuel Gratzl
 *
 */
public class GLGraphicsTracing extends GLGraphics {
	private Deque<float[]> stack = new ArrayDeque<>();
	private float xacc = 0;
	private float yacc = 0;

	/**
	 * @param gl
	 * @param text
	 * @param textures
	 * @param loader
	 * @param originInTopLeft
	 */
	public GLGraphicsTracing(GL2 gl, ITextRenderer text, TextureManager textures, IResourceLocator loader,
			boolean originInTopLeft) {
		super(gl, text, textures, loader, originInTopLeft);
		debug("########created");
	}

	@Override
	public GLGraphics color(IColor color) {
		debug("color", color);
		return super.color(color);
	}

	@Override
	public GLGraphics color(Color color) {
		debug("color", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		return super.color(color);
	}

	@Override
	public GLGraphics color(float r, float g, float b) {
		return super.color(r, g, b);
	}

	@Override
	public GLGraphics color(float r, float g, float b, float a) {
		debug("color", r, g, b, a);
		return super.color(r, g, b, a);
	}

	@Override
	public GLGraphics color(float[] rgba) {
		return super.color(rgba);
	}

	@Override
	public GLGraphics lineWidth(float lineWidth) {
		debug("lineWidth", lineWidth);
		return super.lineWidth(lineWidth);
	}

	@Override
	public GLGraphics fillRect(float x, float y, float w, float h) {
		debug("fillRect", x, y, w, h);
		return super.fillRect(x, y, w, h);
	}

	@Override
	public GLGraphics fillImage(String texture, float x, float y, float w, float h) {
		debug("fillImage", texture, x, y, w, h);
		return super.fillImage(texture, x, y, w, h);
	}

	@Override
	public GLGraphics drawText(String text, float x, float y, float w, float h) {
		debug("drawText", text, x, y, w, h);
		return super.drawText(text, x, y, w, h);
	}

	@Override
	public GLGraphics drawRect(float x, float y, float w, float h) {
		debug("drawRect", x, y, w, h);
		return super.drawRect(x, y, w, h);
	}

	@Override
	public GLGraphics drawLine(float x, float y, float x2, float y2) {
		debug("drawLine", x, y, x2, y2);
		return super.drawLine(x, y, x2, y2);
	}

	@Override
	public GLGraphics move(float x, float y) {
		debug("move", x, y);
		xacc += x;
		yacc += y;
		return super.move(x, y);
	}

	@Override
	public GLGraphics save() {
		debug("save");
		super.save();
		stack.push(new float[] { xacc, yacc });
		return this;
	}

	@Override
	public GLGraphics restore() {
		debug("restore\n");
		super.restore();
		float[] a = stack.pop();
		xacc = a[0];
		yacc = a[1];
		return this;
	}

	@Override
	public String toString() {
		debug("current position", xacc, yacc);
		return super.toString();
	}

	private static void debug(Object... args) {
		for (int i = 0; i < args.length; ++i) {
			if (i > 0)
				System.out.print(' ');
			System.out.print(args[i]);
		}
		System.out.println();
	}
}