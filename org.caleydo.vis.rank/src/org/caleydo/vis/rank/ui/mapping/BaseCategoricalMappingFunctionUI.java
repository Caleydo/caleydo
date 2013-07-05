/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.mapping;

import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.binsForWidth;
import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.FloatFunctions;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel.CategoryInfo;
import org.caleydo.vis.rank.model.SimpleHistogram;
import org.caleydo.vis.rank.model.mapping.BaseCategoricalMappingFunction;
import org.caleydo.vis.rank.model.mapping.ICategoricalMappingFunction;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * FIXME not implemented
 *
 * @author Samuel Gratzl
 *
 */
public class BaseCategoricalMappingFunctionUI<T> extends GLElementContainer implements IGLLayout, IHasUIConfig {
	private static final float GAP = 10;
	private static final float PADDING = 5;

	private final BaseCategoricalMappingFunction<T> model;
	private final int[] hist;
	private final int histMax;
	private final int histSum;
	private final List<T> order;
	private final Map<T, CategoryInfo> metaData;
	private final Color backgroundColor;
	private final Color color;
	private final ICallback<? super ICategoricalMappingFunction<?>> callback;
	private final IRankTableUIConfig config;

	public BaseCategoricalMappingFunctionUI(BaseCategoricalMappingFunction<T> model, Map<T, Integer> data,
			Map<T, CategoryInfo> metaData, Color color, Color bgColor,
			ICallback<? super ICategoricalMappingFunction<?>> callback, IRankTableUIConfig config) {
		this.model = model;
		this.config = config;

		this.order = new ArrayList<>(metaData.keySet());
		this.metaData = metaData;
		this.color = color;
		this.backgroundColor = bgColor;
		this.callback = callback;

		this.hist = new int[order.size()];
		Arrays.fill(hist, 0);
		int sum = 0;
		int max = 0;
		int i = 0;
		for (T o : order) {
			Integer c = data.get(o);
			if (c != null) {
				hist[i] = c.intValue();
				sum++;
			}
			max = Math.max(hist[i], max);
			i++;
		}
		histMax = max;
		histSum = sum;

		setLayout(this);

		this.add(new RawHistogram());
		this.add(new NormalizedHistogram());
		this.add(new Canvas());
	}

	/**
	 * @return the config, see {@link #config}
	 */
	@Override
	public IRankTableUIConfig getConfig() {
		return config;
	}

	protected SimpleHistogram computeHist(float w) {
		int bins = binsForWidth(w, histSum);
		SimpleHistogram hist = new SimpleHistogram(bins);
		for(int i = 0; i < this.hist.length; ++i) {
			float v = model.applyPrimitive(order.get(i));
			hist.add(v);
		}
		return hist;
	}

	public void onRemovePoint(Point point) {
		point.to = -1;
		model.remove(order.get(point.index));
		repaintMapping();
		point.repaintAll();
		fireCallback();
	}

	public void createPoint(Point point) {
		point.to = 0.5f;
		model.put(order.get(point.index), 0.5f);
		repaintMapping();
		point.repaintAll();
		fireCallback();
	}

	private void repaintMapping() {
		get(1).repaint();
		get(2).repaint();
	}

	public void drag(Point point, float y, int dv) {
		if (dv == 0) // no change
			return;
		Vec2f size = getSize();
		float delta = dv / size.x();
		// float factor = y / size.y();
		float to = FloatFunctions.CLAMP01.apply(point.to + delta);
		point.to = to;
		model.put(order.get(point.index), to);
		point.repaintAll();
		repaintMapping();
		this.relayout();
	}

	public void fireCallback() {
		callback.on(model);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(backgroundColor).fillRect(0, 0, w, h);
		super.renderImpl(g, w, h);
	}

	static class BarRenderer implements IGLRenderer {
		private final Color color;
		private final float value;

		public BarRenderer(Color color, float value) {
			this.color = color;
			this.value = value;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			float y = h * (1 - value);
			g.color(color).fillRect(0, y, w, h - y);
		}
	}

