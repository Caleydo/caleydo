/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.mapping;

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
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.caleydo.vis.rank.ui.RenderStyle;

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

	public PiecewiseMappingCrossUI(PiecewiseMapping model, boolean isNormalLeft) {
		super(model, isNormalLeft);
		setPicker(GLRenderers.DUMMY);
		setLayout(this);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		for (IGLLayoutElement point : children) {
			Point p = (Point) point.asElement();
			float x_v = w * (normalizeRaw(p.from));
			float y_v = h * (1 - p.to);
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
				if (Float.isNaN(model.getFromMin()))
					this.add(new Point(model.getActMin(), 0, true));
				if (Float.isNaN(model.getFromMax()))
					this.add(new Point(model.getActMax(), 1, true));
			}
			for (Map.Entry<Float, Float> entry : model) {
				this.add(new Point(entry.getKey(), entry.getValue(), false));
			}
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
			this.linePoint = this.toRelative(pick.getDIPPickedPoint());
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
		if (model.isDefinedMapping()) {
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
				g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), this.linePoint.x() - 5, this.linePoint.y() - 5, 10,
						10, Color.LIGHT_GRAY);
			}
		}

		super.renderImpl(g, w, h);
	}

	private void drawHintLines(GLGraphics g, Vec2f loc, float w, float h) {
		Vec2f size = getSize();
		float from = inverseNormalize(loc.x() / size.x());
		float to = (size.y() - loc.y()) / size.y();
		g.textColor(Color.GRAY);
		if (isNormalLeft) {
			g.drawPath(false, new Vec2f(-GAP, loc.y()), loc, new Vec2f(loc.x(), h + GAP));
			g.drawText(Formatter.formatNumber(to), 1 - GAP, loc.y() + 1, 40, 11);
		} else {
			g.drawPath(false, new Vec2f(w + GAP, loc.y()), loc, new Vec2f(loc.x(), h + GAP));
			g.drawText(Formatter.formatNumber(to), w - 1 + GAP, loc.y() + 1, 40, 11);
		}
		g.drawText(Formatter.formatNumber(from), loc.x() + 1, h - 12 + GAP, 40, 11);

		g.textColor(Color.BLACK).drawText("f(" + Formatter.formatNumber(from) + ") = " + Formatter.formatNumber(to),
				loc.x() + 5, loc.y() - 4, 100, 11);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (model.isDefinedMapping()) {
			g.pushName(pickingId);
			this.renderLine(g);
			g.popName();
		}
		super.renderPickImpl(g, w, h);
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

	public void drag(Point point, float dx, float dy) {
		if (dx == 0 && dy == 0) // no change
			return;
		Vec2f size = getSize();
		float from = normalizeRaw(point.from) + dx / (size.x());
		float to = point.to - dy / (size.y());
		updateMapping(point, from, to);
		repaintMapping();
		this.relayout();
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

