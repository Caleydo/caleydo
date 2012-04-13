package org.caleydo.data.importer.tcga;

import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.data.importing.DataSetDescription;
import org.caleydo.core.data.importing.DataSetDescriptionCollection;
import org.caleydo.core.data.importing.GroupingParseSpecification;
import org.caleydo.core.data.importing.IDSpecification;
import org.caleydo.core.data.importing.ParsingRule;
import org.caleydo.core.util.collection.Pair;

/**
 * Generator class that writes the loading information of a series of TCGA data
 * sets to an XML file.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class TCGATestDataXMLGenerator {

	public static final String DROPBOX_GBM_FOLDER = System.getProperty("user.home")
			+ System.getProperty("file.separator")
			+ "Dropbox/TCGA GDAC/Omics Integration/testdata/20110728/gbm/";

	public static final String MRNA = DROPBOX_GBM_FOLDER
			+ "mrna_cnmf/outputprefix.expclu.gct";
	public static final String MRNA_GROUPING = DROPBOX_GBM_FOLDER
			+ "mrna_cnmf/cnmf.membership.txt";

	public static final String MIRNA = DROPBOX_GBM_FOLDER
			+ "mir_cnmf/cnmf.normalized.gct";
	public static final String MIRNA_GROUPING = DROPBOX_GBM_FOLDER
			+ "mir_cnmf/cnmf.membership.txt";

	public static final String METHYLATION = DROPBOX_GBM_FOLDER
			+ "methylation_cnmf/cnmf.normalized.gct";
	public static final String METHYLATION_GROUPING = DROPBOX_GBM_FOLDER
			+ "methylation_cnmf/cnmf.membership.txt";

	public static final String COPY_NUMBER = DROPBOX_GBM_FOLDER
			+ "copy_number/all_thresholded_by_genes.txt";

	public static final String GROUND_TRUTH_GROUPING = DROPBOX_GBM_FOLDER
			+ "ground_truth/2011_exp_assignments.txt";

	public static final String CLINICAL = DROPBOX_GBM_FOLDER
			+ "clinical/clinical_patient_public_GBM.txt";

	public static final String MUTATION = DROPBOX_GBM_FOLDER
			+ "mutation/mut_patient_centric_table_public_transposed_01.txt";

	public static final String OUTPUT_FILE_PATH = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "tcga_gbm_data.xml";

	public static final String TCGA_ID_SUBSTRING_REGEX = "TCGA\\-|\\-...\\-";

	private IDSpecification sampleIDSpecification;

	/*
	 * public static final String DROPBOX_GBM_FOLDER =
	 * System.getProperty("user.home") + System.getProperty("file.separator") +
	 * "Dropbox/TCGA GDAC/Omics Integration/testdata/20111026/brca/"; public
	 * static final String MRNA = DROPBOX_GBM_FOLDER +
	 * "mrna_cnmf/outputprefix.expclu.gct"; public static final String
	 * MRNA_GROUPING = DROPBOX_GBM_FOLDER + "mrna_cnmf/cnmf.membership.txt";
	 * public static final String MIRNA = DROPBOX_GBM_FOLDER +
	 * "mir_cnmf/cnmf.normalized.gct"; public static final String MIRNA_GROUPING
	 * = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.membership.txt"; public static
	 * final String METHYLATION = DROPBOX_GBM_FOLDER +
	 * "methylation_cnmf/cnmf.normalized.gct"; public static final String
	 * METHYLATION_GROUPING = DROPBOX_GBM_FOLDER +
	 * "methylation_cnmf/cnmf.membership.txt"; public static final String
	 * COPY_NUMBER = DROPBOX_GBM_FOLDER +
	 * "copy_number/all_thresholded_by_genes.txt"; public static final String
	 * OUTPUT_FILE_PATH = System.getProperty("user.home") +
	 * System.getProperty("file.separator") + "tcga_brca_data.xml";
	 */

	public static void main(String[] args) {

		TCGATestDataXMLGenerator generator = new TCGATestDataXMLGenerator();
		generator.run();
	}

	private void run() {
		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdType("SAMPLE");
		sampleIDSpecification.setReplacementExpression("\\.", "-");
		sampleIDSpecification.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);

		ArrayList<DataSetDescription> dataSetDescriptions = new ArrayList<DataSetDescription>();
		dataSetDescriptions.add(setUpMRNAData());
		// dataSetDescriptions.add(setUpMutationData());
		dataSetDescriptions.add(setUpMiRNAData());
		dataSetDescriptions.add(setUpMethylationData());
		// dataSetDescriptions.add(setUpCopyNumberData());
		// dataSetDescriptions.add(setUpClinicalData());

		DataSetDescriptionCollection dataSetDescriptionCollection = new DataSetDescriptionCollection();
		dataSetDescriptionCollection.setDataSetDescriptionCollection(dataSetDescriptions);

		JAXBContext context = null;
		try {
			Class<?>[] serializableClasses = new Class<?>[2];
			serializableClasses[0] = DataSetDescription.class;
			serializableClasses[1] = DataSetDescriptionCollection.class;

			context = JAXBContext.newInstance(serializableClasses);

			Marshaller marshaller;
			marshaller = context.createMarshaller();
			marshaller.marshal(dataSetDescriptionCollection, new File(OUTPUT_FILE_PATH));

			System.out.println("Created configuration for " + dataSetDescriptions.size()
					+ " datasets: " + dataSetDescriptions);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	private DataSetDescription setUpMRNAData() {
		DataSetDescription mrnaData = new DataSetDescription();
		mrnaData.setDataSetName("mRNA");

		mrnaData.setDataSourcePath(MRNA);
		mrnaData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setDataType("FLOAT");
		mrnaData.addParsingRule(parsingRule);
		mrnaData.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mrnaData.setRowIDSpecification(geneIDSpecification);
		mrnaData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseClustering = new GroupingParseSpecification(
				MRNA_GROUPING);
		firehoseClustering.setContainsColumnIDs(false);
		firehoseClustering.setRowIDSpecification(sampleIDSpecification);
		mrnaData.addColumnGroupingSpecification(firehoseClustering);

		GroupingParseSpecification groundTruthGrouping = new GroupingParseSpecification();
		groundTruthGrouping.setDataSourcePath(GROUND_TRUTH_GROUPING);
		groundTruthGrouping.addColum(2);
		groundTruthGrouping.setRowIDSpecification(sampleIDSpecification);
		mrnaData.addColumnGroupingSpecification(groundTruthGrouping);

		return mrnaData;
	}

	private DataSetDescription setUpMiRNAData() {
		DataSetDescription mirnaData = new DataSetDescription();
		mirnaData.setDataSetName("miRNA");

		mirnaData.setDataSourcePath(MIRNA);
		mirnaData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setDataType("FLOAT");
		mirnaData.addParsingRule(parsingRule);

		IDSpecification mirnaIDSpecification = new IDSpecification();
		mirnaIDSpecification.setIdType("miRNA");
		mirnaData.setRowIDSpecification(mirnaIDSpecification);
		mirnaData.setTransposeMatrix(true);

		IDSpecification sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdType("SAMPLE");
		sampleIDSpecification.setReplacementExpression("\\.", "-");
		sampleIDSpecification.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);
		mirnaData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseClustering = new GroupingParseSpecification(
				MIRNA_GROUPING);
		firehoseClustering.setContainsColumnIDs(false);
		firehoseClustering.setRowIDSpecification(sampleIDSpecification);
		mirnaData.addColumnGroupingSpecification(firehoseClustering);

		return mirnaData;
	}

	private DataSetDescription setUpMethylationData() {
		DataSetDescription methylationData = new DataSetDescription();
		methylationData.setDataSetName("Methylation");

		methylationData.setDataSourcePath(METHYLATION);
		methylationData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setDataType("FLOAT");
		methylationData.addParsingRule(parsingRule);
		methylationData.setTransposeMatrix(true);

		IDSpecification sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdType("SAMPLE");
		sampleIDSpecification.setReplacementExpression("\\.", "-");
		sampleIDSpecification.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);

		IDSpecification methylationIDSpecification = new IDSpecification();
		methylationIDSpecification.setIdType("methylation");
		methylationData.setRowIDSpecification(methylationIDSpecification);
		methylationData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseClustering = new GroupingParseSpecification(
				METHYLATION_GROUPING);
		firehoseClustering.setContainsColumnIDs(false);
		firehoseClustering.setRowIDSpecification(sampleIDSpecification);
		methylationData.addColumnGroupingSpecification(firehoseClustering);

		return methylationData;
	}
	//
	// private static DataSetDescription setUpCopyNumberData() {
	// DataSetDescription copyNumberData = new DataSetDescription();
	// copyNumberData.setDataSetName("Copy number");
	// copyNumberData.setDataDomainType("org.caleydo.datadomain.genetic");
	// copyNumberData.setDataPath(COPY_NUMBER);
	// // methylationData.setGroupingPath(METHYLATION_GROUPING);
	// //
	// copyNumberData.setColorScheme(EDefaultColorSchemes.RED_YELLOW_BLUE_DIVERGING.name());
	//
	// DataDomainConfiguration copyNumberConfiguration = new
	// DataDomainConfiguration();
	// copyNumberConfiguration.setMappingFile("data/bootstrap/bootstrap.xml");
	// copyNumberConfiguration.setRecordIDCategory("SAMPLE");
	// copyNumberConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
	// copyNumberConfiguration.setHumanReadableRecordIDType("SAMPLE");
	// copyNumberConfiguration.setRecordDenominationPlural("samples");
	// copyNumberConfiguration.setRecordDenominationSingular("sample");
	//
	// copyNumberConfiguration.setDimensionIDCategory("GENE");
	// copyNumberConfiguration.setPrimaryDimensionMappingType("DAVID");
	// copyNumberConfiguration.setHumanReadableDimensionIDType("GENE_SYMBOL");
	// copyNumberConfiguration.setDimensionDenominationPlural("genes");
	// copyNumberConfiguration.setDimensionDenominationSingular("gene");
	//
	// copyNumberData.setDataDomainConfiguration(copyNumberConfiguration);
	//
	// setUpLoadDataParameters(copyNumberData);
	//
	// doCopyNumberSpecificStuff(copyNumberData);
	//
	// return copyNumberData;
	// }
	//
	// private static DataSetDescription setUpClinicalData() {
	// DataSetDescription clinicalData = new DataSetDescription();
	// clinicalData.setDataSetName("Clinical");
	// clinicalData.setDataDomainType("org.caleydo.datadomain.generic");
	// clinicalData.setDataPath(CLINICAL);
	//
	// DataDomainConfiguration clinicalConfiguration = new
	// DataDomainConfiguration();
	// clinicalConfiguration.setRecordIDCategory("SAMPLE");
	// clinicalConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
	// clinicalConfiguration.setHumanReadableRecordIDType("SAMPLE");
	// clinicalConfiguration.setRecordDenominationPlural("samples");
	// clinicalConfiguration.setRecordDenominationSingular("sample");
	//
	// clinicalConfiguration.setDimensionIDCategory("CLINICAL");
	// clinicalConfiguration.setHumanReadableDimensionIDType("Clinical");
	// clinicalConfiguration.setDimensionDenominationPlural("Clinical");
	// clinicalConfiguration.setDimensionDenominationSingular("Clinical");
	//
	// clinicalData.setDataDomainConfiguration(clinicalConfiguration);
	//
	// String delimiter = "\t";
	//
	// LoadDataParameters loadDataParameters = new LoadDataParameters();
	// loadDataParameters.setLabel(clinicalData.getDataSetName());
	// loadDataParameters.setFileName(clinicalData.getDataPath());
	// loadDataParameters.setDelimiter(delimiter);
	//
	// loadDataParameters.setMathFilterMode("Normal");
	// loadDataParameters.setIsDataHomogeneous(true);
	// loadDataParameters.setColumnDimension(true);
	//
	// try {
	// loadDataParameters.setStartParseFileAtLine(1);
	//
	// // open file to read second line to determine number of rows and
	// // columns
	// BufferedReader reader = new BufferedReader(new FileReader(
	// clinicalData.getDataPath()));
	//
	// // read dimensions of data matrix
	// // String dimensionString = reader.readLine();
	//
	// // TODO: check if there are two numeric columns
	// // String[] dimensions =
	// // dimensionString.split(loadDataParameters.getDelimiter());
	//
	// StringBuffer inputPattern = new StringBuffer(
	// "SKIP;SKIP;SKIP;SKIP;SKIP;SKIP;SKIP;SKIP;SKIP;SKIP;SKIP;FLOAT;SKIP;FLOAT;FLOAT;FLOAT;FLOAT;ABORT;");
	//
	// // list to store column labels
	// List<String> columnLabels = new ArrayList<String>();
	// columnLabels.add("Days to birth");
	// columnLabels.add("Days to death");
	// columnLabels.add("Days to last followup");
	// columnLabels.add("Days to tumor progression");
	// columnLabels.add("Days to tumor recurrence");
	//
	// loadDataParameters.setInputPattern(inputPattern.toString());
	// loadDataParameters.setRowIDStringConverter(new TCGAIDStringConverter());
	// loadDataParameters.setColumnLabels(columnLabels);
	// clinicalData.setLoadDataParameters(loadDataParameters);
	//
	// } catch (Exception e) {
	// throw new IllegalStateException(e);
	// }
	//
	// // doCopyNumberSpecificStuff(clinicalConfiguration);
	//
	// return clinicalData;
	// }
	//
	// private static DataSetDescription setUpMutationData() {
	// DataSetDescription mutationDataMetaInfo = new DataSetDescription();
	// mutationDataMetaInfo.setDataSetName("Mutation Status");
	// mutationDataMetaInfo.setDataDomainType("org.caleydo.datadomain.genetic");
	// mutationDataMetaInfo.setDataPath(MUTATION);
	//
	// CategoryProperties categoryProperties = new CategoryProperties();
	// categoryProperties.setNumberOfCategories(2);
	// categoryProperties.setColorScheme(EDefaultColorSchemes.WHITE_RED.name());
	//
	// DataDomainConfiguration dataConfiguration = new
	// DataDomainConfiguration();
	// dataConfiguration.setMappingFile("data/bootstrap/bootstrap.xml");
	//
	// dataConfiguration.setRecordIDCategory("SAMPLE");
	// dataConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
	// dataConfiguration.setHumanReadableRecordIDType("SAMPLE");
	// dataConfiguration.setRecordDenominationPlural("samples");
	// dataConfiguration.setRecordDenominationSingular("sample");
	//
	// dataConfiguration.setDimensionIDCategory("GENE");
	// dataConfiguration.setPrimaryDimensionMappingType("DAVID");
	// dataConfiguration.setHumanReadableDimensionIDType("GENE_SYMBOL");
	// dataConfiguration.setDimensionDenominationPlural("genes");
	// dataConfiguration.setDimensionDenominationSingular("gene");
	//
	// mutationDataMetaInfo.setDataDomainConfiguration(dataConfiguration);
	//
	// try {
	// LoadDataParameters loadDataParameters =
	// setUpLoadDataParameters(mutationDataMetaInfo);
	//
	// loadDataParameters.setStartParseFileAtLine(3);
	//
	// // open file to read second line to determine number of rows and
	// // columns
	// BufferedReader reader;
	//
	// reader = new BufferedReader(
	// new FileReader(mutationDataMetaInfo.getDataPath()));
	//
	// // read dimensions of data matrix
	// String dimensionString = reader.readLine();
	//
	// // TODO: check if there are two numeric columns
	// String[] dimensionHeadings = dimensionString.split(loadDataParameters
	// .getDelimiter());
	//
	// int columns = new Integer(dimensionHeadings.length);
	//
	// // loadDataParameters.setMinDefined(true);
	// // loadDataParameters.setMin(min);
	// // loadDataParameters.setMaxDefined(true);
	// // loadDataParameters.setMax(max);
	//
	// StringBuffer inputPattern = new StringBuffer("SKIP;");
	//
	// // list to store column labels
	// List<String> columnLabels = new ArrayList<String>();
	//
	// for (int i = 0; i < columns - 1; ++i) {
	// inputPattern.append("FLOAT;");
	// columnLabels.add(dimensionHeadings[i + 1]);
	// }
	//
	// loadDataParameters.setInputPattern(inputPattern.toString());
	// loadDataParameters
	// .setColumnHeaderStringConverter(new DashToPointStringConverter());
	// loadDataParameters.setColumnLabels(columnLabels);
	//
	// mutationDataMetaInfo.setLoadDataParameters(loadDataParameters);
	//
	// } catch (Exception e) {
	// throw new IllegalStateException(e);
	// }
	//
	// return mutationDataMetaInfo;
	// }

	// private static LoadDataParameters setUpLoadDataParameters(
	// DataSetDescription dataSetMetaInfo) {
	//
	// String delimiter = "\t";
	//
	// LoadDataParameters loadDataParameters = new LoadDataParameters();
	// loadDataParameters.setLabel(dataSetMetaInfo.getDataSetName());
	// loadDataParameters.setFileName(dataSetMetaInfo.getDataPath());
	// loadDataParameters.setDelimiter(delimiter);
	//
	// loadDataParameters.setMathFilterMode("Normal");
	// loadDataParameters.setIsDataHomogeneous(true);
	// loadDataParameters.setColumnDimension(false);
	//
	// return loadDataParameters;
	// }

	//
	// private static void doCopyNumberSpecificStuff(DataSetDescription
	// dataSetMetaInfo) {
	// try {
	// LoadDataParameters loadDataParameters =
	// setUpLoadDataParameters(dataSetMetaInfo);
	//
	// loadDataParameters.setStartParseFileAtLine(1);
	//
	// // open file to read second line to determine number of rows and
	// // columns
	// BufferedReader reader = new BufferedReader(new FileReader(
	// dataSetMetaInfo.getDataPath()));
	//
	// // read dimensions of data matrix
	// String dimensionString = reader.readLine();
	//
	// // TODO: check if there are two numeric columns
	// String[] dimensions = dimensionString
	// .split(loadDataParameters.getDelimiter());
	//
	// int columns = dimensions.length - 3;// new Integer(dimensions[1]);
	//
	// // read column headers
	// // String headerString = reader.readLine();
	//
	// // TODO: check if there are as many column headers as there are
	// // columns (+ 2)
	// // String[] headers =
	// // headerString.split(loadDataParameters.getDelimiter());
	//
	// // loadDataParameters.setMinDefined(true);
	// // loadDataParameters.setMin(min);
	// // loadDataParameters.setMaxDefined(true);
	// // loadDataParameters.setMax(max);
	//
	// StringBuffer inputPattern = new StringBuffer("SKIP;SKIP;SKIP;");
	//
	// // list to store column labels
	// List<String> columnLabels = new ArrayList<String>();
	//
	// for (int i = 0; i < columns; ++i) {
	// inputPattern.append("FLOAT;");
	// columnLabels.add(dimensions[i + 3]);
	// }
	//
	// loadDataParameters.setInputPattern(inputPattern.toString());
	// loadDataParameters
	// .setColumnHeaderStringConverter(new TCGAIDStringConverter());
	// loadDataParameters.setColumnLabels(columnLabels);
	// dataSetMetaInfo.setLoadDataParameters(loadDataParameters);
	//
	// } catch (Exception e) {
	// throw new IllegalStateException(e);
	// }
	// }

}
