package org.caleydo.data.importer.tcga.startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.datadomain.DataDomainConfiguration;
import org.caleydo.core.util.mapping.color.EDefaultColorSchemes;

/**
 * Generator class that writes the loading information of a series of TCGA data sets to an XML file.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class TCGATestDataXMLGenerator {

	public static final String DROPBOX_GBM_FOLDER = System.getProperty("user.home")
		+ System.getProperty("file.separator") + "Dropbox/TCGA GDAC/Omics Integration/testdata/20110728/gbm/";

	public static final String MRNA = DROPBOX_GBM_FOLDER + "mrna_cnmf/outputprefix.expclu.gct";
	public static final String MRNA_GROUPING = DROPBOX_GBM_FOLDER + "mrna_cnmf/cnmf.membership.txt";

	public static final String MIRNA = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.normalized.gct";
	public static final String MIRNA_GROUPING = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.membership.txt";

	public static final String METHYLATION = DROPBOX_GBM_FOLDER + "methylation_cnmf/cnmf.normalized.gct";
	public static final String METHYLATION_GROUPING = DROPBOX_GBM_FOLDER
		+ "methylation_cnmf/cnmf.membership.txt";

	public static final String COPY_NUMBER = DROPBOX_GBM_FOLDER + "copy_number/all_thresholded_by_genes.txt";
	
	public static final String GROUND_TRUTH_GROUPING = DROPBOX_GBM_FOLDER + "ground_truth/2011_exp_assignments.txt";

	public static final String OUTPUT_FILE_PATH = System.getProperty("user.home")
		+ System.getProperty("file.separator") + "tcga_gbm_data.xml";

	/*
	public static final String DROPBOX_GBM_FOLDER = System.getProperty("user.home")
		+ System.getProperty("file.separator") + "Dropbox/TCGA GDAC/Omics Integration/testdata/20111026/brca/";

	public static final String MRNA = DROPBOX_GBM_FOLDER + "mrna_cnmf/outputprefix.expclu.gct";
	public static final String MRNA_GROUPING = DROPBOX_GBM_FOLDER + "mrna_cnmf/cnmf.membership.txt";

	public static final String MIRNA = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.normalized.gct";
	public static final String MIRNA_GROUPING = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.membership.txt";

	public static final String METHYLATION = DROPBOX_GBM_FOLDER + "methylation_cnmf/cnmf.normalized.gct";
	public static final String METHYLATION_GROUPING = DROPBOX_GBM_FOLDER
		+ "methylation_cnmf/cnmf.membership.txt";

	public static final String COPY_NUMBER = DROPBOX_GBM_FOLDER + "copy_number/all_thresholded_by_genes.txt";

	public static final String OUTPUT_FILE_PATH = System.getProperty("user.home")
		+ System.getProperty("file.separator") + "tcga_brca_data.xml";
	*/


	public static void main(String[] args) {

		ArrayList<DataSetMetaInfo> dataTypeSets = new ArrayList<DataSetMetaInfo>();
		dataTypeSets.add(setUpMRNAData());
		dataTypeSets.add(setUpMiRNAData());
		dataTypeSets.add(setUpMethylationData());
		dataTypeSets.add(setUpCopyNumberData());

		DataSetMetaInfoCollection dataTypeSetCollection = new DataSetMetaInfoCollection();
		dataTypeSetCollection.setDataTypeSetCollection(dataTypeSets);

		JAXBContext context = null;
		try {
			Class<?>[] serializableClasses = new Class<?>[3];
			serializableClasses[0] = DataSetMetaInfo.class;
			serializableClasses[1] = DataSetMetaInfoCollection.class;
			serializableClasses[2] = TCGAIDStringConverter.class;
			context = JAXBContext.newInstance(serializableClasses);

			Marshaller marshaller;
			marshaller = context.createMarshaller();
			marshaller.marshal(dataTypeSetCollection, new File(OUTPUT_FILE_PATH));

			System.out.println("Created configuration for " + dataTypeSets.size() + " datasets: "
				+ dataTypeSets);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	private static DataSetMetaInfo setUpMRNAData() {
		DataSetMetaInfo mrnaData = new DataSetMetaInfo();
		mrnaData.setName("mRNA data");
		mrnaData.setDataDomainType("org.caleydo.datadomain.genetic");
		mrnaData.setDataPath(MRNA);
		mrnaData.setGroupingPath(MRNA_GROUPING);
		mrnaData.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED.name());

		DataDomainConfiguration dataConfiguration = new DataDomainConfiguration();
		dataConfiguration.setMappingFile("data/bootstrap/bootstrap.xml");

		dataConfiguration.setRecordIDCategory("SAMPLE");
		dataConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
		dataConfiguration.setHumanReadableRecordIDType("SAMPLE");
		dataConfiguration.setRecordDenominationPlural("samples");
		dataConfiguration.setRecordDenominationSingular("sample");

		dataConfiguration.setDimensionIDCategory("GENE");
		dataConfiguration.setPrimaryDimensionMappingType("DAVID");
		dataConfiguration.setHumanReadableDimensionIDType("GENE_SYMBOL");
		dataConfiguration.setDimensionDenominationPlural("genes");
		dataConfiguration.setDimensionDenominationSingular("gene");
		
		mrnaData.setDataDomainConfiguration(dataConfiguration);

		mrnaData.setExternalGroupingPath(GROUND_TRUTH_GROUPING);
		
		doGCTSpecificStuff(mrnaData);
		return mrnaData;
	}

	private static DataSetMetaInfo setUpMiRNAData() {
		DataSetMetaInfo mirnaData = new DataSetMetaInfo();
		mirnaData.setName("miRNA data");
		mirnaData.setDataDomainType("org.caleydo.datadomain.generic");
		mirnaData.setDataPath(MIRNA);
		mirnaData.setGroupingPath(MIRNA_GROUPING);
		//mirnaData.setColorScheme(EDefaultColorSchemes.GREEN_WHITE_BROWN.name());
		mirnaData.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED.name());
		
		DataDomainConfiguration mirnaConfiguration = new DataDomainConfiguration();
		mirnaConfiguration.setRecordIDCategory("SAMPLE");
		mirnaConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
		mirnaConfiguration.setHumanReadableRecordIDType("SAMPLE");
		mirnaConfiguration.setRecordDenominationPlural("samples");
		mirnaConfiguration.setRecordDenominationSingular("sample");

		mirnaConfiguration.setDimensionIDCategory("miRNA");
		mirnaConfiguration.setHumanReadableDimensionIDType("miRNA");
		mirnaConfiguration.setDimensionDenominationPlural("miRNAs");
		mirnaConfiguration.setDimensionDenominationSingular("miRNA");

		mirnaData.setDataDomainConfiguration(mirnaConfiguration);
		doGCTSpecificStuff(mirnaData);
		return mirnaData;
	}

	private static DataSetMetaInfo setUpMethylationData() {
		DataSetMetaInfo methylationData = new DataSetMetaInfo();
		methylationData.setName("Methylation data");
		methylationData.setDataDomainType("org.caleydo.datadomain.generic");
		methylationData.setDataPath(METHYLATION);
		methylationData.setGroupingPath(METHYLATION_GROUPING);
		//methylationData.setColorScheme(EDefaultColorSchemes.GREEN_WHITE_PURPLE.name());
		methylationData.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED.name());

		DataDomainConfiguration methylationConfiguration = new DataDomainConfiguration();
		methylationConfiguration.setRecordIDCategory("SAMPLE");
		methylationConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
		methylationConfiguration.setHumanReadableRecordIDType("SAMPLE");
		methylationConfiguration.setRecordDenominationPlural("samples");
		methylationConfiguration.setRecordDenominationSingular("sample");

		methylationConfiguration.setDimensionIDCategory("Methylation");
		methylationConfiguration.setHumanReadableDimensionIDType("Methylation");
		methylationConfiguration.setDimensionDenominationPlural("methylations");
		methylationConfiguration.setDimensionDenominationSingular("methylation");

		methylationData.setDataDomainConfiguration(methylationConfiguration);
		doGCTSpecificStuff(methylationData);
		return methylationData;
	}

	private static DataSetMetaInfo setUpCopyNumberData() {
		DataSetMetaInfo copyNumberData = new DataSetMetaInfo();
		copyNumberData.setName("Copy number data");
		copyNumberData.setDataDomainType("org.caleydo.datadomain.genetic");
		copyNumberData.setDataPath(COPY_NUMBER);
		// methylationData.setGroupingPath(METHYLATION_GROUPING);
		//copyNumberData.setColorScheme(EDefaultColorSchemes.RED_YELLOW_BLUE_DIVERGING.name());
		copyNumberData.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED.name());
		
		DataDomainConfiguration copyNumberConfiguration = new DataDomainConfiguration();
		copyNumberConfiguration.setMappingFile("data/bootstrap/bootstrap.xml");
		copyNumberConfiguration.setRecordIDCategory("SAMPLE");
		copyNumberConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
		copyNumberConfiguration.setHumanReadableRecordIDType("SAMPLE");
		copyNumberConfiguration.setRecordDenominationPlural("samples");
		copyNumberConfiguration.setRecordDenominationSingular("sample");

		copyNumberConfiguration.setDimensionIDCategory("GENE");
		copyNumberConfiguration.setPrimaryDimensionMappingType("DAVID");
		copyNumberConfiguration.setHumanReadableDimensionIDType("GENE_SYMBOL");
		copyNumberConfiguration.setDimensionDenominationPlural("genes");
		copyNumberConfiguration.setDimensionDenominationSingular("gene");

		copyNumberData.setDataDomainConfiguration(copyNumberConfiguration);

		setUpLoadDataParameters(copyNumberData);

		doCopyNumberSpecificStuff(copyNumberData);

		return copyNumberData;
	}

	private static LoadDataParameters setUpLoadDataParameters(DataSetMetaInfo dataSetMetaInfo) {

		String delimiter = "\t";

		LoadDataParameters loadDataParameters = new LoadDataParameters();
		loadDataParameters.setLabel(dataSetMetaInfo.getName());
		loadDataParameters.setFileName(dataSetMetaInfo.getDataPath());
		loadDataParameters.setDelimiter(delimiter);

		loadDataParameters.setMathFilterMode("Normal");
		loadDataParameters.setIsDataHomogeneous(true);
		loadDataParameters.setColumnDimension(false);

		return loadDataParameters;
	}

	private static void doGCTSpecificStuff(DataSetMetaInfo dataSetMetaInfo) {

		dataSetMetaInfo.setRunClusteringOnRows(true);
		dataSetMetaInfo.setCreateGeneSamples(true);

		try {
			LoadDataParameters loadDataParameters = setUpLoadDataParameters(dataSetMetaInfo);

			loadDataParameters.setStartParseFileAtLine(3);

			// open file to read second line to determine number of rows and columns
			BufferedReader reader;

			reader = new BufferedReader(new FileReader(dataSetMetaInfo.getDataPath()));
			reader.readLine();

			// read dimensions of data matrix
			String dimensionString = reader.readLine();

			// TODO: check if there are two numeric columns
			String[] dimensions = dimensionString.split(loadDataParameters.getDelimiter());

			int columns = new Integer(dimensions[1]);

			// read column headers
			String headerString = reader.readLine();

			// TODO: check if there are as many column headers as there are columns (+ 2)
			String[] headers = headerString.split(loadDataParameters.getDelimiter());

			// loadDataParameters.setMinDefined(true);
			// loadDataParameters.setMin(min);
			// loadDataParameters.setMaxDefined(true);
			// loadDataParameters.setMax(max);

			StringBuffer inputPattern = new StringBuffer("SKIP;SKIP;");

			// list to store column labels
			List<String> columnLabels = new ArrayList<String>();

			for (int i = 0; i < columns; ++i) {
				inputPattern.append("FLOAT;");
				columnLabels.add(headers[i + 2]);
			}

			loadDataParameters.setInputPattern(inputPattern.toString());
			loadDataParameters.setColumnHeaderStringConverter(new TCGAIDStringConverter());
			loadDataParameters.setColumnLabels(columnLabels);

			dataSetMetaInfo.setLoadDataParameters(loadDataParameters);

		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static void doCopyNumberSpecificStuff(DataSetMetaInfo dataSetMetaInfo) {
		try {
			LoadDataParameters loadDataParameters = setUpLoadDataParameters(dataSetMetaInfo);

			loadDataParameters.setStartParseFileAtLine(1);

			// open file to read second line to determine number of rows and columns
			BufferedReader reader = new BufferedReader(new FileReader(dataSetMetaInfo.getDataPath()));

			// read dimensions of data matrix
			String dimensionString = reader.readLine();

			// TODO: check if there are two numeric columns
			String[] dimensions = dimensionString.split(loadDataParameters.getDelimiter());

			int columns = dimensions.length - 3;// new Integer(dimensions[1]);

			// read column headers
			// String headerString = reader.readLine();

			// TODO: check if there are as many column headers as there are columns (+ 2)
			// String[] headers = headerString.split(loadDataParameters.getDelimiter());

			// loadDataParameters.setMinDefined(true);
			// loadDataParameters.setMin(min);
			// loadDataParameters.setMaxDefined(true);
			// loadDataParameters.setMax(max);

			StringBuffer inputPattern = new StringBuffer("SKIP;SKIP;SKIP;");

			// list to store column labels
			List<String> columnLabels = new ArrayList<String>();

			for (int i = 0; i < columns; ++i) {
				inputPattern.append("FLOAT;");
				columnLabels.add(dimensions[i + 3]);
			}

			loadDataParameters.setInputPattern(inputPattern.toString());
			loadDataParameters.setColumnHeaderStringConverter(new TCGAIDStringConverter());
			loadDataParameters.setColumnLabels(columnLabels);
			dataSetMetaInfo.setLoadDataParameters(loadDataParameters);

		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
