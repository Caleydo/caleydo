package org.caleydo.data.importer.tcga.startup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainConfiguration;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
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
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.EDefaultColorSchemes;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication {

	public static final String CHECKED_IN_DATA = "data/genome/microarray/tcga/cnmf.normalized.gct";
	public static final String CHECKED_IN_DATA_GROUPING = "data/genome/microarray/tcga/cnmf.membership.txt";

	public static final String DROPBOX_GBM_FOLDER = System.getProperty("user.home")
		+ System.getProperty("file.separator") + "Dropbox/TCGA GDAC/Omics Integration/testdata/20110728/gbm/";

	public static final String MRNA = DROPBOX_GBM_FOLDER + "mrna_cnmf/outputprefix.expclu.gct";
	public static final String MRNA_GROUPING = DROPBOX_GBM_FOLDER + "mrna_cnmf/cnmf.membership.txt";

	public static final String MI_RNA = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.normalized.gct";
	public static final String MI_RNA_GROUPING = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.membership.txt";

	public static final String METHYLATION = DROPBOX_GBM_FOLDER + "methylation_cnmf/cnmf.normalized.gct";
	public static final String METHYLATION_GROUPING = DROPBOX_GBM_FOLDER
		+ "methylation_cnmf/cnmf.membership.txt";

	// Dropbox/TCGA GDAC/Omics Integration/testdata/20110728/gbm/methylation_cnmf/

	private ATableBasedDataDomain dataDomain;

	private boolean useQuickClustering = true;

	// public String dataSource = DROPBOX_GBM_MRNA;
	// public String groupingSource = DROPBOX_GBM_MRNA_GROUPING;

	@Override
	public Object start(IApplicationContext context) throws Exception {

		GeneralManager.get().init();

		// DataDomainConfiguration mrnaConfiguration = GeneticDataDomain.getConfigurationWithSamplesAsRows();

		DataDomainConfiguration mrnaConfiguration = new DataDomainConfiguration();
		mrnaConfiguration.setLabel("mRNA data");
		mrnaConfiguration.setMappingFile("data/bootstrap/bootstrap.xml");

		mrnaConfiguration.setColumnDimension(false);

		mrnaConfiguration.setRecordIDCategory("SAMPLE");
		mrnaConfiguration.setPrimaryRecordMappingType("SAMPLE");
		mrnaConfiguration.setHumanReadableRecordIDType("SAMPLE");
		mrnaConfiguration.setRecordDenominationPlural("samples");
		mrnaConfiguration.setRecordDenominationSingular("sample");

		mrnaConfiguration.setDimensionIDCategory("GENE");
		mrnaConfiguration.setPrimaryDimensionMappingType("DAVID");
		mrnaConfiguration.setHumanReadableDimensionIDType("GENE_SYMBOL");
		mrnaConfiguration.setDimensionDenominationPlural("genes");
		mrnaConfiguration.setDimensionDenominationSingular("gene");

		loadSources(MRNA, MRNA_GROUPING, "org.caleydo.datadomain.genetic",
			ColorMapper.createDefaultMapper(EDefaultColorSchemes.BLUE_WHITE_RED), mrnaConfiguration);

		DataDomainConfiguration mirnaConfiguration = new DataDomainConfiguration();
		mirnaConfiguration.setLabel("miRNA data");
		mirnaConfiguration.setColumnDimension(false);
		mirnaConfiguration.setRecordIDCategory("SAMPLE");
		mirnaConfiguration.setHumanReadableRecordIDType("SAMPLE");
		mirnaConfiguration.setRecordDenominationPlural("samples");
		mirnaConfiguration.setRecordDenominationSingular("sample");

		mirnaConfiguration.setDimensionIDCategory("miRNA");
		mirnaConfiguration.setHumanReadableDimensionIDType("miRNA");
		mirnaConfiguration.setDimensionDenominationPlural("miRNAs");
		mirnaConfiguration.setDimensionDenominationSingular("miRNA");

		loadSources(MI_RNA, MI_RNA_GROUPING, "org.caleydo.datadomain.generic",
			ColorMapper.createDefaultMapper(EDefaultColorSchemes.GREEN_WHITE_BROWN), mirnaConfiguration);

		DataDomainConfiguration methylationConfiguration = new DataDomainConfiguration();
		methylationConfiguration.setLabel("Methylation data");
		methylationConfiguration.setColumnDimension(false);
		methylationConfiguration.setRecordIDCategory("SAMPLE");
		methylationConfiguration.setHumanReadableRecordIDType("SAMPLE");
		methylationConfiguration.setRecordDenominationPlural("samples");
		methylationConfiguration.setRecordDenominationSingular("sample");

		methylationConfiguration.setDimensionIDCategory("Methylation");
		methylationConfiguration.setHumanReadableDimensionIDType("Methylation");
		methylationConfiguration.setDimensionDenominationPlural("methylations");
		methylationConfiguration.setDimensionDenominationSingular("methylation");

		loadSources(METHYLATION, METHYLATION_GROUPING, "org.caleydo.datadomain.generic",
			ColorMapper.createDefaultMapper(EDefaultColorSchemes.GREEN_WHITE_PURPLE),
			methylationConfiguration);

		// the default save path is usually your home directory
		new ProjectSaver().save(System.getProperty("user.home") + System.getProperty("file.separator")
			+ "export_" + (new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())) + ".cal", true);

		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
	}

	private void loadSources(String dataSource, String groupingSource, String dataDomainType,
		ColorMapper colorMapper, DataDomainConfiguration configuration) throws FileNotFoundException,
		IOException {

		convertGctFile(dataSource, dataDomainType, colorMapper, configuration);
		loadClusterInfo(groupingSource);

		PerspectiveInitializationData clusterResult = runClusteringOnRows();
		createSampleOfGenes(clusterResult);
	}

	protected void convertGctFile(String fileName, String dataDomainType, ColorMapper colorMapper,
		DataDomainConfiguration configuration) throws FileNotFoundException, IOException {
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
			(ATableBasedDataDomain) DataDomainManager.get().createDataDomain(dataDomainType, configuration);
		dataDomain.setColorMapper(colorMapper);
		// dataDomain.setColumnDimension(false);
		loadDataParameters.setDataDomain(dataDomain);

		loadDataParameters.setFileIDType(dataDomain.getHumanReadableDimensionIDType());
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
		loadDataParameters.setColumnHeaderStringConverter(new TCGAIDStringConverter());
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
}