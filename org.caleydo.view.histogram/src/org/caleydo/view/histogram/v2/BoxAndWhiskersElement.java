/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TableDoubleLists;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.core.util.function.IDoubleIterator;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElement;

/**
 * renders an average bar similar to enroute.
 *
 * @author Samuel Gratzl
 */
public class BoxAndWhiskersElement extends ASingleTablePerspectiveElement {
	/**
	 * height of the box in percentage of the total height
	 */
	private static final float BOX_HEIGHT_PERCENTAGE = 1 / 3.f;
	private static final int LINE_TAIL_HEIGHT = 3;

	private final EDetailLevel detailLevel;

	private AdvancedDoubleStatistics rawStats;
	private AdvancedDoubleStatistics normalizedStats;
	/**
	 * normalized value which is just above the <code>25 quartile - iqr*1.5</code> margin
	 */
	private double nearestIQRMin;
	/**
	 * normalized value which is just below the <code>75 quartile + iqr*1.5</code> margin
	 */
	private double nearestIQRMax;

	public BoxAndWhiskersElement(TablePerspective tablePerspective) {
		this(tablePerspective, EDetailLevel.HIGH);
		// is a numerical one
		assert DataSupportDefinitions.numericalTables.apply(tablePerspective);
	}

	public BoxAndWhiskersElement(TablePerspective tablePerspective, EDetailLevel detailLevel) {
		super(tablePerspective);
		this.detailLevel = detailLevel;
		onVAUpdate(tablePerspective);
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

		final double lowerIQRBounds = normalizedStats.getQuartile25() - normalizedStats.getIQR() * 1.5;
		final double upperIQRBounds = normalizedStats.getQuartile75() + normalizedStats.getIQR() * 1.5;

		nearestIQRMin = upperIQRBounds;
		nearestIQRMax = lowerIQRBounds;

		// find the values which are at the within iqr borders
		for (IDoubleIterator it = l.iterator(); it.hasNext();) {
			double v = it.nextPrimitive();
			if (Double.isNaN(v))
				continue;
			if (v > lowerIQRBounds && v < nearestIQRMin)
				nearestIQRMin = v;
			if (v < upperIQRBounds && v > nearestIQRMax)
				nearestIQRMax = v;
		}

		super.onVAUpdate(tablePerspective);
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
		final float y_whiskers = y / 2;
		g.color(0, 0, 0);
		g.drawLine(min, y_whiskers, firstQuantrileBoundary, y_whiskers);
		g.drawLine(max, y_whiskers, thirdQuantrileBoundary, y_whiskers);

		g.drawLine(min, y_whiskers - LINE_TAIL_HEIGHT, min, y_whiskers + LINE_TAIL_HEIGHT);
		g.drawLine(max, y_whiskers - LINE_TAIL_HEIGHT, max, y_whiskers + LINE_TAIL_HEIGHT);
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
