/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.mapping;

import gleem.linalg.Vec2f;

import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;
import org.caleydo.vis.lineup.ui.RenderStyle;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class PiecewiseMappingParallelUI extends MappingParallelUI<PiecewiseMapping> implements IPickingListener,
		IGLLayout {

	private int pickingId = -1;

	public PiecewiseMappingParallelUI(PiecewiseMapping model, boolean isHorizontal) {
		super(model, isHorizontal);
		setPicker(GLRenderers.DUMMY);
		setLayout(this);
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
					this.add(new PointLinePoint(model.getActMin(), 0, true));
				if (Double.isNaN(model.getFromMax()))
					this.add(new PointLinePoint(model.getActMax(), 1, true));
			}
			for (Map.Entry<Double, Double> entry : model) {
				this.add(new PointLinePoint(entry.getKey(), entry.getValue(), false));
			}
		}
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(pickingId);
		super.takeDown();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (model.isDefinedMapping()) {
			g.pushName(pickingId);
			if (isHorizontal) {
				g.fillRect(0, -5, w, 20);
				g.fillRect(0, h - 15, w, 20);
			} else {
				g.fillRect(-5, 0, 20, h);
				g.fillRect(w - 15, 0, 20, h);
			}
			g.popName();
		}
		super.renderPickImpl(g, w, h);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		for (PointLinePoint p : Iterables.filter(this, PointLinePoint.class)) {
			if (p.hovered) {
				drawHint(g, w, h, p.from, p.to);
			}
		}
		super.renderImpl(g, w, h);

	}

	@Override
	public void pick(Pick pick) {
		if (!model.isDefinedMapping() || pick.isAnyDragging())
			return;
		switch (pick.getPickingMode()) {
		case CLICKED:
			Vec2f r = toRelative(pick.getPickedPoint());
			onAddPoint(toDragMode(pick.getPickedPoint()), isHorizontal ? r.x() : r.y());
			this.repaint();
			break;
		default:
			break;
		}
	}
	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		for(IGLLayoutElement child : children)
			child.setBounds(0, 0, w, h);
	}

	public void drag(PointLinePoint point, EDragMode mode, double dv) {
		if (dv == 0) // no change
			return;
		float max = isHorizontal ? getSize().x() : -getSize().y();

		double from = normalizeRaw(point.from) + (mode == EDragMode.BOTH || mode == EDragMode.FROM ? (dv / max) : 0);
		double to = point.to + (mode == EDragMode.BOTH || mode == EDragMode.TO ? (dv / max) : 0);
		updateMapping(point, from, to);
		repaintMapping();
		this.relayout();
	}

	private void updateMapping(PointLinePoint current, double from, double to) {
		double oldFrom = current.from;
		double oldTo = current.to;

		from = (from < 0 ? 0 : (from > 1 ? 1 : from));
		to = (to < 0 ? 0 : (to > 1 ? 1 : to));

		from = inverseNormalize(from);
		model.update(oldFrom, oldTo, from, to);

		current.set(from, to);
	}

	void onRemovePoint(PointLinePoint point) {
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
			PointLinePoint other = (PointLinePoint) (get(0) == point ? get(0 + 1) : get(0));
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

	private void onAddPoint(EDragMode mode, float at) {
		Vec2f size = getSize();

		at = at / (isHorizontal ? size.x() : size.y());

		double from;
		double to;
		switch (mode) {
		case FROM:
			from = (isHorizontal ? at : (1 - at));
			to = model.apply(inverseNormalize(from));
			break;
		default:
			from = to = (isHorizontal ? at : (1 - at));
			break;
		}

		model.put(from, to);
		switch (model.size()) {
		case 1:
			List<PointLinePoint> points = Lists.newArrayList(Iterables.filter(this, PointLinePoint.class));
			assert points.size() == 2;
			PointLinePoint p1 = points.get(0);
			PointLinePoint p2 = points.get(1);
			PointLinePoint t;
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
			for (PointLinePoint p : Iterables.filter(this, PointLinePoint.class)) {
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
			this.add(new PointLinePoint(from, to, false));
			break;
		}
		repaintMapping();
		fireCallback();
	}

	class PointLinePoint extends PickableGLElement {
		private boolean hovered;
		private boolean pseudo;
		private double from;
		private double to;

		public PointLinePoint(double from, double to, boolean pseudo) {
			this.from = from;
			this.to = to;
			this.pseudo = pseudo;
		}

		public void set(double from, double to) {
			this.from = from;
			this.to = to;
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			Color color = this.hovered ? Color.RED : Color.BLACK;
			g.color(color);
			double f = normalizeRaw(from);
			double t = to;
			if (!pseudo) {
				if (isHorizontal) {
					float x2 = (float) f * w;
					float x1 = (float) t * w;
					g.drawLine(x1, 0, x2, h);
					g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), -5 + x1, -5, 10, 10, color);
					g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), -5 + x2, h - 5, 10, 10, color);
				} else {
					float y1 = (float) (1 - f) * h;
					float y2 = (float) (1 - t) * h;
					g.drawLine(0, y1, w, y2);
					g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), -5, -5 + y1, 10, 10, color);
					g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), w - 5, -5 + y2, 10, 10, color);
				}
			} else {
				g.gl.glEnable(GL2.GL_LINE_STIPPLE);
				g.gl.glLineStipple(2, (short) 0xAAAA);
				g.lineWidth(2);
				if (isHorizontal) {
					float x2 = (float) f * w;
					float x1 = (float) t * w;
					g.drawLine(x1, 0, x2, h);
					g.drawCircle(x1, 0, 5);
					g.drawCircle(x2, h, 5);
				} else {
					float y1 = (float) (1 - f) * h;
					float y2 = (float) (1 - t) * h;
					g.drawLine(0, y1, w, y2);
					g.drawCircle(0, y1, 5);
					g.drawCircle(w, y2, 5);
				}
				g.gl.glDisable(GL2.GL_LINE_STIPPLE);
				g.lineWidth(1);
			}
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			g.lineWidth(2);
			if (isHorizontal) {
				float x2 = (float) normalizeRaw(from) * w;
				float x1 = (float) to * w;
				g.drawLine(x1, 0, x2, h);
				g.fillRect(x1 - 5, -5, 10, 10);
				g.fillRect(x2 - 5, h - 5, 10, 10);
			} else {
				float y1 = (float) (1 - normalizeRaw(from)) * h;
				float y2 = (float) (1 - to) * h;
				g.drawLine(0, y1, w, y2);
				g.fillRect(-5, y1 - 5, 10, 10);
				g.fillRect(w - 5, y2 - 5, 10, 10);
			}

			g.lineWidth(1);
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
			float dv = isHorizontal ? pick.getDx() : pick.getDy();
			if (dv == 0)
				return;
			this.pseudo = false;
			drag(this, toDragMode(pick.getPickedPoint()), dv);
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

	/**
	 * @param pickedPoint
	 */
	private EDragMode toDragMode(Vec2f pickedPoint) {
		Vec2f r = toRelative(pickedPoint);
		float v = isHorizontal ? r.y() : r.x();
		float max = isHorizontal ? getSize().y() : getSize().x();
		if (v < max * 0.15f)
			return isHorizontal ? EDragMode.TO : EDragMode.FROM;
		if (v > max * 0.85f)
			return !isHorizontal ? EDragMode.TO : EDragMode.FROM;
		return EDragMode.BOTH;
	}

	enum EDragMode {
		FROM, TO, BOTH
	}

}

