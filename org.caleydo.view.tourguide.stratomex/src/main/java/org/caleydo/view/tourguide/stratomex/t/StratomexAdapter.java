/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.t;

import static org.caleydo.view.tourguide.stratomex.StratomexRenderStyle.stratomexHitBand;
import static org.caleydo.view.tourguide.stratomex.StratomexRenderStyle.stratomexHitGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.RcpGLStratomexView;
import org.caleydo.view.stratomex.addin.IStratomeXAddIn;
import org.caleydo.view.stratomex.event.HighlightBandEvent;
import org.caleydo.view.stratomex.event.SelectElementsEvent;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.model.InhomogenousPerspectiveRow;
import org.caleydo.view.tourguide.api.model.MaxGroupCombiner;
import org.caleydo.view.tourguide.api.model.PathwayPerspectiveRow;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapter;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.stratomex.StratomexRenderStyle;
import org.caleydo.view.tourguide.stratomex.event.HighlightBrickEvent;
import org.caleydo.view.tourguide.stratomex.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.tourguide.stratomex.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.tourguide.stratomex.event.UpdateStratificationPreviewEvent;
import org.caleydo.view.tourguide.stratomex.event.WizardEndedEvent;
import org.caleydo.view.tourguide.stratomex.s.TourGuideAddin;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.eclipse.ui.IWorkbenchPart;

/**
 * facade / adapter to {@link GLStratomex} to hide the communication details
 *
 * @author Samuel Gratzl
 *
 */
public class StratomexAdapter implements IViewAdapter {
	private WeakReference<GLStratomex> receiver;

	/**
	 * events that has to be triggered one frame later
	 */
	private final List<AEvent> delayedEvents = new ArrayList<>();

	private String currentPreviewRowID = null;
	private TablePerspective currentPreview = null;
	private Group currentPreviewGroup = null;

	private final ITourGuideView tourGuide;

	public StratomexAdapter(ITourGuideView vis) {
		this.tourGuide = vis;
	}

	public void sendDelayedEvents() {
		for (AEvent event : delayedEvents)
			triggerEvent(event);
		delayedEvents.clear();
	}

	@Override
	public void cleanUp() {
		cleanupPreview();
	}

	@Override
	public boolean canShowPreviews() {
		return isWizardVisible();
	}

	private TourGuideAddin getAddin() {
		GLStratomex stratomex = receiver.get();
		if (stratomex == null)
			return null;
		for (IStratomeXAddIn addin : stratomex.getAddins()) {
			if (addin instanceof TourGuideAddin)
				return (TourGuideAddin) addin;
		}
		return null;
	}

	@Override
	public void renderRowBackground(GLGraphics g, float x, float y, float w, float h, boolean even, AScoreRow row,
			boolean selected) {
		if (selected) {
			g.color(StratomexRenderStyle.colorSelectedRow());
			g.incZ();
			g.fillRect(x, y, w, h);
			if (isPreviewing(row)) {
				StratomexRenderStyle.COLOR_PREVIEW_BORDER_ROW.set(g.gl);
				g.drawLine(x, y, x + w, y);
				g.drawLine(x, y + h, x + w, y + h);
				StratomexRenderStyle.COLOR_PREVIEW_BORDER_ROW.clear(g.gl);
			} else {
				g.color(RenderStyle.COLOR_SELECTED_BORDER);
				g.drawLine(x, y, x + w, y);
				g.drawLine(x, y + h, x + w, y + h);
			}
			g.decZ();
		} else if (isVisible(row)) {
			g.color(StratomexRenderStyle.COLOR_STRATOMEX_ROW);
			g.fillRect(x, y, w, h);
		} else if (!even) {
			g.color(RenderStyle.COLOR_BACKGROUND_EVEN);
			g.fillRect(x, y, w, h);
		}
	}

	@ListenTo
	private void onWizardEnded(WizardEndedEvent event) {
		tourGuide.removeLeadingScoreColumns();
		tourGuide.clearSelection();
	}

	private void cleanupPreview() {
		currentPreviewRowID = null;
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
		currentPreviewRowID = null;

	}

	/**
	 * binds this adapter to a concrete stratomex instance
	 *
	 * @param receiver
	 * @return
	 */
	public boolean setStratomex(GLStratomex receiver) {
		if (this.receiver != null && this.receiver.get() == receiver)
			return false;
		this.cleanupPreview();
		this.receiver = new WeakReference<GLStratomex>(receiver);
		return true;
	}

	@Override
	public void attach() {

	}

	/**
	 * detach but not close from stratomex, by cleanup up temporary data but keeping them in min
	 */
	@Override
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

