/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.mapping;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;
import org.caleydo.vis.lineup.model.mapping.ScriptedMappingFunction.Filter;
import org.caleydo.vis.lineup.ui.RenderStyle;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class PiecewiseMappingCrossUI extends MappingCrossUI<PiecewiseMapping> implements IPickingListener,
		IGLLayout {
	private static final float GAP = 10;

	private boolean lineHovered;
	private Vec2f linePoint;

	private int pickingId = -1;
	private final Filter filter;

	public PiecewiseMappingCrossUI(PiecewiseMapping model, boolean isNormalLeft) {
		super(model, isNormalLeft);
		setPicker(GLRenderers.DUMMY);
		setLayout(this);
		this.filter = model.getFilter();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		for (IGLLayoutElement point : children) {
			Point p = (Point) point.asElement();
			float x_v = w * ((float) normalizeRaw(p.from));
			float y_v = h * (float) (1 - p.to);
			point.setBounds(x_v, y_v, 3, 3);
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		pickingId = context.registerPickingListener(this);
		reset();
	}

	@Override
	public void reset() {
		this.clear();
		if (model.isDefinedMapping()) {
			if (model.isMappingDefault()) {
				if (Double.isNaN(model.getFromMin()))
					this.add(new Point(model.getActMin(), 0, EMode.PSEUDO));
				if (Double.isNaN(model.getFromMax()))
					this.add(new Point(model.getActMax(), 1, EMode.PSEUDO));
			}
			for (Map.Entry<Double, Double> entry : model) {
				this.add(new Point(entry.getKey(), entry.getValue(), EMode.REGULAR));
			}
		} else {
			this.add(new Point(Double.isNaN(filter.getRaw_min()) ? model.getActMin() : filter.getRaw_min(), filter
					.getNormalized_min(), EMode.FILTER_MIN));
			this.add(new Point(Double.isNaN(filter.getRaw_max()) ? model.getActMax() : filter.getRaw_max(), filter
					.getNormalized_max(), EMode.FILTER_MAX));
		}
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(pickingId);
		super.takeDown();
	}

	@Override
	public void pick(Pick pick) {
		if (!model.isDefinedMapping())
			return;
		switch (pick.getPickingMode()) {
		case CLICKED:
			this.lineHovered = false;
			onAddPoint(this.linePoint.x(), this.linePoint.y());
			this.repaint();
			break;
		case MOUSE_OVER:
			this.lineHovered = true;
			this.linePoint = this.toRelative(pick.getPickedPoint());
			this.repaint();
			break;
		case MOUSE_OUT:
			this.lineHovered = false;
			this.linePoint = null;
			this.repaint();
			break;
		case MOUSE_MOVED:
			if (this.lineHovered)
				this.repaint();
			break;
		default:
			break;
		}
	}

	private void renderLine(GLGraphics g) {
		List<Vec2f> line = new ArrayList<>();
		for (GLElement point : this) {
			line.add(point.getLocation());
		}
		Collections.sort(line, new Comparator<Vec2f>() {
			@Override
			public int compare(Vec2f o1, Vec2f o2) {
				return Float.compare(o1.x(), o2.x());
			}
		});
		g.color(Color.BLACK).drawPath(line, false);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		this.renderLine(g);
		g.color(Color.LIGHT_GRAY);
		for (GLElement point : this) {
			Vec2f loc = point.getLocation();
			if (((Point) point).hovered) {
				drawHintLines(g, loc, w, h);
			}
		}
		if (this.lineHovered) {
			drawHintLines(g, this.linePoint, w, h);
			g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), this.linePoint.x() - 5, this.linePoint.y() - 5, 10, 10,
					Color.LIGHT_GRAY);
		}

		super.renderImpl(g, w, h);
	}

	private void drawHintLines(GLGraphics g, Vec2f loc, float w, float h) {
		Vec2f size = getSize();
		double from = inverseNormalize(loc.x() / size.x());
		float to = (size.y() - loc.y()) / size.y();
		g.textColor(Color.GRAY);
		if (isNormalLeft) {
			g.drawPath(false, new Vec2f(-GAP, loc.y()), loc, new Vec2f(loc.x(), h + GAP));
			g.drawText(Formatter.formatNumber(to), 1 - GAP, loc.y() + 1, 40, 12);
		} else {
			g.drawPath(false, new Vec2f(w + GAP, loc.y()), loc, new Vec2f(loc.x(), h + GAP));
			g.drawText(Formatter.formatNumber(to), w - 1 + GAP, loc.y() + 1, 40, 12);
		}
		g.drawText(Formatter.formatNumber(from), loc.x() + 1, h - 12 + GAP, 40, 12);

		g.textColor(Color.BLACK).drawText("f(" + Formatter.formatNumber(from) + ") = " + Formatter.formatNumber(to),
				loc.x() + 5, loc.y() - 4, 100, 12);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.pushName(pickingId);
		this.renderLine(g);
		g.popName();
		super.renderPickImpl(g, w, h);
	}

	void onRemovePoint(Point point) {
		if (point.mode != EMode.REGULAR) // can't remove pseudo points
			return;
		if (model.hasDefinedMappingBounds() && model.size() == 2) // can't remove defined bounds
			return;
		if ((!model.isMaxDefined() || !model.isMinDefined()) && model.size() == 1) // can't remove single defined
			return;
		model.remove(point.from);
		int s = model.size();
		switch (s) {
		case 0: // add the second
			point.mode = EMode.PSEUDO;
			Point other = (Point) (get(0) == point ? get(0 + 1) : get(0));
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
			point.mode = EMode.PSEUDO;
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

	protected void onAddPoint(double from, double to) {
		Vec2f size = getSize();

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
			t.mode = EMode.REGULAR;
			t.repaint();
			relayout();
			break;
		case 2:
			for (Point p : Iterables.filter(this, Point.class)) {
				if (p.mode == EMode.PSEUDO) {
					p.from = from;
					p.to = to;
					p.mode = EMode.REGULAR;
					p.repaint();
					break;
				}
			}
			relayout();
			break;
		default:
			this.add(new Point(from, to, EMode.REGULAR));
			break;
		}
		repaintMapping();
		fireCallback();
	}

	public void drag(Point point, float dx, float dy) {
		if (dx == 0 && dy == 0) // no change
			return;
		Vec2f size = getSize();
		double from = normalizeRaw(point.from) + dx / (size.x());
		double to = point.to - dy / (size.y());
		updateMapping(point, from, to);
		repaintMapping();
		this.relayout();
	}

	private void updateMapping(Point current, double from, double to) {
		double oldFrom = current.from;
		double oldTo = current.to;

		from = (from < 0 ? 0 : (from > 1 ? 1 : from));
		to = (to < 0 ? 0 : (to > 1 ? 1 : to));

		from = inverseNormalize(from);
		if (model.isDefinedMapping())
			model.update(oldFrom, oldTo, from, to);
		else if (filter != null) {
			assert current.mode.isFilter();
			if (current.mode == EMode.FILTER_MIN) {
				filter.setRaw_min(from);
				filter.setNormalized_min(to);
			} else if (current.mode == EMode.FILTER_MAX) {
				filter.setRaw_max(from);
				filter.setNormalized_max(to);
			}
		}
		current.set(from, to);
	}

	private class Point extends PickableGLElement {
		private boolean hovered;
		private EMode mode;
		private double from;
		private double to;

		public Point(double from, double to, EMode mode) {
			this.from = from;
			this.to = to;
			this.mode = mode;
		}

		public void set(double from, double to) {
			this.from = from;
			this.to = to;
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			Color color = this.hovered ? Color.RED : (mode.isFilter() ? Color.BLUE : Color.BLACK);
			if (mode != EMode.PSEUDO)
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
			if (this.mode == EMode.PSEUDO && (pick.getDx() != 0 || pick.getDy() != 0))
				this.mode = EMode.REGULAR;
			drag(this, pick.getDx(), pick.getDy());
			this.repaintAll();
		}

		@Override
		protected void onMouseOver(Pick pick) {
			this.hovered = true;
			this.repaint();
			getParent().repaint();
		}

		@Override
		protected void onMouseReleased(Pick pick) {
			if (pick.isDoDragging()) {
				this.hovered = false;
				this.repaint();
				getParent().repaint();
				fireCallback();
			}
		}

		@Override
		protected void onMouseOut(Pick pick) {
			if (!pick.isDoDragging()) {
				this.hovered = false;
				this.repaint();
				getParent().repaint();
			}
		}
	}
}

