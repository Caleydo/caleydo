/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

/**
 * basic implementation of a {@link IScore} that is based on groups including caching implementation
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AComputedGroupScore extends DefaultLabelProvider implements IScore {
	protected Map<Integer, Double> scores = new ConcurrentHashMap<>();
	private final Color color;
	private final Color bgColor;

	public AComputedGroupScore(String label, Color color, Color bgColor) {
		super(label);
		this.color = color;
		this.bgColor = bgColor;
	}

	public boolean contains(IComputeElement perspective, Group g) {
		// have the value or it the same stratification
		return scores.containsKey(g.getID());
	}

	public final void put(Group elem, double value) {
		scores.put(elem.getID(), value);
	}

	@Override
	public double apply(IComputeElement elem, Group g) {
		if (g == null)
			return Double.NaN;
		Double f = scores.get(g.getID());
		return f == null ? Double.NaN : f.doubleValue();
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
