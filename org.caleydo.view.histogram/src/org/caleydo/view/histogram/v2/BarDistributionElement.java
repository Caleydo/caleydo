/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.view.histogram.v2.internal.IDistributionData;

import com.google.common.collect.Sets;

/**
 * Rendering the distribution of a categorical element in various forms
 *
 * @author Samuel Gratzl
 */
public class BarDistributionElement extends ADistributionElement {
	private final boolean vertical;

	public BarDistributionElement(IDistributionData data, boolean vertical) {
		super(data);
		this.vertical = vertical;
	}

	@Override
	protected void render(GLGraphics g, float w, float h) {
		final float factor = (vertical ? h : w) / data.size();
		float x = 0;
		final Histogram hist = data.getHist();
		for (int i = 0; i < hist.size(); ++i) {
			int bucket = hist.get(i);
			float v = bucket * factor;
			g.color(toHighlight(data.getBinColor(i), i));
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
				Set<Integer> elements = data.getElements(selectionType);
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

	@Override
	public final Vec2f getMinSize() {
		int size = data.size();
		return vertical ? new Vec2f(20, size) : new Vec2f(size, 20);
	}

	@Override
	public GLLocation apply(int dataIndex) {
		float max = EDimension.get(!vertical).select(getSize());
		float m = max / data.size();
		return new GLLocation(dataIndex * m, m);
	}

	/**
	 * @return
	 */
	public EDimension getDimension() {
		return EDimension.get(!vertical);
	}
}
