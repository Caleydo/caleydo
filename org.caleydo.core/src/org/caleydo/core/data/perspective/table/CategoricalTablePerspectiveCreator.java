/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;

/**
 * Helper class that generates table perspectives for categorical datasets.
 *
 * @author Marc Streit
 * @author Alexander Lex
 *
 */
public class CategoricalTablePerspectiveCreator {

	/**
	 * List holding all data domains for which we already have created the categorical data.
	 *
	 * use the label instead of the real object to avoid a reference for unloading
	 */
	private Set<String> alreadyDone = new HashSet<>();

	public void createAllTablePerspectives(ATableBasedDataDomain dataDomain) {
		// Make sure that the categorical initialized is only done once.
		if (alreadyDone.contains(dataDomain.getDataDomainID()))
			return;
		alreadyDone.add(dataDomain.getDataDomainID());

		String rowPerspectiveID = null;
		IDType rowIDType = null;
		Iterable<Integer> rowIDs;

		if (dataDomain.isColumnDimension()) {
			rowPerspectiveID = dataDomain.getDefaultTablePerspective().getRecordPerspective().getPerspectiveID();
			rowIDType = dataDomain.getRecordIDType();
			rowIDs = dataDomain.getRecordVA(rowPerspectiveID);
		} else {
			rowPerspectiveID = dataDomain.getDefaultTablePerspective().getDimensionPerspective().getPerspectiveID();
			rowIDType = dataDomain.getDimensionIDType();
			rowIDs = dataDomain.getDimensionVA(rowPerspectiveID);
		}

		for (int rowID : rowIDs) {
			createTablePerspeciveByRowID(dataDomain, rowID, rowIDType, true);
		}

		DataDomainUpdateEvent event = new DataDomainUpdateEvent(dataDomain);
		event.setSender(this);
		
		EventPublisher.trigger(event);
	}

	/**
	 * Creates a new table perspective with one row, specified by the row ID, and all columns.
	 *
	 * @param dataDomain
	 * @param rowID
	 * @param sourceRowIDType
	 * @param isTablePerspectivePrivate
	 */
	public void createTablePerspeciveByRowID(ATableBasedDataDomain dataDomain, int rowID, IDType sourceRowIDType,
			boolean isTablePerspectivePrivate) {

		IDType rowIDType = null;

		if (dataDomain.isColumnDimension()) {
			rowIDType = dataDomain.getRecordIDType();
		} else {
			rowIDType = dataDomain.getDimensionIDType();
		}

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(sourceRowIDType);

		List<Integer> ids = new ArrayList<Integer>();
		Integer convertedID = idMappingManager.getID(sourceRowIDType, rowIDType, rowID);
		if (convertedID == null)
			return;

		ids.add(convertedID);

		Perspective perspective;

		if (dataDomain.isColumnDimension()) {
			perspective = new Perspective(dataDomain, dataDomain.getRecordIDType());
			dataDomain.getTable().registerRecordPerspective(perspective, false);
		} else {
			perspective = new Perspective(dataDomain, dataDomain.getDimensionIDType());
			dataDomain.getTable().registerDimensionPerspective(perspective, false);
		}
		perspective.setPrivate(isTablePerspectivePrivate);

		String label = idMappingManager.getID(sourceRowIDType, rowIDType.getIDCategory().getHumanReadableIDType(),
				rowID);
		perspective.setLabel(label, false);

		PerspectiveInitializationData data = new PerspectiveInitializationData();

		data.setData(ids);
		perspective.init(data);


		// for (String recordPerspectiveID : dataDomain.getTable().getRecordPerspectiveIDs()) {
		Perspective recordPerspective = dataDomain.getTable().getDefaultRecordPerspective(false);
		Perspective binnedPerspective = binCategorical(convertedID, recordPerspective, dataDomain, label,
				isTablePerspectivePrivate);

		if (binnedPerspective != null) {

			boolean existsAlready = false;
			if (dataDomain.hasTablePerspective(binnedPerspective.getPerspectiveID(), perspective.getPerspectiveID()))
				existsAlready = true;

			TablePerspective tablePerspective = dataDomain.getTablePerspective(binnedPerspective.getPerspectiveID(),
					perspective.getPerspectiveID(), false);
			tablePerspective.setLabel(label, false);

			// We do not want to overwrite the state of already existing public
			// table perspectives.
			if (!existsAlready)
				tablePerspective.setPrivate(isTablePerspectivePrivate);
		}
	}

