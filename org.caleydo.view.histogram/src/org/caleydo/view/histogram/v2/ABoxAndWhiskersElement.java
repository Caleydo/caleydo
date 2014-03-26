/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.core.util.function.ArrayDoubleList;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.IDoubleFunction;
import org.caleydo.core.util.function.IDoubleIterator;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.util.function.IInvertableDoubleFunction;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

import com.google.common.base.Objects;
import com.google.common.primitives.Doubles;

/**
 * renders an box and whiskers plot for numerical data domains
 *
 * @author Samuel Gratzl
 */
public abstract class ABoxAndWhiskersElement extends PickableGLElement implements ILabeled, IHasMinSize {
	private static final int TICK_STEP = 50;
	/**
	 * height of the box in percentage of the total height
	 */
	private static final float BOX_HEIGHT_PERCENTAGE = 1 / 3.f;
	private static final float LINE_TAIL_HEIGHT_PERCENTAGE = 0.75f;

	private final EDetailLevel detailLevel;
	private final EDimension direction;
	private final boolean showOutlier;
	private final boolean showMinMax;
	private boolean showScale = false;
	private Color frameColor = Color.LIGHT_GRAY;

	private AdvancedDoubleStatistics stats;
	protected IInvertableDoubleFunction normalize;

	private final GLPadding padding;
	/**
	 * value which is just above the <code>25 quartile - iqr*1.5</code> margin
	 */
	private double nearestIQRMin;
	/**
	 * value which is just below the <code>75 quartile + iqr*1.5</code> margin
	 */
	private double nearestIQRMax;

	private IDoubleList outliers;

	public ABoxAndWhiskersElement(EDetailLevel detailLevel, EDimension direction, boolean showOutlier,
			boolean showMinMax, GLPadding padding) {
		this.detailLevel = detailLevel;
		this.direction = direction;
		this.showOutlier = showOutlier;
		this.showMinMax = showMinMax;
		this.padding = padding;
	}

	/**
	 * @return the frameColor, see {@link #frameColor}
	 */
	public Color getFrameColor() {
		return frameColor;
	}

	/**
	 * @param frameColor
	 *            setter, see {@link frameColor}
	 */
	public void setFrameColor(Color frameColor) {
		if (Objects.equal(this.frameColor, frameColor))
			return;
		this.frameColor = frameColor;
		repaint();
	}

	/**
	 * @return the direction, see {@link #direction}
	 */
	public EDimension getDirection() {
		return direction;
	}

	/**
	 * @return the showScale, see {@link #showScale}
	 */
	public boolean isShowScale() {
		return showScale;
	}

	/**
	 * @param showScale
	 *            setter, see {@link showScale}
	 */
	public void setShowScale(boolean showScale) {
		this.showScale = showScale;
	}

	protected abstract Color getColor();

	@Override
	public String getTooltip() {
		if (stats == null)
			return null;
		StringBuilder b = new StringBuilder();
		b.append(getLabel()).append('\n');
		b.append(String.format("%s:\t%d", "count", stats.getN()));
		if (stats.getNaNs() > 0) {
			b.append(String.format("(+%d invalid)\n", stats.getNaNs()));
		} else
			b.append('\n');
		b.append(String.format("%s:\t%s\n", "median", Formatter.formatNumber(stats.getMedian())));
		b.append(String.format("%s:\t%s\n", "mean", Formatter.formatNumber(stats.getMean())));
		b.append(String.format("%s:\t%s\n", "median", Formatter.formatNumber(stats.getMedian())));
		b.append(String.format("%s:\t%s\n", "sd", Formatter.formatNumber(stats.getSd())));
		b.append(String.format("%s:\t%s\n", "var", Formatter.formatNumber(stats.getVar())));
		b.append(String.format("%s:\t%s\n", "mad", Formatter.formatNumber(stats.getMedianAbsoluteDeviation())));
		b.append(String.format("%s:\t%s\n", "min", Formatter.formatNumber(stats.getMin())));
		b.append(String.format("%s:\t%s", "max", Formatter.formatNumber(stats.getMax())));
		return b.toString();
	}

