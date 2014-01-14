/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.kaplanmeier.v2;

import static org.caleydo.view.kaplanmeier.GLKaplanMeier.AXIS_LABEL_TEXT_HEIGHT_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.AXIS_TICK_LABEL_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.BOTTOM_AXIS_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.LEFT_AXIS_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.RIGHT_AXIS_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.TOP_AXIS_SPACING_PIXELS;
import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.core.view.opengl.util.spline.TesselatedPolygons;

import com.google.common.base.Supplier;

/**
 * kaplan meier plot implementation as a {@link GLElement}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AKaplanMeierElement extends PickableGLElement implements IHasMinSize {
	protected final EDetailLevel detailLevel;
	protected final GLPadding padding;
	/**
	 * @param tablePerspective
	 */
	public AKaplanMeierElement(EDetailLevel detailLevel) {
		this.detailLevel = detailLevel;

		if (detailLevel.compareTo(EDetailLevel.MEDIUM) < 0)
			setVisibility(EVisibility.VISIBLE);

		if (detailLevel == EDetailLevel.HIGH)
			padding = new GLPadding(LEFT_AXIS_SPACING_PIXELS, TOP_AXIS_SPACING_PIXELS, RIGHT_AXIS_SPACING_PIXELS,
					BOTTOM_AXIS_SPACING_PIXELS);
		else
			padding = GLPadding.ZERO;
		setPicker(null);
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isAssignableFrom(Vec2f.class))
			return clazz.cast(getMinSize());
		return super.getLayoutDataAs(clazz, default_);
	}


	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		render(g, w, h);
		super.renderImpl(g, w, h);
	}

	private void render(GLGraphics g, float w, float h) {
		// resolve data
		g.gl.glPushAttrib(GL2.GL_LINE_BIT);
		{
			g.save().move(padding.left, padding.top);

			float wp = w - padding.hor();
			float hp = h - padding.vert();

			renderCurve(g, wp, hp);

			g.restore();
		}

		if (detailLevel == EDetailLevel.HIGH && !g.isPickingPass())
			renderAxes(g, w, h);

		g.gl.glPopAttrib();
	}

	protected abstract void renderCurve(GLGraphics g, float w, float h);


	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (getVisibility() == EVisibility.PICKABLE) {
			render(g, w, h);
		}
		super.renderPickImpl(g, w, h);
	}

	private void renderAxes(GLGraphics g, float w, float h) {
		assert detailLevel == EDetailLevel.HIGH;

		g.color(Color.BLACK);
		Axis xaxis = getAxis(EDimension.DIMENSION);
		Axis yaxis = getAxis(EDimension.RECORD);

		g.drawText(xaxis.label, 0, h - AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS - AXIS_LABEL_TEXT_HEIGHT_PIXELS, w,
				AXIS_LABEL_TEXT_HEIGHT_PIXELS, VAlign.CENTER);
		{// draw rotated text
			g.save();
			g.gl.glRotatef(-90, 0, 0, 1);
			g.move(-h, 0);
			g.drawText(yaxis.label, 0, AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS, h, AXIS_LABEL_TEXT_HEIGHT_PIXELS,
					VAlign.CENTER);
			g.restore();
		}

		g.move(padding.left, padding.top);
		float wp = w - padding.hor();
		float hp = h - padding.vert();
		g.lineWidth(2);
		renderSingleAxis(g, true, xaxis.ticks, xaxis.max, wp, hp);
		renderSingleAxis(g, false, yaxis.ticks, yaxis.max, wp, hp);
		g.move(-padding.left, -padding.top);
	}

	/**
	 * @param dimension
	 * @return
	 */
	protected abstract Axis getAxis(EDimension dim);

	private void renderSingleAxis(GLGraphics g, boolean isXAxis, int numTicks, float maxTickValue, float w, float h) {
		Vec2f start = new Vec2f(0, h);
		Vec2f end = isXAxis ? new Vec2f(w, h) : new Vec2f(0, 0);

		g.drawLine(start.x(), start.y(), end.x(), end.y());

		float factor = (isXAxis ? w : h) / (numTicks - 1);
		float step = maxTickValue / (numTicks - 1);

		// render ticks
		for (int i = 0; i < numTicks; i++) {
			float v = factor * i;
			if (isXAxis) {
				String text = String.valueOf((int) (i * step));
				g.drawLine(v, h - 7, v, h + 7);
				g.drawText(text, v - 100, h + AXIS_TICK_LABEL_SPACING_PIXELS, 200, 13, VAlign.CENTER);
			} else {
				String text = String.valueOf((int) ((numTicks - i - 1) * step));
				g.drawLine(-7, v, 7, v);
				g.drawText(text, -100, v - 7, 100 - AXIS_TICK_LABEL_SPACING_PIXELS, 13, VAlign.RIGHT);
			}
		}
	}

	protected final void drawCurve(GLGraphics g, IDoubleList data, Color color, float w, float h, boolean fillCurve,
			SelectionType selectionType, String label) {
		List<Vec2f> linePoints = createCurve(data, w, h);

		if (fillCurve) {
			if (color.isGray())
				g.color(Color.MEDIUM_DARK_GRAY);
			else
				g.color(color.lessSaturated());
			// add addition points to get a closed shape
			linePoints.add(new Vec2f(w, h));
			linePoints.add(new Vec2f(0, h)); // origin
			g.fillPolygon(TesselatedPolygons.polygon2(linePoints));
			linePoints.remove(linePoints.size() - 1); // remove again not part of the curve
			linePoints.remove(linePoints.size() - 1); // remove again not part of the curve
		}

		float translationZ = 0;
		if (label != null && !g.isPickingPass()) {
			translationZ = renderCurveLabel(g, color, selectionType, label, linePoints, translationZ);
		}
		g.color(color);
		g.incZ(translationZ);
		g.drawPath(linePoints, false);
		g.incZ(-translationZ);
	}

	private float renderCurveLabel(GLGraphics g, Color color, SelectionType selectionType, String label,
			List<Vec2f> linePoints, float translationZ) {
		Color bgColor = new Color(1, 1, 1, 0.5f);
		Color textColor = Color.BLACK;
		if (selectionType == SelectionType.SELECTION) {
			bgColor = Color.WHITE;
			textColor = color;
			translationZ = 0.1f;
		} else if (selectionType == SelectionType.MOUSE_OVER) {
			bgColor = Color.WHITE;
			textColor = color;
			translationZ = 0.2f;
		}
		// add a label to the end of the curve
		Vec2f pos = linePoints.get(linePoints.size() - 1).copy();
		float textWidth = g.text.getTextWidth(label, 13);
		g.save();
		g.move(pos.x(), pos.y() - 13);
		g.incZ(translationZ);
		g.color(bgColor).fillRect(0, 0, textWidth + 2, 13 + 2);
		g.textColor(textColor).drawText(label, 1, 1, textWidth, 13).textColor(Color.BLACK);
		g.restore();
		return translationZ;
	}

	/**
	 * create a curve out of the data by minimizing the point by not sampling the data but compute the curve
	 *
	 * @param data
	 * @param w
	 * @param h
	 * @return
	 */
	protected List<Vec2f> createCurve(IDoubleList data, float w, float h) {
		assert data.size() != 0;

		List<Vec2f> linePoints = new ArrayList<>();

		float timeFactor = w; // / maxAxisTime; //as normalized in 0...maxtime
		float valueFactor = h / data.size();

		double last = data.get(0);
		float lastTime = 0;
		int lastIndex = 0;

		for (int i = 1; i < data.size(); ++i) {
			double v = data.getPrimitive(i);
			if (v == last)
				continue;
			assert v > last; // as sorted
			float time = ((float) v) * timeFactor;
			linePoints.add(new Vec2f(lastTime, lastIndex * valueFactor));
			linePoints.add(new Vec2f(time, lastIndex * valueFactor)); // as we have a sharp dropoff
			lastTime = time;
			lastIndex = i;
			last = v;
		}
		// final one
		linePoints.add(new Vec2f(lastTime, lastIndex * valueFactor));
		linePoints.add(new Vec2f(w, lastIndex * valueFactor));
		return linePoints;
	}

	protected final Pair<Vec2f, Vec2f> getLocation(List<Vec2f> curve, double value, float w) {
		float x = (float) value * w;
		Vec2f last = curve.get(0);
		for (Vec2f point : curve.subList(1, curve.size())) {
			float lastX = last.x();
			float x2 = point.x();
			if (lastX <= x && x <= x2)
				return Pair.make(last, point);
			last = point;
		}
		return Pair.make(last, last);
	}

	@Override
	public Vec2f getMinSize() {
		switch (detailLevel) {
		case HIGH:
			return new Vec2f(400, 400);
		default:
			return new Vec2f(100, 100);
		}
	}

	public abstract List<GLLocation> getLocations(EDimension dim, Iterable<Integer> dataIndizes);

	@Override
	public String toString() {
		return "Kaplan Maier";
	}

	protected static class Axis {
		protected String label;
		protected int ticks;
		protected float max;

		public Axis(String label, int ticks, float max) {
			this.label = label;
			this.ticks = ticks;
			this.max = max;
		}
	}
}