	private static Perspective binCategorical(Integer dimensionID, Perspective recordPerspective,
			ATableBasedDataDomain dataDomain, String label, boolean isTablePerspectivePrivate) {


		CategoricalClassDescription<?> categoryDescriptions = (CategoricalClassDescription<?>) dataDomain.getTable()
				.getDataClassSpecificDescription(
				dimensionID, 0);
		int nrBins = categoryDescriptions.size();

		HashMap<CategoryProperty<?>, ArrayList<Integer>> bins = new HashMap<>(nrBins * 2);
		for (CategoryProperty<?> property : categoryDescriptions) {
			bins.put(property, new ArrayList<Integer>());
		}

		Table table = dataDomain.getTable();
		for (Integer recordID : recordPerspective.getVirtualArray()) {
			CategoryProperty<?> property = categoryDescriptions
					.getCategoryProperty(table.getRaw(dimensionID, recordID));
			if (property == null) {
				System.out.println("recordID: " + recordID + " dimensionID " + dimensionID + " raw: "
						+ table.getRaw(dimensionID, recordID));

			} else {
				// System.out.println(" raw: " + table.getRaw(dimensionID, recordID));
				ArrayList<Integer> bin = bins.get(property);
				bin.add(recordID);
			}
			// float value = table.getNormalizedValue(dimensionID, recordID);
			//
			// // System.out.println(value);
			//
			// // this works because value is normalized
			// int bin = (int) (value * nrBins);
			// if (bin == nrBins)
			// bin = nrBins - 1;
			// bins.get(bin).add(recordID);
		}

		ArrayList<Integer> binnedIDList = new ArrayList<Integer>();
		ArrayList<Integer> clusterSizes = new ArrayList<Integer>(nrBins);
		// TODO: not needed
		ArrayList<Integer> sampleElements = new ArrayList<Integer>(nrBins);

		ArrayList<String> groupLabels = new ArrayList<String>(nrBins);

		for (CategoryProperty<?> property : categoryDescriptions) {
			ArrayList<Integer> bin = bins.get(property);
			groupLabels.add(property.getCategoryName());
			binnedIDList.addAll(bin);
			clusterSizes.add(bin.size());
			sampleElements.add(0);
		}

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(binnedIDList, clusterSizes, sampleElements, groupLabels);

		Perspective binnedPerspective = new Perspective(dataDomain, dataDomain.getRecordIDType());
		binnedPerspective.init(data);
		binnedPerspective.setLabel(label, false);
		binnedPerspective.setPrivate(isTablePerspectivePrivate);
		table.registerRecordPerspective(binnedPerspective, false);

		return binnedPerspective;

	}

	/**
	 * Creates a grouping by dividing the data equidistant based on their normalized score.
	 *
	 * @param nrBins
	 * @param dimensionID
	 * @param recordPerspective
	 * @param dataDomain
	 * @param label
	 * @param groupLabels
	 * @param isTablePerspectivePrivate
	 * @return
	 */
	private static Perspective binRecords(int nrBins, Integer dimensionID, Perspective recordPerspective,
			ATableBasedDataDomain dataDomain, String label, ArrayList<String> groupLabels,
			boolean isTablePerspectivePrivate) {

		ArrayList<ArrayList<Integer>> bins = new ArrayList<ArrayList<Integer>>(nrBins);
		for (int count = 0; count < nrBins; count++) {
			bins.add(new ArrayList<Integer>());
		}

		Table table = dataDomain.getTable();
		for (Integer recordID : recordPerspective.getVirtualArray()) {
			float value = table.getNormalizedValue(dimensionID, recordID);

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

		Perspective binnedPerspective = new Perspective(dataDomain, dataDomain.getRecordIDType());
		binnedPerspective.init(data);
		binnedPerspective.setLabel(label, false);
		binnedPerspective.setPrivate(isTablePerspectivePrivate);
		table.registerRecordPerspective(binnedPerspective, false);

		return binnedPerspective;
	}
}
