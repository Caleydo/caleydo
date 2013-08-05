/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.util.PickingPool;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElement;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;

/**
 * Rendering the distribution of a categorical element.
 *
 * @author Samuel Gratzl
 */
public class DistributionElement extends ASingleTablePerspectiveElement implements IPickingLabelProvider {
	private CategoricalHistogram hist;
	private final EDistributionMode mode;
	/**
	 * factor to convert a count to percentages
	 */
	private float bucket2percentage;

	private PickingPool bucketPickingIds;
	private int hovered = -1;

	public enum EDistributionMode {
		VERTICAL_BAR, HORIZONTAL_BAR, PIE
	}

	public DistributionElement(TablePerspective tablePerspective, EDistributionMode mode) {
		super(tablePerspective);
		this.mode = mode;
		setPicker(null);
		onVAUpdate(tablePerspective);
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
	protected void takeDown() {
		bucketPickingIds.clear();
		bucketPickingIds = null;
		super.takeDown();
	}

	@Override
	public String getLabel(Pick pick) {
		int bucket = pick.getObjectID();
		return String.format("%s: %d (%.2f%%)", hist.getName(bucket), hist.get(bucket), hist.get(bucket)
				* bucket2percentage);
	}

	/**
	 * @param pick
	 */
	protected void onBucketPick(Pick pick) {
		int bucket = pick.getObjectID();
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			hovered = bucket;
			repaint();
			break;
		case MOUSE_OUT:
			hovered = -1;
			repaint();
			break;
		default:
			break;
		}
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		Histogram hist = tablePerspective.getContainerStatistics().getHistogram();
		assert hist instanceof CategoricalHistogram;
		this.hist = (CategoricalHistogram) hist;

		int total = 0;
		for (int i = 0; i < hist.size(); ++i) {
			total += hist.get(i);
		}
		this.bucket2percentage = 1.f / total;

		if (bucketPickingIds != null)
			bucketPickingIds.ensure(0, hist.size());

		super.onVAUpdate(tablePerspective);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		switch (mode) {
		case HORIZONTAL_BAR:
			renderBar(false, g, w, h);
			break;
		case VERTICAL_BAR:
			renderBar(true, g, w, h);
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
		switch (mode) {
		case HORIZONTAL_BAR:
			renderBar(false, g, w, h);
			break;
		case VERTICAL_BAR:
			renderBar(true, g, w, h);
			break;
		case PIE:
			renderPie(g, w, h);
			break;
		}
	}

	private void renderPie(GLGraphics g, float w, float h) {
		final float r = Math.min(w,h)*0.5f;
		g.save();
		g.gl.glTranslatef(w * 0.5f, h * 0.5f, g.z());

		GLU glu = g.glu();
		GLUquadric quad = glu.gluNewQuadric();
		double factor = 360*bucket2percentage;
		double acc = 0;
		for(int i = 0; i < hist.size(); ++i) {
			int bucket = hist.get(i);
			double sweep = factor * bucket;
			g.color(toHighlight(hist.getColor(i), i));
			g.pushName(bucketPickingIds.get(i));
			glu.gluPartialDisk(quad, 0, r, (int) (sweep * 0.1f), 2, acc, sweep);
			g.popName();
			acc += sweep;
		}
		if (RenderStyle.COLOR_BORDER != null && !g.isPickingPass()) {
			g.color(RenderStyle.COLOR_BORDER);
			glu.gluQuadricDrawStyle(quad, GLU.GLU_SILHOUETTE);
			acc = 0;
			for (int i = 0; i < hist.size(); ++i) {
				int bucket = hist.get(i);
				double sweep = factor * bucket;
				glu.gluPartialDisk(quad, 0, r, (int) (sweep * 0.1f), 2, acc, sweep);
				acc += sweep;
			}
		}
		glu.gluDeleteQuadric(quad);

		g.restore();
	}

	/**
	 * @param b
	 */
	private void renderBar(boolean vertical, GLGraphics g, float w, float h) {
		final float factor = (vertical ? h : w) * bucket2percentage;
		float x = 0;
		for (int i = 0; i < hist.size(); ++i) {
			int bucket = hist.get(i);
			float v = bucket * factor;
			g.color(toHighlight(hist.getColor(i), i));
			g.pushName(bucketPickingIds.get(i));
			if (vertical)
				g.fillRect(0, x, w, v);
			else
				g.fillRect(x, 0, v, h);
			g.popName();
			x += v;
		}
		x = 0;
		if (RenderStyle.COLOR_BORDER != null && !g.isPickingPass()) {
			g.color(RenderStyle.COLOR_BORDER);
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
		int size = getTablePerspective().getRecordPerspective().getVirtualArray().size();
		switch (mode) {
		case VERTICAL_BAR:
			return new Vec2f(20, size);
		case HORIZONTAL_BAR:
			return new Vec2f(size, 20);
		case PIE:
			return new Vec2f(200, 200);
		}
		throw new IllegalStateException();
	}
}
