/**
 * 
 */
package org.caleydo.core.data.perspective.table;

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
import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;

/**
 * Helper class that generates table perspectives for categorical datasets.
 * 
 * @author Marc Streit
 * 
 */
public class CategoricalTablePerspectiveCreator {

	public void createAllTablePerspectives(ATableBasedDataDomain dataDomain) {

		String rowPerspectiveID = null;
		IDType rowIDType = null;

		if (dataDomain.isColumnDimension()) {
			rowPerspectiveID = dataDomain.getDefaultTablePerspective().getRecordPerspective()
					.getPerspectiveID();
			rowIDType = dataDomain.getRecordIDType();

			for (int rowID : dataDomain.getRecordVA(rowPerspectiveID)) {
				createTablePerspeciveByRowID(dataDomain, rowID, rowIDType, true);
			}
		}
		else {
			rowPerspectiveID = dataDomain.getDefaultTablePerspective()
					.getDimensionPerspective().getPerspectiveID();
			rowIDType = dataDomain.getDimensionIDType();

			for (int rowID : dataDomain.getDimensionVA(rowPerspectiveID)) {
				createTablePerspeciveByRowID(dataDomain, rowID, rowIDType, true);
			}
		}
		
		DataDomainUpdateEvent event = new DataDomainUpdateEvent(dataDomain);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	public void createTablePerspeciveByRowID(ATableBasedDataDomain dataDomain,
			int rowID, IDType sourceRowIDType, boolean isTablePerspectivePrivate) {

		IDType rowIDType = null;

		if (dataDomain.isColumnDimension()) {
			rowIDType = dataDomain.getRecordIDType();
		}
		else {
			rowIDType = dataDomain.getDimensionIDType();
		}

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(sourceRowIDType);

		List<Integer> ids = new ArrayList<Integer>();
		Integer convertedID = idMappingManager.getID(sourceRowIDType, rowIDType, rowID);
		if (convertedID == null)
			return;

		ids.add(convertedID);

		AVariablePerspective<?, ?, ?, ?> perspective;

		if (dataDomain.isColumnDimension()) {
			perspective = new RecordPerspective(dataDomain);
			dataDomain.getTable().registerRecordPerspective((RecordPerspective) perspective, false);
		}
		else {
			perspective = new DimensionPerspective(dataDomain);
			dataDomain.getTable().registerDimensionPerspective(
					(DimensionPerspective) perspective, false);
		}
		perspective.setPrivate(isTablePerspectivePrivate);
		
		String label = idMappingManager.getID(sourceRowIDType, rowIDType.getIDCategory()
				.getHumanReadableIDType(), rowID);
		perspective.setLabel(label, false);

		PerspectiveInitializationData data = new PerspectiveInitializationData();

		data.setData(ids);
		perspective.init(data);

		RecordPerspective binnedPerspective = null;

		// FIXME TCGA Specific hack! Move to some place sane
		int numberOfBins = 1;

		if (dataDomain.getLabel().contains("Copy")) {
			for (String recordPerspectiveID : dataDomain.getTable().getRecordPerspectiveIDs()) {
				RecordPerspective recordPerspective = dataDomain.getTable()
						.getRecordPerspective(recordPerspectiveID);
				ArrayList<String> groupLabels = new ArrayList<String>();
				groupLabels.add("Homozygous deletion");
				groupLabels.add("Heterozygous deletion");
				groupLabels.add("Normal");
				groupLabels.add("Low level amplification");
				groupLabels.add("High level amplification");
				numberOfBins = 5;
				binnedPerspective = binRecords(numberOfBins, convertedID, recordPerspective,
						dataDomain, label, groupLabels, isTablePerspectivePrivate);
				break;
			}
		}
		if (dataDomain.getLabel().contains("Mutation")) {
			for (String recordPerspectiveID : dataDomain.getTable().getRecordPerspectiveIDs()) {
				RecordPerspective recordPerspective = dataDomain.getTable()
						.getRecordPerspective(recordPerspectiveID);
				ArrayList<String> groupLabels = new ArrayList<String>();
				groupLabels.add("Not Mutated");
				groupLabels.add("Mutated");
				numberOfBins = 2;
				binnedPerspective = binRecords(numberOfBins, convertedID, recordPerspective,
						dataDomain, label, groupLabels, isTablePerspectivePrivate);
				break;

			}
		}

		if (binnedPerspective != null) {
			
			boolean existsAlready = false;
			if (dataDomain.hasTablePerspective(binnedPerspective.getPerspectiveID(), perspective.getPerspectiveID()))
				existsAlready = true;
			
			TablePerspective tablePerspective = dataDomain.getTablePerspective(
					binnedPerspective.getPerspectiveID(), perspective.getPerspectiveID(), false);
			tablePerspective.setLabel(label, false);

			// We do not want to overwrite the state of already existing public table perspectives.
			if (!existsAlready)
				tablePerspective.setPrivate(isTablePerspectivePrivate);

			tablePerspective.getContainerStatistics().setNumberOfBucketsForHistogram(
					numberOfBins);
		}
	}

	private static RecordPerspective binRecords(int nrBins, Integer dimensionID,
			RecordPerspective recordPerspective, ATableBasedDataDomain dataDomain,
			String label, ArrayList<String> groupLabels, boolean isTablePerspectivePrivate) {

		ArrayList<ArrayList<Integer>> bins = new ArrayList<ArrayList<Integer>>(nrBins);
		for (int count = 0; count < nrBins; count++) {
			bins.add(new ArrayList<Integer>());
		}

		DataTable table = dataDomain.getTable();
		for (Integer recordID : recordPerspective.getVirtualArray()) {
			float value = table.getFloat(DataRepresentation.NORMALIZED, recordID, dimensionID);

			// System.out.println(value);

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
		binnedPerspective.setPrivate(isTablePerspectivePrivate);
		table.registerRecordPerspective(binnedPerspective, false);

		return binnedPerspective;
	}
}