	public final void setData(IDoubleList list) {
		setData(list, Double.NaN, Double.NaN);
	}

	public void setData(IDoubleList list, double min, double max) {
		this.stats = AdvancedDoubleStatistics.of(list);
		updateIQRMatches(list);
		min = Double.isNaN(min) ? stats.getMin() : min;
		max = Double.isNaN(max) ? stats.getMax() : max;
		normalize = DoubleFunctions.normalize(min, max);
		repaint();
	}

	public final void setData(AdvancedDoubleStatistics stats) {
		setData(stats, Double.NaN, Double.NaN);
	}

	public void setData(AdvancedDoubleStatistics stats, double min, double max) {
		this.stats = stats;
		if (stats != null) {
			updateIQRMatches(null);
			min = Double.isNaN(min) ? stats.getMin() : min;
			max = Double.isNaN(max) ? stats.getMax() : max;
			normalize = DoubleFunctions.normalize(min, max);
		}
		repaint();
	}

	private void updateIQRMatches(IDoubleList l) {
		final double lowerIQRBounds = stats.getQuartile25() - stats.getIQR() * 1.5;
		final double upperIQRBounds = stats.getQuartile75() + stats.getIQR() * 1.5;

		if (l == null) { // invalid raw data
			nearestIQRMin = lowerIQRBounds;
			nearestIQRMax = upperIQRBounds;
			outliers = null;
			return;
		}

		nearestIQRMin = upperIQRBounds;
		nearestIQRMax = lowerIQRBounds;

		// values which are out of the iqr bounds
		List<Double> outliers = new ArrayList<>();

		// find the values which are at the within iqr bounds
		for (IDoubleIterator it = l.iterator(); it.hasNext();) {
			double v = it.nextPrimitive();
			if (Double.isNaN(v))
				continue;
			if (v > lowerIQRBounds && v < nearestIQRMin)
				nearestIQRMin = v;
			if (v < upperIQRBounds && v > nearestIQRMax)
				nearestIQRMax = v;
			// optionally compute the outliers
			if (showOutlier && (v < lowerIQRBounds || v > upperIQRBounds))
				outliers.add(v);
		}
		this.outliers = new ArrayDoubleList(Doubles.toArray(outliers));
	}

	/**
	 * @param renderBackground
	 *            setter, see {@link renderBackground}
	 */
	public void setRenderBackground(boolean renderBackground) {
		if (renderBackground)
			setRenderer(GLRenderers.fillRect(Color.WHITE));
		else
			setRenderer(null);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		if (frameColor != null)
			g.color(frameColor).drawRect(0, 0, w, h);
		if (stats == null)
			return;
		float hor = padding.hor();
		float vert = padding.vert();
		w -= hor;
		h -= vert;
		g.save().move(padding.left, padding.top);
		if (direction.isRecord()) {
			g.save().move(w, 0).gl.glRotatef(90, 0, 0, 1);
			renderBoxAndWhiskers(g, h, w);
			g.restore();
		} else
			renderBoxAndWhiskers(g, w, h);

		if (showScale && detailLevel == EDetailLevel.HIGH)
			renderScale(g, w, h);
		g.restore();
	}

