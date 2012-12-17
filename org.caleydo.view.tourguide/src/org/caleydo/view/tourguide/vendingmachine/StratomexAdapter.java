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

import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.STRATOMEX_SELECTED_ELEMENTS;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.STRATOMEX_TEMP_COLUMN;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.STRATOMEX_TEMP_GROUP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.event.HighlightBrickEvent;
import org.caleydo.view.stratomex.event.SelectElementsEvent;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.compute.CachedIDTypeMapper;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.data.score.ICompositeScore;
import org.caleydo.view.tourguide.data.score.IGroupScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.IStratificationScore;

import com.google.common.base.Objects;

/**
 * facade / adapter to {@link GLStratomex} to hide the communication details
 *
 * @author Samuel Gratzl
 *
 */
public class StratomexAdapter {
	private GLStratomex receiver;

	private final List<AEvent> delayedEvents = new ArrayList<>();

	private List<TablePerspective> brickColumns = new ArrayList<>();

	private TablePerspective currentPreview = null;
	private Group currentPreviewGroup = null;

	private boolean temporaryPreview = false;

	private final SelectionType previewSelectionType;

	public StratomexAdapter() {
		// Create volatile selection type
		previewSelectionType = new SelectionType("Tour Guide preview selection type",
				STRATOMEX_SELECTED_ELEMENTS.getRGBA(), 1, true, true, 1);
		previewSelectionType.setManaged(false);

		triggerEvent(new SelectionTypeEvent(previewSelectionType));
	}

	public void sendDelayedEvents() {
		for (AEvent event : delayedEvents)
			triggerEvent(event);
		delayedEvents.clear();
	}

	public void cleanUp() {
		cleanupPreview();
		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(previewSelectionType);
		selectionTypeEvent.setRemove(true);
		triggerEvent(selectionTypeEvent);
	}

	private void cleanupPreview() {
		if (currentPreview != null) {
			TablePerspective bak = currentPreview;
			removePreview();
			clearHighlightRows(bak);
		}
		this.brickColumns.clear();
	}
	/**
	 * binds this adapter to a concrete stratomex instance
	 *
	 * @param receiver
	 * @return
	 */
	public boolean setStratomex(GLStratomex receiver) {
		if (this.receiver == receiver)
			return false;
		this.cleanupPreview();
		this.receiver = receiver;
		if (this.receiver != null) {
			this.brickColumns.addAll(this.receiver.getTablePerspectives());
		}
		return true;
	}

	/**
	 * @param stratification
	 * @return
	 */
	public boolean contains(TablePerspective stratification) {
		for (TablePerspective t : this.brickColumns)
			if (t.equals(stratification))
				return true;
		return false;
	}

	public void removeBrick(int tablePerspectiveID) {
		for (Iterator<TablePerspective> it = brickColumns.iterator(); it.hasNext();) {
			if (it.next().getID() == tablePerspectiveID) {
				it.remove();
				break;
			}
		}
		if (currentPreview != null && currentPreview.getID() == tablePerspectiveID) {
			currentPreview = null;
			currentPreviewGroup = null;
		}
	}

	public void addBricks(Collection<TablePerspective> tablePerspectives) {
		this.brickColumns.addAll(tablePerspectives);
	}

	public void replaceBricks(TablePerspective oldPerspective, TablePerspective newPerspective) {
		for (ListIterator<TablePerspective> it = brickColumns.listIterator(); it.hasNext();) {
			if (it.next().equals(oldPerspective)) {
				it.set(newPerspective);
				break;
			}
		}
	}

	public void updatePreview(ScoringElement old, ScoringElement new_, Collection<IScore> visibleColumns) {
		if (!hasOne())
			return;
		TablePerspective strat = new_ == null ? null : new_.getStratification();
		Group group = new_ == null ? null : new_.getGroup();

		// handle stratification changes
		if (currentPreview != null && strat != null) { // update
			if (currentPreview.equals(strat)) {
				if (!Objects.equal(currentPreviewGroup, group)) {
					unhighlightBrick(currentPreview, currentPreviewGroup);
					hightlightBrick(currentPreview, group);
					currentPreviewGroup = group;
				}
			} else { // not same stratification
				if (!temporaryPreview || contains(strat)) {
					removePreview();
					createPreview(strat, group);
				} else {
					updatePreview(currentPreview, strat);
					this.currentPreview = strat;
					hightlightBrick(currentPreview, group);
					currentPreviewGroup = group;
				}
			}
		} else if (currentPreview != null) { // last
			removePreview();
		} else if (new_ != null) { // first
			createPreview(strat, group);
		}

		// highlight connection band
		if (new_ != null)
			hightlightRows(new_, visibleColumns);
		else if (old != null) {
			clearHighlightRows(old.getStratification());
		}
	}

	private void createPreview(TablePerspective strat, Group group) {
		this.temporaryPreview = !contains(strat);
		if (this.temporaryPreview) // create a new one if it is temporary
			createBrickColumn(strat);
		this.currentPreview = strat;
		if (group != null) {
			hightlightBrick(strat, group);
			currentPreviewGroup = group;
		} else
			hightlightBrickColumn(strat);
	}

