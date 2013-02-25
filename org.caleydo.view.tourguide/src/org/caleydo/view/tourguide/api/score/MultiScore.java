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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.PiecewiseLinearMapping;

import com.google.common.collect.Iterators;

/**
 * marker for a default multi score
 *
 * @author Samuel Gratzl
 *
 */
public class MultiScore extends DefaultLabelProvider implements IScore, Iterable<IScore> {
	private Collection<IScore> children = new ArrayList<>();
	private Color color;
	private Color bgColor;

	public MultiScore(String label, Color color, Color bgColor) {
		setLabel(label);
		this.color = color;
		this.bgColor = bgColor;
	}

	/**
	 * @param createJaccardME
	 */
	public void add(IScore score) {
		children.add(score);
	}

	@Override
	public Iterator<IScore> iterator() {
		return Iterators.unmodifiableIterator(children.iterator());
	}

	@Override
	public PiecewiseLinearMapping createMapping() {
		return null;
	}

	@Override
	public String getAbbreviation() {
		return "";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public Color getBGColor() {
		return bgColor;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Float apply(IRow row) {
		return applyPrimitive(row);
	}

	@Override
	public float applyPrimitive(IRow row) {
		return Float.NaN;
	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		for (IScore s : children) {
			if (!s.supports(mode))
				return false;
		}
		return true;
	}

	public Collection<? extends IScore> getChildren() {
		return children;
	}

}
