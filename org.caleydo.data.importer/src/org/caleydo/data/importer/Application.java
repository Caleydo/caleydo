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
package org.caleydo.data.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainConfiguration;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VAUtils;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataProcessingDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescriptionCollection;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.parser.ascii.GroupingParser;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectSaver;
import org.caleydo.core.util.clusterer.initialization.AClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.EDefaultColorSchemes;
import org.caleydo.datadomain.generic.GenericDataDomain;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 * 
 * @author Alexander Lex
 * @author Marc Streit
 * @author Nils Gehlenborg
 */
public class Application implements IApplication {

	private boolean useQuickClustering = true;

	/** {link JAXBContext} for DataTypeSet (de-)serialization */
	private JAXBContext context;

	private String dataSetDescriptionFilePath = "";

	@Override
	public Object start(IApplicationContext context) throws Exception {

		String[] runConfigParameters = (String[]) context.getArguments().get(
				"application.args");
		String outputCaleydoProjectFilePath = "";

		if (runConfigParameters == null || runConfigParameters.length != 2) {

			dataSetDescriptionFilePath = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "caleydo_data.xml";

			outputCaleydoProjectFilePath = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "export_"
					+ (new SimpleDateFormat("yyyy.MM.dd_HH.mm").format(new Date()))
					+ ".cal";
		} else {
			outputCaleydoProjectFilePath = runConfigParameters[0];
			dataSetDescriptionFilePath = runConfigParameters[1];
		}

		GeneralManager.get().init();
		// FIXME: temp hack
		// GeneralManager.get().getBasicInfo().setOrganism(Organism.MUS_MUSCULUS);

		createJAXBContext();
		DataSetDescriptionCollection dataSetMetInfoCollection = deserialzeDataSetMetaInfo();

		// Iterate over data type sets and trigger processing
		for (DataSetDescription dataTypeSet : dataSetMetInfoCollection
				.getDataSetDescriptionCollection())
			loadSources(dataTypeSet);

		// calculateVAIntersections();

		new ProjectSaver().save(outputCaleydoProjectFilePath, true);

		return IApplication.EXIT_OK;
	}

	private void calculateVAIntersections() {
		ArrayList<RecordVirtualArray> vasToIntersect = new ArrayList<RecordVirtualArray>(
				5);
		// int loopCount = 0;
		ArrayList<ATableBasedDataDomain> dataDomains = DataDomainManager.get()
				.getDataDomainsByType(ATableBasedDataDomain.class);
		for (ATableBasedDataDomain dataDomain : dataDomains) {
			vasToIntersect.add(dataDomain.getTable().getDefaultRecordPerspective()
					.getVirtualArray());
		}
		List<RecordVirtualArray> intersectedVAs = VAUtils
				.createIntersectingVAs(vasToIntersect);

		for (int i = 0; i < dataDomains.size(); i++) {
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(intersectedVAs.get(i));
			RecordPerspective intersectedPerspective = new RecordPerspective(
					dataDomains.get(i));
			intersectedPerspective.setLabel("Intersected 4 Clusters", false);
			intersectedPerspective.setIDType(intersectedVAs.get(i).getIdType());
			intersectedPerspective.init(data);
			dataDomains.get(i).getTable()
					.registerRecordPerspective(intersectedPerspective);
		}
	}

	@Override
	public void stop() {
	}

	private void loadSources(DataSetDescription dataSetDescription)
			throws FileNotFoundException, IOException {

		ATableBasedDataDomain dataDomain = loadData(dataSetDescription);
		loadGroupings(dataDomain, dataSetDescription);

		runDataProcessing(dataDomain, dataSetDescription);

		// if (dataSetDescription.areAllColumnTypesContinuous()
		// && dataSetDescription.getRowGroupingSpecifications() == null
		// && dataSetDescription.isDataHomogeneous()) {

		// if (dataSetDescription.areAllColumnTypesContinuous()
		// && dataSetDescription.getRowGroupingSpecifications() == null
		// && dataSetDescription.isDataHomogeneous())
		//
		// runClusteringOnDimensions(dataDomain, true, 4);
		// }

		// }

		// createSampleOfGenes(dataDomain, runClusteringOnDimensions(dataDomain,
		// true, 5)
		// .getDimensionResult());

		// runClusteringOnDimensions(dataDomain, true, 6);

		// runClusteringOnRows(false, -1);
		// if (metaInfo.isCreateGeneSamples())

		// }

		// if we don't have a row-grouping we create one
		// if (dataSetDescription.areAllColumnTypesContinuous()
		// && dataSetDescription.getRowGroupingSpecifications() == null
		// && dataSetDescription.isDataHomogeneous()) {

		// runClusteringOnRecords(dataDomain, false, 4);

		// createSampleOfGenes(dataDomain,
		// runClusteringOnRecords(dataDomain, true, 5).getDimensionResult());

		// runClusteringOnRecords(dataDomain, true, 6);

		// runClusteringOnRecords(dataDomain, false, 6);

		// runClusteringOnRows(false, -1);
		// if (metaInfo.isCreateGeneSamples())

		// }

	}