	private void renderBoxAndWhiskers(GLGraphics g, float w, float h) {
		final float hi = h * BOX_HEIGHT_PERCENTAGE;
		final float y = (h - hi) * 0.5f;
		final float center = h / 2;

		{
			final float firstQuantrileBoundary = (float) (normalize.apply(stats.getQuartile25())) * w;
			final float thirdQuantrileBoundary = (float) (normalize.apply(stats.getQuartile75())) * w;

			g.color(getColor())
					.fillRect(firstQuantrileBoundary, y, thirdQuantrileBoundary - firstQuantrileBoundary, hi);
			g.color(Color.BLACK).drawRect(firstQuantrileBoundary, y, thirdQuantrileBoundary - firstQuantrileBoundary,
					hi);

			final float iqrMin = (float) normalize.apply(nearestIQRMin) * w;
			final float iqrMax = (float) normalize.apply(nearestIQRMax) * w;

			// Median
			float median = (float) normalize.apply(stats.getMedian()) * w;
			g.color(0.2f, 0.2f, 0.2f).drawLine(median, y, median, y + hi);

			// Whiskers
			g.color(0, 0, 0);
			// line to whiskers
			g.drawLine(iqrMin, center, firstQuantrileBoundary, center);
			g.drawLine(iqrMax, center, thirdQuantrileBoundary, center);

			float h_whiskers = hi * LINE_TAIL_HEIGHT_PERCENTAGE;
			g.drawLine(iqrMin, center - h_whiskers * 0.5f, iqrMin, center + h_whiskers * 0.5f);
			g.drawLine(iqrMax, center - h_whiskers * 0.5f, iqrMax, center + h_whiskers * 0.5f);
		}

		renderOutliers(g, w, hi, center, normalize);

		if (showMinMax) {
			g.gl.glPushAttrib(GL2.GL_POINT_BIT);
			g.gl.glPointSize(2f);
			g.color(0f, 0f, 0f, 1f);
			float min = (float) normalize.apply(stats.getMin()) * w;
			float max = (float) normalize.apply(stats.getMax()) * w;
			g.drawPoint(min, center);
			g.drawPoint(max, center);
			g.gl.glPopAttrib();
		}

	}

	private void renderOutliers(GLGraphics g, float w, final float hi, final float center, IDoubleFunction normalize) {
		if (!showOutlier || outliers == null)
			return;

		g.color(0.2f, 0.2f, 0.2f, outlierAlhpa(outliers.size()));
		g.gl.glPushAttrib(GL2.GL_POINT_BIT);
		g.gl.glPointSize(2f);

		for (IDoubleIterator it = outliers.iterator(); it.hasNext();) {
			float v = (float) normalize.apply(it.nextPrimitive()) * w;
			g.drawPoint(v, center);
		}
		g.gl.glPopAttrib();
	}

	private float outlierAlhpa(int size) {
		if (size < 10)
			return 1.0f;
		float v = 5.0f / size;
		if (v < 0.05f)
			return 0.05f;
		if (v > 1)
			return 1;
		return v;
	}

	private void renderScale(GLGraphics g, float w, float h) {
		float hi = h * 0.85f;

		g.color(Color.BLACK).drawLine(0, hi, w, hi);
		final int ticks = numberOfTicks(w);

		float delta = w / ticks;
		final float v_delta = 1.f / ticks;

		final int textHeight = 10;

		g.drawText(Formatter.formatNumber(normalize.unapply(0)), 1, hi, delta, textHeight);
		for (int i = 1; i < ticks; ++i) {
			g.drawText(Formatter.formatNumber(normalize.unapply(v_delta * i)), delta * i - delta * 0.5f, hi, delta,
					textHeight,
					VAlign.CENTER);
		}
		g.drawText(Formatter.formatNumber(normalize.unapply(1)), w - delta - 1, hi, delta - 1, textHeight, VAlign.RIGHT);
	}

	/**
	 * @param w
	 * @return
	 */
	private static int numberOfTicks(float w) {
		return Math.max(Math.round(w / TICK_STEP), 1);
	}

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		repaint();
	}

	@Override
	public final Vec2f getMinSize() {
		Vec2f r = getHorizontalMinSize();
		return direction.isHorizontal() ? r : new Vec2f(r.y(), r.x());
	}

	private Vec2f getHorizontalMinSize() {
		switch (detailLevel) {
		case HIGH:
			return new Vec2f(300, 90);
		case MEDIUM:
			return new Vec2f(100, 30);
		default:
			return new Vec2f(40, 15);
		}
	}
}