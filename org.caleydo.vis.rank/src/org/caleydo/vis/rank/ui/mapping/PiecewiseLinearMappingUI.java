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
package org.caleydo.vis.rank.ui.mapping;

import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.binsForWidth;
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
import org.caleydo.vis.rank.model.DataUtils;
import org.caleydo.vis.rank.model.SimpleHistogram;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseLinearMapping;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.RenderUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * ui for a {@link PiecewiseLinearMapping}
 *
 * @author Samuel Gratzl
 *
 */
public class PiecewiseLinearMappingUI extends GLElementContainer implements IGLLayout {
	private static final float GAP = 10;
	private static final float PADDING = 5;

	private static final int RAW_HIST = 0;
	private static final int NORMAL_HIST = 1;
	private static final int CANVAS = 2;
	private static final int FIRST_POINT = 3;

	private final PiecewiseLinearMapping model;
	private final IFloatList raw;

	private final Color color;
	private final Color backgroundColor;
	/**
	 * callback to call when the mapping changes
	 */
	private final ICallback<IMappingFunction> callback;


	public PiecewiseLinearMappingUI(PiecewiseLinearMapping model, IFloatList data, Color color, Color bgColor,
			ICallback<IMappingFunction> callback) {
		this.callback = callback;
		setLayout(this);
		setSize(PADDING * 2 + 200 + GAP * 2, PADDING * 2 + 200 + GAP * 2);
		this.model = model;
		this.raw = data;
		this.color = color;
		this.backgroundColor = bgColor;

		this.add(new RawHistogramElement(raw.map(FloatFunctions.normalize(model.getActMin(), model.getActMax()))));
		this.add(new NormalizedHistogramElement());
		this.add(new Canvas());

		if (model.isMappingDefault()) {
			if (Float.isNaN(model.getFromMin()))
				this.add(new Point(model.getActMin(), 0, true));
			if (Float.isNaN(model.getFromMax()))
				this.add(new Point(model.getActMax(), 1, true));
		}
		for (Map.Entry<Float, Float> entry : model) {
			this.add(new Point(entry.getKey(), entry.getValue(), false));
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(0.95f, .95f, .95f, 0.95f).fillRect(0, 0, w, h);
		super.renderImpl(g, w, h);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement raw = children.get(RAW_HIST);
		raw.setBounds(HIST_HEIGHT + GAP + PADDING, h - HIST_HEIGHT - PADDING, w - HIST_HEIGHT - GAP * 2 - 2 * PADDING,
				HIST_HEIGHT);
		IGLLayoutElement norm = children.get(NORMAL_HIST);
		norm.setBounds(PADDING, GAP + PADDING, HIST_HEIGHT, h - HIST_HEIGHT - GAP * 2 - PADDING * 2);

		float x_canvas = HIST_HEIGHT + GAP + PADDING;
		float y_canvas = GAP + PADDING;
		float w_canvas = w - HIST_HEIGHT - GAP * 2 - PADDING * 2;
		float h_canvas = h - HIST_HEIGHT - GAP * 2 - PADDING * 2;
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
		if (point.pseudo) // can't remove pseudo points
			return;
		if (model.hasDefinedMappingBounds() && model.size() == 2) // can't remove defined bounds
			return;
		if ((!model.isMaxDefined() || !model.isMinDefined()) && model.size() == 1) // can't remove single defined
			return;
		model.remove(point.from);
		int s = model.size();
		switch (s) {
		case 0: // add the second
			point.pseudo = true;
			Point other = (Point) (get(FIRST_POINT) == point ? get(FIRST_POINT + 1) : get(FIRST_POINT));
			if (other.from == model.getActMin()) {
				point.from = model.getActMax();
				point.to = 1;
			} else {
				point.from = model.getActMin();
				point.to = 0;
			}
			point.repaint();
			relayout();
			break;
		case 1:
			point.pseudo = true;
			if (!model.isMinDefined()) {
				point.from = model.getActMin();
				point.to = 0;
			} else {
				point.from = model.getActMax();
				point.to = 1;
			}
			point.repaint();
			relayout();
			break;
		default:
			this.remove(point);
			break;
		}
		repaintMapping();
		fireCallback();
	}

	protected void onAddPoint(float from, float to) {
		Vec2f size = getCanvas().getSize();

		from = from / size.x();
		to = (size.y() - to) / size.y();

		from = inverseNormalize(from);
		model.put(from, to);
		switch (model.size()) {
		case 1:
			List<Point> points = Lists.newArrayList(Iterables.filter(this, Point.class));
			assert points.size() == 2;
			Point p1 = points.get(0);
			Point p2 = points.get(1);
			Point t;
			if (Math.abs(p1.from - from) < Math.abs(p2.from - from))
				t = p1;
			else
				t = p2;
			t.from = from;
			t.to = to;
			t.pseudo = false;
			t.repaint();
			relayout();
			break;
		case 2:
			for (Point p : Iterables.filter(this, Point.class)) {
				if (p.pseudo) {
					p.from = from;
					p.to = to;
					p.pseudo = false;
					p.repaint();
					break;
				}
			}
			relayout();
			break;
		default:
			this.add(new Point(from, to, false));
			break;
		}
		repaintMapping();
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
		float from = normalizeRaw(point.from) + dx / (size.x());
		float to = point.to - dy / (size.y());
		updateMapping(point, from, to);
		repaintMapping();
		this.relayout();
	}

	public void repaintMapping() {
		getCanvas().repaintAll();
		get(RAW_HIST).repaint();
		get(NORMAL_HIST).repaint();
	}

	private void updateMapping(Point current, float from, float to) {
		float oldFrom = current.from;
		float oldTo = current.to;

		from = (from < 0 ? 0 : (from > 1 ? 1 : from));
		to = (to < 0 ? 0 : (to > 1 ? 1 : to));

		from = inverseNormalize(from);
		model.update(oldFrom, oldTo, from, to);

		current.set(from, to);
	}

	public SimpleHistogram computeHist(float w) {
		return DataUtils.getHist(binsForWidth(w), raw.map(model));
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
				g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), this.linePoint.x() - 5, this.linePoint.y() - 5,
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
			g.textColor(Color.GRAY);
			g.drawText(Formatter.formatNumber(from), loc.x() + 1, h - 12 + GAP, 40, 11);
			g.drawText(Formatter.formatNumber(to), 1 - GAP, loc.y() + 1, 40, 11);

			g.textColor(Color.BLACK).drawText(
					"m(" + Formatter.formatNumber(from) + ") = " + Formatter.formatNumber(to), loc.x() + 5,
					loc.y() - 5, 100, 11);
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
		private boolean pseudo;
		private float from;
		private float to;

		public Point(float from, float to, boolean pseudo) {
			this.from = from;
			this.to = to;
			this.pseudo = pseudo;
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
			if (!pseudo)
				g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), -5, -5, 10, 10, color);
			else {
				g.gl.glEnable(GL2.GL_LINE_STIPPLE);
				g.gl.glLineStipple(2, (short) 0xAAAA);
				g.lineWidth(2);
				g.color(color).drawCircle(0, 0, 5);
				g.gl.glDisable(GL2.GL_LINE_STIPPLE);
				g.lineWidth(1);
			}
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
			if (pick.getDx() != 0 || pick.getDy() != 0)
				this.pseudo = false;
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
		private IFloatList data;
		private RawHistogramElement(IFloatList raw) {
			this.data = raw;
		}
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			g.drawText(Formatter.formatNumber(model.getActMin()), -40, h - 14, 38, 12, VAlign.RIGHT);
			g.drawText(Formatter.formatNumber(model.getActMax()), w + 2, h - 14, 38, 12, VAlign.LEFT);
			g.color(backgroundColor).fillRect(0, 0, w, h);
			RenderUtils.renderHist(g, DataUtils.getHist(binsForWidth(w), data), w, h, -1, color,
					Color.BLACK);
			g.color(color).drawRect(0, 0, w, h);

