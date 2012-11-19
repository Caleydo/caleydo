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

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AGroupScore implements IScore {
	protected TablePerspective stratification;
	protected Group group;
	protected Map<Integer, Float> scores = new HashMap<>();

	public AGroupScore() {

	}

	public AGroupScore(TablePerspective stratification, Group group) {
		this.stratification = stratification;
		this.group = group;
	}

	protected boolean contains(TablePerspective perspective, Group elem) {
		// have the value or it the same stratification
		return scores.containsKey(elem.getID()) || (perspective.equals(stratification));
	}


	protected void put(Group elem, float value) {
		scores.put(elem.getID(), value);
	}

	@Override
	public String getLabel() {
		return stratification.getLabel() + ": " + group.getLabel();
	}

	@Override
	public String getProviderName() {
		return stratification.getLabel();
	}

	/**
	 * @return the stratification
	 */
	public TablePerspective getStratification() {
		return stratification;
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	@Override
	public final boolean isGroupScore() {
		return true;
	}

	@Override
	public float getScore(ScoringElement elem) {
		if (elem.getGroup() == null)
			return Float.NaN;
		Float f = scores.get(elem.getGroup().getID());
		return f == null ? Float.NaN : f.floatValue();
	}

	@Override
	public String getRepr(ScoringElement elem) {
		float f = getScore(elem);
		return Float.isNaN(f) ? "" : Formatter.formatNumber(f);
	}



}