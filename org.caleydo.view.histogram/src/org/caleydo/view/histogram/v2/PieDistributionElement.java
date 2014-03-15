/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.List;
import java.util.Set;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.view.histogram.HistogramRenderStyle;
import org.caleydo.view.histogram.v2.internal.IDistributionData;
import org.caleydo.view.histogram.v2.internal.IDistributionData.DistributionEntry;

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
		float s = 0;
		boolean idBased = data.hasIds();
		List<DistributionEntry> entries = data.getEntries();
		if (idBased)
			s = data.size();
		else {
			for (DistributionEntry entry : entries)
				s += entry.getValue();
		}
		double factor = 360. / s;
		double acc = 180.f;
		final int bins = entries.size();
		for (int i = 0; i < bins; ++i) {
			DistributionEntry bucket = entries.get(i);
			double sweep = factor * (idBased ? bucket.getIDs().size() : bucket.getValue());
			g.color(toHighlight(bucket.getColor(), i));
			g.pushName(bucketPickingIds.get(i));
			if (sweep > 0)
				glu.gluPartialDisk(quad, 0, r, toSlices(sweep), 2, acc, -sweep);
			g.popName();
			acc -= sweep;
		}

		if (!g.isPickingPass()) {
			glu.gluQuadricDrawStyle(quad, GLU.GLU_SILHOUETTE);
			if (RenderStyle.COLOR_BORDER != null) {
				g.color(RenderStyle.COLOR_BORDER);
				acc = 180;
				for (int i = 0; i < bins; ++i) {
					DistributionEntry bucket = entries.get(i);
					double sweep = factor * (idBased ? bucket.getIDs().size() : bucket.getValue());
					if (sweep > 0)
						glu.gluPartialDisk(quad, 0, r, toSlices(sweep), 2, acc, -sweep);
					acc -= sweep;
				}
			}
			glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
			for (SelectionType selectionType : SELECTIONTYPES) {
				Set<Integer> elements = data.getElements(selectionType);
				if (elements.isEmpty())
					continue;
				g.color(toHighlightColor(selectionType));
				acc = 180;
				for (int i = 0; i < bins; ++i) {
					DistributionEntry bucket = entries.get(i);
					final Set<Integer> ids = bucket.getIDs();
					float p = ids.isEmpty() ? 0 : Sets.intersection(elements, ids).size() / ids.size();
					double sweep = factor * p * (idBased ? ids.size() : bucket.getValue());
					if (sweep > 0)
						glu.gluPartialDisk(quad, 0, r, toSlices(sweep), 2, acc, -sweep);
					sweep = factor * (idBased ? ids.size() : bucket.getValue());
					acc -= sweep;
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
		return new Vec2f(100, 100);
	}

	@Override
	public GLLocation apply(int dataIndex, boolean topLeft) {
		return GLLocation.UNKNOWN;
	}

	@Override
	public Set<Integer> unapply(GLLocation location) {
		return GLLocation.UNKNOWN_IDS;
	}
}
