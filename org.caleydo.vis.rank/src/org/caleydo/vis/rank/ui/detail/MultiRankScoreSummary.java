/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.vis.rank.ui.detail;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.internal.event.AnnotationEditEvent;
import org.caleydo.vis.rank.model.AMultiRankColumnModel;


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