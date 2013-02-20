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
package org.caleydo.view.tourguide.v2.r.ui;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomGenerator;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLPadding;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.v2.r.model.DataUtils;
import org.caleydo.view.tourguide.v2.r.model.IValue;
import org.caleydo.view.tourguide.v2.r.model.InteractiveNormalization;
import org.caleydo.view.tourguide.v2.r.model.Values;
import org.caleydo.view.tourguide.v3.ui.RenderUtils;

/**
 * @author Samuel Gratzl
 *
 */
public class InteractiveTransformationUI extends GLElementContainer implements IGLLayout {

	private final InteractiveNormalization model;
	private final FloatContainer raw;

	private final Color color;
	private final Color backgroundColor;
	private float min;
	private float max;

	public InteractiveTransformationUI(InteractiveNormalization model, FloatContainer raw, float min, float max) {
		setLayout(this);
		this.model = model;
		this.raw = raw;
		this.min = min;
		this.max = max;
		this.color = Color.decode("#ffb380");
		this.backgroundColor = Color.decode("#ffe6d5");
		this.add(new RawHistogramElement(raw.normalizeWithAtrificalExtrema(min, max)));
		this.add(new NormalizedHistogramElement());
		for (Map.Entry<Float, Float> m : model) {
			this.add(new Line(m.getKey(), normalizeRaw(m.getKey()), m.getValue()));
		}
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement raw = children.get(0);
		raw.setBounds(0, 0, w, 40);
		IGLLayoutElement norm = children.get(1);
		norm.setBounds(0, h - 40, w, 40);
		for (IGLLayoutElement line : children.subList(2, children.size())) {
			line.setBounds(0, 40, w, h - 80);
		}
	}

	private float normalizeRaw(float v) {
		return (v - min) / (max - min);
	}

	private float inverseNormalize(float n) {
		return n * (max - min) + min;
	}

	float[] updateMapping(float oldFrom, float oldTo, float from, float to) {
		oldFrom = inverseNormalize(oldFrom);
		from = inverseNormalize(from);
		model.update(oldFrom, oldTo, from, to);
		repaintAll();
		get(1).repaint();
		return new float[] { from, normalizeRaw(from), to, to };
	}

	public Histogram computeHist() {
		return DataUtils.getHist(200, new Iterator<IValue>() {
			int cursor = 0;
			@Override
			public void remove() {

			}

			@Override
			public IValue next() {
				return model.apply(Values.of(raw.get(cursor++)));
			}

			@Override
			public boolean hasNext() {
				return cursor != raw.size();
			}
		});
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(.3f, .3f, .3f, .3f);
		GL2 gl = g.gl;
		final float z = g.z();
		gl.glBegin(GL.GL_LINES);
		for (int i = 0; i < raw.size(); ++i) {
			float v = raw.getPrimitive(i);
			float x = normalizeRaw(v) * w;
			float x2 = model.apply(v) * w;
			if (Float.isNaN(x2))
				continue;
			gl.glVertex3f(x, 40, z);
			gl.glVertex3f(x2, h - 40, z);
		}
		gl.glEnd();
		super.renderImpl(g, w, h);
	}

	class RawHistogramElement extends GLElement {
		private final Histogram rawHist;

