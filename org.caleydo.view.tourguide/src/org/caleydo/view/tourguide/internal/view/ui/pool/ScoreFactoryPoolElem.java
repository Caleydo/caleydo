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
package org.caleydo.view.tourguide.internal.view.ui.pool;

import java.awt.Color;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.view.tourguide.internal.event.CreateScoreEvent;
import org.caleydo.view.tourguide.spi.IScoreFactory;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreFactoryPoolElem extends APoolElem {

	private final String label;
	private final IScoreFactory factory;
	private final Object receiver;

	public ScoreFactoryPoolElem(String label, IScoreFactory factory, Object receiver) {
		this.label = label;
		this.factory = factory;
		this.receiver = receiver;
		setTooltip("Double-Click to create a new " + label + " score");
		setMode(EDataDomainQueryMode.STRATIFICATIONS); // default
	}

	public void setMode(EDataDomainQueryMode mode) {
		setVisibility(factory.supports(mode) ? EVisibility.PICKABLE : EVisibility.VISIBLE);
	}

	@Override
	protected Color getBackgroundColor() {
		return Color.WHITE;
	}

	@Override
	protected void onDoubleClicked(Pick pick) {
		EventPublisher.trigger(new CreateScoreEvent(label).to(receiver));
		super.onDoubleClicked(pick);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		final boolean isDisabled = getVisibility() == EVisibility.VISIBLE;
		float x = 0;
		if (isDisabled) {
			g.textColor(Color.GRAY);
		} else {
			x = 10;
			g.fillImage(TourGuideRenderStyle.ICON_ADD_COLOR, 1, (h - 10) * 0.5f, 10, 10);
		}
		g.drawText(label, x + 2, 3, w - 2 - x, h - 9);
		if (isDisabled) {
			g.textColor(Color.BLACK);
		}
		g.color(armed ? Color.BLACK : Color.DARK_GRAY);
		g.drawRoundedRect(0, 0, w, h, 5);
	}

}