		for (TablePerspective t : receiver.get().getTablePerspectives())
			if (t.equals(stratification))
				return true;
		return false;
	}

	@Override
	public boolean isVisible(AScoreRow row) {
		if (!hasOne())
			return false;
		for (TablePerspective col : receiver.get().getTablePerspectives()) {
			if (row.is(col))
				return true;
		}
		return false;
	}

	public boolean isPreviewed(AScoreRow row) {
		if (!hasOne())
			return false;
		return row.getPersistentID().equals(currentPreviewRowID);
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
		currentPreviewRowID = new_ == null ? null : new_.getPersistentID();
	}

	private void updateNumerical(InhomogenousPerspectiveRow old, InhomogenousPerspectiveRow new_,
			Collection<IScore> visibleColumns, IScore sortedBy) {
		if (new_ != null && hasOne()) {
			UpdateNumericalPreviewEvent event = new UpdateNumericalPreviewEvent(new_.asTablePerspective());
			event.to(getAddin());
			triggerEvent(event);
		}
	}

	private void updatePathway(PathwayPerspectiveRow old, PathwayPerspectiveRow new_,
			Collection<IScore> visibleColumns, IScore sortedBy) {
		if (new_ != null && hasOne()) {
			UpdatePathwayPreviewEvent event = new UpdatePathwayPreviewEvent(new_.getPathway());
			event.to(getAddin());
			triggerEvent(event);
		}
	}

	private void updateTableBased(ITablePerspectiveScoreRow old, ITablePerspectiveScoreRow new_,
			Collection<IScore> visibleColumns, IScore sortedBy) {
		TablePerspective strat = new_ == null ? null : new_.asTablePerspective();
		Group group = new_ == null ? null : MaxGroupCombiner.select(new_, sortedBy);

		// handle stratification changes
		if (currentPreview != null && strat != null) { // update
			if (currentPreview.equals(strat)) {
				if (!Objects.equals(currentPreviewGroup, group)) {
					unhighlightBrick(currentPreview, currentPreviewGroup);
					highlightBrick(currentPreview, group, true);
					currentPreviewGroup = group;
				}
			} else { // not same stratification
				unhighlightBrick(currentPreview, currentPreviewGroup);
				if (contains(strat)) { // part of stratomex
					highlightBrick(strat, group, true);
				} else {
					updatePreview(strat, group, true);
				}
			}
		} else if (currentPreview != null) { // last
			removePreview();
		} else if (strat != null) { // first
			updatePreview(strat, group, true);
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

	private void updatePreview(TablePerspective strat, Group group, boolean highlightBrick) {
		if (!hasOne())
			return;
		this.currentPreview = strat;
		UpdateStratificationPreviewEvent event = new UpdateStratificationPreviewEvent(strat);
		event.to(getAddin());
		triggerEvent(event);

		if (group != null || highlightBrick)
			highlightBrick(strat, group, false);
		currentPreviewGroup = group;
	}

	private void removePreview() {
		this.currentPreview = null;
		this.currentPreviewGroup = null;
		currentPreviewRowID = null;

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
		IGroupScore s = null;
		for (IScore score : visibleColumns)
			if (score instanceof IGroupScore) {
				s = (IGroupScore) score;
				break;
			}
		if (s == null)
			return;
		Group bg = group;

		Group a_g = s.getGroup();
		highlightBand(a_g, bg);
	}

	private void unhighlightBrick(TablePerspective strat, Group g) {
		if (!hasOne())
			return;
		triggerEvent(new HighlightBrickEvent(strat, g, null).to(getAddin()));
	}
	private void highlightBrick(TablePerspective strat, Group g, boolean now) {
		if (!hasOne())
			return;
		AEvent event = new HighlightBrickEvent(strat, g, stratomexHitGroup()).to(getAddin());
		if (now)
			triggerEvent(event);
		else
			triggerDelayedEvent(event);
	}

	private void unhighlightBand(Group g_a, Group g_b) {
		if (stratomexHitBand() == null)
			return;
		triggerEvent(new HighlightBandEvent(g_a, g_b, null).to(receiver));
	}

	private void highlightBand(Group g_a, Group g_b) {
		if (g_a == null || g_b == null || stratomexHitBand() == null)
			return;
		triggerEvent(new HighlightBandEvent(g_a, g_b, stratomexHitBand()).to(receiver));
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
		return this.receiver != null && this.receiver.get() != null;
	}

	/**
	 * @return checks if the given receiver is the currently bound stratomex
	 */
	public boolean is(ITablePerspectiveBasedView receiver) {
		return this.receiver != null && this.receiver.get() == receiver;
	}

	/**
	 * @return checks if the given receiver is the currently bound stratomex
	 */
	public boolean is(Integer receiverID) {
		return hasOne() && this.receiver.get().getID() == receiverID;
	}

	/**
	 * @return
	 */
	public boolean isWizardVisible() {
		return hasOne() && this.getAddin().isWizardActive();
	}

	@Override
	public void setup() {

	}

	@Override
	public boolean isPreviewing(AScoreRow row) {
		return isPreviewed(row);
	}

	@Override
	public void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleScores, EDataDomainQueryMode mode,
			IScore sortedByScore) {
		updatePreview(old, new_, visibleScores, mode, sortedByScore);
	}

	@Override
	public void preDisplay() {
		sendDelayedEvents();
	}

	@Override
	public boolean isRepresenting(IWorkbenchPart part) {
		if (!(part instanceof RcpGLStratomexView))
			return false;
		return this.is(((RcpGLStratomexView) part).getView());
	}
}
