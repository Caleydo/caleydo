/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.collection.table.TableUtils;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.NewDataDomainLoadedEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IDTypeInitializer;
import org.caleydo.core.io.parser.ascii.GroupingParser;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.collect.Lists;

/**
 * Creates datadomains, perspectives, etc based on a {@link DataSetDescription}
 *
 * @author Alexander Lex
 *
 */
public class DataLoader {

	/**
	 * Creates a {@link ATableBasedDataDomain} and loads the dataset and groupings into it. Creates all necessary
	 * IDTypes for a dataset. Also does the processing of the data.
	 *
	 * @param dataSetDescription
	 *            The information for how to create everything
	 * @param monitor
	 *            Monitor for progress indication. May be null.
	 * @return the loaded {@link ATableBasedDataDomain} or null if an error occurred
	 */
	public static ATableBasedDataDomain loadData(DataSetDescription dataSetDescription, IProgressMonitor monitor) {

		monitor.beginTask("Loading Dataset", dataSetDescription.getDataDescription() == null ? 5 : 4);
		if (monitor != null)
			monitor.subTask("Initializing ID Types");
		IDTypeInitializer.initIDs(dataSetDescription);
		if (monitor != null)
			monitor.worked(1);
		ATableBasedDataDomain dataDomain = null;

		dataDomain = loadDataSet(dataSetDescription, monitor);
		loadGroupings(dataDomain, dataSetDescription, monitor);
		runDataProcessing(dataDomain, dataSetDescription, monitor);

		// Create perspectives per column for inhomogeneous datasets
		if (dataSetDescription.getDataDescription() == null) {
			createInitialPerspectives(dataDomain, monitor);
		}

		EventPublisher.trigger(new NewDataDomainLoadedEvent(dataDomain));

		return dataDomain;
	}

	/**
	 * Load a single grouping to a {@link ATableBasedDataDomain}
	 *
	 * @param dataDomain
	 * @param groupingSpec
	 *            the specification of the grouping
	 * */
	public static List<Perspective> loadGrouping(ATableBasedDataDomain dataDomain,
			GroupingParseSpecification groupingSpec) {
		ArrayList<GroupingParseSpecification> groupingList = new ArrayList<GroupingParseSpecification>(1);
		groupingList.add(groupingSpec);
		IDCategory category = IDCategory.getIDCategory(groupingSpec.getRowIDSpecification().getIdCategory());

		List<Perspective> perspectives = null;
		if (dataDomain.getRecordIDCategory().equals(category)) {
			perspectives = loadRecordGroupings(dataDomain, groupingList);
		} else if (dataDomain.getDimensionIDCategory().equals(category)) {
			perspectives = loadDimensionGroupings(dataDomain, groupingList);
		}

		return perspectives;
	}

	/**
	 * Creates the {@link ATableBasedDataDomain} and loads the data file
	 *
	 * @param dataSetDescription
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static ATableBasedDataDomain loadDataSet(DataSetDescription dataSetDescription, IProgressMonitor monitor) {

		if (monitor != null)
			monitor.subTask("Loading data");

		ATableBasedDataDomain dataDomain;

		IDSpecification columnIDSpecification = dataSetDescription.getColumnIDSpecification();
		IDSpecification rowIDSpecification = dataSetDescription.getRowIDSpecification();

		if ((rowIDSpecification.isIDTypeGene() && !dataSetDescription.isTransposeMatrix())
				|| (columnIDSpecification.isIDTypeGene() && dataSetDescription.isTransposeMatrix())) {

			dataDomain = (ATableBasedDataDomain) DataDomainManager.get().createDataDomain(
					"org.caleydo.datadomain.genetic", dataSetDescription);

		} else if ((columnIDSpecification.isIDTypeGene() && !dataSetDescription.isTransposeMatrix())
				|| (rowIDSpecification.isIDTypeGene() && dataSetDescription.isTransposeMatrix())) {

			dataDomain = (ATableBasedDataDomain) DataDomainManager.get().createDataDomain(
					"org.caleydo.datadomain.genetic", dataSetDescription);

		} else {

			dataDomain = (ATableBasedDataDomain) DataDomainManager.get().createDataDomain(
					"org.caleydo.datadomain.generic", dataSetDescription);

		}
		// dataDomain.init();

		boolean createDefaultRecordPerspective = true;

		try {
			// the place the matrix is stored:
			TableUtils.loadData(dataDomain, dataSetDescription, true, createDefaultRecordPerspective);
		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, "DataLoader", "Failed to load data for dataset "
					+ dataSetDescription.getDataSetName(), e));
			DataDomainManager.get().unregister(dataDomain);
			throw new IllegalStateException(e.getMessage(), e);
		}
		if (monitor != null)
			monitor.worked(1);

		return dataDomain;

	}

	/**
	 * Loads all groupings for columns and rows that are specified in the {@link DataSetDescription}. Respects
	 * transposition.
	 *
	 * @param dataSetDescription
	 */
	public static void loadGroupings(ATableBasedDataDomain dataDomain,
			DataSetDescription dataSetDescription, IProgressMonitor monitor) {

		if (monitor != null)
			monitor.subTask("Loading Groupings");

		List<GroupingParseSpecification> columnGroupingSpecifications = dataSetDescription
				.getColumnGroupingSpecifications();
		if (columnGroupingSpecifications != null) {
			if (dataSetDescription.isTransposeMatrix()) {
				loadRecordGroupings(dataDomain, columnGroupingSpecifications);
			} else {
				loadDimensionGroupings(dataDomain, columnGroupingSpecifications);
			}
		}

		List<GroupingParseSpecification> rowGroupingSpecifications = dataSetDescription.getRowGroupingSpecifications();
		if (rowGroupingSpecifications != null) {

			if (dataSetDescription.isTransposeMatrix()) {
				loadDimensionGroupings(dataDomain, rowGroupingSpecifications);

			} else {
				loadRecordGroupings(dataDomain, rowGroupingSpecifications);
			}
		}

		if (monitor != null)
			monitor.worked(1);
	}

