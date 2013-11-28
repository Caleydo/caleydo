/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.TableDoubleLists;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.ADoubleFunction;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.core.util.function.ArrayDoubleList;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleFunction;
import org.caleydo.core.util.function.IDoubleIterator;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElement;

import com.google.common.primitives.Doubles;

/**
 * renders an box and whiskers plot for numerical data domains
 *
 * @author Samuel Gratzl
 */
public class BoxAndWhiskersElement extends ASingleTablePerspectiveElement {
	/**
	 *
	 */
	private static final int TICK_STEP = 50;
	/**
	 * height of the box in percentage of the total height
	 */
	private static final float BOX_HEIGHT_PERCENTAGE = 1 / 3.f;
	private static final float LINE_TAIL_HEIGHT_PERCENTAGE = 0.75f;
	private static final float OUTLIER_HEIGHT_PERCENTAGE = 0.5f;

	private final EDetailLevel detailLevel;
	private final EDimension direction;
	private final boolean showOutlier;
	private boolean showScale = false;

	private AdvancedDoubleStatistics rawStats;
	private DoubleStatistics rootStats;
	private AdvancedDoubleStatistics normalizedStats;
	/**
	 * normalized value which is just above the <code>25 quartile - iqr*1.5</code> margin
	 */
	private double nearestIQRMin;
	/**
	 * normalized value which is just below the <code>75 quartile + iqr*1.5</code> margin
	 */
	private double nearestIQRMax;

	private IDoubleList outliers;

	public BoxAndWhiskersElement(TablePerspective tablePerspective) {
		this(tablePerspective, EDetailLevel.HIGH, EDimension.RECORD, false);
	}

	public BoxAndWhiskersElement(TablePerspective tablePerspective, EDetailLevel detailLevel, EDimension direction,
			boolean showOutlier) {
		super(tablePerspective);
		this.detailLevel = detailLevel;
		this.direction = direction;
		this.showOutlier = showOutlier;
		onVAUpdate(tablePerspective);
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

	@Override
	public String getTooltip() {
		StringBuilder b = new StringBuilder();
		b.append(getTablePerspective().getLabel()).append('\n');
		b.append(String.format("%s:\t%.3f\n", "mean", rawStats.getMean()));
		b.append(String.format("%s:\t%.3f\n", "median", rawStats.getMedian()));
		b.append(String.format("%s:\t%.3f\n", "sd", rawStats.getSd()));
		b.append(String.format("%s:\t%.3f\n", "var", rawStats.getVar()));
		b.append(String.format("%s:\t%.3f\n", "mad", rawStats.getMedianAbsoluteDeviation()));
		b.append(String.format("%s:\t%.3f\n", "min", rawStats.getMin()));
		b.append(String.format("%s:\t%.3f", "max", rawStats.getMax()));
		return b.toString();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		this.rawStats = AdvancedDoubleStatistics.of(TableDoubleLists.asRawList(tablePerspective));
		IDoubleList l = TableDoubleLists.asNormalizedList(tablePerspective);
		this.normalizedStats = AdvancedDoubleStatistics.of(l);

		updateIQRMatches(l);
		updateRootState(tablePerspective);

		super.onVAUpdate(tablePerspective);
	}

	private void updateIQRMatches(IDoubleList l) {
		final double lowerIQRBounds = normalizedStats.getQuartile25() - normalizedStats.getIQR() * 1.5;
		final double upperIQRBounds = normalizedStats.getQuartile75() + normalizedStats.getIQR() * 1.5;

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

	private void updateRootState(TablePerspective tablePerspective) {
		if (tablePerspective.getParentTablePerspective() != null
				&& !(tablePerspective.getDataDomain().getTable() instanceof NumericalTable)) {
			rootStats = DoubleStatistics.of(TableDoubleLists.asRawList(tablePerspective.getParentTablePerspective()));
		} else {
			rootStats = rawStats;
		}
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

		if (direction.isHorizontal()) {
			g.save().gl.glRotatef(90, 0, 0, 1);
			renderBoxAndWhiskers(g, h, w);
			g.restore();
		} else
			renderBoxAndWhiskers(g, w, h);

		if (showScale && detailLevel == EDetailLevel.HIGH)
			renderScale(g, w, h);
	}

	private void renderBoxAndWhiskers(GLGraphics g, float w, float h) {
		final float hi = h * BOX_HEIGHT_PERCENTAGE;
		final float y = (h - hi) * 0.5f;

		final float firstQuantrileBoundary = (float) (normalizedStats.getQuartile25()) * w;
		final float thirdQuantrileBoundary = (float) (normalizedStats.getQuartile75()) * w;

		g.color(getTablePerspective().getDataDomain().getColor()).fillRect(firstQuantrileBoundary, y,
				thirdQuantrileBoundary - firstQuantrileBoundary, hi);
		g.color(Color.BLACK).drawRect(firstQuantrileBoundary,y,thirdQuantrileBoundary-firstQuantrileBoundary, hi);

		final float min = (float) nearestIQRMin * w;
		final float max = (float) nearestIQRMax * w;

		// Median
		float median = (float) normalizedStats.getMedian() * w;
		g.color(0.2f, 0.2f, 0.2f).drawLine(median, y, median, y + hi);

		// Whiskers
		final float center = h / 2;
		g.color(0, 0, 0);
		g.drawLine(min, center, firstQuantrileBoundary, center);
		g.drawLine(max, center, thirdQuantrileBoundary, center);

		float h_whiskers = hi * LINE_TAIL_HEIGHT_PERCENTAGE;
		g.drawLine(min, center - h_whiskers * 0.5f, min, center + h_whiskers * 0.5f);
		g.drawLine(max, center - h_whiskers * 0.5f, max, center + h_whiskers * 0.5f);

		renderOutliers(g, w, hi, center);
	}

	private void renderOutliers(GLGraphics g, float w, final float hi, final float center) {
		if (!showOutlier || outliers == null)
			return;

		g.color(0.2f);
		float h_outlier = hi * OUTLIER_HEIGHT_PERCENTAGE * 0.5f;

		for (IDoubleIterator it = outliers.iterator(); it.hasNext();) {
			float v = (float) it.nextPrimitive() * w;
			g.drawLine(v, center - h_outlier, v, center + h_outlier);
		}
	}


	private void renderScale(GLGraphics g, float w, float h) {
		final TablePerspective t = getTablePerspective();
		float hi = h * 0.85f;

		g.color(Color.BLACK).drawLine(0, hi, w, hi);
		final int ticks = numberOfTicks(w);

		float delta = w / ticks;
		float x = delta;
		final float v_delta = 1.f / ticks;

		final Table table = t.getDataDomain().getTable();
		IDoubleFunction f;
		if (table instanceof NumericalTable) {
			// we can use the built in function
			final NumericalTable tab = (NumericalTable) table;
			f = new ADoubleFunction() {
				@Override
				public double apply(double v) {
					return tab.getRawForNormalized(Table.Transformation.LINEAR, v);
				}
			};
		} else {
			// use the data from the min max stats
			f = DoubleFunctions.unnormalize(rootStats.getMin(), rootStats.getMax());
		}
		final int textHeight = 10;

		g.drawText(Formatter.formatNumber(f.apply(0)), 1, hi, delta, textHeight);
		for (int i = 1; i < ticks; ++i) {
			g.drawText(Formatter.formatNumber(f.apply(v_delta * i)), x - delta * 0.5f, hi, delta, textHeight,
					VAlign.CENTER);
		}
		g.drawText(Formatter.formatNumber(f.apply(1)), w - delta - 1, hi, delta - 1, textHeight, VAlign.RIGHT);
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
