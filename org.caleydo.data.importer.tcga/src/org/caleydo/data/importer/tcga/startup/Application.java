package org.caleydo.data.importer.tcga.startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.mapping.MappingType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VAUtils;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectSaver;
import org.caleydo.core.util.clusterer.ClusterManager;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClustererType;
import org.caleydo.core.util.clusterer.initialization.EClustererAlgo;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.EDefaultColorSchemes;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication {

	private ATableBasedDataDomain dataDomain;

	private boolean useQuickClustering = true;

	/** {link JAXBContext} for DataTypeSet (de-)serialization */
	private JAXBContext context;

	private String inputDataTypeSetCollectionFile = "";

	@Override
	public Object start(IApplicationContext context) throws Exception {

		String[] runConfigParameters = (String[]) context.getArguments().get("application.args");
		String outputCaleydoProjectFile = "";

		if (runConfigParameters == null || runConfigParameters.length != 2) {

			inputDataTypeSetCollectionFile =
				System.getProperty("user.home") + System.getProperty("file.separator") + "tcga_gbm_data.xml";

			outputCaleydoProjectFile =
				System.getProperty("user.home") + System.getProperty("file.separator") + "export_"
					+ (new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())) + ".cal";
		}
		else {
			outputCaleydoProjectFile = runConfigParameters[0];
			inputDataTypeSetCollectionFile = runConfigParameters[1];
		}

		GeneralManager.get().init();
		createJAXBContext();
		DataSetMetaInfoCollection dataSetMetInfoCollection = deserialzeDataSetMetaInfo();

		boolean isColumnDimension = false;

		// Iterate over data type sets and trigger processing
		for (DataSetMetaInfo dataTypeSet : dataSetMetInfoCollection.getDataTypeSetCollection())
			loadSources(dataTypeSet, isColumnDimension);

//		calculateVAIntersections();

		new ProjectSaver().save(outputCaleydoProjectFile, true);

		return IApplication.EXIT_OK;
	}

	private void calculateVAIntersections() {
		ArrayList<RecordVirtualArray> vasToIntersect = new ArrayList<RecordVirtualArray>(5);
		// int loopCount = 0;
		ArrayList<ATableBasedDataDomain> dataDomains =
			DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class);
		for (ATableBasedDataDomain dataDomain : dataDomains) {
			vasToIntersect.add(dataDomain.getTable().getDefaultRecordPerspective().getVirtualArray());
		}
		List<RecordVirtualArray> intersectedVAs = VAUtils.createIntersectingVAs(vasToIntersect);

		for (int i = 0; i < dataDomains.size(); i++) {
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(intersectedVAs.get(i));
			RecordPerspective intersectedPerspective = new RecordPerspective(dataDomains.get(i));
			intersectedPerspective.setLabel("Intersected 4 Clusters");
			intersectedPerspective.setIDType(intersectedVAs.get(i).getIdType());
			intersectedPerspective.init(data);
			dataDomains.get(i).getTable().registerRecordPerspective(intersectedPerspective);
		}
	}
	
	@Override
	public void stop() {
	}

	private void loadSources(DataSetMetaInfo metaInfo, boolean isColumnDimension)
		throws FileNotFoundException, IOException {

		loadData(metaInfo);
		loadClusterInfo(metaInfo.getGroupingPath());

		// if (metaInfo.isRunClusteringOnRows()) {
		PerspectiveInitializationData clusterResult = runClusteringOnRows();
		// if (metaInfo.isCreateGeneSamples())
		createSampleOfGenes(clusterResult);
		// }

	}

	protected void loadData(DataSetMetaInfo dataSetMetaInfo) throws FileNotFoundException, IOException {

		LoadDataParameters loadDataParameters = dataSetMetaInfo.getLoadDataParameters();
		loadDataParameters.setColumnHeaderStringConverter(new TCGAIDStringConverter());
		dataDomain =
			(ATableBasedDataDomain) DataDomainManager.get().createDataDomain(
				dataSetMetaInfo.getDataDomainType(), dataSetMetaInfo.getDataDomainConfiguration());

		loadDataParameters.setDataDomain(dataDomain);
		dataDomain.setColorMapper(ColorMapper.createDefaultMapper(EDefaultColorSchemes
			.valueOf(dataSetMetaInfo.getColorScheme())));

		dataDomain.init();
		loadDataParameters.setFileIDType(dataDomain.getHumanReadableDimensionIDType());
		Thread thread = new Thread(dataDomain, dataDomain.getDataDomainType());
		thread.start();

		// construct input pattern string based on number of columns in file
		DataTableUtils.createColumns(loadDataParameters);

		boolean createDefaultRecordPerspective = false;
		if (dataSetMetaInfo.getGroupingPath() == null)
			createDefaultRecordPerspective = true;
		// the place the matrix is stored:
		DataTable table = DataTableUtils.createData(dataDomain, true, createDefaultRecordPerspective);
		if (table == null)
			throw new IllegalStateException("Problem while creating table!");
	}

	private void loadClusterInfo(String clusterFile) throws FileNotFoundException, IOException {

		String delimiter = "\t";

		if (clusterFile == null) {
			Logger.log(new Status(Status.INFO, this.toString(), "No Cluster Information specified"));
			return;
		}
		// open file to read second line to determine number of rows and columns
		BufferedReader reader = new BufferedReader(new FileReader(clusterFile));

		// skip header ("#1.2")
		// TODO: check if file is indeed a gct file
		reader.readLine();

		// read dimensions of data matrix

		ArrayList<HashMap<String, ArrayList<Integer>>> listOfGroupLists =
			new ArrayList<HashMap<String, ArrayList<Integer>>>();

		int lineCounter = 0;
		while (true) {

			String line = reader.readLine();
			if (line == null)
				break;
			String[] columns = line.split(delimiter);

			TCGAIDStringConverter stringConverter = new TCGAIDStringConverter();
			String originalID = stringConverter.convert(columns[0]);
			// String originalID = columns[0];

			Integer mappedID =
				dataDomain.getRecordIDMappingManager().getID(dataDomain.getHumanReadableRecordIDType(),
					dataDomain.getRecordIDType(), originalID);

			for (int columnCount = 1; columnCount < columns.length; columnCount++) {
				HashMap<String, ArrayList<Integer>> groupList;
				if (lineCounter == 0) {
					groupList = new HashMap<String, ArrayList<Integer>>();
					listOfGroupLists.add(groupList);

				}
				else {
					groupList = listOfGroupLists.get(columnCount - 1);
				}

				ArrayList<Integer> group = groupList.get(columns[columnCount]);
				if (group == null) {
					group = new ArrayList<Integer>();
					groupList.put(columns[columnCount], group);
				}
				group.add(mappedID);
			}
			lineCounter++;
		}

		for (HashMap<String, ArrayList<Integer>> groupList : listOfGroupLists) {

			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			recordPerspective.setLabel(groupList.size() + " clusters");
			if (groupList.size() == 4)
				recordPerspective.setDefault(true);
			ArrayList<Integer> sortedIDs = new ArrayList<Integer>();
			ArrayList<Integer> clusterSizes = new ArrayList<Integer>(groupList.size());
			ArrayList<Integer> sampleElements = new ArrayList<Integer>(groupList.size());
			int sampleIndex = 0;
			for (ArrayList<Integer> group : groupList.values()) {

				sortedIDs.addAll(group);
				clusterSizes.add(group.size());
				sampleElements.add(sampleIndex);
				sampleIndex += group.size();
			}

			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(sortedIDs, clusterSizes, sampleElements);

			recordPerspective.init(data);
			dataDomain.getTable().registerRecordPerspective(recordPerspective);
		}
	}

	private PerspectiveInitializationData runClusteringOnRows() {
		ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setClustererType(ClustererType.DIMENSION_CLUSTERING);
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		if (useQuickClustering) {
			clusterConfiguration.setClustererAlgo(EClustererAlgo.KMEANS_CLUSTERER);
			clusterConfiguration.setkMeansNumberOfClustersForDimensions(5);

		}
		else {
			clusterConfiguration.setClustererAlgo(EClustererAlgo.AFFINITY_PROPAGATION);
			clusterConfiguration.setAffinityPropClusterFactorGenes(9);
		}

		String recordPerspectiveID = dataDomain.getTable().getRecordPerspectiveIDs().iterator().next();
		String dimensionPerspectiveID = dataDomain.getTable().getDimensionPerspectiveIDs().iterator().next();

		DimensionPerspective dimensionPerspective =
			dataDomain.getTable().getDimensionPerspective(dimensionPerspectiveID);

		clusterConfiguration.setSourceRecordPerspective(dataDomain.getTable().getRecordPerspective(
			recordPerspectiveID));
		clusterConfiguration.setSourceDimensionPerspective(dimensionPerspective);

		ClusterManager clusterManager = new ClusterManager(dataDomain);
		ClusterResult result = clusterManager.cluster(clusterConfiguration);

		dimensionPerspective.init(result.getDimensionResult());
		dimensionPerspective.setLabel("All genes clustered, size: "
			+ dimensionPerspective.getVirtualArray().size());

		return result.getDimensionResult();
	}

	private void createSampleOfGenes(PerspectiveInitializationData clusterResult) {
		DimensionPerspective sampledDimensionPerspective = new DimensionPerspective(dataDomain);

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
			+ sampledDimensionPerspective.getVirtualArray().size());
		dataDomain.getTable().registerDimensionPerspective(sampledDimensionPerspective);
	}

	private void createJAXBContext() {
		try {
			Class<?>[] serializableClasses = new Class<?>[3];
			serializableClasses[0] = DataSetMetaInfo.class;
			serializableClasses[1] = DataSetMetaInfoCollection.class;
			serializableClasses[2] = TCGAIDStringConverter.class;
			context = JAXBContext.newInstance(serializableClasses);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	private DataSetMetaInfoCollection deserialzeDataSetMetaInfo() {

		DataSetMetaInfoCollection dataTypeSetCollection = null;
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();

			dataTypeSetCollection =
				(DataSetMetaInfoCollection) unmarshaller.unmarshal(new File(inputDataTypeSetCollectionFile));
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}

		return dataTypeSetCollection;
	}
}