package org.caleydo.view.genesearch;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.browser.ChangeURLEvent;
import org.caleydo.core.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
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

		ADataPerspective<?, ?, ?, ?> perspective;

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
		if (dataDomain.getLabel().contains("Copy")) {
			for (String recordPerspectiveID : dataDomain.getTable()
					.getRecordPerspectiveIDs()) {
				RecordPerspective recordPerspective = dataDomain.getTable()
						.getRecordPerspective(recordPerspectiveID);
				ArrayList<String> groupLabels = new ArrayList<String>();
				groupLabels.add("Heterozygous del.");
				groupLabels.add("Homozygous del.");
				groupLabels.add("Normal");
				groupLabels.add("Amplification 1");
				groupLabels.add("Amplification 2");
				binnedPerspective = binRecords(5, id, recordPerspective, dataDomain,
						label, groupLabels);
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
				binnedPerspective = binRecords(2, id, recordPerspective, dataDomain,
						label, groupLabels);
				break;

			}
		}

		if (binnedPerspective != null) {
			DataContainer dataContainer = dataDomain.getDataContainer(
					binnedPerspective.getID(), perspective.getID());
			dataContainer.setLabel(label, false);
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
