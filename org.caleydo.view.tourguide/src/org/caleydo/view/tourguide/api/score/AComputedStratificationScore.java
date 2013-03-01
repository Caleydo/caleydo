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

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.PiecewiseLinearMapping;

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

	public boolean contains(Perspective elem) {
		// have in cache or the same
		return scores.containsKey(elem.getPerspectiveID());
	}

	public final void put(Perspective elem, float value) {
		scores.put(elem.getPerspectiveID(), value);
	}

	@Override
	public final float applyPrimitive(IRow elem) {
		Perspective p = ((PerspectiveRow) elem).getStratification();
		Float f = scores.get(p.getPerspectiveID());
		return f == null ? Float.NaN : f.floatValue();
	}

	@Override
	public final Float apply(IRow elem) {
		return applyPrimitive(elem);
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
	public PiecewiseLinearMapping createMapping() {
		return new PiecewiseLinearMapping(0, 1);
	}

}