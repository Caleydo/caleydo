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
package org.caleydo.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IDTypeInitializer;
import org.caleydo.core.io.parser.ascii.GroupingParser;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.EDefaultColorSchemes;

/**
 * Creates datadomains, perspectives, etc based on a {@link DataSetDescription}
 * 
 * @author Alexander Lex
 * 
 */
public class DataLoader {

	/**
	 * Creates a {@link ATableBasedDataDomain} and loads the dataset and
	 * groupings into it. Creates all necessary IDTypes for a dataset. Also does
	 * the processing of the data.
	 * 
	 * @param dataSetDescription
	 *            The information for how to create everything
	 */
	public static ATableBasedDataDomain loadData(DataSetDescription dataSetDescription)
			throws FileNotFoundException, IOException {
		IDTypeInitializer.initIDs(dataSetDescription);
		ATableBasedDataDomain dataDomain = loadDataSet(dataSetDescription);
		loadGroupings(dataDomain, dataSetDescription);
		runDataProcessing(dataDomain, dataSetDescription);
		return dataDomain;
	}

	/**
	 * Load a single grouping to a {@link ATableBasedDataDomain}
	 * 
	 * @param dataDomain
	 * @param groupingSpec
	 *            the specification of the grouping
	 * */
	public static void loadGrouping(ATableBasedDataDomain dataDomain,
			GroupingParseSpecification groupingSpec) {
		ArrayList<GroupingParseSpecification> groupingList = new ArrayList<GroupingParseSpecification>(
				1);
		groupingList.add(groupingSpec);
		IDCategory category = IDCategory.getIDCategory(groupingSpec
				.getRowIDSpecification().getIdCategory());

		if (dataDomain.getRecordIDCategory().equals(category)) {
			loadRecordGroupings(dataDomain, groupingList);
		} else if (dataDomain.getDimensionIDCategory().equals(category)) {
			loadDimensionGroupings(dataDomain, groupingList);
		}
	}

	/**
	 * Creates the {@link ATableBasedDataDomain} and loads the data file
	 * 
	 * @param dataSetDescription
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static ATableBasedDataDomain loadDataSet(DataSetDescription dataSetDescription)
			throws FileNotFoundException, IOException {

		ATableBasedDataDomain dataDomain;

		IDSpecification columnIDSpecification = dataSetDescription
				.getColumnIDSpecification();
		IDSpecification rowIDSpecification = dataSetDescription.getRowIDSpecification();

		if ((rowIDSpecification.isIDTypeGene() && !dataSetDescription.isTransposeMatrix())
				|| (columnIDSpecification.isIDTypeGene() && dataSetDescription
						.isTransposeMatrix())) {

			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain("org.caleydo.datadomain.genetic",
							dataSetDescription);

		} else if ((columnIDSpecification.isIDTypeGene() && !dataSetDescription
				.isTransposeMatrix())
				|| (rowIDSpecification.isIDTypeGene() && dataSetDescription
						.isTransposeMatrix())) {

			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain("org.caleydo.datadomain.genetic",
							dataSetDescription);

		} else {

			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain("org.caleydo.datadomain.generic",
							dataSetDescription);

		}

		dataDomain.setColorMapper(ColorMapper
				.createDefaultMapper(EDefaultColorSchemes.BLUE_WHITE_RED));

//		dataDomain.init();

		boolean createDefaultRecordPerspective = true;

		// the place the matrix is stored:
		DataTableUtils.loadData(dataDomain, dataSetDescription, true,
				createDefaultRecordPerspective);
		return dataDomain;

	}

	/**
	 * Loads all groupings for columns and rows that are specified in the
	 * {@link DataSetDescription}. Respects transposition.
	 * 
	 * @param dataSetDescription
	 */
	public static void loadGroupings(ATableBasedDataDomain dataDomain,
			DataSetDescription dataSetDescription) {

		ArrayList<GroupingParseSpecification> columnGroupingSpecifications = dataSetDescription
				.getColumnGroupingSpecifications();
		if (columnGroupingSpecifications != null) {
			if (dataSetDescription.isTransposeMatrix()) {
				loadRecordGroupings(dataDomain, columnGroupingSpecifications);
			} else {
				loadDimensionGroupings(dataDomain, columnGroupingSpecifications);
			}
		}

		ArrayList<GroupingParseSpecification> rowGroupingSpecifications = dataSetDescription
				.getRowGroupingSpecifications();
		if (rowGroupingSpecifications != null) {

			if (dataSetDescription.isTransposeMatrix()) {
				loadDimensionGroupings(dataDomain, rowGroupingSpecifications);

			} else {
				loadRecordGroupings(dataDomain, rowGroupingSpecifications);
			}
		}
	}

