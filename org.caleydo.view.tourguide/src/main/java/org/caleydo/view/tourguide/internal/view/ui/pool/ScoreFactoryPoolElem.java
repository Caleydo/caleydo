/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui.pool;

import org.caleydo.core.util.color.Color;

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
