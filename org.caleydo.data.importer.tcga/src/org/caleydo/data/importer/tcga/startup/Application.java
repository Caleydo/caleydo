package org.caleydo.data.importer.tcga.startup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.events.VADeltaEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.serialize.ProjectSaver;
import org.caleydo.core.util.clusterer.ClusterManager;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClustererType;
import org.caleydo.core.util.clusterer.initialization.EClustererAlgo;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication {

	public static String NILS_FILE =
		"/Users/nils/Data/Caleydo/testdata/20110728/ov/mrna_cnmf/outputprefix.expclu.gct";

	public static String ALEX_TEST_1 = "data/genome/microarray/tcga/cnmf.normalized.gct";
	public static String ALEX_TEST_1_GROUPING = "data/genome/microarray/tcga/cnmf.membership.txt";

	public static String ALEX_TEST_2 =
		"/home/alexsb/Dropbox/Omics Integration/testdata/20110728/gbm/mrna_cnmf/outputprefix.expclu.gct";
	public static String ALEX_TEST_2_GROUPING =
		"/home/alexsb/Dropbox/Omics Integration/testdata/20110728/gbm/mrna_cnmf/cnmf.membership.txt";

	/** class which takes the parameters for parsing */
	private LoadDataParameters loadDataParameters = new LoadDataParameters();

	private ATableBasedDataDomain dataDomain;

	public String dataSource = ALEX_TEST_1;
	public String groupingSource = ALEX_TEST_1_GROUPING;

	@Override
	public Object start(IApplicationContext context) throws Exception {

		GeneralManager.get().init();

		/*
		 * loadDataParameters
		 * .setFileName("data/genome/microarray/kashofer/all_hcc_clip_greater_10_plus_refseq.csv");
		 * loadDataParameters.setDelimiter(";"); // loadDataParameters.setMinDefined(true); //
		 * loadDataParameters.setMin(min); // loadDataParameters.setMaxDefined(true); //
		 * loadDataParameters.setMax(max); dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
		 * .createDataDomain("org.caleydo.datadomain.genetic"); loadDataParameters.setDataDomain(dataDomain);
		 * loadDataParameters.setFileIDType(dataDomain.getHumanReadableRecordIDType());
		 * loadDataParameters.setMathFilterMode("Normal"); loadDataParameters.setIsDataHomogeneous(true);
		 * loadDataParameters.setInputPattern("SKIP;FLOAT;FLOAT;"); List<String> dimensionLabels = new
		 * ArrayList<String>(); dimensionLabels.add("column 1"); dimensionLabels.add("column 2");
		 * loadDataParameters.setDimensionLabels(dimensionLabels);
		 * DataTableUtils.createDimensions(loadDataParameters); // the place the matrix is stored: DataTable
		 * table = DataTableUtils.createData(dataDomain); if (table == null) throw new
		 * IllegalStateException("Problem while creating table!"); // the default save path is usually your
		 * home directory new ProjectSaver().save(System.getProperty("user.home") +
		 * System.getProperty("file.separator") + "test.cal", true);
		 */

		convertGctFile(dataSource);

		loadClusterInfo(groupingSource);

		runClusteringOnRows();

		// the default save path is usually your home directory
		new ProjectSaver().save(System.getProperty("user.home") + System.getProperty("file.separator")
			+ "gct.cal", true);

		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
	}

	protected void convertGctFile(String fileName) throws FileNotFoundException, IOException {

		String delimiter = "\t";

		// open file to read second line to determine number of rows and columns
		BufferedReader reader = new BufferedReader(new FileReader(fileName));

		// skip header ("#1.2")
		// TODO: check if file is indeed a gct file
		reader.readLine();

		// read dimensions of data matrix
		String dimensionString = reader.readLine();

		// TODO: check if there are two numeric columns
		String[] dimensions = dimensionString.split(delimiter);

		int rows = new Integer(dimensions[0]);
		int columns = new Integer(dimensions[1]);

		// read column headers
		String headerString = reader.readLine();

		// TODO: check if there are as many column headers as there are columns (+ 2)
		String[] headers = headerString.split(delimiter);

		LoadDataParameters loadDataParameters = new LoadDataParameters();

		loadDataParameters.setFileName(fileName);
		loadDataParameters.setDelimiter(delimiter);
		loadDataParameters.setStartParseFileAtLine(3);

		// loadDataParameters.setMinDefined(true);
		// loadDataParameters.setMin(min);
		// loadDataParameters.setMaxDefined(true);
		// loadDataParameters.setMax(max);

		dataDomain =
			(GeneticDataDomain) DataDomainManager.get().createDataDomain("org.caleydo.datadomain.genetic");
		dataDomain.setColumnDimension(false);
		loadDataParameters.setDataDomain(dataDomain);

		loadDataParameters.setFileIDType(dataDomain.getHumanReadableRecordIDType());
		loadDataParameters.setMathFilterMode("Normal");
		loadDataParameters.setIsDataHomogeneous(true);

		// construct input pattern string based on number of columns in file
		StringBuffer buffer = new StringBuffer("SKIP;SKIP;");

		// list to store column labels
		List<String> columnLabels = new ArrayList<String>();

		for (int i = 0; i < columns; ++i) {
			buffer.append("FLOAT;");
			columnLabels.add(headers[i + 2]);
		}

		loadDataParameters.setInputPattern(buffer.toString());
		loadDataParameters.setColumnLabels(columnLabels);

		DataTableUtils.createColumns(loadDataParameters);

		// the place the matrix is stored:
		DataTable table = DataTableUtils.createData(dataDomain, true);
		if (table == null)
			throw new IllegalStateException("Problem while creating table!");

	}

	private void loadClusterInfo(String clusterFile) throws FileNotFoundException, IOException {

		String delimiter = "\t";

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

			// this is specific to the two files used
			String originalID = columns[0].replace("-", ".");
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
			dataDomain.getTable().registerRecordPerspecive(recordPerspective);

		}

	}

	private void runClusteringOnRows() {
		ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		clusterConfiguration.setClustererAlgo(EClustererAlgo.AFFINITY_PROPAGATION);
		clusterConfiguration.setAffinityPropClusterFactorGenes(9);
		clusterConfiguration.setClustererType(ClustererType.DIMENSION_CLUSTERING);

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
		dimensionPerspective.setLabel("Clustered Perspective");

		DimensionPerspective sampledDimensionPerspective = new DimensionPerspective(dataDomain);
		sampledDimensionPerspective.setLabel("Clustered and sampled Perspective");

		sampledDimensionPerspective.init(result.getDimensionResult());

		DimensionVADelta delta = new DimensionVADelta();
		DimensionVirtualArray va = sampledDimensionPerspective.getVirtualArray();
		int moduloFactor = va.size() / 50;
		for (int vaIndex = 0; vaIndex < va.size(); vaIndex++) {
			if (vaIndex % moduloFactor != 0)
				delta.add(VADeltaItem.removeElement(va.get(vaIndex)));
		
		}
		
		

		sampledDimensionPerspective.setVADelta(delta);

		dataDomain.getTable().registerDimensionPerspective(sampledDimensionPerspective);
	}
}