	protected ATableBasedDataDomain loadData(DataSetDescription dataSetDescription)
			throws FileNotFoundException, IOException {

		String dimensionIDCategory;
		String dimensionIDType;
		String recordIDCategory;
		String recordIDType;

		if (dataSetDescription.isTransposeMatrix()) {
			dimensionIDType = dataSetDescription.getRowIDSpecification().getIdType();
			dimensionIDCategory = dataSetDescription.getRowIDSpecification()
					.getIdCategory();

			recordIDType = dataSetDescription.getColumnIDSpecification().getIdType();
			recordIDCategory = dataSetDescription.getColumnIDSpecification()
					.getIdCategory();
		} else {
			dimensionIDType = dataSetDescription.getColumnIDSpecification().getIdType();
			dimensionIDCategory = dataSetDescription.getColumnIDSpecification()
					.getIdCategory();

			recordIDType = dataSetDescription.getRowIDSpecification().getIdType();
			recordIDCategory = dataSetDescription.getRowIDSpecification().getIdCategory();
		}

		if (dimensionIDCategory == null)
			dimensionIDCategory = dimensionIDType;
		if (recordIDCategory == null)
			recordIDCategory = recordIDType;

		ATableBasedDataDomain dataDomain;
		if (dataSetDescription.getColumnIDSpecification().isIDTypeGene()
				|| dataSetDescription.getRowIDSpecification().isIDTypeGene()) {
			// we use the default provided by the data domain
			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain(GeneticDataDomain.DATA_DOMAIN_TYPE);

		} else {

			DataDomainConfiguration dataDomainConfiguration = new DataDomainConfiguration();
			dataDomainConfiguration.setRecordIDCategory(recordIDCategory);
			dataDomainConfiguration.setPrimaryRecordMappingType(recordIDType + "_INT");
			dataDomainConfiguration.setHumanReadableRecordIDType(recordIDType
					.toUpperCase());
			dataDomainConfiguration.setRecordDenominationPlural(recordIDType + "s");
			dataDomainConfiguration.setRecordDenominationSingular(recordIDType);

			dataDomainConfiguration.setDimensionIDCategory(dimensionIDCategory);
			dataDomainConfiguration.setHumanReadableDimensionIDType(dimensionIDType);
			dataDomainConfiguration.setDimensionDenominationSingular(dimensionIDType);
			dataDomainConfiguration.setDimensionDenominationPlural(dimensionIDType + "s");

			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain(GenericDataDomain.DATA_DOMAIN_TYPE,
							dataDomainConfiguration);
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

	/**
	 * Loads all groupings for columns and rows that are specified in the
	 * {@link DataSetDescription}. Respects transposition.
	 * 
	 * @param dataSetDescription
	 */
	private void loadGroupings(ATableBasedDataDomain dataDomain,
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

	private ArrayList<PerspectiveInitializationData> createPerspectivesForGroupings(
			ArrayList<GroupingParseSpecification> groupingSpecifications,
			IDType sourceIDType, IDType targetIDType) {

		ArrayList<PerspectiveInitializationData> perspectiveDatas = new ArrayList<PerspectiveInitializationData>();
		for (GroupingParseSpecification groupingSpecification : groupingSpecifications) {
			GroupingParser parser = new GroupingParser(groupingSpecification);
			perspectiveDatas.addAll(parser.parseGrouping(targetIDType));
		}

		return perspectiveDatas;
	}

	private void runDataProcessing(ATableBasedDataDomain dataDomain,
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

	private void setUpDimensionClustering(AClusterConfiguration clusterConfiguration,
			ATableBasedDataDomain dataDomain) {
		clusterConfiguration.setClusterTarget(EClustererTarget.DIMENSION_CLUSTERING);
		DimensionPerspective targetDimensionPerspective = new DimensionPerspective(
				dataDomain);
		dataDomain.getTable().registerDimensionPerspective(targetDimensionPerspective);

		targetDimensionPerspective.setLabel(clusterConfiguration.toString(), false);
		clusterConfiguration
				.setOptionalTargetDimensionPerspective(targetDimensionPerspective);
	}

	private void setUpRecordClustering(AClusterConfiguration clusterConfiguration,
			ATableBasedDataDomain dataDomain) {
		clusterConfiguration.setClusterTarget(EClustererTarget.RECORD_CLUSTERING);
		RecordPerspective targetRecordPerspective = new RecordPerspective(dataDomain);
		dataDomain.getTable().registerRecordPerspective(targetRecordPerspective);
		targetRecordPerspective.setLabel(clusterConfiguration.toString(), false);
		clusterConfiguration.setOptionalTargetRecordPerspective(targetRecordPerspective);
	}

	

	private void createSampleOfGenes(ATableBasedDataDomain dataDomain,
			PerspectiveInitializationData clusterResult) {
		if (clusterResult.getIndices().size() < 50)
			return;
		DimensionPerspective sampledDimensionPerspective = new DimensionPerspective(
				dataDomain);

		sampledDimensionPerspective.init(clusterResult);

		DimensionVADelta delta = new DimensionVADelta();
		DimensionVirtualArray va = sampledDimensionPerspective.getVirtualArray();
		int moduloFactor = va.size() / 50;
		for (int vaIndex = 0; vaIndex < va.size(); vaIndex++) {
			if (vaIndex % moduloFactor != 0)
				delta.add(VADeltaItem.removeElement(va.get(vaIndex)));
		}

		sampledDimensionPerspective.setVADelta(delta);

		sampledDimensionPerspective.setLabel("Clustered, sampled genes, size: "
				+ sampledDimensionPerspective.getVirtualArray().size(), false);
		dataDomain.getTable().registerDimensionPerspective(sampledDimensionPerspective);
	}

	private void createJAXBContext() {
		try {
			Class<?>[] serializableClasses = new Class<?>[2];
			serializableClasses[0] = DataSetDescription.class;
			serializableClasses[1] = DataSetDescriptionCollection.class;
			context = JAXBContext.newInstance(serializableClasses);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	private DataSetDescriptionCollection deserialzeDataSetMetaInfo() {

		DataSetDescriptionCollection dataTypeSetCollection = null;
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();

			dataTypeSetCollection = (DataSetDescriptionCollection) unmarshaller
					.unmarshal(new File(dataSetDescriptionFilePath));
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}

		return dataTypeSetCollection;
	}

}