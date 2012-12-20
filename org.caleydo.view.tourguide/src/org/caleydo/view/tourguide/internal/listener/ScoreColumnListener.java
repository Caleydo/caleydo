/*******************************************************************************
 * 	Caleydo - visualization for molecular biology - http://caleydo.org
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
package org.caleydo.view.tourguide.internal.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.RemoveScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.RenameScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.ToggleNaNFilterScoreColumnEvent;
import org.caleydo.view.tourguide.internal.view.VendingMachine;

public class ScoreColumnListener extends AEventListener<VendingMachine> {

	public ScoreColumnListener(VendingMachine m) {
		setHandler(m);
	}
	@Override
	public void handleEvent(AEvent event) {
		if (handler.getScoreQueryUI() != event.getSender() && event.getSender() != null)
			return;
		if (event instanceof AddScoreColumnEvent) {
			handler.onAddColumn(((AddScoreColumnEvent) event).getScores());
		} else if (event instanceof RemoveScoreColumnEvent) {
			RemoveScoreColumnEvent e = (RemoveScoreColumnEvent) event;
			handler.onRemoveColumn(e.getScore(), e.isRemove());
		} else if (event instanceof RenameScoreColumnEvent) {
			RenameScoreColumnEvent e = (RenameScoreColumnEvent) event;
			handler.onRename(e.getColumn());
		} else if (event instanceof ToggleNaNFilterScoreColumnEvent) {
			ToggleNaNFilterScoreColumnEvent e = (ToggleNaNFilterScoreColumnEvent) event;
			handler.onToggleNaNFilter(e.getScore());
		}
	}
}

