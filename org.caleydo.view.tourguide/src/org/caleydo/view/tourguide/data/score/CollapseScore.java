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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * special kind of composite score, which doesn't combine the scores but triggers that the rows will be multiplied to
 * show all values of the children in the same column
 *
 * @author Samuel Gratzl
 *
 */
public class CollapseScore extends DefaultLabelProvider implements ICompositeScore {
	private final ECollapseOperator op;
	private final Collection<IScore> children;

	public CollapseScore(String label) {
		this(label, ECollapseOperator.NONE, Collections.<IScore> emptyList());
	}

	public CollapseScore(String label, ECollapseOperator op, Collection<IScore> children) {
		super(label);
		this.op = op;
		this.children = new ArrayList<>();
		for (IScore child : children)
			add(child);
	}

	public void add(IScore child) {
		if (child instanceof CollapseScore)
			this.children.addAll(((CollapseScore) child).getChildren());
		else
			this.children.add(child);
	}

	@Override
	public Iterator<IScore> iterator() {
		return children.iterator();
	}

	@Override
	public Collection<IScore> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	@Override
	public int size() {
		return children.size();
	}

	public ECollapseOperator getOp() {
		return op;
	}

	@Override
	public final EScoreType getScoreType() {
		int maxOrdinal = 0;
		for (IScore child : this)
			maxOrdinal = Math.max(maxOrdinal, child.getScoreType().ordinal());
		return EScoreType.values()[maxOrdinal];
	}

	@Override
	public float getScore(ScoringElement elem) {
		IScore current = elem.getSelected(this);
		return current == null ? Float.NaN : op.apply(elem, current, children);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollapseScore other = (CollapseScore) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (op != other.op)
			return false;
		return true;
	}


}