	/**
	 * Load groupings for dimensions
	 * 
	 * @param dataDomain
	 * @param dimensionGroupings
	 */
	private static void loadDimensionGroupings(ATableBasedDataDomain dataDomain,
			ArrayList<GroupingParseSpecification> dimensionGroupings) {

		IDType targetIDType = dataDomain.getDimensionIDType();

		ArrayList<PerspectiveInitializationData> dimensionPerspectivesInitData = parseGrouping(
				dimensionGroupings, targetIDType);

		for (PerspectiveInitializationData data : dimensionPerspectivesInitData) {
			DimensionPerspective dimensionPerspective = new DimensionPerspective(
					dataDomain);
			dimensionPerspective.init(data);
			dataDomain.getTable().registerDimensionPerspective(dimensionPerspective);
		}

	}

	/**
	 * Load groupings for records
	 * 
	 * @param dataDomain
	 * @param recordGroupings
	 */
	private static void loadRecordGroupings(ATableBasedDataDomain dataDomain,
			ArrayList<GroupingParseSpecification> recordGroupings) {

		IDType targetIDType = dataDomain.getRecordIDType();

		ArrayList<PerspectiveInitializationData> dimensionPerspectivesInitData = parseGrouping(
				recordGroupings, targetIDType);

		for (PerspectiveInitializationData data : dimensionPerspectivesInitData) {
			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			recordPerspective.init(data);
			dataDomain.getTable().registerRecordPerspective(recordPerspective);
		}
	}

	/**
	 * Runs the parser on the groupings and returns a lisst of
	 * {@link PerspectiveInitializationData}
	 * 
	 * @param groupingSpecifications
	 * @param targetIDType
	 * @return
	 */
	private static ArrayList<PerspectiveInitializationData> parseGrouping(
			ArrayList<GroupingParseSpecification> groupingSpecifications,
			IDType targetIDType) {

		ArrayList<PerspectiveInitializationData> perspectiveDatas = new ArrayList<PerspectiveInitializationData>();
		for (GroupingParseSpecification groupingSpecification : groupingSpecifications) {
			GroupingParser parser = new GroupingParser(groupingSpecification,
					targetIDType);
			parser.loadData();
			perspectiveDatas.addAll(parser.getPerspectiveInitializationDatas());
		}
		return perspectiveDatas;
	}

	private static void runDataProcessing(ATableBasedDataDomain dataDomain,
			DataSetDescription dataSetDescription) {
		DataProcessingDescription dataProcessingDescription = dataSetDescription
				.getDataProcessingDescription();
		if (dataProcessingDescription == null)
			return;

		ArrayList<ClusterConfiguration> rowClusterConfigurations = dataProcessingDescription
				.getRowClusterConfigurations();
		if (rowClusterConfigurations != null) {
			for (ClusterConfiguration clusterConfiguration : rowClusterConfigurations) {
				if (dataSetDescription.isTransposeMatrix()) {
					setUpDimensionClustering(clusterConfiguration, dataDomain);
				} else {
					setUpRecordClustering(clusterConfiguration, dataDomain);
				}
				clusterConfiguration.setSourceDimensionPerspective(dataDomain.getTable()
						.getDefaultDimensionPerspective());
				clusterConfiguration.setSourceRecordPerspective(dataDomain.getTable()
						.getDefaultRecordPerspective());

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
				clusterConfiguration.setSourceRecordPerspective(dataDomain.getTable()
						.getDefaultRecordPerspective());

				dataDomain.startClustering(clusterConfiguration);
			}

		}

	}

	private static void setUpRecordClustering(ClusterConfiguration clusterConfiguration,
			ATableBasedDataDomain dataDomain) {
		clusterConfiguration.setClusterTarget(EClustererTarget.RECORD_CLUSTERING);
		RecordPerspective targetRecordPerspective = new RecordPerspective(dataDomain);
		dataDomain.getTable().registerRecordPerspective(targetRecordPerspective);
		targetRecordPerspective.setLabel(clusterConfiguration.toString(), false);
		clusterConfiguration.setOptionalTargetRecordPerspective(targetRecordPerspective);
	}

	private static void setUpDimensionClustering(
			ClusterConfiguration clusterConfiguration, ATableBasedDataDomain dataDomain) {
		clusterConfiguration.setClusterTarget(EClustererTarget.DIMENSION_CLUSTERING);
		DimensionPerspective targetDimensionPerspective = new DimensionPerspective(
				dataDomain);
		dataDomain.getTable().registerDimensionPerspective(targetDimensionPerspective);

		targetDimensionPerspective.setLabel(clusterConfiguration.toString(), false);
		clusterConfiguration
				.setOptionalTargetDimensionPerspective(targetDimensionPerspective);
	}

}