		private RawHistogramElement(FloatContainer raw) {
			rawHist = DataUtils.getHist(200, raw);
		}
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			GL2 gl = g.gl;
			final float z = g.z();
			gl.glBegin(GL2.GL_QUADS);
			g.color(backgroundColor);
			gl.glVertex3f(0, 0, z);
			g.color(Color.WHITE);
			gl.glVertex3f(w, 0, z);
			gl.glVertex3f(w, h, z);
			g.color(backgroundColor);
			gl.glVertex3f(0, h, z);
			gl.glEnd();
			RenderUtils.renderHist(g, rawHist, w, h, -1, color, Color.BLACK);
			g.color(color).drawRect(0, 0, w, h);
		}
	}

	class NormalizedHistogramElement extends GLElement {
		private NormalizedHistogramElement() {

		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			g.color(backgroundColor).fillRect(0, 0, w, h);
			g.color(color).drawRect(0, 0, w, h);
			RenderUtils.renderHist(g, computeHist(), w, h, -1, color, Color.BLACK);
		}
	}

	public static void main(String[] args) {
		RandomGenerator gen = new JDKRandomGenerator();
		gen.setSeed(100);

		final int count = 1000;
		FloatContainer tmp = new FloatContainer(count);
		for (int i = 0; i < count; ++i)
			tmp.add(Math.abs((float) gen.nextGaussian()));
		InteractiveNormalization n = new InteractiveNormalization();
		n.put(0, 0);
		n.put(3, 1);
		GLSandBox.main(args, new InteractiveTransformationUI(n, tmp, 0, 3), new GLPadding(20));
	}

	class Line extends GLElementContainer implements IGLLayout {
		private float from;
		private float to;

		public Line(float from, float fromN, float to) {
			this.add(new LineGlyph(true, from, this));
			this.add(new LineGlyph(false, to, this));
			this.from = fromN;
			this.to = to;
			setLayout(this);
		}
		@Override
		public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
			children.get(0).setBounds(from * w, 0, w, h);
			children.get(1).setBounds(to * w, h, w, h);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(Color.BLACK);
			float x1 = get(0).getLocation().x();
			float x2 = get(1).getLocation().x();
			// fancy line
			g.drawLine(x1, 3, x2, h - 6);
			super.renderImpl(g, w, h);
		}

		float drag(boolean up, int dx) {
			float deltaFrom = !up ? 0 : dx / getSize().x();
			float deltaTo = up ? 0 : dx / getSize().x();
			float[] newvalues = updateMapping(from, to, from + deltaFrom, to + deltaTo);
			this.from = newvalues[0];
			this.to = newvalues[2];
			layout();
			return up ? newvalues[1] : newvalues[3];
		}
	}

	class LineGlyph extends PickableGLElement {
		private float value;
		private final Line line;
		private final boolean up;
		private boolean hovered = false;

		LineGlyph(boolean up, float value, Line line) {
			this.up = up;
			this.value = value;
			this.setPicker(GLRenderers.DUMMY);
			this.line = line;
		}

		@Override
		protected void onMouseOver(Pick pick) {
			hovered = true;
			repaintAll();
		}

		@Override
		protected void onMouseOut(Pick pick) {
			if (pick.isDoDragging())
				return;
			hovered = false;
			repaintAll();
		}

		@Override
		protected void onClicked(Pick pick) {
			pick.setDoDragging(true);
		}

		@Override
		protected void onDragged(Pick pick) {
			value = line.drag(up, pick.getDx());
			repaintAll();
		}

		@Override
		protected void onMouseReleased(Pick pick) {
			if (pick.isDoDragging()) {
				hovered = false;
				repaintAll();
			}
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			float f = hovered ? 2 : 1;
			g.fillRect(-5 * f, 0, 10 * f, up ? 8 : -8);
			super.renderPickImpl(g, w, h);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(Color.BLACK);
			final float z = g.z();
			GL2 gl = g.gl;
			float s = hovered ? 1.5f : 1.f;

			if (up) {
				gl.glBegin(GL.GL_TRIANGLES);
				gl.glVertex3f(0, 0, z);
				gl.glVertex3f(5 * s, 8 * s, z);
				gl.glVertex3f(-5 * s, 8 * s, z);
				gl.glEnd();
				if (hovered) {
					g.color(new Color(240, 240, 240)).fillRect(-15, 8 * s, 30, 15);
					g.drawText(Formatter.formatNumber(value), -13, 8 * s, 26, 13, VAlign.CENTER);
				}
			} else {
				gl.glBegin(GL.GL_TRIANGLES);
				gl.glVertex3f(0, 0, z);
				gl.glVertex3f(5 * s, -8 * s, z);
				gl.glVertex3f(-5 * s, -8 * s, z);
				gl.glEnd();
				if (hovered) {
					g.color(new Color(240, 240, 240)).fillRect(-15, -8 * s - 15, 30, 15);
					g.drawText(Formatter.formatNumber(value), -13, -8 * s - 15, 26, 13, VAlign.CENTER);
				}
			}
			super.renderImpl(g, w, h);
		}
	}
}