			float[] m = model.getMappedMin();
			if (m[0] > model.getActMin()) {
				float from = normalizeRaw(m[0]);
				g.color(0, 0, 0, 0.25f).fillRect(0, 0, from * w, h);
			}
			m = model.getMappedMax();
			if (m[0] < model.getActMax()) {
				float to = normalizeRaw(m[0]);
				g.color(0, 0, 0, 0.25f).fillRect(to * w, 0, (1 - to) * w, h);
			}
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
			RenderUtils.renderHist(g, computeHist(h), h, w, -1, color, Color.BLACK);

			float m = model.getMinTo();
			if (m > 0) {
				g.color(0, 0, 0, 0.25f).fillRect(0, 0, m * h, w);
			}
			m = model.getMaxTo();
			if (m < 1) {
				g.color(0, 0, 0, 0.25f).fillRect(m * h, 0, (1 - m) * h, w);
			}

			g.restore();
		}
	}

	public static void main(String[] args) {
		PiecewiseLinearMapping model = new PiecewiseLinearMapping(0, Float.NaN);
		float[] arr = new float[100];
		Random r = new Random(100);
		for (int i = 0; i < arr.length; ++i)
			arr[i] = r.nextFloat();
		IFloatList data = new ArrayFloatList(arr);
		float[] s = data.computeStats();
		model.setAct(s[0], s[1]);
		final PiecewiseLinearMappingUI root = new PiecewiseLinearMappingUI(model, data, Color.GRAY, Color.LIGHT_GRAY,
				null);
		GLSandBox.main(args, root, new GLPadding(10), new Dimension(260, 260));
	}
}
