/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.ESampleMappingMode;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;
import org.caleydo.datadomain.pathway.listener.SampleMappingModeEvent;

/**
 * Stores the a single mapping perspective for single table perspective based pathway augmentations and also multiple
 * table perspectives for multi table perspective based augmentations. Handles Events that update these table
 * perspective and caches {@link Average}s of the mapped samples for each table perspective for all pathway vertices of
 * a {@link IPathwayRepresentation}.
 *
 * @author Christian
 *
 */
public class PathwayDataMappingHandler implements IEventBasedSelectionManagerUser {

	/**
	 * The table perspective used for single table perspective augmentations.
	 */
	protected TablePerspective mappingPerspective;
	/**
	 * The currently used sampling mode (all samples of a perspective, or only selected samples)
	 */
	protected ESampleMappingMode sampleMappingMode = ESampleMappingMode.ALL;
	protected String eventSpace;
	protected EventBasedSelectionManager sampleSelectionManager;
	protected IPathwayRepresentation pathwayRepresentation;
	protected Map<PathwayVertexRep, Average> mappingPerspectiveAverages = new HashMap<>();
	protected Map<TablePerspective, Map<PathwayVertexRep, Average>> addedTablePerspectiveAverages = new HashMap<>();

	protected List<IPathwayMappingListener> listeners = new ArrayList<>();

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onMapTablePerspective(PathwayMappingEvent event) {
		// if (event.getEventSpace() != null && event.getEventSpace().equals(eventSpace)) {
		setMappingPerspective(event.getTablePerspective());
		// }
	}

	@ListenTo
	public void onUpdateColorMapping(UpdateColorMappingEvent event) {
		notifyListeners();
	}