	public float normalizeRaw(int index) {
		return (index + 0.5f) / order.size();
	}

	private class RawHistogram extends GLElementContainer {
		public RawHistogram() {
			setLayout(new GLFlowLayout(true, 5, new GLPadding(2, 2, 2, LABEL_HEIGHT)));
			int i = 0;
			float largest = histMax;
			for(T value : order) {
				CategoryInfo info = metaData.get(value);
				PickableGLElement v = new PickableGLElement();
				v.setTooltip(info.getLabel());
				v.setRenderer(new BarRenderer(info.getColor(), hist[i] / largest));
				this.add(v);
				i++;
			}
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(Color.BLACK).drawRect(0, 0, w, h - LABEL_HEIGHT);
			g.drawText("Raw", 0, h - LABEL_HEIGHT, w, LABEL_HEIGHT - 5, VAlign.CENTER);
			super.renderImpl(g, w, h);
		}
	}

	private class NormalizedHistogram extends HistogramElement {
		public NormalizedHistogram() {
			super("Score", color, backgroundColor);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			boolean vertical = w < h;
			render(g, vertical, 0, 1, w, h, Float.NaN, Float.NaN, computeHist(w - LABEL_HEIGHT),
					getLayoutDataAs(Boolean.class, Boolean.FALSE));
		}
	}

	private class Canvas extends GLElementContainer {
		public Canvas() {
			setLayout(GLLayouts.LAYERS);
			for (int i = 0; i < order.size(); ++i) {
				add(new Point(i, model.applyPrimitive(order.get(i))));
			}
		}
	}

	class Point extends PickableGLElement {
		private boolean hovered;
		private int index;
		private float to;

		public Point(int index, float to) {
			this.index = index;
			this.to = to;
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			Color color = this.hovered ? Color.RED : Color.BLACK;
			g.color(color);
			float f = normalizeRaw(index);
			float t = to;

			float x2 = f * w;
			if (to >= 0) {
				float x1 = t * w;
				g.drawLine(x1, 0, x2, h);
				g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), -5 + x1, -5, 10, 10, color);
				g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), -5 + x2, h - 5, 10, 10, color);
				if (hovered) {
					g.drawText("f(\"" + order.get(index) + "\") = " + Formatter.formatNumber(to), x1 + 5, 0 + 4,
							100, 11);
				}
			} else {
				g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), -5 + x2, h - 5, 10, 10, color);
			}
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			g.lineWidth(2);
			g.color(Color.BLUE);
			float x2 = normalizeRaw(index) * w;
			float x1 = to * w;
			if (to >= 0) {
				g.drawLine(x1, 0, x2, h);
				g.fillRect(x1 - 5, -5, 10, 10);
				g.fillRect(x2 - 5, h - 5, 10, 10);
			} else {
				g.fillRect(x2 - 5, h - 5, 10, 10);
			}
			g.color(Color.RED);
			g.lineWidth(1);
		}

		@Override
		protected void onClicked(Pick pick) {
			if (to < 0) {
				createPoint(this);
			}
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
			int dv = pick.getDx();
			if (dv == 0)
				return;
			drag(this, toRelative(pick.getPickedPoint()).y(), dv);
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

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		float x = PADDING;
		float y = PADDING;
		w -= PADDING * 2;
		h -= PADDING * 2;
		IGLLayoutElement raw = children.get(0);
		IGLLayoutElement norm = children.get(1);
		IGLLayoutElement canvas = children.get(2);

		final float histHeight = HIST_HEIGHT + RenderStyle.LABEL_HEIGHT;

		norm.setBounds(x + GAP, y, w - GAP * 2, histHeight);
		raw.setBounds(x + GAP, y + h - histHeight, w - GAP * 2, histHeight);
		canvas.setBounds(x + GAP, y + histHeight + GAP, w - GAP * 2, h - histHeight * 2 - GAP * 2);

		raw.asElement().setLayoutData(true);
		norm.asElement().setLayoutData(false);
	}

}