	private void removePreview() {
		if (temporaryPreview) // if it is just temporary remove it
			removeBrickColumn(currentPreview);
		else {
			// otherwise just lowlight it
			if (currentPreviewGroup != null)
				unhighlightBrick(currentPreview, currentPreviewGroup);
			else
				unhighlightBrickColumn(currentPreview);
		}
		this.currentPreview = null;
		this.currentPreviewGroup = null;
	}

	private void clearHighlightRows(TablePerspective strat) {
		AEvent event = new SelectElementsEvent(Collections.<Integer> emptyList(), strat.getRecordPerspective()
				.getIdType(),
				this.previewSelectionType, receiver, this);
		event.setDataDomainID(strat.getDataDomain().getDataDomainID());
		triggerEvent(event);
	}

	private void hightlightRows(ScoringElement new_, Collection<IScore> visibleColumns) {
		TablePerspective strat = new_.getStratification();
		Group group = new_.getGroup();

		//select nearest score
		Collection<IStratificationScore> relevant = filterRelevantColumns(new_, visibleColumns);

		IDType target = strat.getRecordPerspective().getIdType();
		for(IStratificationScore elem : relevant) {
			IDType type = elem.getStratification().getRecordPerspective().getIdType();
			if (!target.getIDCategory().equals(type.getIDCategory()))
				continue;
			if (!target.equals(type))
				target = target.getIDCategory().getPrimaryMappingType();
		}

		CachedIDTypeMapper mapper = new CachedIDTypeMapper();

		//compute the intersection of all
		IDType source = strat.getRecordPerspective().getIdType();

		RecordVirtualArray va = strat.getRecordPerspective().getVirtualArray();
		Collection<Integer> ids = (group == null )? va.getIDs(): va.getIDsOfGroup(group.getGroupIndex());

		if (!relevant.isEmpty()) {
			Collection<Integer> intersection = new ArrayList<>(mapper.get(source, target).apply(ids));
			for (IStratificationScore score : relevant) {
				va = score.getStratification().getRecordPerspective().getVirtualArray();
				Group g = (score instanceof IGroupScore) ? ((IGroupScore) score).getGroup() : null;
				ids = (g == null) ? va.getIDs() : va.getIDsOfGroup(g.getGroupIndex());
				Set<Integer> mapped = mapper.get(score.getStratification().getRecordPerspective().getIdType(), target)
						.apply(ids);
				for (Iterator<Integer> it = intersection.iterator(); it.hasNext();) {
					if (!mapped.contains(it.next())) // not part of
						it.remove();
				}
			}
			ids = intersection;
		}

		AEvent event = new SelectElementsEvent(ids, target, this.previewSelectionType, receiver, this);
		event.setDataDomainID(strat.getDataDomain().getDataDomainID());
		triggerEvent(event);
	}

	private Set<IStratificationScore> filterRelevantColumns(ScoringElement new_,
			Collection<IScore> columns) {
		Set<IStratificationScore> relevant = new HashSet<>();
		for(IScore score : columns) {
			if (score instanceof CollapseScore)
				score = new_.getSelected((CollapseScore)score);
			if (score instanceof IStratificationScore)
				relevant.add((IStratificationScore)score);
			if (score instanceof ICompositeScore) {
				relevant.addAll(filterRelevantColumns(new_, ((ICompositeScore) score).getChildren()));
			}
		}
		return relevant;
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
		if (strat.equals(currentPreview)) { // its the preview
			temporaryPreview = false; // definitely explicit
			removePreview();
		} else if (!contains(strat)) { // add it not existing
			createBrickColumn(strat);
		}
	}

	private void createBrickColumn(TablePerspective strat) {
		AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(strat);
		event.setReceiver(receiver);
		triggerEvent(event);
	}

	private void updatePreview(TablePerspective from, TablePerspective to) {
		triggerEvent(new ReplaceTablePerspectiveEvent(receiver.getID(), to, from));
	}

	private void removeBrickColumn(TablePerspective strat) {
		if (strat == null)
			return;
		triggerEvent(new RemoveTablePerspectiveEvent(strat.getID(), receiver));
	}

	private void unhighlightBrickColumn(TablePerspective strat) {
		if (strat == null)
			return;
		triggerEvent(new HighlightBrickEvent(strat, receiver, this, null));
	}

	private void unhighlightBrick(TablePerspective strat, Group g) {
		if (g == null)
			return;
		triggerDelayedEvent(new HighlightBrickEvent(strat, g, receiver, this, null));
	}

	private void hightlightBrickColumn(TablePerspective strat) {
		if (strat == null)
			return;
		triggerEvent(new HighlightBrickEvent(strat, receiver, this, STRATOMEX_TEMP_COLUMN));
	}

	private void hightlightBrick(TablePerspective strat, Group g) {
		if (g == null)
			return;
		triggerDelayedEvent(new HighlightBrickEvent(strat, g, receiver, this, STRATOMEX_TEMP_GROUP));
	}

	private void triggerEvent(AEvent event) {
		if (event == null)
			return;
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	private void triggerDelayedEvent(AEvent event) {
		if (event == null)
			return;
		delayedEvents.add(event);
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
	public boolean is(ITablePerspectiveBasedView receiver) {
		return this.receiver == receiver && this.receiver != null;
	}

	/**
	 * @return checks if the given receiver is the currently bound stratomex
	 */
	public boolean is(Integer receiverID) {
		return this.receiver != null && this.receiver.getID() == receiverID;
	}
}
