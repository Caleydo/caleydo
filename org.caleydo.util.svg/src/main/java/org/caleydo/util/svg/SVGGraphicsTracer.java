/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.util.svg;

import gleem.linalg.Vec2f;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.Units;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLGraphicsTracer;
import org.caleydo.core.view.opengl.util.spline.ITesselatedPolygon;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.jogamp.opengl.util.texture.Texture;

/**
 * wrapper around opengl for simpler usage of common tasks
 *
 * @author Samuel Gratzl
 *
 */
public class SVGGraphicsTracer implements IGLGraphicsTracer {
	private static final Logger log = Logger.create(SVGGraphicsTracer.class);

	private boolean enabled = false;

	private final SVGGraphics2D g;
	private final File file;
	private final Deque<AffineTransform> stack = new ArrayDeque<>();

	private float lineWidth = 1;
	private float pointSize = 1;

	private int stippleFactor = -1;
	private int stpplePattern;


	private Color textColor = Color.BLACK;

	public SVGGraphicsTracer(File file) {
		this.file = file;

		// Get a DOMImplementation.
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		Document document = domImpl.createDocument(svgNS, "svg", null);

		// Create an instance of the SVG Generator.
		this.g = new SVGGraphics2D(document);
	}

	@Override
	public boolean forceRepaint() {
		return true;
	}

	@Override
	public void switchTo(ERenderPass pass) {
		if (pass == ERenderPass.DONE)
			flush();
		enabled = pass == ERenderPass.RENDERING;
	}
	/**
	 *
	 */
	public void flush() {
		boolean useCSS = true; // we want to use CSS style attributes
		try (Writer out = new FileWriter(file)) {
			g.stream(out, useCSS);
		} catch (IOException e) {
			log.error("can't write to " + file, e);
		}
	}


	@Override
	public void color(float r, float g, float b, float a) {
		if (!enabled)
			return;
		this.g.setColor(new java.awt.Color(r, g, b, a));
	}

	@Override
	public void textColor(Color color) {
		if (!enabled)
			return;
		this.textColor = color;
	}

	@Override
	public void lineWidth(float lineWidth) {
		if (!enabled)
			return;
		this.lineWidth = lineWidth;
		updateStroke();
	}

