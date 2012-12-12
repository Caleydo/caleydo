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
package org.caleydo.view.tourguide.vendingmachine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.event.HighlightBrickEvent;
import org.caleydo.view.stratomex.event.SelectElementsEvent;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.score.IGroupScore;
import org.caleydo.view.tourguide.data.score.IScore;

/**
 * facade / adapter to {@link GLStratomex} to hide the communication details
 *
 * @author Samuel Gratzl
 *
 */
public class StratomexAdapter {
	private GLStratomex receiver;

	private TablePerspective currentPreview = null;
	private Group currentPreviewGroup = null;

	/**
	 * caches events that can't be send now but must be send in a later frame
	 */
	private Collection<AEvent> delayedEvents = new ArrayList<>();

	/**
	 * binds this adapter to a concrete stratomex instance
	 *
	 * @param receiver
	 * @return
	 */
	public boolean setStratomex(GLStratomex receiver) {
		if (this.receiver == receiver)
			return false;
		this.cleanUp();
		this.receiver = receiver;
		return true;
	}

	/**
	 *
	 */
	public void cleanUp() {
		if (currentPreview != null) {
			removeBrickColumn(currentPreview);
			this.currentPreview = null;
		}
	}

	/**
	 * see {@link #delayedEvents}
	 */
	public void triggerDelayedEvents() {
		for (AEvent event : delayedEvents)
			triggerEvent(event);
		delayedEvents.clear();
	}

	public void updatePreview(ScoringElement old, ScoringElement new_, IScore column, Collection<IScore> visibleColumns) {
		if (!hasOne())
			return;
		TablePerspective strat = new_ == null ? null : new_.getStratification();
		Group g = new_ == null ? null : new_.getGroup();

		// handle stratification changes
		if (currentPreview != null && strat != null) {
			if (!currentPreview.equals(strat)) { // not same stratification
				updateBrickColumn(currentPreview, strat);
				this.currentPreview = strat;
				this.currentPreviewGroup = null;
			}
		} else if (currentPreview != null) {
			removeBrickColumn(currentPreview);
			this.currentPreview = null;
			this.currentPreviewGroup = null;
		} else if (new_ != null) {
			createBrickColumn(strat);
			hightlightBrickColumn(strat);
			this.currentPreview = strat;
		}

		// handle group changes
		if (currentPreviewGroup != null && g != null) {
			if (!currentPreviewGroup.equals(g)) {
				unhighlightBrick(currentPreview, currentPreviewGroup);
				highlightBrick(strat, g);
				currentPreviewGroup = g;
			}
		} else if (currentPreviewGroup != null) {
			unhighlightBrick(currentPreview, currentPreviewGroup);
			currentPreviewGroup = null;
		} else if (g != null) {
			highlightBrick(strat, g);
			currentPreviewGroup = g;
		}

		// handle connection changes
		if (column instanceof IGroupScore) {
			TablePerspective otherStrat = ((IGroupScore) column).getStratification();
			Group other = ((IGroupScore) column).getGroup();
			selectBand(currentPreview, currentPreviewGroup, otherStrat, other);
		}
	}



	private void selectBand(TablePerspective aStrat, Group aGroup, TablePerspective bStrat, Group bGroup) {
		// one frame later
		delayedEvents.add(new SelectElementsEvent(aStrat, aGroup, bStrat, bGroup, receiver, this));
	}

	/**
	 * persists or and table perspective of the given
	 *
	 * @param elem
	 */
	public void addToStratomex(ScoringElement elem) {
		if (!hasOne())
			return;
		TablePerspective strat = elem.getStratification();
		if (currentPreview == strat) {
			unhightlightBrickColumn(strat);
			if (currentPreviewGroup != null)
				unhighlightBrick(currentPreview, currentPreviewGroup);
			currentPreview = null;
			currentPreviewGroup = null;
		} else {
			createBrickColumn(strat);
		}
	}

	private void createBrickColumn(TablePerspective strat) {
		AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(strat);
		event.setReceiver(receiver);
		triggerEvent(event);
	}

	private void updateBrickColumn(TablePerspective from, TablePerspective to) {
		triggerEvent(new ReplaceTablePerspectiveEvent(receiver.getID(), to, from));
	}

	private void removeBrickColumn(TablePerspective strat) {
		triggerEvent(new RemoveTablePerspectiveEvent(strat.getID(), receiver));
	}

	private void unhightlightBrickColumn(TablePerspective strat) {
		triggerEvent(new HighlightBrickEvent(strat, receiver, this, null));
	}

	private void hightlightBrickColumn(TablePerspective strat) {
		triggerEvent(new HighlightBrickEvent(strat, receiver, this, Colors.YELLOW));
	}

	private void highlightBrick(TablePerspective strat, Group group) {
		triggerEvent(new HighlightBrickEvent(strat, group, receiver, this, Colors.of(Color.ORANGE.darker())));
	}

	private void unhighlightBrick(TablePerspective strat, Group group) {
		triggerEvent(new HighlightBrickEvent(strat, group, receiver, this, null));
	}

	private void triggerEvent(AEvent event) {
		if (event == null)
			return;
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	/**
	 * @return whether this adapter is bound to a real stratomex
	 */
	public boolean hasOne() {
		return this.receiver != null;
	}

	/**
	 * @return checks if the given receiver is the currently bound stratomex
	 */
	public boolean is(IMultiTablePerspectiveBasedView receiver) {
		return this.receiver == receiver && this.receiver != null;
	}
}
