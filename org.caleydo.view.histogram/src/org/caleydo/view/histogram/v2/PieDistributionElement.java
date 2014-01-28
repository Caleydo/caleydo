/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.Set;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.view.histogram.HistogramRenderStyle;
import org.caleydo.view.histogram.v2.internal.IDistributionData;

import com.google.common.collect.Sets;

/**
 * Rendering the distribution of a categorical element in various forms
 *
 * @author Samuel Gratzl
 */
public class PieDistributionElement extends ADistributionElement {

	/**
	 * @param data
	 */
	public PieDistributionElement(IDistributionData data) {
		super(data);
	}
	@Override
	protected void render(GLGraphics g, float w, float h) {
		final float r = Math.min(w, h) * 0.5f - HistogramRenderStyle.SIDE_SPACING_DETAIL_LOW;
		g.save();
		g.gl.glTranslatef(w * 0.5f, h * 0.5f, g.z());

		GLU glu = g.glu();
		GLUquadric quad = glu.gluNewQuadric();
		double factor = 360. / data.size();
		double acc = 0;
		final Histogram hist = data.getHist();
		for(int i = 0; i < hist.size(); ++i) {
			int bucket = hist.get(i);
			double sweep = factor * bucket;
			g.color(toHighlight(data.getBinColor(i), i));
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
				Set<Integer> elements = data.getElements(selectionType);
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
	/**
	 * @param sweep
	 * @return
	 */
	private static int toSlices(double sweep) {
		return Math.max((int) (sweep * 0.1f), 2);
	}

	@Override
	public final Vec2f getMinSize() {
		return new Vec2f(200, 200);
	}

	@Override
	public GLLocation apply(int dataIndex) {
		return GLLocation.UNKNOWN;
	}

	@Override
	public Set<Integer> unapply(GLLocation location) {
		return GLLocation.UNKNOWN_IDS;
	}
}
