/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.t;

import static org.caleydo.view.tourguide.stratomex.StratomexRenderStyle.stratomexHitBand;
import static org.caleydo.view.tourguide.stratomex.StratomexRenderStyle.stratomexHitGroup;

import java.lang.ref.WeakReference;
import java.net.URL;
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
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.RcpGLStratomexView;
import org.caleydo.view.stratomex.addin.IStratomeXAddIn;
import org.caleydo.view.stratomex.event.HighlightBandEvent;
import org.caleydo.view.stratomex.event.SelectElementsEvent;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.stratomex.StratomexRenderStyle;
import org.caleydo.view.tourguide.stratomex.event.HighlightBrickEvent;
import org.caleydo.view.tourguide.stratomex.event.WizardEndedEvent;
import org.caleydo.view.tourguide.stratomex.s.TourGuideAddin;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * facade / adapter to {@link GLStratomex} to hide the communication details
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AStratomexAdapter implements ITourGuideAdapter {

	private final ITourGuideDataMode mode;

	/**
	 * events that has to be triggered one frame later
	 */
	private final List<AEvent> delayedEvents = new ArrayList<>();

	private String currentPreviewRowID = null;
	protected TablePerspective currentPreview = null;
	protected Group currentPreviewGroup = null;

	private ITourGuideView tourGuide;
	private WeakReference<GLStratomex> receiver = new WeakReference<GLStratomex>(null);

	public AStratomexAdapter(ITourGuideDataMode mode) {
		this.mode = mode;
	}

	@Override
	public final String getLabel() {
		GLStratomex r = receiver.get();
		return r == null ? "" : r.getLabel();
	}

	public void sendDelayedEvents() {
		for (AEvent event : delayedEvents)
			triggerEvent(event);
		delayedEvents.clear();
	}

	@Override
	public void setup(ITourGuideView vis, GLElementContainer lineUp) {
		this.tourGuide = vis;
	}

	@Override
	public void cleanup() {
		cleanupPreview();
	}

	@Override
	public boolean canShowPreviews() {
		return isWizardVisible();
	}

	@Override
	public void onRowClick(RankTableModel table, PickingMode pickingMode, AScoreRow row, boolean isSelected,
			IGLElementContext context) {

	}

	protected TourGuideAddin getAddin() {
		GLStratomex stratomex = receiver.get();
		if (stratomex == null)
			return null;
		for (IStratomeXAddIn addin : stratomex.getAddins()) {
			if (addin instanceof TourGuideAddin)
				return (TourGuideAddin) addin;
		}
		return null;
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
	@Override
	public final void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleColumns, IScore sortedBy) {
		if (!hasOne())
			return;

		updatePreview(old, new_, visibleColumns, sortedBy);
		currentPreviewRowID = new_ == null ? null : new_.getPersistentID();
	}

	protected abstract void updatePreview(AScoreRow old, AScoreRow new_, Collection<IScore> visibleColumns,
			IScore sortedBy);

	protected void removePreview() {
		this.currentPreview = null;
		this.currentPreviewGroup = null;
		currentPreviewRowID = null;

	}

	protected void clearHighlightRows(IDType idType, IDataDomain dataDomain) {
		AEvent event = new SelectElementsEvent(Collections.<Integer> emptyList(), idType, SelectionType.SELECTION)
				.to(receiver);
		event.setEventSpace(dataDomain.getDataDomainID());
		triggerEvent(event);
	}

	protected void highlightRows(ITablePerspectiveScoreRow new_, Collection<IScore> visibleColumns, Group new_g) {
		Pair<Collection<Integer>, IDType> intersection = new_.getIntersection(visibleColumns, new_g);
		AEvent event = new SelectElementsEvent(intersection.getFirst(), intersection.getSecond(),
				SelectionType.SELECTION).to(receiver);
		event.setEventSpace(new_.getDataDomain().getDataDomainID());
		triggerEvent(event);
	}

	protected void highlightBand(ITablePerspectiveScoreRow new_, Collection<IScore> visibleColumns, Group group) {
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

	protected void unhighlightBrick(TablePerspective strat, Group g) {
		if (!hasOne())
			return;
		triggerEvent(new HighlightBrickEvent(strat, g, null).to(getAddin()));
	}

	protected void highlightBrick(TablePerspective strat, Group g, boolean now) {
		if (!hasOne())
			return;
		AEvent event = new HighlightBrickEvent(strat, g, stratomexHitGroup()).to(getAddin());
		if (now)
			triggerEvent(event);
		else
			triggerDelayedEvent(event);
	}

	protected void unhighlightBand(Group g_a, Group g_b) {
		if (stratomexHitBand() == null)
			return;
		triggerEvent(new HighlightBandEvent(g_a, g_b, null).to(receiver));
	}

	private void highlightBand(Group g_a, Group g_b) {
		if (g_a == null || g_b == null || stratomexHitBand() == null)
			return;
		triggerEvent(new HighlightBandEvent(g_a, g_b, stratomexHitBand()).to(receiver));
	}

	protected void triggerEvent(AEvent event) {
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
	public boolean isPreviewing(AScoreRow row) {
		return isPreviewed(row);
	}

	@Override
	public void preDisplay() {
		sendDelayedEvents();
	}

	@Override
	public boolean isRepresenting(IWorkbenchPart part, boolean isBoundTo) {
		if (!(part instanceof RcpGLStratomexView))
			return false;
		return !isBoundTo || this.is(((RcpGLStratomexView) part).getView());
	}

	@Override
	public URL getIcon() {
		return StratomexRenderStyle.ICON_STRATOMEX;
	}

	@Override
	public void addDefaultColumns(RankTableModel table) {
		mode.addDefaultColumns(table);
	}

	@Override
	public ITourGuideDataMode asMode() {
		return mode;
	}

	@Override
	public void bindTo(IViewPart part) {
		if (!(part instanceof RcpGLStratomexView))
			part = null;
		this.receiver = new WeakReference<GLStratomex>(part == null ? null : ((RcpGLStratomexView) part).getView());
		if (tourGuide != null) {
			tourGuide.clearSelection();
			tourGuide.removeAllSimpleFilter();
			tourGuide.updateBound2ViewState();
		}
	}

	@Override
	public boolean ignoreActive(IViewPart part) {
		return false;
	}

	@Override
	public boolean isBound2View() {
		return this.receiver.get() != null;
	}

	@Override
	public boolean filterBoundView(ADataDomainQuery query) {
		return true;
	}
}
