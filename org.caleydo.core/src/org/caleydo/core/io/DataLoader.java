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
import org.caleydo.core.data.datadomain.DataDomainConfiguration;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.parser.ascii.GroupingParser;
import org.caleydo.core.util.clusterer.initialization.AClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.EDefaultColorSchemes;
import org.eclipse.core.runtime.Status;

/**
 * Creates datadomains, perspectives, etc based on a {@link DataSetDescription}
 * 
 * @author Alexander Lex
 * 
 */
public class DataLoader {

	/**
	 * Loads the datasets, groupings and does the processing of the data as
	 * defined in the {@link DataSetDescription}
	 */
	public static ATableBasedDataDomain loadData(DataSetDescription dataSetDescription)
			throws FileNotFoundException, IOException {
		ATableBasedDataDomain dataDomain = loadDataSet(dataSetDescription);
		loadGroupings(dataDomain, dataSetDescription);
		runDataProcessing(dataDomain, dataSetDescription);
		return dataDomain;
	}

	private static ATableBasedDataDomain loadDataSet(DataSetDescription dataSetDescription)
			throws FileNotFoundException, IOException {
		String dimensionIDCategory;
		String dimensionIDType;
		String recordIDCategory;
		String recordIDType;

		IDSpecification rowIDSpecification = dataSetDescription.getRowIDSpecification();
		if (rowIDSpecification == null) {
			rowIDSpecification = new IDSpecification();
			rowIDSpecification.setIDSpecification(dataSetDescription.getDataSetName()
					+ "_row", dataSetDescription.getDataSetName() + "_row");
			dataSetDescription.setRowIDSpecification(rowIDSpecification);
			Logger.log(new Status(Status.INFO, "DataLoader",
					"Automatically creating row ID specification for "
							+ dataSetDescription.getDataSetName()));

		}
		IDSpecification columnIDSpecification = dataSetDescription
				.getColumnIDSpecification();
		if (columnIDSpecification == null) {
			columnIDSpecification = new IDSpecification();
			columnIDSpecification.setIDSpecification(dataSetDescription.getDataSetName()
					+ "_column", dataSetDescription.getDataSetName() + "_column");
			dataSetDescription.setColumnIDSpecification(columnIDSpecification);
			Logger.log(new Status(Status.INFO, "DataLoader",
					"Automatically creating column ID specification for "
							+ dataSetDescription.getDataSetName()));
		}

		if (dataSetDescription.isTransposeMatrix()) {
			dimensionIDType = rowIDSpecification.getIdType();
			dimensionIDCategory = rowIDSpecification.getIdCategory();

			recordIDType = columnIDSpecification.getIdType();
			recordIDCategory = columnIDSpecification.getIdCategory();
		} else {
			dimensionIDType = columnIDSpecification.getIdType();
			dimensionIDCategory = columnIDSpecification.getIdCategory();

			recordIDType = rowIDSpecification.getIdType();
			recordIDCategory = rowIDSpecification.getIdCategory();
		}

		if (dimensionIDCategory == null)
			dimensionIDCategory = dimensionIDType;
		if (recordIDCategory == null)
			recordIDCategory = recordIDType;

		ATableBasedDataDomain dataDomain;

		DataDomainConfiguration dataDomainConfiguration = new DataDomainConfiguration();
		dataDomainConfiguration.setRecordIDCategory(recordIDCategory);

		dataDomainConfiguration.setHumanReadableRecordIDType(recordIDType);
		dataDomainConfiguration.setRecordDenominationPlural(recordIDType + "s");
		dataDomainConfiguration.setRecordDenominationSingular(recordIDType);

		dataDomainConfiguration.setDimensionIDCategory(dimensionIDCategory);
		dataDomainConfiguration.setHumanReadableDimensionIDType(dimensionIDType);
		dataDomainConfiguration.setDimensionDenominationSingular(dimensionIDType);
		dataDomainConfiguration.setDimensionDenominationPlural(dimensionIDType + "s");

		if ((rowIDSpecification.isIDTypeGene() && !dataSetDescription.isTransposeMatrix())
				|| (columnIDSpecification.isIDTypeGene() && dataSetDescription
						.isTransposeMatrix())) {
			dataDomainConfiguration.setPrimaryRecordMappingType("DAVID");
			dataDomainConfiguration.setPrimaryDimensionMappingType(dimensionIDType
					+ "_INT");
			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain("org.caleydo.datadomain.genetic",
							dataDomainConfiguration);
		} else if ((columnIDSpecification.isIDTypeGene() && !dataSetDescription
				.isTransposeMatrix())
				|| (rowIDSpecification.isIDTypeGene() && dataSetDescription
						.isTransposeMatrix())) {

			dataDomainConfiguration.setPrimaryDimensionMappingType("DAVID");
			dataDomainConfiguration.setPrimaryRecordMappingType(recordIDType + "_INT");
			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain("org.caleydo.datadomain.genetic",
							dataDomainConfiguration);

		} else {
			dataDomainConfiguration.setPrimaryRecordMappingType(recordIDType + "_INT");
			dataDomainConfiguration.setPrimaryDimensionMappingType(dimensionIDType);

			// TODO: check for plug-in?
			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain("org.caleydo.datadomain.generic",
							dataDomainConfiguration);
			// dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
			// .createDataDomain(GenericDataDomain.DATA_DOMAIN_TYPE,
			// dataDomainConfiguration);
		}

		dataDomain.setDataSetDescription(dataSetDescription);
		dataDomain.setColorMapper(ColorMapper
				.createDefaultMapper(EDefaultColorSchemes.BLUE_WHITE_RED));

		dataDomain.init();

		Thread thread = new Thread(dataDomain, dataDomain.getDataDomainType());
		thread.start();

		boolean createDefaultRecordPerspective = true;

		// the place the matrix is stored:
		DataTableUtils.loadData(dataDomain, dataSetDescription, true,
				createDefaultRecordPerspective);
		return dataDomain;

	}

