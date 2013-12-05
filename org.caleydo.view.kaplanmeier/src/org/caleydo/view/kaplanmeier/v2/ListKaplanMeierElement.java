/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.kaplanmeier.v2;

import java.util.Arrays;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.ADoubleFunction;
import org.caleydo.core.util.function.ArrayDoubleList;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

import com.google.common.base.Preconditions;

/**
 * kaplan meier plot implementation as a {@link GLElement}
 *
 * @author Samuel Gratzl
 *
 */
public class ListKaplanMeierElement extends AKaplanMeierElement {
	private final IDoubleList data;
	private Axis xAxis;
	private Axis yAxis;
	private Color color = Color.GRAY;
	private boolean isFillCurve = false;

	/**
	 * @param tablePerspective
	 */
	public ListKaplanMeierElement(IDoubleList data, EDetailLevel detailLevel) {
		super(detailLevel);
		this.xAxis = new Axis("Time", 6, (float) DoubleStatistics.of(data).getMax());
		this.yAxis = new Axis("Percentage", 6, 100);

		this.data = convert(Preconditions.checkNotNull(data));
	}

	/**
	 * @param xAxis setter, see {@link xAxis}
	 */
	public void setXAxis(String xAxis) {
		if (this.xAxis.label.equals(xAxis))
			return;
		this.xAxis.label = xAxis;
		repaint();
	}

	public String getXAxis() {
		return xAxis.label;
	}

	public String getYAxis() {
		return yAxis.label;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	public void setXMaxValue(float value) {
		if (this.xAxis.max == value)
			return;
		this.xAxis.max = value;
		repaint();
	}

	public float getXMaxValue() {
		return xAxis.max;
	}

	public void setYAxis(String yAxis) {
		if (this.yAxis.label.equals(yAxis))
			return;
		this.yAxis.label = yAxis;
		repaint();
	}

	/**
	 * @param color setter, see {@link color}
	 */
	public void setColor(Color color) {
		if (this.color.equals(color))
			return;
		this.color = color;
		repaint();
	}


	/**
	 * @param checkNotNull
	 * @return
	 */
	private IDoubleList convert(IDoubleList data) {
		// remove nan
		data = data.map(new ADoubleFunction() {
			@Override
			public double apply(double v) {
				return Double.isNaN(v) ? 1 : v;
			}
		});
		DoubleStatistics stats = DoubleStatistics.of(data);
		// normalize
		data = data.map(DoubleFunctions.normalize(stats.getMin(), stats.getMax()));
		// sort
		double[] vs = data.toPrimitiveArray();
		Arrays.sort(vs);

		return new ArrayDoubleList(vs);
	}

	@Override
	protected void renderCurve(GLGraphics g, float w, float h) {
		drawCurve(g, data, color, w, h, isFillCurve, null, null);
	}


	@Override
	protected Axis getAxis(EDimension dim) {
		return dim.select(xAxis, yAxis);
	}
}
