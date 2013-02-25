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
package org.caleydo.view.tourguide.v3.ui;

import gleem.linalg.Vec2f;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.ArrayFloatList;
import org.caleydo.core.util.function.FloatFunctions;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.v3.model.DataUtils;
import org.caleydo.view.tourguide.v3.model.PiecewiseLinearMapping;
import org.caleydo.view.tourguide.v3.model.SimpleHistogram;

/**
 * @author Samuel Gratzl
 *
 */
public class PiecewiseLinearMappingUI extends GLElementContainer implements IGLLayout {
	private static final float GAP = 10;
	private static final int RAW_HIST = 0;
	private static final int NORMAL_HIST = 1;
	private static final int CANVAS = 2;
	private static final int FIRST_POINT = 3;

	private final PiecewiseLinearMapping model;
	private final IFloatList raw;

	private final Color color;
	private final Color backgroundColor;
	private final ICallback<PiecewiseLinearMapping> callback;


	public PiecewiseLinearMappingUI(PiecewiseLinearMapping model, IFloatList data, Color color, Color bgColor,
			ICallback<PiecewiseLinearMapping> callback) {
		this.callback = callback;
		setLayout(this);
		setSize(260, 260);
		this.model = model;
		this.raw = data;
		this.color = color;
		this.backgroundColor = bgColor;

		this.add(new RawHistogramElement(raw.map(FloatFunctions.normalize(model.getActMin(), model.getActMax()))));
		this.add(new NormalizedHistogramElement());
		this.add(new Canvas());

		for (Map.Entry<Float, Float> entry : model) {
			this.add(new Point(entry.getKey(), entry.getValue()));
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(0.95f, .95f, .95f, 0.9f).fillRoundedRect(0, 0, w, h, 5);
		super.renderImpl(g, w, h);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		final float histHeight = 40;
		IGLLayoutElement raw = children.get(RAW_HIST);
		raw.setBounds(histHeight + GAP, h - histHeight, w - histHeight - GAP * 2, histHeight);
		IGLLayoutElement norm = children.get(NORMAL_HIST);
		norm.setBounds(0, GAP, histHeight, h - histHeight - GAP * 2);

		float x_canvas = histHeight + GAP;
		float y_canvas = GAP;
		float w_canvas = w - histHeight - GAP * 2;
		float h_canvas = h - histHeight - GAP * 2;
		IGLLayoutElement canvas = children.get(CANVAS);
		canvas.setBounds(x_canvas, y_canvas, w_canvas, h_canvas);
		for (IGLLayoutElement point : children.subList(FIRST_POINT, children.size())) {
			Point p = (Point) point.asElement();
			float x_v = x_canvas + w_canvas * (normalizeRaw(p.from));
			float y_v = y_canvas + h_canvas * (1 - p.to);
			point.setBounds(x_v, y_v, 3, 3);
		}
	}

	void onRemovePoint(Point point) {
		if (model.size() <= 2) // at least two points
			return;
		model.remove(point.from);
		this.remove(point);
		getCanvas().repaintAll();
		get(NORMAL_HIST).repaint();
		fireCallback();
	}

	private void fireCallback() {
		if (callback != null)
			callback.on(model);
	}

	private GLElement getCanvas() {
		return this.get(CANVAS);
	}

	private List<GLElement> getPoints() {
		return asList().subList(FIRST_POINT, size());
	}

	private float normalizeRaw(float v) {
		return (v - model.getActMin()) / (model.getActMax() - model.getActMin());
	}

	private float inverseNormalize(float n) {
		return n * (model.getActMax() - model.getActMin()) + model.getActMin();
	}

	public void drag(Point point, int dx, int dy) {
		if (dx == 0 && dy == 0) // no change
			return;
		Vec2f size = getCanvas().getSize();
		float from = point.from + dx / (size.x());
		float to = point.to - dy / (size.y());
		updateMapping(point, from, to);
		getCanvas().repaintAll();
		get(NORMAL_HIST).repaint();
		this.relayout();
	}

	protected void onAddPoint(float from, float to) {
		Vec2f size = getCanvas().getSize();

		from = from / size.x();
		to = (size.y() - to) / size.y();

		from = inverseNormalize(from);
		model.put(from, to);
		this.add(new Point(from, to));
		get(NORMAL_HIST).repaint();
		fireCallback();
	}

	private void updateMapping(Point current, float from, float to) {
		float oldFrom = current.from;
		float oldTo = current.to;

		from = (from < 0 ? 0 : (from > 1 ? 1 : from));
		to = (to < 0 ? 0 : (to > 1 ? 1 : to));

		oldFrom = inverseNormalize(oldFrom);
		from = inverseNormalize(from);
		model.update(oldFrom, oldTo, from, to);

		current.set(from, to);
	}

	public SimpleHistogram computeHist() {
		return DataUtils.getHist(200, raw.map(model));
	}

	/**
	 * line and background drawing
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private class Canvas extends PickableGLElement {
		private boolean lineHovered;
		private Vec2f linePoint;

		@Override
		protected void onClicked(Pick pick) {
			this.lineHovered = false;
			onAddPoint(this.linePoint.x(), this.linePoint.y());
			this.repaint();
		}

		@Override
		protected void onMouseOver(Pick pick) {
			this.lineHovered = true;
			this.linePoint = this.toRelative(pick.getPickedPoint());
			this.repaint();
		}

		@Override
		protected void onMouseOut(Pick pick) {
			this.lineHovered = false;
			this.linePoint = null;
			this.repaint();
		}

		@Override
		protected void onMouseMoved(Pick pick) {
			if (this.lineHovered)
				this.repaint();
		}

		private List<GLElement> renderLine(GLGraphics g) {
			List<GLElement> points = getPoints();
			List<Vec2f> line = new ArrayList<>();
			Vec2f loc = this.getLocation();
			for (GLElement point : points) {
				line.add(point.getLocation().minus(loc));
			}
			Collections.sort(line, new Comparator<Vec2f>() {
				@Override
				public int compare(Vec2f o1, Vec2f o2) {
					return Float.compare(o1.x(), o2.x());
				}
			});
			g.color(Color.BLACK).drawPath(line, false);
			return points;
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			// render all point mappings
			renderMapping(g, w, h);

			List<GLElement> points = this.renderLine(g);
			g.color(Color.LIGHT_GRAY);
			Vec2f myloc = this.getLocation();
			for (GLElement point : points) {
				Vec2f loc = point.getLocation().minus(myloc);
				if (((Point) point).hovered) {
					drawHintLines(g, loc, h);
				}
			}
			if (this.lineHovered) {
				drawHintLines(g, this.linePoint, h);
				g.fillImage(g.getTexture("resources/icons/circle.png"), this.linePoint.x() - 5, this.linePoint.y() - 5,
						10, 10,
						Color.LIGHT_GRAY);
			}
		}

		private void renderMapping(GLGraphics g, float w, float h) {
			g.color(.3f, .3f, .3f, .3f);
			GL2 gl = g.gl;
			final float z = g.z();
			gl.glBegin(GL.GL_LINES);
			if (raw.size() < 1000) {
				for (int i = 0; i < raw.size(); ++i) {
					float v = raw.getPrimitive(i);
					float x = normalizeRaw(v) * w;
					float y = (1 - model.apply(v)) * h;
					gl.glVertex3f(x, h, z);
					gl.glVertex3f(0, y, z);
				}
			} else {
				// sample 1000 elements
				List<Integer> r = new ArrayList<>(1000);
				for (int i = 0; i < 1000; ++i)
					r.add(i);
				Collections.shuffle(r);
				for (int i = 0; i < 1000; ++i) {
					float v = raw.getPrimitive(r.get(i));
					float x = normalizeRaw(v) * w;
					float y = (1 - model.apply(v)) * h;
					gl.glVertex3f(x, h, z);
					gl.glVertex3f(0, y, z);
				}
			}

			gl.glEnd();
		}

		private void drawHintLines(GLGraphics g, Vec2f loc, float h) {
			g.drawPath(false, new Vec2f(-GAP, loc.y()), loc, new Vec2f(loc.x(), h + GAP));
			Vec2f size = getSize();
			float from = inverseNormalize(loc.x() / size.x());
			float to = (size.y() - loc.y()) / size.y();
			g.textColor(Color.LIGHT_GRAY);
			g.drawText(Formatter.formatNumber(from), loc.x() + 1, h - 12 + GAP, 40, 10);
			g.drawText(Formatter.formatNumber(to), 1 - GAP, loc.y() + 1, 40, 10);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			this.renderLine(g);
		}
	}

	/**
	 * a mapping point
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private class Point extends PickableGLElement {
		private boolean hovered;
		private float from;
		private float to;

		public Point(float from, float to) {
			this.from = from;
			this.to = to;
		}

		public void set(float from, float to) {
			this.from = from;
			this.to = to;
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			Color color = this.hovered ? Color.RED : Color.BLACK;
			g.fillImage(g.getTexture("resources/icons/circle.png"), -5, -5, 10, 10, color);
			// g.fillCircle(0, 0, 5);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			g.incZ().fillCircle(0, 0, 10, 8).decZ();
		}

		@Override
		protected void onClicked(Pick pick) {
			pick.setDoDragging(true);
		}

		@Override
		protected void onRightClicked(Pick pick) {
			onRemovePoint(this);
		}

		@Override
		protected void onDragged(Pick pick) {
			if (!pick.isDoDragging())
				return;
			drag(this, pick.getDx(), pick.getDy());
			this.repaintAll();
		}

		@Override
		protected void onMouseOver(Pick pick) {
			this.hovered = true;
			this.repaint();
			getCanvas().repaint();
		}

		@Override
		protected void onMouseReleased(Pick pick) {
			if (pick.isDoDragging()) {
				this.hovered = false;
				this.repaint();
				getCanvas().repaint();
				fireCallback();
			}
		}

		@Override
		protected void onMouseOut(Pick pick) {
			if (!pick.isDoDragging()) {
				this.hovered = false;
				this.repaint();
				getCanvas().repaint();
				fireCallback();
			}
		}
	}

	class RawHistogramElement extends GLElement {
		private final SimpleHistogram rawHist;

		private RawHistogramElement(IFloatList raw) {
			rawHist = DataUtils.getHist(200, raw);
		}
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			GL2 gl = g.gl;
			final float z = g.z();
			g.drawText(Formatter.formatNumber(model.getActMin()), -40, h - 14, 38, 12, VAlign.RIGHT);
			g.drawText(Formatter.formatNumber(model.getActMax()), w + 2, h - 14, 38, 12, VAlign.LEFT);
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
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			g.drawText(Formatter.formatNumber(1), 0, -14, 38, 12, VAlign.LEFT);
			g.drawText(Formatter.formatNumber(0), 0, h + 2, 38, 12, VAlign.LEFT);
			g.color(backgroundColor).fillRect(0, 0, w, h);
			g.color(color).drawRect(0, 0, w, h);
			g.save();
			g.gl.glRotatef(-90, 0, 0, 1);
			g.move(-h, 0);
			RenderUtils.renderHist(g, computeHist(), h, w, -1, color, Color.BLACK);
			g.restore();
		}
	}

	public static void main(String[] args) {
		PiecewiseLinearMapping model = new PiecewiseLinearMapping(0, 1);
		float[] arr = new float[100];
		Random r = new Random(100);
		for (int i = 0; i < arr.length; ++i)
			arr[i] = r.nextFloat();
		IFloatList data = new ArrayFloatList(arr);
		final PiecewiseLinearMappingUI root = new PiecewiseLinearMappingUI(model, data, Color.GRAY, Color.LIGHT_GRAY,
				null);
		GLSandBox.main(args, root, new GLPadding(10), new Dimension(260, 260));
	}
}