	/**
	 * Load groupings for dimensions
	 *
	 * @param dataDomain
	 * @param dimensionGroupings
	 */
	private static List<Perspective> loadDimensionGroupings(ATableBasedDataDomain dataDomain,
			List<GroupingParseSpecification> dimensionGroupings) {

		IDType targetIDType = dataDomain.getDimensionIDType();

		List<PerspectiveInitializationData> dimensionPerspectivesInitData = parseGrouping(dimensionGroupings,
				targetIDType);
		List<Perspective> perspectives = new ArrayList<>(dimensionPerspectivesInitData.size());
		for (PerspectiveInitializationData data : dimensionPerspectivesInitData) {
			Perspective dimensionPerspective = new Perspective(dataDomain, dataDomain.getDimensionIDType());
			dimensionPerspective.init(data);
			dataDomain.getTable().registerDimensionPerspective(dimensionPerspective);
			perspectives.add(dimensionPerspective);
		}

		return perspectives;
	}

	/**
	 * Load groupings for records
	 *
	 * @param dataDomain
	 * @param recordGroupings
	 */
	private static List<Perspective> loadRecordGroupings(ATableBasedDataDomain dataDomain,
			List<GroupingParseSpecification> recordGroupings) {

		IDType targetIDType = dataDomain.getRecordIDType();

		List<PerspectiveInitializationData> recordPerspectivesInitData = parseGrouping(recordGroupings, targetIDType);
		List<Perspective> perspectives = new ArrayList<>(recordPerspectivesInitData.size());
		for (PerspectiveInitializationData data : recordPerspectivesInitData) {
			Perspective recordPerspective = new Perspective(dataDomain, targetIDType);
			recordPerspective.init(data);
			dataDomain.getTable().registerRecordPerspective(recordPerspective);
			perspectives.add(recordPerspective);
		}

		return perspectives;
	}

	/**
	 * Runs the parser on the groupings and returns a lisst of {@link PerspectiveInitializationData}
	 *
	 * @param groupingSpecifications
	 * @param targetIDType
	 * @return
	 */
	private static List<PerspectiveInitializationData> parseGrouping(
			List<GroupingParseSpecification> groupingSpecifications, IDType targetIDType) {

		List<PerspectiveInitializationData> perspectiveDatas = new ArrayList<PerspectiveInitializationData>();
		for (GroupingParseSpecification groupingSpecification : groupingSpecifications) {
			GroupingParser parser = new GroupingParser(groupingSpecification, targetIDType);
			parser.loadData();
			perspectiveDatas.addAll(parser.getPerspectiveInitializationDatas());
		}
		return perspectiveDatas;
	}

	private static void runDataProcessing(ATableBasedDataDomain dataDomain, DataSetDescription dataSetDescription,
			IProgressMonitor monitor) {
		if (monitor != null)
			monitor.subTask("Processing Data");

		DataProcessingDescription dataProcessingDescription = dataSetDescription.getDataProcessingDescription();
		if (dataProcessingDescription == null)
			return;

		List<ClusterConfiguration> rowClusterConfigurations = dataProcessingDescription.getRowClusterConfigurations();
		if (rowClusterConfigurations != null) {
			for (ClusterConfiguration clusterConfiguration : rowClusterConfigurations) {
				if (dataSetDescription.isTransposeMatrix()) {
					setUpDimensionClustering(clusterConfiguration, dataDomain);
				} else {
					setUpRecordClustering(clusterConfiguration, dataDomain);
				}
				clusterConfiguration.setSourceDimensionPerspective(dataDomain.getTable()
						.getDefaultDimensionPerspective());
				clusterConfiguration.setSourceRecordPerspective(dataDomain.getTable().getDefaultRecordPerspective());

				dataDomain.startClustering(clusterConfiguration);
			}

		}

		ArrayList<ClusterConfiguration> columnClusterConfigurations = dataProcessingDescription
				.getColumnClusterConfigurations();
		if (columnClusterConfigurations != null) {
			for (ClusterConfiguration clusterConfiguration : columnClusterConfigurations) {
				if (!dataSetDescription.isTransposeMatrix()) {
					setUpDimensionClustering(clusterConfiguration, dataDomain);
				} else {
					setUpRecordClustering(clusterConfiguration, dataDomain);
				}
				clusterConfiguration.setSourceDimensionPerspective(dataDomain.getTable()
						.getDefaultDimensionPerspective());
				clusterConfiguration.setSourceRecordPerspective(dataDomain.getTable().getDefaultRecordPerspective());

				dataDomain.startClustering(clusterConfiguration);
			}

		}

		if (monitor != null)
			monitor.worked(1);

	}

