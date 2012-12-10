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
import java.util.Iterator;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * @author Samuel Gratzl
 *
 */
public class MutualExclusiveScore implements ICompositeScore, IGroupScore {
	private final JaccardIndexScore act;
	private final Collection<IScore> all;

	public MutualExclusiveScore(JaccardIndexScore act, Collection<JaccardIndexScore> all) {
		super();
		this.act = act;
		this.all = new ArrayList<IScore>(all);
	}

	@Override
	public Collection<IScore> getChildren() {
		return all;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<IScore> iterator() {
		return getChildren().iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.view.tourguide.data.score.ICompositeScore#size()
	 */
	@Override
	public int size() {
		return all.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.util.base.ILabelProvider#getLabel()
	 */
	@Override
	public String getLabel() {
		return "MT: " + act.getLabel();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.view.tourguide.data.score.IGroupScore#getGroup()
	 */
	@Override
	public Group getGroup() {
		return act.getGroup();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.view.tourguide.data.score.IStratificationScore#getStratification()
	 */
	@Override
	public TablePerspective getStratification() {
		return act.getStratification();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.util.base.ILabelProvider#getProviderName()
	 */
	@Override
	public String getProviderName() {
		return null;
	}

	@Override
	public float getScore(ScoringElement elem) {
		return ECollapseOperator.MUTUTAL_EXCLUSIVE.apply(elem, act, all);
	}

	@Override
	public EScoreType getScoreType() {
		return EScoreType.GROUP_SCORE;
	}
}
