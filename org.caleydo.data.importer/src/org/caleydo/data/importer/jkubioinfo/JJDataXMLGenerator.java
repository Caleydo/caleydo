package org.caleydo.data.importer.jkubioinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.data.datadomain.DataDomainConfiguration;
import org.caleydo.core.data.importing.DataSetDescription;
import org.caleydo.core.util.mapping.color.EDefaultColorSchemes;

/**
 * Generator class that writes the loading information of a series of
 * Johnsen&Johnsen data sets to an XML file.
 * 
 * @author Marc Streit
 */
public class JJDataXMLGenerator {

	public static final String DATA_FOLDER = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "caleydo/data/JKU_BioInfo/mGlu2/export/";

	public static final String MGLU2_GENE_EXPRESSION_DATA = DATA_FOLDER + "fabiaResNormX.csv";
	public static final String MGLU2_EXPERIMENT_CHEM_CLUSTER_GROUPING = DATA_FOLDER
			+ "clusterAssignments.csv";
	
	public static final String MGLU2_EXPERIMENT_BICLUSTER_DATA = DATA_FOLDER
			+ "fabiaResNormZ.csv";

	public static final String MGLU2_PIC50_DATA = DATA_FOLDER + "pic50.csv";

	public static final String OUTPUT_FILE_PATH = DATA_FOLDER + "caleydo_mGlu2.xml";