	/**
	 *
	 */
	private void updateStroke() {
		float dash[] = null;
		float dash_phase = 0;
		// FIXME stippleFactor
		g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, dash, dash_phase));
	}

	@Override
	public void lineStippled(int factor, int pattern) {
		if (!enabled)
			return;
		this.stippleFactor = factor;
		this.stpplePattern = pattern;
		updateStroke();
	}

	@Override
	public void pointSize(float pointSize) {
		if (!enabled)
			return;
		this.pointSize = pointSize;
	}

	private static int i(float v) {
		return Math.round(v);
	}

	@Override
	public void renderRect(boolean fill, float x, float y, float w, float h) {
		if (!enabled)
			return;
		if (fill)
			g.fillRect(i(x), i(y), i(w), i(h));
		else
			g.drawRect(i(x), i(y), i(w), i(h));
	}

	@Override
	public void renderRoundedRect(boolean fill, float x, float y, float w, float h, float radius, int segments) {
		if (!enabled)
			return;
		if (fill)
			g.fillRoundRect(i(x), i(y), i(w), i(h), i(radius), i(radius));
		else
			g.drawRoundRect(i(x), i(y), i(w), i(h), i(radius), i(radius));
	}

	@Override
	public void fillImage(URL texture, float x, float y, float w, float h) {
		if (!enabled)
			return;
		try {
			BufferedImage image = ImageIO.read(texture);
			fillImage(image, x, y, w, h);
		} catch (IOException e) {
			log.error("can't render image: "+texture,e);
		}
	}

	@Override
	public void fillImage(String texture, IResourceLocator locator, float x, float y, float w, float h) {
		if (!enabled)
			return;
		try {
			InputStream in = locator.get(texture);
			assert in != null;
			BufferedImage image = ImageIO.read(in);
			fillImage(image, x, y, w, h);
			in.close();
		} catch (IOException e) {
			log.error("can't render image: " + texture, e);
		}
	}

	private void fillImage(BufferedImage image, float x, float y, float w, float h) {
		if (w < 0) {
			w *= -1;
			x -= w;
		}
		if (h < 0) {
			h *= -1;
			y -= h;
		}
		// AffineTransform xform = AffineTransform.getScaleInstance(w / image.getWidth(), h / image.getHeight());
		// if ()
		// int[] params = new int[1];
		// final GL gl = GLContext.getCurrentGL();
		// gl.glGetTexParameteriv(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, params, 0);
		//
		// g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, clamp);
		// g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, clamp);
		//
		// g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		// g.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		//
		// AffineTransformOp p = new AffineTransformOp(xform, );
		g.drawImage(image, i(x), i(y), i(w), i(h), null);
		// g.drawImage(image, p, i(x), i(y));
	}

	@Override
	public void fillImage(Texture texture, float x, float y, float w, float h, Color color) {
		int tw = texture.getWidth();
		int th = texture.getHeight();
		BufferedImage image = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
		IntBuffer b = IntBuffer.allocate(tw*th); // w*h*rgba
		final GL2 gl2 = GLContext.getCurrentGL().getGL2();
		gl2.glGetTexImage(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, b);
		GLGraphics.checkError(gl2);

		for (int i = 0; i < th; ++i)
			for (int j = 0; j < tw; ++j) {
				final int v = b.get(i * tw + j);
				int a = (v >> 24) & 0xFF;
				int rgb = v >> 8;
				image.setRGB(j, i, rgb);
			}
		fillImage(image, x, y, w, h);

	}

	/**
	 * @param i
	 * @return
	 */
	private static int rgba2argb(int v) {
		int a = (v >> 24) & 0xFF;
		return v << 8 | a;
	}

	@Override
	public void renderCircle(boolean fill, float x, float y, float radius, int numSlices) {
		if (!enabled)
			return;
		g.fillOval(i(x - radius), i(y - radius), i(radius * 2), i(radius * 2));
	}

	@Override
	public void drawText(List<String> lines, float x, float y, float w, float h, float lineSpace,
			VAlign valign, ETextStyle style) {
		if (!enabled)
			return;

		final int nlines = lines.size();
		final float lineHeight = h - lineSpace * (nlines - 1) / nlines;
		Font textFont = new Font("Arial", style.toAWTFontStyle(), i(Units.PT.unapply(lineHeight)));
		FontMetrics textMetrics = g.getFontMetrics(textFont);
		g.setFont(textFont);
		java.awt.Color bak = g.getColor();
		g.setColor(textColor.getAWTColor());

		for (String line : lines) {
			int xi;
			switch (valign) {
			case CENTER:
				xi = i((w / 2) - (textMetrics.stringWidth(line) / 2));
				break;
			case RIGHT:
				xi = i(w - textMetrics.stringWidth(line));
				break;
			default:
				xi = i(x);
				break;
			}
			g.drawString(line, xi, i(y) + textFont.getSize());
			y += lineSpace + lineHeight;
		}

		g.setColor(bak);
	}

	@Override
	public void drawRotatedText(List<String> lines, float x, float y, float w, float h, float lineSpace,
			VAlign valign, ETextStyle style, float angle) {

	}

	@Override
	public void drawLine(float x, float y, float x2, float y2) {
		if (!enabled)
			return;
		g.drawLine(i(x), i(y), i(x2), i(y2));
	}

	@Override
	public void render(int mode, Iterable<Vec2f> points) {
		if (!enabled)
			return;
		switch (mode) {
		case GL.GL_LINE_LOOP:
			g.draw(asShape(points, true));
			break;
		case GL.GL_LINE_STRIP:
			g.draw(asShape(points, false));
			break;
		case GL.GL_POINTS:
			// FIXME
			break;
		case GL2.GL_POLYGON:
			g.fill(asShape(points, true));
			break;
		}
	}

	@Override
	public void renderPolygon(boolean fill, ITesselatedPolygon polygon) {
		// TODO Auto-generated method stub

	}
	/**
	 * @param points
	 * @param b
	 * @return
	 */
	private static Shape asShape(Iterable<Vec2f> points, boolean closed) {
		Path2D.Float p = new Path2D.Float();
		boolean first = true;
		for (Vec2f point : points) {
			if (first)
				p.moveTo(point.x(), point.y());
			else
				p.lineTo(point.x(), point.y());
			first = false;
		}
		if (closed)
			p.closePath();
		return p;
	}

	@Override
	public void move(float x, float y) {
		if (!enabled)
			return;
		AffineTransform t = g.getTransform();
		t.concatenate(AffineTransform.getTranslateInstance(x, y));
		g.setTransform(t);
	}

	@Override
	public void rotate(float angle) {
		if (!enabled)
			return;
		AffineTransform t = g.getTransform();
		t.concatenate(AffineTransform.getRotateInstance(Math.toRadians(angle)));
		g.setTransform(t);
	}

	@Override
	public void scale(float x, float y) {
		if (!enabled)
			return;
		AffineTransform t = g.getTransform();
		t.concatenate(AffineTransform.getScaleInstance(x, y));
		g.setTransform(t);
	}

	@Override
	public void incZ(float zDelta) {

	}

	@Override
	public void save() {
		if (!enabled)
			return;
		this.stack.push(g.getTransform());
	}

	@Override
	public void restore() {
		if (!enabled)
			return;
		g.setTransform(this.stack.pop());
	}
}