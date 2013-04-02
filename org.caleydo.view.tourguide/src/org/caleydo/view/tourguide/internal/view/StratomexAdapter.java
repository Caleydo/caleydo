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
package org.caleydo.view.tourguide.internal.view;

import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.STRATOMEX_SELECTED_ELEMENTS;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.STRATOMEX_TEMP_COLUMN;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.STRATOMEX_TEMP_GROUP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayRecordPerspective;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.configurer.PathwayDataConfigurer;
import org.caleydo.view.stratomex.event.AddGroupsToStratomexEvent;
import org.caleydo.view.stratomex.event.AddKaplanMaiertoStratomexEvent;
import org.caleydo.view.stratomex.event.HighlightBrickEvent;
import org.caleydo.view.stratomex.event.ReplaceKaplanMaierPerspectiveEvent;
import org.caleydo.view.stratomex.event.SelectElementsEvent;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.impl.GeneSetEnrichmentScoreFactory;
import org.caleydo.view.tourguide.impl.GeneSetEnrichmentScoreFactory.GeneSetScore;
import org.caleydo.view.tourguide.impl.LogRankMetricFactory.LogRankMetric;
import org.caleydo.view.tourguide.impl.LogRankMetricFactory.LogRankPValue;
import org.caleydo.view.tourguide.spi.compute.IComputedStratificationScore;
import org.caleydo.view.tourguide.spi.score.IScore;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

	private List<TablePerspective> currentDependentPreviews = Lists.newArrayList();

	private boolean temporaryPreview = false;

	private final SelectionType previewSelectionType;

	public StratomexAdapter() {
		// Create volatile selection type
		previewSelectionType = new SelectionType("Tour Guide preview selection type",
				STRATOMEX_SELECTED_ELEMENTS.getRGBA(), 1, true, 1);
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
			clearHighlightRows(bak.getRecordPerspective());
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
	 * @param stratification
	 * @return
	 */
	public boolean contains(TablePerspective stratification) {
		for (TablePerspective t : this.brickColumns)
			if (t.equals(stratification))
				return true;
		return false;
	}

	/**
	 * returns whether the given {@link TablePerspective} is the currently temporary preview of Stratomex
	 *
	 * @param strat
	 * @return
	 */
	public boolean isTemporaryPreviewed(TablePerspective strat) {
		return temporaryPreview && currentPreview != null && currentPreview.equals(strat);
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

	/**
	 * central point for updating the current preview in Stratomex
	 *
	 * @param old
	 * @param new_
	 * @param visibleColumns
	 *            the currently visible scores of the new_ element
	 * @param mode
	 */
	public void updatePreview(PerspectiveRow old, PerspectiveRow new_, Collection<IScore> visibleColumns,
			EDataDomainQueryMode mode) {
		if (!hasOne())
			return;

		switch (mode) {
		case GENE_SET:
			updatePathwayPreview(old, new_, visibleColumns);
			break;
		case TABLE_BASED:
			updateTableBased(old, new_, visibleColumns);
			break;
		}

	}

	/**
	 * tries to find a {@link GeneSetScore} for having an underlying table perspective as reference
	 *
	 * @param scores
	 * @return
	 */
	private static Pair<TablePerspective, Group> findReferencingGSEATablePerspective(Collection<IScore> scores) {
		for (IScore s : (scores == null ? Collections.<IScore> emptyList() : scores)) {
			if (s instanceof IComputedStratificationScore) {
				Pair<TablePerspective, Group> p = GeneSetEnrichmentScoreFactory
						.resolve(((IComputedStratificationScore) s).getAlgorithm());
				if (p != null) {
					return p;
				}
			}
		}
		return null;
	}

	private static List<Integer> findReferencingClinicialVariables(Collection<IScore> scores) {
		Set<Integer> r = Sets.newLinkedHashSet();
		for (IScore s : (scores == null ? Collections.<IScore> emptyList() : scores)) {
			if (s instanceof LogRankMetric) {
				r.add(((LogRankMetric) s).getClinicalVariable());
			} else if (s instanceof LogRankPValue)
				r.add(((LogRankPValue) s).getClinicalVariable());
		}
		return Lists.newArrayList(r);
	}

	private void updatePathwayPreview(PerspectiveRow old, PerspectiveRow new_, Collection<IScore> visibleColumns) {
		PathwayGraph pathway = new_ == null ? null : ((PathwayRecordPerspective) new_.getStratification()).getPathway();

		Pair<TablePerspective, Group> undderlyingPair = findReferencingGSEATablePerspective(visibleColumns);
		// no good column found remove old and return
		if (undderlyingPair == null) {
			if (old != null) {
				removePreview();
			}
			return;
		}

		TablePerspective underlying = undderlyingPair.getFirst();
		Group underlyingGroup = undderlyingPair.getSecond();

		// handle stratification changes
		if (currentPreview != null && pathway != null) { // update
			TablePerspective strat = asPerspective(underlying, pathway);
			if (currentPreview.equals(strat)) {
				// nothing todo
			} else { // not same stratification
				if (!temporaryPreview || contains(strat)
						|| !currentPreview.getDataDomain().equals(strat.getDataDomain())) {
					// if different data domains create new one, see #1017
					removePreview();
					createPreview(strat, underlyingGroup, new PathwayDataConfigurer());
				} else {
					updateBrickColumn(currentPreview, strat);
					this.currentPreview = strat;
					hightlightBrick(currentPreview, underlyingGroup);
					currentPreviewGroup = underlyingGroup;
				}
			}
		} else if (currentPreview != null) { // last
			removePreview();
		} else if (pathway != null) { // first
			createPreview(asPerspective(underlying, pathway), underlyingGroup, new PathwayDataConfigurer());
		}
	}

	/**
	 * creates a PathwayTablePerspective out of the given {@link PathwayGraph} using the underlying
	 * {@link TablePerspective}
	 *
	 * @param underlying
	 * @param pathway
	 * @return
	 */
	private TablePerspective asPerspective(TablePerspective underlying, PathwayGraph pathway) {
		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				PathwayDataDomain.DATA_DOMAIN_TYPE);

		for (PathwayTablePerspective p : pathwayDataDomain.getTablePerspectives()) {
			if (p.getPathway().equals(pathway) && p.getRecordPerspective().equals(underlying.getRecordPerspective())
					&& p.getDimensionPerspective().equals(underlying.getDimensionPerspective()))
				return p;
		}
		// not found create new one

		PathwayTablePerspective pathwayDimensionGroup = new PathwayTablePerspective(underlying.getDataDomain(),
				pathwayDataDomain, underlying.getRecordPerspective(), underlying.getDimensionPerspective(), pathway);

		pathwayDimensionGroup.setPrivate(true);
		pathwayDataDomain.addTablePerspective(pathwayDimensionGroup);

		return pathwayDimensionGroup;
	}

	private void updateTableBased(PerspectiveRow old, PerspectiveRow new_, Collection<IScore> visibleColumns) {
		TablePerspective strat = new_ == null ? null : new_.getPerspective();
		Group group = new_ == null ? null : new_.getGroup();

		List<Integer> clinicialVariables = findReferencingClinicialVariables(visibleColumns);

		// handle stratification changes
		if (currentPreview != null && strat != null) { // update
			if (currentPreview.equals(strat)) {
				if (!Objects.equal(currentPreviewGroup, group)) {
					unhighlightBrick(currentPreview, currentPreviewGroup);
					hightlightBrick(currentPreview, group);
					currentPreviewGroup = group;
				}
			} else { // not same stratification
				if (!temporaryPreview || contains(strat)
						|| !currentPreview.getDataDomain().equals(strat.getDataDomain())) {
					// if different data domains create new one, see #1017
					removePreview();
					createPreview(strat, group);
					createDependent(strat, clinicialVariables);
				} else {
					updateBrickColumn(currentPreview, strat);
					int update = Math.min(clinicialVariables.size(), currentDependentPreviews.size());
					for (int i = 0; i < update; ++i) {
						TablePerspective to = asPerspective(strat, clinicialVariables.get(i));
						updateKaplanMaierBrickColumn(currentDependentPreviews.get(i), to, strat);
						currentDependentPreviews.set(i, to);
					}
					List<TablePerspective> toremove = currentDependentPreviews.subList(update,
							currentDependentPreviews.size());
					for (TablePerspective dependent : toremove)
						removeBrickColumn(dependent);
					toremove.clear();

					createDependent(strat, clinicialVariables.subList(update, clinicialVariables.size()));

					this.currentPreview = strat;
					hightlightBrick(currentPreview, group);
					currentPreviewGroup = group;
				}
			}
		} else if (currentPreview != null) { // last
			removePreview();
		} else if (strat != null) { // first
			createPreview(strat, group);
			createDependent(strat, clinicialVariables);
		}

		// highlight connection band
		if (strat != null)
			hightlightRows(new_, visibleColumns);
		else if (old != null && old.getPerspective() != null) {
			clearHighlightRows(old.getStratification());
		}
	}

	private void createDependent(TablePerspective strat, Collection<Integer> clinicialVariables) {
		for (Integer clinicalVariable : clinicialVariables) {
			TablePerspective d = asPerspective(strat, clinicalVariable);
			currentDependentPreviews.add(d);
			createKaplanMaierBrickColumn(d, strat);
		}
	}

	private void createPreview(TablePerspective strat, Group group) {
		createPreview(strat, group, null);
	}

	private void createPreview(TablePerspective strat, Group group, IBrickConfigurer config) {
		this.temporaryPreview = !contains(strat);
		if (this.temporaryPreview) // create a new one if it is temporary
			createBrickColumn(strat, config);
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

		cleanupDependentPreviews();
	}

	private void cleanupDependentPreviews() {
		for (TablePerspective dependent : currentDependentPreviews)
			removeBrickColumn(dependent);
		currentDependentPreviews.clear();
	}

	private void clearHighlightRows(Perspective strat) {
		AEvent event = new SelectElementsEvent(Collections.<Integer> emptyList(), strat.getIdType(),
				this.previewSelectionType, receiver, this);
		event.setEventSpace(strat.getDataDomain().getDataDomainID());
		triggerEvent(event);
	}

	private void hightlightRows(PerspectiveRow new_, Collection<IScore> visibleColumns) {
		Pair<Collection<Integer>, IDType> intersection = new_.getIntersection(visibleColumns);
		AEvent event = new SelectElementsEvent(intersection.getFirst(), intersection.getSecond(),
				this.previewSelectionType, receiver, this);
		event.setEventSpace(new_.getDataDomain().getDataDomainID());
		triggerEvent(event);
	}

	/**
	 * persists or and table perspective of the given
	 *
	 * @param elem
	 * @param visibleColumns
	 * @param mode
	 */
	public void addToStratomex(PerspectiveRow elem, Collection<IScore> visibleColumns, EDataDomainQueryMode mode) {
		if (!hasOne())
			return;
		switch (mode) {
		case GENE_SET:
			addToStratomexGeneSet(elem, visibleColumns);
			break;
		case TABLE_BASED:
			TablePerspective strat = elem.getPerspective();
			// TODO
			if (strat == null)
				return;
			if (strat.equals(currentPreview)) { // its the preview
				temporaryPreview = false; // definitely explicit
				removePreview();
			} else if (!contains(strat)) { // add it not existing
				createBrickColumn(strat, null);
			}
		}
	}

	private void addToStratomexGeneSet(PerspectiveRow elem, Collection<IScore> visibleColumns) {
		Pair<TablePerspective, Group> undderlyingPair = findReferencingGSEATablePerspective(visibleColumns);
		if (undderlyingPair == null)
			return; // can't add no reference given

		PathwayGraph pathway = ((PathwayRecordPerspective) elem.getStratification()).getPathway();

		TablePerspective strat = asPerspective(undderlyingPair.getFirst(), pathway);
		if (strat.equals(currentPreview)) { // its the preview
			temporaryPreview = false; // definitely explicit
			removePreview();
		} else if (!contains(strat)) { // add it not existing
			createBrickColumn(strat, new PathwayDataConfigurer());
		}
	}

	private void createBrickColumn(TablePerspective strat, IBrickConfigurer config) {
		if (config != null) {
			AddGroupsToStratomexEvent event = new AddGroupsToStratomexEvent(strat);
			event.setDataConfigurer(config);
			event.setReceiver(receiver);
			triggerEvent(event);
		} else {
			AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(strat);
			event.setReceiver(receiver);
			triggerEvent(event);
		}
	}

	private void createKaplanMaierBrickColumn(TablePerspective strat, TablePerspective underlying) {
		triggerDelayedEvent(new AddKaplanMaiertoStratomexEvent(strat, underlying, receiver));
	}

	private void updateBrickColumn(TablePerspective from, TablePerspective to) {
		triggerEvent(new ReplaceTablePerspectiveEvent(receiver.getID(), to, from));
	}

	private void updateKaplanMaierBrickColumn(TablePerspective from, TablePerspective to, TablePerspective underlying) {
		if (from.getRecordPerspective().equals(to.getRecordPerspective())
				&& from.getDimensionPerspective().equals(to.getDimensionPerspective()))
			triggerEvent(new ReplaceKaplanMaierPerspectiveEvent(receiver.getID(), to, from, underlying));
		else {
			triggerEvent(new RemoveTablePerspectiveEvent(from, receiver));
			triggerDelayedEvent(new AddKaplanMaiertoStratomexEvent(to, underlying, receiver));
		}
	}

	private void removeBrickColumn(TablePerspective strat) {
		if (strat == null)
			return;
		triggerEvent(new RemoveTablePerspectiveEvent(strat, receiver));
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

	/**
	 * converts the given clinicial Variable using the underlying {@link TablePerspective}
	 *
	 * @param underlying
	 * @param clinicalVariable
	 * @return
	 */
	private static TablePerspective asPerspective(TablePerspective underlying, Integer clinicalVariable) {
		ATableBasedDataDomain dataDomain = DataDomainOracle.getClinicalDataDomain();

		Perspective dim = null;
		for (String id : dataDomain.getDimensionPerspectiveIDs()) {
			Perspective d = dataDomain.getTable().getDimensionPerspective(id);
			VirtualArray va = d.getVirtualArray();
			if (va.size() == 1 && va.get(0) == clinicalVariable) {
				dim = d;
				break;
			}
		}
		if (dim == null) { // not yet existing create a new one
			dim = new Perspective(dataDomain, dataDomain.getDimensionIDType());
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(Lists.newArrayList(clinicalVariable));
			dim.init(data);
			dim.setLabel(dataDomain.getDimensionLabel(clinicalVariable), false);

			dataDomain.getTable().registerDimensionPerspective(dim);
		}

		Perspective rec = null;
		Perspective underlyingRP = underlying.getRecordPerspective();

		for (String id : dataDomain.getRecordPerspectiveIDs()) {
			Perspective r = dataDomain.getTable().getRecordPerspective(id);
			if (r.getDataDomain().equals(underlying.getDataDomain())
					&& r.isLabelDefault() == underlyingRP.isLabelDefault()
					&& r.getLabel().equals(underlyingRP.getLabel())) {
				rec = r;
				break;
			}
		}
		if (rec == null) { // not found create a new one
			rec = dataDomain.convertForeignPerspective(underlyingRP);
			dataDomain.getTable().registerRecordPerspective(rec);
		}
		return dataDomain.getTablePerspective(rec.getPerspectiveID(), dim.getPerspectiveID(), false);
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

	public GLStratomex get() {
		return this.receiver;
	}
}
