/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.stratomex;

import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.STRATOMEX_HIT_BAND;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.STRATOMEX_HIT_GROUP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.event.SelectElementsEvent;
import org.caleydo.view.stratomex.tourguide.event.HighlightBandEvent;
import org.caleydo.view.stratomex.tourguide.event.HighlightBrickEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdateStratificationPreviewEvent;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.model.AScoreRow;
import org.caleydo.view.tourguide.internal.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.internal.model.InhomogenousPerspectiveRow;
import org.caleydo.view.tourguide.internal.model.MaxGroupCombiner;
import org.caleydo.view.tourguide.internal.model.PathwayPerspectiveRow;
import org.caleydo.view.tourguide.internal.stratomex.event.WizardEndedEvent;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;

import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * facade / adapter to {@link GLStratomex} to hide the communication details
 *
 * @author Samuel Gratzl
 *
 */
public class StratomexAdapter {
	private GLStratomex receiver;

	/**
	 * events that has to be triggered one frame later
	 */
	private final List<AEvent> delayedEvents = new ArrayList<>();

	private TablePerspective currentPreview = null;
	private Group currentPreviewGroup = null;

	public StratomexAdapter() {
	}

	public void sendDelayedEvents() {
		for (AEvent event : delayedEvents)
			triggerEvent(event);
		delayedEvents.clear();
	}

	public void cleanUp() {
		cleanupPreview();
	}

	private void cleanupPreview() {
		if (currentPreview != null) {
			TablePerspective bak = currentPreview;
			removePreview();
			clearHighlightRows(bak.getRecordPerspective().getIdType(), bak.getDataDomain());
			unhighlightBand(null, null);
		}
	}

	@ListenTo
	private void on(WizardEndedEvent event) {
		// remove all temporary stuff
		if (currentPreview != null) {
			clearHighlightRows(currentPreview.getRecordPerspective().getIdType(), currentPreview.getDataDomain());
			unhighlightBand(null, null);
		}
		this.currentPreview = null;
		this.currentPreviewGroup = null;
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
		return true;
	}

	public void attach() {
		// TODO Auto-generated method stub

	}

	/**
	 * detach but not close from stratomex, by cleanup up temporary data but keeping them in min
	 */
	public void detach() {
		cleanupPreview();
	}

	/**
	 * whether stratomex currently showing the stratification
	 *
	 * @param stratification
	 * @return
	 */
	public boolean contains(TablePerspective stratification) {
		if (!hasOne())
			return false;

		for (TablePerspective t : receiver.getTablePerspectives())
			if (t.equals(stratification))
				return true;
		return false;
	}

	public boolean isVisible(AScoreRow row) {
		if (!hasOne())
			return false;
		for (TablePerspective col : receiver.getTablePerspectives()) {
			if (row.is(col))
				return true;
		}
		return false;
	}

	/**
	 * central point for updating the current preview in Stratomex
	 *
	 * @param old
	 * @param new_
	 * @param visibleColumns
	 *            the currently visible scores of the new_ element
	 * @param mode
	 */
	public void updatePreview(AScoreRow old, AScoreRow new_, Collection<IScore> visibleColumns,
			EDataDomainQueryMode mode, IScore sortedBy) {
		if (!hasOne())
			return;

		switch (mode) {
		case PATHWAYS:
			updatePathway((PathwayPerspectiveRow) old, (PathwayPerspectiveRow) new_, visibleColumns, sortedBy);
			break;
		case STRATIFICATIONS:
			updateTableBased((ITablePerspectiveScoreRow) old, (ITablePerspectiveScoreRow) new_, visibleColumns,
					sortedBy);
			break;
		case OTHER:
			updateNumerical((InhomogenousPerspectiveRow) old, (InhomogenousPerspectiveRow) new_, visibleColumns,
					sortedBy);
			break;
		}
	}

	private void updateNumerical(InhomogenousPerspectiveRow old, InhomogenousPerspectiveRow new_,
			Collection<IScore> visibleColumns, IScore sortedBy) {
		if (new_ != null) {
			UpdateNumericalPreviewEvent event = new UpdateNumericalPreviewEvent(new_.asTablePerspective());
			event.to(receiver.getTourguide());
			triggerEvent(event);
		}
	}

	private void updatePathway(PathwayPerspectiveRow old, PathwayPerspectiveRow new_,
			Collection<IScore> visibleColumns, IScore sortedBy) {
		if (new_ != null) {
			UpdatePathwayPreviewEvent event = new UpdatePathwayPreviewEvent(new_.getPathway());
			event.to(receiver.getTourguide());
			triggerEvent(event);
		}
	}

