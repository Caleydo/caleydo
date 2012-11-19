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
package org.caleydo.view.tourguide.data.score;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.caleydo.core.util.format.Formatter;
import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * special kind of composite score, which doesn't combine the scores but triggers that the rows will be multiplied to
 * show all values of the children in the same column
 *
 * @author Samuel Gratzl
 *
 */
public class ProductScore implements ICompositeScore {
	private final String label;
	private final Collection<IScore> children;

	public ProductScore(String label, Collection<IScore> children) {
		this.label = label;
		this.children = children;
	}

	@Override
	public String getProviderName() {
		return null;
	}

	@Override
	public Iterator<IScore> iterator() {
		return children.iterator();
	}

	@Override
	public Collection<IScore> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	public int size() {
		return children.size();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isGroupScore() {
		for (IScore child : this)
			if (child.isGroupScore())
				return true;
		return false;
	}

	@Override
	public float getScore(ScoringElement elem) {
		IScore child = elem.getSelected(this);
		return child == null ? Float.NaN : child.getScore(elem);
	}

	@Override
	public String getRepr(ScoringElement elem) {
		IScore child = elem.getSelected(this);
		if (child == null)
			return "";
		float f = child.getScore(elem);
		return Float.isNaN(f) ? "" : String.format("%s (%s)", Formatter.formatNumber(f), elem.getLabel());
	}
}
