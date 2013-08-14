/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import org.caleydo.core.util.color.Color;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AComputedStratificationScore extends DefaultLabelProvider implements IScore {
	protected final Map<String, Float> scores = new ConcurrentHashMap<>();
	private final Color color;
	private final Color bgColor;

	public AComputedStratificationScore(String label, Color color, Color bgColor) {
		super(label);
		this.color = color;
		this.bgColor = bgColor;
	}

	public boolean contains(IComputeElement elem) {
		// have in cache or the same
		return scores.containsKey(elem.getPersistentID());
	}

	public final void put(IComputeElement elem, float value) {
		scores.put(elem.getPersistentID(), value);
	}

	@Override
	public final float apply(IComputeElement elem, Group g) {
		Float f = scores.get(elem.getPersistentID());
		return f == null ? Float.NaN : f.floatValue();
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Color getBGColor() {
		return bgColor;
	}

	@Override
	public PiecewiseMapping createMapping() {
		return new PiecewiseMapping(0, 1);
	}

}