	@ListenTo
	public void onSampleMappingModeChanged(SampleMappingModeEvent event) {
		// if (event.getEventSpace().equals(eventSpace)) {
		setSampleMappingMode(event.getSampleMappingMode());
		// }
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onAddTablePerspective(AddTablePerspectivesEvent event) {
		// if (event.getEventSpace() != null && event.getEventSpace().equals(eventSpace)) {
		for (TablePerspective tp : event.getTablePerspectives()) {
			if (tp.getDataDomain().hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
				addTablePerspective(tp);
			}
		}
		// }
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onRemoveTablePerspective(RemoveTablePerspectiveEvent event) {
		// if (event.getEventSpace() != null && event.getEventSpace().equals(eventSpace)) {
		removeTablePerspective(event.getTablePerspective());
		// }
	}

	protected void addTablePerspective(TablePerspective tablePerspective) {
		if (addedTablePerspectiveAverages.keySet().contains(tablePerspective))
			return;
		Map<PathwayVertexRep, Average> averages = new HashMap<>();
		addedTablePerspectiveAverages.put(tablePerspective, averages);
		updatePerspectiveAverages(tablePerspective, averages);
		notifyListeners();
	}

	protected void removeTablePerspective(TablePerspective tablePerspective) {
		if (addedTablePerspectiveAverages.remove(tablePerspective) != null)
			notifyListeners();
	}

	protected void updateAllAverages() {
		updatePerspectiveAverages(mappingPerspective, mappingPerspectiveAverages);
		for (Entry<TablePerspective, Map<PathwayVertexRep, Average>> entry : addedTablePerspectiveAverages.entrySet()) {
			updatePerspectiveAverages(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @param sampleMappingMode
	 *            setter, see {@link sampleMappingMode}
	 */
	public void setSampleMappingMode(ESampleMappingMode sampleMappingMode) {
		this.sampleMappingMode = sampleMappingMode;
		updateAllAverages();
		notifyListeners();
	}

	/**
	 * @param mappingPerspective
	 *            setter, see {@link mappingPerspective}
	 */
	public void setMappingPerspective(TablePerspective mappingPerspective) {
		if (this.mappingPerspective == mappingPerspective)
			return;

		if (mappingPerspective != null) {
			if (!mappingPerspective.getDataDomain().getDataDomainType().equals(GeneticDataDomain.DATA_DOMAIN_TYPE))
				throw new IllegalArgumentException(
						"Non-Genetic table perspectives are not supported by this pathway augmentation!");
			GeneticDataDomain dataDomain = (GeneticDataDomain) mappingPerspective.getDataDomain();
			IDType primarySampleMappingType = dataDomain.getSampleIDType().getIDCategory().getPrimaryMappingType();

			if (sampleSelectionManager == null || primarySampleMappingType != sampleSelectionManager.getIDType()) {
				if (sampleSelectionManager != null) {
					sampleSelectionManager.unregisterEventListeners();
				}
				sampleSelectionManager = new EventBasedSelectionManager(this, primarySampleMappingType);
				sampleSelectionManager.registerEventListeners();
			}
		} else if (sampleSelectionManager != null) {
			sampleSelectionManager.unregisterEventListeners();
			sampleSelectionManager = null;
		}

		this.mappingPerspective = mappingPerspective;
		updatePerspectiveAverages(mappingPerspective, mappingPerspectiveAverages);
		notifyListeners();
	}

	/**
	 * @param eventSpace
	 *            setter, see {@link eventSpace}
	 */
	public void setEventSpace(String eventSpace) {
		this.eventSpace = eventSpace;
	}

	/**
	 * @return the eventSpace, see {@link #eventSpace}
	 */
	public String getEventSpace() {
		return eventSpace;
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		if (selectionManager == sampleSelectionManager && sampleMappingMode == ESampleMappingMode.SELECTED) {
			updateAllAverages();
			notifyListeners();
		}
	}

	/**
	 * Adds a listener to be notified about table perspective changes.
	 *
	 * @param listener
	 */
	public void addListener(IPathwayMappingListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IPathwayMappingListener listener) {
		listeners.remove(listener);
	}

	protected void notifyListeners() {
		for (IPathwayMappingListener listener : listeners) {
			listener.update(this);
		}
	}

	/**
	 * @return the mappingPerspective, see {@link #mappingPerspective}
	 */
	public TablePerspective getMappingPerspective() {
		return mappingPerspective;
	}

	/**
	 * @return the sampleMappingMode, see {@link #sampleMappingMode}
	 */
	public ESampleMappingMode getSampleMappingMode() {
		return sampleMappingMode;
	}

	/**
	 * @return the sampleSelectionManager, see {@link #sampleSelectionManager}
	 */
	public EventBasedSelectionManager getSampleSelectionManager() {
		return sampleSelectionManager;
	}

	public void takeDown() {
		if (sampleSelectionManager != null) {
			sampleSelectionManager.unregisterEventListeners();
			sampleSelectionManager = null;
		}
	}

	protected void updatePerspectiveAverages(TablePerspective tablePerspective, Map<PathwayVertexRep, Average> averages) {
		averages.clear();
		if (pathwayRepresentation == null)
			return;
		for (PathwayGraph pathway : pathwayRepresentation.getPathways()) {
			for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
				averages.put(vertexRep, calcAverageMapping(vertexRep, tablePerspective));
			}
		}
	}

	private Average calcAverageMapping(PathwayVertexRep vertexRep, TablePerspective tablePerspective) {

		if (tablePerspective == null)
			return null;

		Average average = null;
		IDType geneIDType = PathwayVertexRep.getIdType();
		List<Integer> ids = Arrays.asList(vertexRep.getID());

		if (sampleMappingMode == ESampleMappingMode.ALL) {
			average = tablePerspective.getContainerStatistics().getAverage(geneIDType, ids);
		} else {

			Set<Integer> selectedSamples = sampleSelectionManager.getElements(SelectionType.SELECTION);
			List<Integer> selectedSamplesArray = new ArrayList<Integer>();

			selectedSamplesArray.addAll(selectedSamples);
			if (!selectedSamplesArray.isEmpty()) {

				VirtualArray selectedSamplesVA = new VirtualArray(sampleSelectionManager.getIDType(),
						selectedSamplesArray);
				GroupList groupList = new GroupList();
				groupList.append(new Group(selectedSamplesVA.size()));
				selectedSamplesVA.setGroupList(groupList);

				average = TablePerspectiveStatistics.calculateAverage(selectedSamplesVA,
						tablePerspective.getDataDomain(), geneIDType, ids);
				if (Double.isNaN(average.getArithmeticMean()))
					average = null;
			}
		}
		return average;
	}

	/**
	 *
	 *
	 * @param vertexRep
	 * @return The average for the specified vertex from mapped samples in {@link #mappingPerspective}. Null, if no
	 *         average exists.
	 */
	public Average getMappingAverage(PathwayVertexRep vertexRep) {
		return mappingPerspectiveAverages.get(vertexRep);
	}

	/**
	 * @param tablePerspective
	 * @param vertexRep
	 * @return The cached average for the samples mapped to the provided vertex in the provided table perspective. Null,
	 *         if no cached average exists.
	 */
	public Average getCachedAverage(TablePerspective tablePerspective, PathwayVertexRep vertexRep) {
		Map<PathwayVertexRep, Average> averages = addedTablePerspectiveAverages.get(tablePerspective);
		if (averages == null)
			return null;
		return averages.get(vertexRep);
	}

	/**
	 * @param pathwayRepresentation
	 *            setter, see {@link pathwayRepersentation}
	 */
	public void setPathwayRepersentation(IPathwayRepresentation pathwayRepresentation) {
		this.pathwayRepresentation = pathwayRepresentation;
		updateAllAverages();
		notifyListeners();
	}

	/**
	 * @return All table perspectives for multi-table perspective augmentations
	 */
	public Set<TablePerspective> getTablePerspectives() {
		return addedTablePerspectiveAverages.keySet();
	}
}