	private static void runDataProcessing(ATableBasedDataDomain dataDomain,
			DataSetDescription dataSetDescription) {
		DataProcessingDescription dataProcessingDescription = dataSetDescription
				.getDataProcessingDescription();
		if (dataProcessingDescription == null)
			return;

		ArrayList<AClusterConfiguration> rowClusterConfigurations = dataProcessingDescription
				.getRowClusterConfigurations();
		if (rowClusterConfigurations != null) {
			for (AClusterConfiguration clusterConfiguration : rowClusterConfigurations) {
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

		ArrayList<AClusterConfiguration> columnClusterConfigurations = dataProcessingDescription
				.getColumnClusterConfigurations();
		if (columnClusterConfigurations != null) {
			for (AClusterConfiguration clusterConfiguration : columnClusterConfigurations) {
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

	private static void setUpRecordClustering(AClusterConfiguration clusterConfiguration,
			ATableBasedDataDomain dataDomain) {
		clusterConfiguration.setClusterTarget(EClustererTarget.RECORD_CLUSTERING);
		RecordPerspective targetRecordPerspective = new RecordPerspective(dataDomain);
		dataDomain.getTable().registerRecordPerspective(targetRecordPerspective);
		targetRecordPerspective.setLabel(clusterConfiguration.toString(), false);
		clusterConfiguration.setOptionalTargetRecordPerspective(targetRecordPerspective);
	}

	private static void setUpDimensionClustering(
			AClusterConfiguration clusterConfiguration, ATableBasedDataDomain dataDomain) {
		clusterConfiguration.setClusterTarget(EClustererTarget.DIMENSION_CLUSTERING);
		DimensionPerspective targetDimensionPerspective = new DimensionPerspective(
				dataDomain);
		dataDomain.getTable().registerDimensionPerspective(targetDimensionPerspective);

		targetDimensionPerspective.setLabel(clusterConfiguration.toString(), false);
		clusterConfiguration
				.setOptionalTargetDimensionPerspective(targetDimensionPerspective);
	}

	/**
	 * Loads all groupings for columns and rows that are specified in the
	 * {@link DataSetDescription}. Respects transposition.
	 * 
	 * @param dataSetDescription
	 */
	private static void loadGroupings(ATableBasedDataDomain dataDomain,
			DataSetDescription dataSetDescription) {
		ArrayList<GroupingParseSpecification> columnGroupingSpecifications = dataSetDescription
				.getColumnGroupingSpecifications();

		if (columnGroupingSpecifications != null) {

			IDType sourceIDType, targetIDType;
			if (dataSetDescription.isTransposeMatrix()) {
				sourceIDType = dataDomain.getHumanReadableRecordIDType();
				targetIDType = dataDomain.getRecordIDType();
			} else {
				sourceIDType = dataDomain.getHumanReadableDimensionIDType();
				targetIDType = dataDomain.getDimensionIDType();
			}

			ArrayList<PerspectiveInitializationData> columnPerspective = createPerspectivesForGroupings(
					columnGroupingSpecifications, sourceIDType, targetIDType);

			for (PerspectiveInitializationData data : columnPerspective) {
				if (dataSetDescription.isTransposeMatrix()) {
					RecordPerspective recordPerspective = new RecordPerspective(
							dataDomain);
					recordPerspective.init(data);

					dataDomain.getTable().registerRecordPerspective(recordPerspective);
				} else {
					DimensionPerspective dimensionPerspective = new DimensionPerspective(
							dataDomain);
					dimensionPerspective.init(data);

					dataDomain.getTable().registerDimensionPerspective(
							dimensionPerspective);
				}
			}
		}

		ArrayList<GroupingParseSpecification> rowGroupingSpecifications = dataSetDescription
				.getRowGroupingSpecifications();
		if (rowGroupingSpecifications != null) {

			IDType sourceIDType, targetIDType;
			if (dataSetDescription.isTransposeMatrix()) {
				sourceIDType = dataDomain.getHumanReadableDimensionIDType();
				targetIDType = dataDomain.getDimensionIDType();

			} else {
				sourceIDType = dataDomain.getHumanReadableRecordIDType();
				targetIDType = dataDomain.getRecordIDType();
			}

			ArrayList<PerspectiveInitializationData> rowPerspective = createPerspectivesForGroupings(
					rowGroupingSpecifications, sourceIDType, targetIDType);

			for (PerspectiveInitializationData data : rowPerspective) {
				if (dataSetDescription.isTransposeMatrix()) {
					DimensionPerspective dimensionPerspective = new DimensionPerspective(
							dataDomain);
					dimensionPerspective.init(data);

					dataDomain.getTable().registerDimensionPerspective(
							dimensionPerspective);

				} else {
					RecordPerspective recordPerspective = new RecordPerspective(
							dataDomain);
					recordPerspective.init(data);

					dataDomain.getTable().registerRecordPerspective(recordPerspective);
				}
			}
		}
	}

	private static ArrayList<PerspectiveInitializationData> createPerspectivesForGroupings(
			ArrayList<GroupingParseSpecification> groupingSpecifications,
			IDType sourceIDType, IDType targetIDType) {

		ArrayList<PerspectiveInitializationData> perspectiveDatas = new ArrayList<PerspectiveInitializationData>();
		for (GroupingParseSpecification groupingSpecification : groupingSpecifications) {
			GroupingParser parser = new GroupingParser(groupingSpecification);
			perspectiveDatas.addAll(parser.parseGrouping(targetIDType));
		}

		return perspectiveDatas;
	}

}
