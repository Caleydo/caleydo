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
package org.caleydo.view.tourguide.listener;

import static org.caleydo.view.tourguide.data.score.ScoreRegistry.createAdjustedRand;
import static org.caleydo.view.tourguide.data.score.ScoreRegistry.createJaccardScore;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.tourguide.data.Scores;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.event.ScoreTablePerspectiveEvent;
import org.caleydo.view.tourguide.event.ScoreTablePerspectiveEvent.EScoreType;
import org.caleydo.view.tourguide.vendingmachine.VendingMachine;

/**
 * Listener for the event {@link ScoreTablePerspectiveEvent}.
 *
 * @author Marc Streit
 *
 */
public class ScoreTablePerspectiveListener
	extends AEventListener<VendingMachine> {

	public ScoreTablePerspectiveListener(VendingMachine handler) {
		this.setHandler(handler);
	}
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ScoreTablePerspectiveEvent) {
			ScoreTablePerspectiveEvent e = (ScoreTablePerspectiveEvent) event;

			TablePerspective strat = e.getStratification();
			switch (e.getMode()) {
			case ADJUSTED_RAND:
				handler.onAddColumn(createAdjustedRand(null, strat));
				break;
			case JACCARD:
				handler.onAddColumn(createJaccardScore(null, strat, e.getGroup().getRecordGroup(), false));
				break;
			case JACCARD_MUTUAL_EXCLUSIVE:
				handler.onAddColumn(createJaccardScore(null, strat, e.getGroup().getRecordGroup(), true));
				break;
			case JACCARD_ALL:
			case JACCARD_ALL_MUTUAL_EXCLUSIVE:
				boolean mutualExclusive = e.getMode() == EScoreType.JACCARD_ALL_MUTUAL_EXCLUSIVE;
				Scores manager = Scores.get();
				CollapseScore composite = new CollapseScore(strat.getRecordPerspective().getLabel());
				for (Group group : strat.getRecordPerspective().getVirtualArray().getGroupList()) {
					composite.add(manager.addIfAbsent(createJaccardScore(null, strat, group, mutualExclusive)));
				}
				handler.onAddColumn(composite);
				break;
			}
		}
	}
}

