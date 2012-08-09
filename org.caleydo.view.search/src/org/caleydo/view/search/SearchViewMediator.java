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
package org.caleydo.view.search;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.browser.ChangeURLEvent;
import org.caleydo.core.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

public class SearchViewMediator {

	EventPublisher eventPublisher;

	public SearchViewMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public void loadPathway(int pathwayID) {
		LoadPathwayEvent event = new LoadPathwayEvent();
		event.setSender(this);
		event.setPathwayID(pathwayID);
		eventPublisher.triggerEvent(event);
	}

	public void loadURLInBrowser(String url) {
		ChangeURLEvent event = new ChangeURLEvent();
		event.setSender(this);
		event.setUrl(url);
		eventPublisher.triggerEvent(event);
	}

	public void loadPathwayByGene(int davidID) {
		LoadPathwaysByGeneEvent loadPathwaysByGeneEvent = new LoadPathwaysByGeneEvent();
		loadPathwaysByGeneEvent.setSender(this);
		loadPathwaysByGeneEvent.setGeneID((davidID));
		loadPathwaysByGeneEvent.setTableIDType(IDType.getIDType("DAVID"));
		eventPublisher.triggerEvent(loadPathwaysByGeneEvent);
	}

	public void selectGeneSystemWide(int davidID) {

		IDType davidIDType = IDType.getIDType("DAVID");
		// First the current selections need to be cleared
		ClearSelectionsEvent clearSelectionsEvent = new ClearSelectionsEvent();
		clearSelectionsEvent.setSender(this);
		eventPublisher.triggerEvent(clearSelectionsEvent);

		// Create new selection with the selected david ID
		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSender(this);
		SelectionDelta delta = new SelectionDelta(davidIDType);
		// Set<Integer> setExpIndex = GeneralManager.get().getIDMappingManager()
		// .getIDAsSet(IDType.getIDType("DAVID"), recordIDType, davidID);

		// for (Integer expressionIndex : setExpIndex) {
		delta.addSelection(davidID, SelectionType.SELECTION);
		// }

		selectionUpdateEvent.setSelectionDelta((SelectionDelta) delta);
		eventPublisher.triggerEvent(selectionUpdateEvent);
	}

	public void createPerspecive(GeneticDataDomain dataDomain, int davidID) {
		IDType davidIDType = IDType.getIDType("DAVID");

		IDMappingManager idMappingManager = dataDomain.getGeneIDMappingManager();

		List<Integer> ids = new ArrayList<Integer>();
		Integer id = idMappingManager.getID(davidIDType, dataDomain.getGeneIDType(),
				davidID);
		if (id == null)
			return;

		ids.add(id);

		AVariablePerspective<?, ?, ?, ?> perspective;

		if (dataDomain.isColumnDimension()) {
			perspective = new RecordPerspective(dataDomain);
			dataDomain.getTable().registerRecordPerspective(
					(RecordPerspective) perspective);
		} else {
			perspective = new DimensionPerspective(dataDomain);
			dataDomain.getTable().registerDimensionPerspective(
					(DimensionPerspective) perspective);
		}
		String label = idMappingManager.getID(davidIDType,
				dataDomain.getHumanReadableGeneIDType(), davidID);
		perspective.setLabel(label, false);

		PerspectiveInitializationData data = new PerspectiveInitializationData();

		data.setData(ids);
		perspective.init(data);

		RecordPerspective binnedPerspective = null;

		// FIXME TCGA Specific hack! Move to some place sane
		int numberOfBins = 1;

		if (dataDomain.getLabel().contains("Copy")) {
			for (String recordPerspectiveID : dataDomain.getTable()
					.getRecordPerspectiveIDs()) {
				RecordPerspective recordPerspective = dataDomain.getTable()
						.getRecordPerspective(recordPerspectiveID);
				ArrayList<String> groupLabels = new ArrayList<String>();
				groupLabels.add("Homozygous deletion");
				groupLabels.add("Heterozygous deletion");
				groupLabels.add("Normal");
				groupLabels.add("Low level amplification");
				groupLabels.add("High level amplification");
				numberOfBins = 5;
				binnedPerspective = binRecords(numberOfBins, id, recordPerspective,
						dataDomain, label, groupLabels);
				break;
			}
		}
		if (dataDomain.getLabel().contains("Mutation")) {
			for (String recordPerspectiveID : dataDomain.getTable()
					.getRecordPerspectiveIDs()) {
				RecordPerspective recordPerspective = dataDomain.getTable()
						.getRecordPerspective(recordPerspectiveID);
				ArrayList<String> groupLabels = new ArrayList<String>();
				groupLabels.add("Not Mutated");
				groupLabels.add("Mutated");
				numberOfBins = 2;
				binnedPerspective = binRecords(numberOfBins, id, recordPerspective,
						dataDomain, label, groupLabels);
				break;

			}
		}

		if (binnedPerspective != null) {
			TablePerspective tablePerspective = dataDomain.getTablePerspective(
					binnedPerspective.getPerspectiveID(), perspective.getPerspectiveID());
			tablePerspective.setLabel(label, false);
			tablePerspective.getContainerStatistics().setNumberOfBucketsForHistogram(
					numberOfBins);
		}

	}

	private RecordPerspective binRecords(int nrBins, Integer dimensionID,
			RecordPerspective recordPerspective, GeneticDataDomain dataDomain,
			String label, ArrayList<String> groupLabels) {
		
		ArrayList<ArrayList<Integer>> bins = new ArrayList<ArrayList<Integer>>(nrBins);
		for (int count = 0; count < nrBins; count++) {
			bins.add(new ArrayList<Integer>());
		}

		DataTable table = dataDomain.getTable();
		for (Integer recordID : recordPerspective.getVirtualArray()) {
			float value = table.getFloat(DataRepresentation.NORMALIZED, recordID,
					dimensionID);
			
			System.out.println(value);
			
			// this works because value is normalized
			int bin = (int) (value * nrBins);
			if (bin == nrBins)
				bin = nrBins - 1;
			bins.get(bin).add(recordID);
		}

		ArrayList<Integer> binnedIDList = new ArrayList<Integer>();
		ArrayList<Integer> clusterSizes = new ArrayList<Integer>(nrBins);
		// TODO: not needed
		ArrayList<Integer> sampleElements = new ArrayList<Integer>(nrBins);

		for (ArrayList<Integer> bin : bins) {
			binnedIDList.addAll(bin);
			clusterSizes.add(bin.size());
			sampleElements.add(0);
		}

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(binnedIDList, clusterSizes, sampleElements, groupLabels);

		RecordPerspective binnedPerspective = new RecordPerspective(dataDomain);
		binnedPerspective.init(data);
		binnedPerspective.setLabel(label, false);
		table.registerRecordPerspective(binnedPerspective);

		return binnedPerspective;
	}
}