	public static void main(String[] args) {

		ArrayList<DataSetDescription> dataSetMetaInfoList = new ArrayList<DataSetDescription>();
		dataSetMetaInfoList.add(setUpXMLGLU2GeneExpressionData());
		// dataSetMetaInfoList.add(setUpXMGLU2Pic50Data());
		dataSetMetaInfoList.add(setUpXMLGLU2BiClusterData());

		DataSetMetaInfoCollection dataTypeSetCollection = new DataSetMetaInfoCollection();
		dataTypeSetCollection.setDataTypeSetCollection(dataSetMetaInfoList);

		JAXBContext context = null;
		try {
			Class<?>[] serializableClasses = new Class<?>[2];
			serializableClasses[0] = DataSetMetaInfo.class;
			serializableClasses[1] = DataSetMetaInfoCollection.class;
			// serializableClasses[2] = TCGAIDStringConverter.class;
			// serializableClasses[3] = DashToPointStringConverter.class;
			context = JAXBContext.newInstance(serializableClasses);

			Marshaller marshaller;
			marshaller = context.createMarshaller();
			marshaller.marshal(dataTypeSetCollection, new File(OUTPUT_FILE_PATH));

			System.out.println("Created configuration for " + dataSetMetaInfoList.size()
					+ " datasets: " + dataSetMetaInfoList);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	private static DataSetMetaInfo setUpXMLGLU2GeneExpressionData() {
		DataSetMetaInfo geneExpressionData = new DataSetMetaInfo();
		geneExpressionData.setName("mGlu2 Gene Expression Data");
		geneExpressionData.setDataDomainType("org.caleydo.datadomain.genetic");
		geneExpressionData.setDataPath(MGLU2_GENE_EXPRESSION_DATA);
		geneExpressionData.setGroupingPath(MGLU2_EXPERIMENT_CHEM_CLUSTER_GROUPING);
		geneExpressionData.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED.name());

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

		geneExpressionData.setDataDomainConfiguration(dataConfiguration);
		
		String delimiter = "\t";

		LoadDataParameters loadDataParameters = new LoadDataParameters();
		loadDataParameters.setLabel(geneExpressionData.getName());
		loadDataParameters.setFileName(geneExpressionData.getDataPath());
		loadDataParameters.setDelimiter(delimiter);

		loadDataParameters.setMathFilterMode("Normal");
		loadDataParameters.setIsDataHomogeneous(true);
		loadDataParameters.setColumnDimension(false);
		
		// geneExpressionData.setRunClusteringOnRows(true);
		// geneExpressionData.setCreateGeneSamples(true);

		try {
			loadDataParameters.setStartParseFileAtLine(1);

			BufferedReader reader = new BufferedReader(new FileReader(
					geneExpressionData.getDataPath()));

			// read dimensions of data matrix
			String dimensionString = reader.readLine();

			String[] dimensions = dimensionString.split(loadDataParameters.getDelimiter());

			int columns = dimensions.length - 1;

			// loadDataParameters.setMinDefined(true);
			// loadDataParameters.setMin(min);
			// loadDataParameters.setMaxDefined(true);
			// loadDataParameters.setMax(max);

			StringBuffer inputPattern = new StringBuffer("SKIP;");

			// list to store column labels
			List<String> columnLabels = new ArrayList<String>();

			for (int i = 0; i < columns; ++i) {
				inputPattern.append("FLOAT;");
				columnLabels.add(dimensions[i + 1]);
			}

			loadDataParameters.setInputPattern(inputPattern.toString());
			// loadDataParameters
			// .setColumnHeaderStringConverter(new TCGAIDStringConverter());
			loadDataParameters.setColumnLabels(columnLabels);

			geneExpressionData.setLoadDataParameters(loadDataParameters);

		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return geneExpressionData;
	}

	private static DataSetMetaInfo setUpXMLGLU2BiClusterData() {
		DataSetMetaInfo expertimentBiClusterData = new DataSetMetaInfo();
		expertimentBiClusterData.setName("mGlu2 Experiment BiCluster Data");
		expertimentBiClusterData.setDataDomainType("org.caleydo.datadomain.generic");
		expertimentBiClusterData.setDataPath(MGLU2_EXPERIMENT_BICLUSTER_DATA);
		//geneExpressionData.setGroupingPath(MGLU2_EXPERIMENT_CHEM_CLUSTER_GROUPING);
		expertimentBiClusterData.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED.name());

		DataDomainConfiguration dataConfiguration = new DataDomainConfiguration();
		dataConfiguration.setRecordIDCategory("SAMPLE");
		dataConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
		dataConfiguration.setHumanReadableRecordIDType("SAMPLE");
		dataConfiguration.setRecordDenominationPlural("samples");
		dataConfiguration.setRecordDenominationSingular("sample");

		dataConfiguration.setDimensionIDCategory("BICLUSTER");
		//dataConfiguration.setPrimaryDimensionMappingType("DAVID");
		dataConfiguration.setHumanReadableDimensionIDType("BICLUSTER");
		dataConfiguration.setDimensionDenominationPlural("biclusters");
		dataConfiguration.setDimensionDenominationSingular("bicluster");

		expertimentBiClusterData.setDataDomainConfiguration(dataConfiguration);

		String delimiter = "\t";

		LoadDataParameters loadDataParameters = new LoadDataParameters();
		loadDataParameters.setLabel(expertimentBiClusterData.getName());
		loadDataParameters.setFileName(expertimentBiClusterData.getDataPath());
		loadDataParameters.setDelimiter(delimiter);

		loadDataParameters.setMathFilterMode("Normal");
		loadDataParameters.setIsDataHomogeneous(true);
		loadDataParameters.setColumnDimension(false);

		// geneExpressionData.setRunClusteringOnRows(true);
		// geneExpressionData.setCreateGeneSamples(true);

		try {
			loadDataParameters.setStartParseFileAtLine(1);

			BufferedReader reader = new BufferedReader(new FileReader(
					expertimentBiClusterData.getDataPath()));

			// read dimensions of data matrix
			String dimensionString = reader.readLine();

			String[] dimensions = dimensionString.split(loadDataParameters.getDelimiter());

			int columns = dimensions.length - 1;

			// loadDataParameters.setMinDefined(true);
			// loadDataParameters.setMin(min);
			// loadDataParameters.setMaxDefined(true);
			// loadDataParameters.setMax(max);

			StringBuffer inputPattern = new StringBuffer("SKIP;");

			// list to store column labels
			List<String> columnLabels = new ArrayList<String>();

			for (int i = 0; i < columns; ++i) {
				inputPattern.append("FLOAT;");
				columnLabels.add(dimensions[i + 1]);
			}

			loadDataParameters.setInputPattern(inputPattern.toString());
			// loadDataParameters
			// .setColumnHeaderStringConverter(new TCGAIDStringConverter());
			loadDataParameters.setColumnLabels(columnLabels);

			expertimentBiClusterData.setLoadDataParameters(loadDataParameters);

		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return expertimentBiClusterData;
	}

	private static DataSetMetaInfo setUpXMGLU2Pic50Data() {
		DataSetMetaInfo pic50Data = new DataSetMetaInfo();
		pic50Data.setName("mGlu2 pic50");
		pic50Data.setDataDomainType("org.caleydo.datadomain.generic");
		pic50Data.setDataPath(MGLU2_PIC50_DATA);
		pic50Data.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED.name());

		DataDomainConfiguration dataConfiguration = new DataDomainConfiguration();
		// dataConfiguration.setMappingFile("data/bootstrap/bootstrap.xml");

		dataConfiguration.setRecordIDCategory("SAMPLE");
		dataConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
		dataConfiguration.setHumanReadableRecordIDType("SAMPLE");
		dataConfiguration.setRecordDenominationPlural("samples");
		dataConfiguration.setRecordDenominationSingular("sample");

		dataConfiguration.setDimensionIDCategory("PIC50ID");
		// dataConfiguration.setPrimaryDimensionMappingType("DAVID");
		dataConfiguration.setHumanReadableDimensionIDType("PIC50ID");
		dataConfiguration.setDimensionDenominationPlural("pic50IDs");
		dataConfiguration.setDimensionDenominationSingular("pic50");

		pic50Data.setDataDomainConfiguration(dataConfiguration);

		String delimiter = "\t";

		LoadDataParameters loadDataParameters = new LoadDataParameters();
		loadDataParameters.setLabel(pic50Data.getName());
		loadDataParameters.setFileName(pic50Data.getDataPath());
		loadDataParameters.setDelimiter(delimiter);

		loadDataParameters.setMathFilterMode("Normal");
		loadDataParameters.setIsDataHomogeneous(true);
		loadDataParameters.setColumnDimension(false);

		// geneExpressionData.setRunClusteringOnRows(true);
		// geneExpressionData.setCreateGeneSamples(true);

		try {
			loadDataParameters.setStartParseFileAtLine(1);

			BufferedReader reader = new BufferedReader(new FileReader(pic50Data.getDataPath()));

			// read dimensions of data matrix
			String dimensionString = reader.readLine();

			String[] dimensions = dimensionString.split(loadDataParameters.getDelimiter());

			int columns = dimensions.length - 1;

			// loadDataParameters.setMinDefined(true);
			// loadDataParameters.setMin(min);
			// loadDataParameters.setMaxDefined(true);
			// loadDataParameters.setMax(max);

			StringBuffer inputPattern = new StringBuffer("SKIP;");

			// list to store column labels
			List<String> columnLabels = new ArrayList<String>();

			for (int i = 0; i < columns; ++i) {
				inputPattern.append("FLOAT;");
				columnLabels.add(dimensions[i + 1]);
			}

			loadDataParameters.setInputPattern(inputPattern.toString());
			// loadDataParameters
			// .setColumnHeaderStringConverter(new TCGAIDStringConverter());
			loadDataParameters.setColumnLabels(columnLabels);

			pic50Data.setLoadDataParameters(loadDataParameters);

		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return pic50Data;
	}
}