	private void updateTableBased(ITablePerspectiveScoreRow old, ITablePerspectiveScoreRow new_,
			Collection<IScore> visibleColumns, IScore sortedBy) {
		TablePerspective strat = new_ == null ? null : new_.asTablePerspective();
		Group group = new_ == null ? null : MaxGroupCombiner.getMax(new_, sortedBy);

		// handle stratification changes
		if (currentPreview != null && strat != null) { // update
			if (currentPreview.equals(strat)) {
				if (!Objects.equal(currentPreviewGroup, group)) {
					unhighlightBrick(currentPreview, currentPreviewGroup);
					highlightBrick(currentPreview, group, true);
					currentPreviewGroup = group;
				}
			} else { // not same stratification
				if (contains(strat)) { // part of stratomex
					unhighlightBrick(currentPreview, currentPreviewGroup);
					highlightBrick(strat, group, true);
				} else {
					updatePreview(strat, group);
				}
			}
		} else if (currentPreview != null) { // last
			removePreview();
		} else if (strat != null) { // first
			updatePreview(strat, group);
		}

		// highlight connection band
		if (strat != null) {
			highlightRows(new_, visibleColumns, group);
			unhighlightBand(null, null);
			highlightBand(new_, visibleColumns, group);
		} else if (old != null) {
			clearHighlightRows(old.getIdType(), old.getDataDomain());
			unhighlightBand(null, null);
		}
	}
	private void updatePreview(TablePerspective strat, Group group) {
		this.currentPreview = strat;
		UpdateStratificationPreviewEvent event = new UpdateStratificationPreviewEvent(strat);
		event.to(receiver.getTourguide());
		triggerEvent(event);

		if (group != null) {
			highlightBrick(strat, group, false);
		}
		currentPreviewGroup = group;
	}

	private void removePreview() {
		this.currentPreview = null;
		this.currentPreviewGroup = null;
	}

	private void clearHighlightRows(IDType idType, IDataDomain dataDomain) {
		AEvent event = new SelectElementsEvent(Collections.<Integer> emptyList(), idType, SelectionType.SELECTION)
				.to(receiver);
		event.setEventSpace(dataDomain.getDataDomainID());
		triggerEvent(event);
	}

	private void highlightRows(ITablePerspectiveScoreRow new_, Collection<IScore> visibleColumns, Group new_g) {
		Pair<Collection<Integer>, IDType> intersection = new_.getIntersection(visibleColumns, new_g);
		AEvent event = new SelectElementsEvent(intersection.getFirst(), intersection.getSecond(),
				SelectionType.SELECTION).to(receiver);
		event.setEventSpace(new_.getDataDomain().getDataDomainID());
		triggerEvent(event);
	}

	private void highlightBand(ITablePerspectiveScoreRow new_, Collection<IScore> visibleColumns, Group group) {
		IScore first = Iterables.find(visibleColumns, Predicates.instanceOf(IGroupScore.class));
		if (first == null)
			return;
		Group bg = group;

		IGroupScore s = (IGroupScore)first;
		Group a_g = s.getGroup();
		highlightBand(a_g, bg);
	}

	private void unhighlightBrick(TablePerspective strat, Group g) {
		if (g == null)
			return;
		triggerEvent(new HighlightBrickEvent(strat, g, null).to(receiver.getTourguide()));

	}

	private void highlightBrick(TablePerspective strat, Group g, boolean now) {
		if (g == null)
			return;
		AEvent event = new HighlightBrickEvent(strat, g, STRATOMEX_HIT_GROUP).to(receiver
				.getTourguide());
		if (now)
			triggerEvent(event);
		else
			triggerDelayedEvent(event);
	}

	private void unhighlightBand(Group g_a, Group g_b) {
		if (STRATOMEX_HIT_BAND == null)
			return;
		triggerEvent(new HighlightBandEvent(g_a, g_b, null).to(receiver));
	}

	private void highlightBand(Group g_a, Group g_b) {
		if (g_a == null || g_b == null || STRATOMEX_HIT_BAND == null)
			return;
		triggerEvent(new HighlightBandEvent(g_a, g_b, STRATOMEX_HIT_BAND).to(receiver));
	}

	private void triggerEvent(AEvent event) {
		if (event == null)
			return;
		event.setSender(this);
		EventPublisher.trigger(event);
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

	/**
	 * @return
	 */
	public boolean isWizardVisible() {
		return receiver != null && receiver.getTourguide().isWizardActive();
	}
}
