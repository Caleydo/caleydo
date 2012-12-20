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
package org.caleydo.view.tourguide.impl;

import java.util.Set;

import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.api.score.EScoreType;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;
import org.caleydo.view.tourguide.spi.IMetricFactory;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public class SizeMetricFactory implements IMetricFactory {
	@Override
	public void addCreateMetricItems(ContextMenuCreator creator, Set<IScore> visible, ScoreQueryUI sender) {
		if (!visible.contains(SizeMetric.get()))
			creator.addContextMenuItem(new GenericContextMenuItem("Add Size Metric", new AddScoreColumnEvent(SizeMetric
					.get(), sender)));
	}
}

/**
 * simple metric returning the number of records i.e. size of the stratification / cluster
 *
 * @author Samuel Gratzl
 *
 */
class SizeMetric implements IScore {
	private static SizeMetric instance = new SizeMetric();

	public static SizeMetric get() {
		return instance;
	}

	private SizeMetric() {

	}

	@Override
	public String getLabel() {
		return "Size";
	}

	@Override
	public String getAbbrevation() {
		return "SI";
	}

	@Override
	public String getProviderName() {
		return null;
	}

	@Override
	public EScoreType getScoreType() {
		return EScoreType.RANK;
	}

	@Override
	public float getScore(ScoringElement elem) {
		if (elem.getGroup() == null) {
			return elem.getStratification().getNrRecords();
		} else
			return elem.getGroup().getSize();
	}
}