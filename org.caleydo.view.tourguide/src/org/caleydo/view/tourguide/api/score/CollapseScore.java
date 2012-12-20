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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.internal.score.Scores;
import org.caleydo.view.tourguide.spi.compute.ICompositeScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * special kind of composite score, which doesn't combine the scores but triggers that the rows will be multiplied to
 * show all values of the children in the same column
 *
 * @author Samuel Gratzl
 *
 */
public class CollapseScore extends DefaultLabelProvider implements ICompositeScore, IStratificationScore {
	private final Collection<IScore> children;

	public CollapseScore(String label) {
		this(label, Collections.<IScore> emptyList());
	}

	public CollapseScore(String label, Collection<IScore> children) {
		super(label);
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
	public void mapChildren(final Function<IScore, IScore> f) {
		Collection<IScore> new_ = Lists.newArrayList(Collections2.transform(children, f));
		this.children.clear();
		this.children.addAll(new_);
	}

	@Override
	public int size() {
		return children.size();
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
		return current == null ? Float.NaN : current.getScore(elem);
	}

	/**
	 * returns the stratification, which have all children in common if there is one, otherwise null
	 *
	 * @return
	 */
	@Override
	public TablePerspective getStratification() {
		TablePerspective r = null;
		for (IScore child : Scores.flatten(this)) {
			TablePerspective c = null;
			if (child instanceof IStratificationScore) {
				c = ((IStratificationScore) child).getStratification();
			} else { // not a stratifiation score
				return null; // no common
			}
			if (Objects.equal(r, c))
				continue;
			if (r == null) // first
				r = c;
			else
				return null; // not a single stratification in the whole list
		}
		return r;
	}

	@Override
	public String getAbbrevation() {
		Set<String> abbrs = Sets.newHashSet();
		for (IScore child : this)
			abbrs.add(child.getAbbrevation());
		if (abbrs.size() == 1)
			return abbrs.iterator().next();
		return "CL";
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
		return true;
	}


}
