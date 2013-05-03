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
package org.caleydo.view.tourguide.api.score;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AComputedGroupScore extends DefaultLabelProvider implements IScore {
	protected Map<Integer, Float> scores = new ConcurrentHashMap<>();
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

	public final void put(Group elem, float value) {
		scores.put(elem.getID(), value);
	}

	@Override
	public float apply(IComputeElement elem, Group g) {
		if (g == null)
			return Float.NaN;
		Float f = scores.get(g.getID());
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