	/**
	 * create initial table perspectives per column
	 *
	 * @param dataDomain
	 */
	private static void createInitialPerspectives(ATableBasedDataDomain dataDomain, IProgressMonitor monitor) {
		if (monitor != null)
			monitor.subTask("Initializing Dataset");
		final Table table = dataDomain.getTable();
		List<Integer> columns = table.getColumnIDList();
		for (Integer col : columns) {
			Perspective dim = new Perspective(dataDomain, dataDomain.getDimensionIDType());
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(Lists.newArrayList(col));

			dim.init(data);
			dim.setLabel(dataDomain.getDimensionLabel(col));
			table.registerDimensionPerspective(dim, false);

			EDataClass dataClass = table.getDataClass(col, 0);
			String recordPer;
			if (dataClass == EDataClass.CATEGORICAL) {
				// create a clustered record perspective
				VirtualArray groupByCategory = groupByCategory(table, table.getDefaultRecordPerspective()
						.getVirtualArray(), col);
				Perspective rec = new Perspective(dataDomain, dataDomain.getRecordIDType());
				data = new PerspectiveInitializationData();
				data.setData(groupByCategory);

				rec.init(data);
				rec.setLabel(dim.getLabel());
				table.registerRecordPerspective(rec, false);
				recordPer = rec.getPerspectiveID();
			} else {
				// default one
				recordPer = table.getDefaultRecordPerspective().getPerspectiveID();
			}

			TablePerspective p = dataDomain.getTablePerspective(recordPer, dim.getPerspectiveID(), false);
			p.setLabel(dim.getLabel());
		}
		if (monitor != null)
			monitor.worked(1);
	}

	public static VirtualArray groupByCategory(Table table, VirtualArray records, Integer category) {
		CategoricalClassDescription<?> categoryDescriptions = (CategoricalClassDescription<?>) table
				.getDataClassSpecificDescription(category, 0);
		int nrBins = categoryDescriptions.size();

		Map<CategoryProperty<?>, List<Integer>> bins = new HashMap<>(nrBins * 2);
		for (CategoryProperty<?> property : categoryDescriptions) {
			bins.put(property, new ArrayList<Integer>());
		}
		for (Integer recordID : records) {
			CategoryProperty<?> property = categoryDescriptions.getCategoryProperty(table.getRaw(category, recordID));
			if (property == null) {
				System.out.println("recordID: " + recordID + " dimensionID " + category + " raw: "
						+ table.getRaw(category, recordID));
			} else {
				// System.out.println(" raw: " + table.getRaw(dimensionID, recordID));
				List<Integer> bin = bins.get(property);
				bin.add(recordID);
			}
		}

		List<Integer> groupIds = new ArrayList<>(records.size());
		GroupList groupList = new GroupList();
		int from = 0;
		int to = 0;

		for (CategoryProperty<?> property : categoryDescriptions) {
			List<Integer> bin = bins.get(property);

			int size = bin.size();

			if (size == 0) // skip empty groups
				continue;

			Group g = new Group(size, size > 0 ? bin.get(0) : 0);
			g.setLabel(property.getCategoryName(), false);
			g.setStartIndex(from);
			to += size;
			from = to;

			groupList.append(g);
			groupIds.addAll(bin);
		}

		VirtualArray va = new VirtualArray(records.getIdType(), groupIds);
		va.setGroupList(groupList);
		return va;
	}

	private static void setUpRecordClustering(ClusterConfiguration clusterConfiguration,
			ATableBasedDataDomain dataDomain) {
		clusterConfiguration.setClusterTarget(EClustererTarget.RECORD_CLUSTERING);
		Perspective targetRecordPerspective = new Perspective(dataDomain, dataDomain.getRecordIDType());
		dataDomain.getTable().registerRecordPerspective(targetRecordPerspective);
		targetRecordPerspective.setLabel(clusterConfiguration.toString(), false);
		clusterConfiguration.setOptionalTargetRecordPerspective(targetRecordPerspective);
	}

	private static void setUpDimensionClustering(ClusterConfiguration clusterConfiguration,
			ATableBasedDataDomain dataDomain) {
		clusterConfiguration.setClusterTarget(EClustererTarget.DIMENSION_CLUSTERING);
		Perspective targetDimensionPerspective = new Perspective(dataDomain, dataDomain.getDimensionIDType());
		dataDomain.getTable().registerDimensionPerspective(targetDimensionPerspective);

		targetDimensionPerspective.setLabel(clusterConfiguration.toString(), false);
		clusterConfiguration.setOptionalTargetDimensionPerspective(targetDimensionPerspective);
	}

}
