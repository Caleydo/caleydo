/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.renderer;

import java.util.Arrays;
import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;

import com.google.common.collect.ImmutableList;

/**
 * @author Samuel Gratzl
 *
 */
public class AxisRenderer implements IGLRenderer {
	private EOrientation o = EOrientation.RIGHT;
	private List<String> ticks = ImmutableList.of();
	private float lineWidth = 2;
	private float tickWidth = 1;
	private float textHeight = 10;

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		renderAxis(g, w, h);
		renderTicks(g, w, h);
		g.lineWidth(1);
	}

	/**
	 * @param g
	 * @param w
	 * @param h
	 */
	private void renderAxis(GLGraphics g, float w, float h) {
		g.lineWidth(lineWidth);
		switch (o) {
		case RIGHT:
			g.drawLine(0, 0, 0, h);
			break;
		case LEFT:
			g.drawLine(w, 0, w, h);
			break;
		case TOP:
			g.drawLine(0, 0, w, 0);
			break;
		case BOTTOM:
			g.drawLine(0, h, w, h);
			break;
		}

	}

	private void renderTicks(GLGraphics g, float w, float h) {
		if (ticks.isEmpty())
			return;
		g.lineWidth(tickWidth);

		final int size = ticks.size();
		final EDimension dim = o.asDim();
		float f = dim.select(w, h) / (size - 1);
		float m = Math.max(dim.select(h, w) * 0.25f, 20);
		switch (o) {
		case RIGHT:
			for (int i = 0; i < size; ++i) {
				g.drawLine(0, i * f, m, i * f);
				final String text = ticks.get(i);
				// if (text != null)
				// g.drawText(text, x, y, w, textHeight);
			}
			break;
		case LEFT:
			for (int i = 0; i < size; ++i) {
				g.drawLine(w, i * f, -m, i * f);
			}
			break;
		case TOP:
			for (int i = 0; i < size; ++i) {
				g.drawLine(i * f, h, i * f, -m);
			}
			break;
		case BOTTOM:
			for (int i = 0; i < size; ++i) {
				g.drawLine(i * f, 0, i * f, m);
			}
			break;
		}
	}

	/**
	 * @param textHeight
	 *            setter, see {@link textHeight}
	 */
	public AxisRenderer setTextHeight(float textHeight) {
		this.textHeight = textHeight;
		return this;
	}

	/**
	 * @return the textHeight, see {@link #textHeight}
	 */
	public float getTextHeight() {
		return textHeight;
	}

	/**
	 * @param lineWidth
	 *            setter, see {@link lineWidth}
	 */
	public AxisRenderer setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
		return this;
	}

	/**
	 * @return the lineWidth, see {@link #lineWidth}
	 */
	public float getLineWidth() {
		return lineWidth;
	}

	/**
	 * @param tickWidth
	 *            setter, see {@link tickWidth}
	 */
	public AxisRenderer setTickWidth(float tickWidth) {
		this.tickWidth = tickWidth;
		return this;
	}

	/**
	 * @return the tickWidth, see {@link #tickWidth}
	 */
	public float getTickWidth() {
		return tickWidth;
	}

	public AxisRenderer setOrientation(EOrientation o) {
		this.o = o;
		return this;
	}

	/**
	 * @return the o, see {@link #o}
	 */
	public EOrientation getOrientation() {
		return o;
	}

	public AxisRenderer setTicks(int n) {
		final String[] l = new String[n];
		Arrays.fill(l, null);
		return setTicks(l);
	}

	public AxisRenderer setTicks(String... ticks) {
		return setTicks(Arrays.asList(ticks));
	}

	public AxisRenderer setTicks(double... ticks) {
		return setTicks("%.2f", ticks);
	}

	public AxisRenderer setTicks(String format, double... ticks) {
		String[] t = new String[ticks.length];
		for (int i = 0; i < ticks.length; ++i)
			t[i] = String.format(format, ticks[i]);
		return setTicks(t);
	}

	public AxisRenderer setTicks(List<String> ticks) {
		this.ticks = ticks;
		return this;
	}

	/**
	 * @return the ticks, see {@link #ticks}
	 */
	public List<String> getTicks() {
		return ticks;
	}

	public enum EOrientation {
		TOP, // - horizontal axis with ticks above the domain path
		BOTTOM, // - horizontal axis with ticks below the domain path
		LEFT, // - vertical axis with ticks to the left of the domain path
		RIGHT; // - vertical axis with ticks to the right of the domain path

		public EDimension asDim() {
			return this == TOP || this == BOTTOM ? EDimension.DIMENSION : EDimension.RECORD;
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, new AxisRenderer().setTicks(11));
	}
}
