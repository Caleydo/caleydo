/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.util.PickingPool;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;
import org.caleydo.view.histogram.HistogramRenderStyle;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * Rendering the distribution of a categorical element in various forms
 *
 * @author Samuel Gratzl
 */
public abstract class ADistributionElement extends PickableGLElement implements IHasMinSize, IPickingLabelProvider {

	private static final List<SelectionType> SELECTIONTYPES = Arrays.asList(SelectionType.MOUSE_OVER,
			SelectionType.SELECTION);

	private EDistributionMode mode;

	protected PickingPool bucketPickingIds;
	protected int hovered = -1;


	public enum EDistributionMode {
		VERTICAL_BAR, HORIZONTAL_BAR, PIE, HISTOGRAM
	}

	public ADistributionElement(EDistributionMode mode) {
		this.mode = mode;
	}

	/**
	 * @return the mode, see {@link #mode}
	 */
	public EDistributionMode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            setter, see {@link mode}
	 */
	public void setMode(EDistributionMode mode) {
		if (this.mode == mode)
			return;
		this.mode = mode;
		repaintAll();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		IPickingListener pick = PickingListenerComposite.concat(context.getSWTLayer().createTooltip(this), new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onBucketPick(pick);
			}
		});
		bucketPickingIds = new PickingPool(context, pick);
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isAssignableFrom(Vec2f.class))
			return clazz.cast(getMinSize());
		return super.getLayoutDataAs(clazz, default_);
	}

	protected abstract Histogram getHist();

	protected abstract String getBinName(int bin);
	protected abstract Color getBinColor(int bin);

	@Override
	protected void takeDown() {
		bucketPickingIds.clear();
		bucketPickingIds = null;
		super.takeDown();
	}

	@Override
	public String getLabel(Pick pick) {
		final Histogram hist = getHist();
		int bucket = pick.getObjectID();
		StringBuilder b = new StringBuilder();
		final int count = hist.get(bucket);
		b.append(String.format("%s: %d (%.2f%%)", getBinName(bucket), count, count * 100. / size()));
		for (SelectionType selectionType : SELECTIONTYPES) {
			Set<Integer> elements = getElements(selectionType);
			if (elements.isEmpty())
				continue;
			Set<Integer> ids = hist.getIDsForBucket(bucket);
			int scount = Sets.intersection(elements, ids).size();
			if (scount > 0)
				b.append(String.format("\n  %s: %d (%.2f%%)", selectionType.getType(), scount, scount * 100f / count));
		}
		return b.toString();
	}

	/**
	 * @param pick
	 */
	protected void onBucketPick(Pick pick) {
		final Histogram hist = getHist();
		int bucket = pick.getObjectID();
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			hovered = bucket;
			select(hist.getIDsForBucket(pick.getObjectID()), SelectionType.MOUSE_OVER, true);
			repaint();
			break;
		case MOUSE_OUT:
			hovered = -1;
			select(Collections.<Integer> emptyList(), SelectionType.MOUSE_OVER, true);
			repaint();
			break;
		case CLICKED:
			// select bucket:
			select(hist.getIDsForBucket(pick.getObjectID()), SelectionType.SELECTION, true);
			break;
		default:
			break;
		}
	}

	/**
	 * @param iDsForBucket
	 */
	protected abstract void select(Collection<Integer> ids, SelectionType selectionType, boolean clear);

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		render(g, w, h);
		g.lineWidth(1);
	}

	private void render(GLGraphics g, float w, float h) {
		switch (mode) {
		case HORIZONTAL_BAR:
			renderBar(false, g, w, h);
			break;
		case VERTICAL_BAR:
			renderBar(true, g, w, h);
			break;
		case HISTOGRAM:
			renderHistogram(g, w, h);
			break;
		case PIE:
			renderPie(g, w, h);
			break;
		}
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);

		if (getVisibility() != EVisibility.PICKABLE)
			return;

		g.incZ();
		render(g, w, h);
		g.decZ();
	}

	private void renderPie(GLGraphics g, float w, float h) {
		final float r = Math.min(w, h) * 0.5f - HistogramRenderStyle.SIDE_SPACING_DETAIL_LOW;
		g.save();
		g.gl.glTranslatef(w * 0.5f, h * 0.5f, g.z());

		GLU glu = g.glu();
		GLUquadric quad = glu.gluNewQuadric();
		double factor = 360. / size();
		double acc = 0;
		final Histogram hist = getHist();
		for(int i = 0; i < hist.size(); ++i) {
			int bucket = hist.get(i);
			double sweep = factor * bucket;
			g.color(toHighlight(getBinColor(i), i));
			g.pushName(bucketPickingIds.get(i));
			if (sweep > 0)
				glu.gluPartialDisk(quad, 0, r, toSlices(sweep), 2, acc, sweep);
			g.popName();
			acc += sweep;
		}

		if (!g.isPickingPass()) {
			glu.gluQuadricDrawStyle(quad, GLU.GLU_SILHOUETTE);
			if (RenderStyle.COLOR_BORDER != null) {
				g.color(RenderStyle.COLOR_BORDER);
				acc = 0;
				for (int i = 0; i < hist.size(); ++i) {
					int bucket = hist.get(i);
					double sweep = factor * bucket;
					if (sweep > 0)
						glu.gluPartialDisk(quad, 0, r, toSlices(sweep), 2, acc, sweep);
					acc += sweep;
				}
			}
			glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
			for (SelectionType selectionType : SELECTIONTYPES) {
				Set<Integer> elements = getElements(selectionType);
				if (elements.isEmpty())
					continue;
				g.color(toHighlightColor(selectionType));
				acc = 0;
				for (int i = 0; i < hist.size(); ++i) {
					Set<Integer> ids = hist.getIDsForBucket(i);
					double sweep = factor * Sets.intersection(elements, ids).size();
					if (sweep > 0)
						glu.gluPartialDisk(quad, 0, r, toSlices(sweep), 2, acc, sweep);
					sweep = factor * hist.get(i);
					acc += sweep;
				}
			}
		}
		glu.gluDeleteQuadric(quad);

		g.restore();
	}

	protected abstract Set<Integer> getElements(SelectionType selectionType);

	private static Color toHighlightColor(SelectionType selectionType) {
		Color c = selectionType.getColor().clone();
		c.a = 0.75f;
		return c;
	}

	/**
	 * @param sweep
	 * @return
	 */
	private static int toSlices(double sweep) {
		return Math.max((int) (sweep * 0.1f), 2);
	}

	private void renderHistogram(GLGraphics g, float w, float h) {
		h -= HistogramRenderStyle.SIDE_SPACING_DETAIL_LOW * 2;
		w -= HistogramRenderStyle.SIDE_SPACING_DETAIL_LOW * 2;
		final Histogram hist = getHist();
		final float factor = h / hist.getLargestValue();
		final float delta = w / hist.size();

		final float lineWidth = Math.min(delta - 1, 25);
		final float lineWidthHalf = lineWidth * 0.5f;
		float x = delta / 2;

		g.save().move(HistogramRenderStyle.SIDE_SPACING_DETAIL_LOW,
				HistogramRenderStyle.SIDE_SPACING_DETAIL_LOW + h - 1);
		g.color(Color.DARK_GRAY).drawLine(0, 0, w, 0);

		for (int i = 0; i < hist.size(); ++i) {
			g.color(toHighlight(getBinColor(i), i));
			float v = -hist.get(i) * factor;

			if (v <= -1) {
				g.pushName(bucketPickingIds.get(i));
				g.fillRect(x - lineWidthHalf, 0, lineWidth, v);
				g.popName();
			}
			x += delta;
		}

		if (!g.isPickingPass()) {
			if (RenderStyle.COLOR_BORDER != null) {
				g.color(RenderStyle.COLOR_BORDER);
				x = delta / 2;
				for (int i = 0; i < hist.size(); ++i) {
					float v = -hist.get(i) * factor;
					if (v <= -1) {
						g.drawRect(x - lineWidthHalf, 0, lineWidth, v);
					}
					x += delta;
				}
			}
			g.lineWidth(2);
			for (SelectionType selectionType : SELECTIONTYPES) {
				Set<Integer> elements = getElements(selectionType);
				if (elements.isEmpty())
					continue;
				g.color(toHighlightColor(selectionType));
				x = delta / 2;
				for (int i = 0; i < hist.size(); ++i) {
					Set<Integer> ids = hist.getIDsForBucket(i);
					float v = -Sets.intersection(elements, ids).size() * factor;
					if (v <= -1) {
						g.fillRect(x - lineWidthHalf, 0, lineWidth, v);
					}
					x += delta;
				}
			}
		}

		g.restore();
	}

	/**
	 * @param b
	 */
	private void renderBar(boolean vertical, GLGraphics g, float w, float h) {
		final float factor = (vertical ? h : w) / size();
		float x = 0;
		final Histogram hist = getHist();
		for (int i = 0; i < hist.size(); ++i) {
			int bucket = hist.get(i);
			float v = bucket * factor;
			g.color(toHighlight(getBinColor(i), i));
			g.pushName(bucketPickingIds.get(i));
			if (vertical)
				g.fillRect(0, x, w, v);
			else
				g.fillRect(x, 0, v, h);
			g.popName();
			x += v;
		}
		if (!g.isPickingPass()) {
			if (RenderStyle.COLOR_BORDER != null) {
				g.color(RenderStyle.COLOR_BORDER);
				x = 0;
				for (int i = 0; i < hist.size(); ++i) {
					int bucket = hist.get(i);
					float v = bucket * factor;
					if (vertical)
						g.drawRect(0, x, w, v);
					else
						g.drawRect(x, 0, v, h);
					x += v;
				}
			}
			g.lineWidth(2);
			for (SelectionType selectionType : SELECTIONTYPES) {
				Set<Integer> elements = getElements(selectionType);
				if (elements.isEmpty())
					continue;
				g.color(toHighlightColor(selectionType));
				x = 0;
				for (int i = 0; i < hist.size(); ++i) {
					Set<Integer> ids = hist.getIDsForBucket(i);
					float v = Sets.intersection(elements, ids).size() * factor;
					if (vertical)
						g.fillRect(0, x, w, v);
					else
						g.fillRect(x, 0, v, h);
					v = hist.get(i) * factor;
					x += v;
				}
			}
		}
	}

	/**
	 * @param color
	 * @param i
	 * @return
	 */
	private Color toHighlight(Color color, int bucket) {
		if (bucket == hovered)
			return color.darker();
		return color;
	}

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		repaint();
	}

	@Override
	public final Vec2f getMinSize() {
		int size = size();
		switch (mode) {
		case VERTICAL_BAR:
			return new Vec2f(20, size);
		case HORIZONTAL_BAR:
			return new Vec2f(size, 20);
		case HISTOGRAM:
			return new Vec2f(100, 100);
		case PIE:
			return new Vec2f(200, 200);
		}
		throw new IllegalStateException();
	}

	protected abstract int size();
}
