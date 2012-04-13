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
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.importing.DataSetDescription;
import org.caleydo.core.data.importing.DataSetDescriptionCollection;
import org.caleydo.core.data.importing.GroupingParseSpecification;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VAUtils;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.parser.ascii.GroupingParser;
import org.caleydo.core.serialize.ProjectSaver;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClustererType;
import org.caleydo.core.util.clusterer.initialization.EClustererAlgo;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.clusterer.initialization.ETreeClustererAlgo;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.EDefaultColorSchemes;
import org.caleydo.datadomain.generic.GenericDataDomain;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	
	private boolean useQuickClustering = true;

	/** {link JAXBContext} for DataTypeSet (de-)serialization */
	private JAXBContext context;

	private String inputDataTypeSetCollectionFile = "";

	@Override
	public Object start(IApplicationContext context) throws Exception {

		String[] runConfigParameters = (String[]) context.getArguments().get(
				"application.args");
		String outputCaleydoProjectFile = "";

		if (runConfigParameters == null || runConfigParameters.length != 2) {

			inputDataTypeSetCollectionFile = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "tcga_gbm_data.xml";

			outputCaleydoProjectFile = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "export_"
					+ (new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())) + ".cal";
		} else {
			outputCaleydoProjectFile = runConfigParameters[0];
			inputDataTypeSetCollectionFile = runConfigParameters[1];
		}

		GeneralManager.get().init();
		createJAXBContext();
		DataSetDescriptionCollection dataSetMetInfoCollection = deserialzeDataSetMetaInfo();

		// Iterate over data type sets and trigger processing
		for (DataSetDescription dataTypeSet : dataSetMetInfoCollection
				.getDataSetDescriptionCollection())
			loadSources(dataTypeSet);

		// calculateVAIntersections();

		new ProjectSaver().save(outputCaleydoProjectFile, true);

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

	private void loadSources( DataSetDescription dataSetDescription)
			throws FileNotFoundException, IOException {

		ATableBasedDataDomain dataDomain =	loadData(dataSetDescription);
		loadGroupings(dataDomain, dataSetDescription);

		// if we don't have a row-grouping we create one
		if (dataSetDescription.getRowGroupingSpecifications() == null) {
			runClusteringOnRows(dataDomain, true, 4);

			createSampleOfGenes(dataDomain, runClusteringOnRows(dataDomain, true, 5).getDimensionResult());
			runClusteringOnRows(dataDomain, true, 6);

			// runClusteringOnRows(false, -1);
			// if (metaInfo.isCreateGeneSamples())

		}

	}

	protected ATableBasedDataDomain loadData(DataSetDescription dataSetDescription)
			throws FileNotFoundException, IOException {

		// if (dataSetDescription.getColumnIDSpecification().isIDTypeGene()
		// || dataSetDescription.getRowIDSpecification().isIDTypeGene()) {
		// dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
		// .createDataDomain(GeneticDataDomain.DATA_DOMAIN_TYPE);
		// } else {
		String dimensionType;
		String recordType;

		if (dataSetDescription.isTransposeMatrix()) {
			dimensionType = dataSetDescription.getRowIDSpecification().getIdType();
			recordType = dataSetDescription.getColumnIDSpecification().getIdType();
		} else {
			dimensionType = dataSetDescription.getColumnIDSpecification().getIdType();
			recordType = dataSetDescription.getRowIDSpecification().getIdType();
		}

		
		ATableBasedDataDomain dataDomain;
		if (dataSetDescription.getColumnIDSpecification().isIDTypeGene()
				|| dataSetDescription.getRowIDSpecification().isIDTypeGene()) {
			// we use the default provided by the data domain
			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain(GeneticDataDomain.DATA_DOMAIN_TYPE);

		} else {

			DataDomainConfiguration dataDomainConfiguration = new DataDomainConfiguration();
			dataDomainConfiguration.setRecordIDCategory(recordType);
			dataDomainConfiguration.setPrimaryRecordMappingType(recordType + "_INT");
			dataDomainConfiguration
					.setHumanReadableRecordIDType(recordType.toUpperCase());
			dataDomainConfiguration.setRecordDenominationPlural(recordType + "s");
			dataDomainConfiguration.setRecordDenominationSingular(recordType);

			dataDomainConfiguration.setDimensionIDCategory(dimensionType);
			dataDomainConfiguration.setHumanReadableDimensionIDType(dimensionType);
			dataDomainConfiguration.setDimensionDenominationSingular(dimensionType);
			dataDomainConfiguration.setDimensionDenominationPlural(dimensionType + "s");

			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.createDataDomain(GenericDataDomain.DATA_DOMAIN_TYPE,
							dataDomainConfiguration);
		}

		dataDomain.setDataSetDescription(dataSetDescription);
		dataDomain.setColorMapper(ColorMapper
				.createDefaultMapper(EDefaultColorSchemes.BLUE_WHITE_RED));

		dataDomain.init();

		// if (dataDomain.isColumnDimension())
		// loadDataParameters.setFileIDType(dataDomain.getHumanReadableRecordIDType());
		// else
		// loadDataParameters
		// .setFileIDType(dataDomain.getHumanReadableDimensionIDType());

		// loadDataParameters.setInputPattern(dataSetDescription.getParsingPattern());

		Thread thread = new Thread(dataDomain, dataDomain.getDataDomainType());
		thread.start();

		boolean createDefaultRecordPerspective = true;

		// the place the matrix is stored:
		DataTableUtils.loadData(dataDomain, dataSetDescription, true,
				createDefaultRecordPerspective);
		return dataDomain;

	}

	// private void loadClusterInfo() throws FileNotFoundException, IOException
	// {
	//
	// String clusterFile;
	//
	// String delimiter = "\t";
	//
	// if (clusterFile == null) {
	// Logger.log(new Status(Status.INFO, this.toString(),
	// "No Cluster Information specified"));
	// return;
	// }
	// // open file to read second line to determine number of rows and columns
	// BufferedReader reader = new BufferedReader(new FileReader(clusterFile));
	//
	// // skip header ("#1.2")
	// // TODO: check if file is indeed a gct file
	// reader.readLine();
	//
	// // read dimensions of data matrix
	//
	// ArrayList<HashMap<String, ArrayList<Integer>>> listOfGroupLists = new
	// ArrayList<HashMap<String, ArrayList<Integer>>>();
	//
	// int lineCounter = 0;
	// while (true) {
	//
	// String line = reader.readLine();
	// if (line == null)
	// break;
	// String[] columns = line.split(delimiter);
	//
	// TCGAIDStringConverter stringConverter = new TCGAIDStringConverter();
	// String originalID = stringConverter.convert(columns[0]);
	// // String originalID = columns[0];
	//
	// Integer mappedID = dataDomain.getRecordIDMappingManager().getID(
	// dataDomain.getHumanReadableRecordIDType(),
	// dataDomain.getRecordIDType(), originalID);
	//
	// for (int columnCount = 1; columnCount < columns.length; columnCount++) {
	// HashMap<String, ArrayList<Integer>> groupList;
	// if (lineCounter == 0) {
	// groupList = new HashMap<String, ArrayList<Integer>>();
	// listOfGroupLists.add(groupList);
	//
	// } else {
	// groupList = listOfGroupLists.get(columnCount - 1);
	// }
	//
	// ArrayList<Integer> group = groupList.get(columns[columnCount]);
	// if (group == null) {
	// group = new ArrayList<Integer>();
	// groupList.put(columns[columnCount], group);
	// }
	// group.add(mappedID);
	// }
	// lineCounter++;
	// }
	//
	// for (HashMap<String, ArrayList<Integer>> groupList : listOfGroupLists) {
	//
	// RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
	// recordPerspective.setLabel(groupList.size() + " clusters", false);
	// if (groupList.size() == 4)
	// recordPerspective.setDefault(true);
	// ArrayList<Integer> sortedIDs = new ArrayList<Integer>();
	// ArrayList<Integer> clusterSizes = new
	// ArrayList<Integer>(groupList.size());
	// ArrayList<Integer> sampleElements = new
	// ArrayList<Integer>(groupList.size());
	// int sampleIndex = 0;
	// for (ArrayList<Integer> group : groupList.values()) {
	//
	// sortedIDs.addAll(group);
	// clusterSizes.add(group.size());
	// sampleElements.add(sampleIndex);
	// sampleIndex += group.size();
	// }
	//
	// PerspectiveInitializationData data = new PerspectiveInitializationData();
	// data.setData(sortedIDs, clusterSizes, sampleElements);
	//
	// recordPerspective.init(data);
	// dataDomain.getTable().registerRecordPerspective(recordPerspective);
	// }
	// }

	/**
	 * Loads all groupings for columns and rows that are specified in the
	 * {@link DataSetDescription}. Respects transposition.
	 * 
	 * @param dataSetDescription
	 */
	private void loadGroupings(ATableBasedDataDomain dataDomain, DataSetDescription dataSetDescription) {
		ArrayList<GroupingParseSpecification> columnGroupingSpecifications = dataSetDescription
				.getColumnGroupingSpecifications();

		if (columnGroupingSpecifications != null) {

			IDType sourceIDType, targetIDType;
			if (dataSetDescription.isTransposeMatrix()) {
				sourceIDType = dataDomain.getHumanReadableRecordIDType();
				targetIDType = dataDomain.getRecordIDType();
			} else {
				sourceIDType = dataDomain.getHumanReadableDimensionIDType();
				targetIDType = dataDomain.getHumanReadableRecordIDType();
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
				targetIDType = dataDomain.getHumanReadableRecordIDType();

			} else {
				sourceIDType = dataDomain.getHumanReadableRecordIDType();
				targetIDType = dataDomain.getRecordIDType();
			}

			ArrayList<PerspectiveInitializationData> rowPerspective = createPerspectivesForGroupings(
					columnGroupingSpecifications, sourceIDType, targetIDType);

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

	/**
	 * Running this once with true creates a new dimension perspective with
	 * k-means. Running this with false creates another dimensionPerspective
	 * using affinity propagation
	 * 
	 * @param useKMeans
	 * @return
	 */
	private ClusterResult runClusteringOnRows(ATableBasedDataDomain dataDomain, boolean useKMeans, int numClusters) {
		ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setClustererType(ClustererType.DIMENSION_CLUSTERING);
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);

		String recordPerspectiveID = dataDomain.getTable().getDefaultRecordPerspective()
				.getID();
		String dimensionPerspectiveID = dataDomain.getTable()
				.getDefaultDimensionPerspective().getID();

		DimensionPerspective sourceDimensionPerspective = dataDomain.getTable()
				.getDimensionPerspective(dimensionPerspectiveID);

		if (useKMeans) {
			clusterConfiguration.setClustererAlgo(EClustererAlgo.KMEANS_CLUSTERER);
			clusterConfiguration.setkMeansNumberOfClustersForDimensions(numClusters);

			DimensionPerspective targetDimensionPerspective = new DimensionPerspective(
					dataDomain);
			dataDomain.getTable()
					.registerDimensionPerspective(targetDimensionPerspective);

			targetDimensionPerspective.setLabel("K-Means, " + numClusters + " Cluster",
					false);

			clusterConfiguration
					.setOptionalTargetDimensionPerspective(targetDimensionPerspective);
		} else {
			// here we create another dimensionPerspective which uses average
			// linkage hierarchical clustering
			clusterConfiguration.setClustererAlgo(EClustererAlgo.TREE_CLUSTERER);
			clusterConfiguration.setTreeClustererAlgo(ETreeClustererAlgo.AVERAGE_LINKAGE);

			sourceDimensionPerspective.setLabel("Average Linkage", false);
		}

		clusterConfiguration.setSourceRecordPerspective(dataDomain.getTable()
				.getRecordPerspective(recordPerspectiveID));
		clusterConfiguration.setSourceDimensionPerspective(sourceDimensionPerspective);

		return dataDomain.startClustering(clusterConfiguration);

		// dimensionPerspective.init(result.getDimensionResult());
		// dimensionPerspective.setLabel("All genes clustered, size: "
		// + dimensionPerspective.getVirtualArray().size(), false);
		//
		// return result.getDimensionResult();
	}

	private void createSampleOfGenes(ATableBasedDataDomain dataDomain, PerspectiveInitializationData clusterResult) {
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
					.unmarshal(new File(inputDataTypeSetCollectionFile));
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}

		return dataTypeSetCollection;
	}

	// private static void doGCTSpecificStuff(DataSetDescription
	// dataSetMetaInfo) {
	//
	//
	// try {
	// // LoadDataParameters loadDataParameters =
	// setUpLoadDataParameters(dataSetMetaInfo);
	//
	// loadDataParameters.setStartParseFileAtLine(3);
	//
	// // open file to read second line to determine number of rows and
	// // columns
	// BufferedReader reader;
	//
	// reader = new BufferedReader(new
	// FileReader(dataSetMetaInfo.getDataSourcePath()));
	// reader.readLine();
	//
	// // read dimensions of data matrix
	// String dimensionString = reader.readLine();
	//
	// // TODO: check if there are two numeric columns
	// String[] dimensions = dimensionString
	// .split(loadDataParameters.getDelimiter());
	//
	// int columns = new Integer(dimensions[1]);
	//
	// // read column headers
	// String headerString = reader.readLine();
	//
	// // TODO: check if there are as many column headers as there are
	// // columns (+ 2)
	// String[] headers = headerString.split(loadDataParameters.getDelimiter());
	//
	// // loadDataParameters.setMinDefined(true);
	// // loadDataParameters.setMin(min);
	// // loadDataParameters.setMaxDefined(true);
	// // loadDataParameters.setMax(max);
	//
	// StringBuffer inputPattern = new StringBuffer("SKIP;SKIP;");
	//
	// // list to store column labels
	// List<String> columnLabels = new ArrayList<String>();
	//
	// for (int i = 0; i < columns; ++i) {
	// inputPattern.append("FLOAT;");
	// columnLabels.add(headers[i + 2]);
	// }
	//
	// loadDataParameters.setInputPattern(inputPattern.toString());
	// loadDataParameters
	// .setColumnHeaderStringConverter(new TCGAIDStringConverter());
	// loadDataParameters.setColumnLabels(columnLabels);
	//
	// dataSetMetaInfo.setLoadDataParameters(loadDataParameters);
	//
	// } catch (Exception e) {
	// throw new IllegalStateException(e);
	// }
	// }
	//

}