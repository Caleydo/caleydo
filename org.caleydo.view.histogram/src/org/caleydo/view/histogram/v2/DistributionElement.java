/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElement;

/**
 * Rendering the distribution of a categorical element.
 *
 * @author Samuel Gratzl
 */
public class DistributionElement extends ASingleTablePerspectiveElement {
	private CategoricalHistogram hist;
	private final boolean vertical;

	public DistributionElement(TablePerspective tablePerspective, boolean vertical) {
		super(tablePerspective);
		this.vertical = vertical;
		setPicker(null);
		onVAUpdate(tablePerspective);
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		Histogram hist = tablePerspective.getContainerStatistics().getHistogram();
		assert hist instanceof CategoricalHistogram;
		this.hist = (CategoricalHistogram) hist;
		super.onVAUpdate(tablePerspective);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		int total = 0;
		for (int i = 0; i < hist.size(); ++i) {
			total += hist.get(i);
		}
		final float factor = (vertical ? h : w) / total;
		float x = 0;
		for (int i = 0; i < hist.size(); ++i) {
			int bucket = hist.get(i);
			float v = bucket * factor;
			g.color(hist.getColor(i));
			if (vertical)
				g.fillRect(0, x, w, v);
			else
				g.fillRect(x, 0, v, h);
			x += v;
		}
		x = 0;
		g.color(Color.DARK_GRAY);
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

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		repaint();
	}

	@Override
	public final Vec2f getMinSize() {
		int size = getTablePerspective().getRecordPerspective().getVirtualArray().size();
		if (vertical)
			return new Vec2f(20, size);
		else
			return new Vec2f(size, 20);
	}
}
