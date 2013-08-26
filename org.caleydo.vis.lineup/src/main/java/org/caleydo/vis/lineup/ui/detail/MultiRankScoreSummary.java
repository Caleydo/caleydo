/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.detail;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.lineup.internal.event.AnnotationEditEvent;
import org.caleydo.vis.lineup.model.AMultiRankColumnModel;


public class MultiRankScoreSummary extends ScoreSummary {
	public MultiRankScoreSummary(AMultiRankColumnModel model, boolean interactive) {
		super(model, interactive);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		AMultiRankColumnModel m = (AMultiRankColumnModel) model;
		if (m.getFilterMin() > 0) {
			g.color(0, 0, 0, 0.25f).fillRect(0, 0, m.getFilterMin() * w, h);
		}
		if (m.getFilterMax() < 1) {
			g.color(0, 0, 0, 0.25f).fillRect(m.getFilterMax() * w, 0, (1 - m.getFilterMax()) * w, h);
		}
	}

	@ListenTo(sendToMe = true)
	private void onSetAnnotation(AnnotationEditEvent event) {
		((AMultiRankColumnModel) model).setTitle(event.getTitle());
		((AMultiRankColumnModel) model).setDescription(event.getDescription());
	}
}
