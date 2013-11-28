/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.view.parcoords.PCRenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class Brush implements IGLRenderer {
	private static final int ANGLE_INC = 5;
	private final Vec2f center;
	private Vec2f end;
	private float area;

	/**
	 * @param pickedPoint
	 */
	public Brush(Vec2f start) {
		this.center = start;
		this.end = new Vec2f(start.x() + 40, start.y());
		this.area = 30;
	}

	public float getStartX() {
		return center.x();
	}

	public float getEndX() {
		return end.x();
	}

	public boolean isPointingLeft() {
		return center.x() > end.x();
	}
	/**
	 * @param angle
	 *            setter, see {@link angle}
	 */
	public void setArea(float angle) {
		this.area = angle;
	}

	private double getAngle() {
		return angle(end.minus(center));
	}

	/**
	 * @return
	 */
	private float getRadius() {
		return end.minus(center).length();
	}

	/**
	 * @param end
	 *            setter, see {@link end}
	 */
	public void setEnd(Vec2f end) {
		this.end = end;
	}

	/**
	 * @param g
	 */
	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.lineWidth(PCRenderStyle.ANGLUAR_LINE_WIDTH);
		g.color(PCRenderStyle.ANGULAR_POLYGON_COLOR);
		Vec2f dir = end.minus(center);
		float length = dir.length();
		double startAngle = angle(dir);
		g.save().move(center);
		g.gl.glTranslatef(0, 0, g.z());
		GLPrimitives.renderPartialDisc(g.glu(), 0, length, (float) (-startAngle - area) + 90, area * 2, 30);
		g.restore();
		g.color(PCRenderStyle.ANGULAR_COLOR);
		g.drawLine(center.x(), center.y(), end.x(), end.y());
		g.lineWidth(1);

	}

	private static double angle(Vec2f dir) {
		return Math.toDegrees(Math.atan2(dir.y(), dir.x()));
	}

	/**
	 * @param seg_a
	 * @param seg_b
	 * @see http://doswa.com/2009/07/13/circle-segment-intersectioncollision.html
	 * @return
	 */
	public boolean apply(Vec2f seg_a, Vec2f seg_b) {
		float radius = getRadius();
		Vec2f point = closestPointOnSegment(seg_a, seg_b);

		Vec2f dir = point.minus(center);
		if (dir.length() > radius)
			return false;
		final double angle = angle(dir);
		final double start = getAngle();
		if (angle < (start - this.area) || angle > (start + this.area))
			return false;
		return true;
	}

	private Vec2f closestPointOnSegment(Vec2f seg_a, Vec2f seg_b) {
		Vec2f cir_pos = center;

		Vec2f seg_v = seg_b.minus(seg_a);
		Vec2f pt_v = cir_pos.minus(seg_a);
		float seg_v_l = seg_v.length();
		seg_v.scale(1 / seg_v_l);
		float proj = pt_v.dot(seg_v);
		if (proj <= 0)
			return seg_a;
		if (proj >= seg_v_l)
			return seg_b;
		Vec2f proj_v = seg_v.times(proj);
		return proj_v.plus(seg_a);
	}

	/**
	 * @param pick
	 * @return
	 */
	public boolean pick(Pick pick) {
		switch (pick.getPickingMode()) {
		case MOUSE_WHEEL:
			int r = ((IMouseEvent) pick).getWheelRotation();
			setArea(Math.min(Math.max(area + ANGLE_INC * r, 0), 180));
			return true;
		case DRAGGED:
			setEnd(pick.getPickedPoint());
			return true;
		default:
			return false;
		}
	}

}
