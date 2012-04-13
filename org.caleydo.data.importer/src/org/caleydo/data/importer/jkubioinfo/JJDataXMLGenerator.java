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
package org.caleydo.data.importer.jkubioinfo;

import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescriptionCollection;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.ParsingRule;

/**
 * Generator class that writes the loading information of a series of
 * Johnsen&Johnsen data sets to an XML file.
 * 
 * @author Marc Streit
 */
public class JJDataXMLGenerator {

	public static final String DATA_FOLDER = System.getProperty("user.home")
			+ System.getProperty("file.separator")
			+ "caleydo/data/JKU_BioInfo/mGlu2/export/";

	public static final String MGLU2_GENE_EXPRESSION_DATA = DATA_FOLDER
			+ "fabiaResNormX.csv";
	public static final String MGLU2_EXPERIMENT_CHEM_CLUSTER_GROUPING = DATA_FOLDER
			+ "clusterAssignments.csv";

	public static final String MGLU2_EXPERIMENT_BICLUSTER_DATA = DATA_FOLDER
			+ "fabiaResNormZ.csv";

	public static final String MGLU2_PIC50_DATA = DATA_FOLDER + "pic50.csv";

	public static final String OUTPUT_FILE_PATH = DATA_FOLDER + "caleydo_mGlu2.xml";

	private IDSpecification sampleIDSpecification;
	
	public static void main(String[] args) {

		JJDataXMLGenerator generator = new JJDataXMLGenerator();
		generator.run();
	}
	
	private void run() {
		
		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdType("SAMPLE");
		//sampleIDSpecification.setReplacementExpression("\\.", "-");
		//sampleIDSpecification.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);

		ArrayList<DataSetDescription> dataSetDescriptions = new ArrayList<DataSetDescription>();
		dataSetDescriptions.add(setUpXMLGLU2GeneExpressionData());
		//dataSetDescriptions.add(setUpXMLGLU2BiClusterData());

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

	private DataSetDescription setUpXMLGLU2GeneExpressionData() {
		
		DataSetDescription mrnaData = new DataSetDescription();
		mrnaData.setDataSetName("mGlu2 Gene Expression Data");

		mrnaData.setDataSourcePath(MGLU2_GENE_EXPRESSION_DATA);
		mrnaData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(1);
		parsingRule.setParseUntilEnd(true);
		//parsingRule.setDataType("FLOAT");
		mrnaData.addParsingRule(parsingRule);
		mrnaData.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mrnaData.setRowIDSpecification(geneIDSpecification);
		mrnaData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification clustering = new GroupingParseSpecification(
				MGLU2_EXPERIMENT_CHEM_CLUSTER_GROUPING);
		clustering.setContainsColumnIDs(false);
		clustering.setRowIDSpecification(sampleIDSpecification);
		mrnaData.addColumnGroupingSpecification(clustering);

		return mrnaData;
	}

//	private DataSetDescription setUpXMLGLU2BiClusterData() {
//		DataSetMetaInfo expertimentBiClusterData = new DataSetMetaInfo();
//		expertimentBiClusterData.setName("mGlu2 Experiment BiCluster Data");
//		expertimentBiClusterData.setDataDomainType("org.caleydo.datadomain.generic");
//		expertimentBiClusterData.setDataPath(MGLU2_EXPERIMENT_BICLUSTER_DATA);
//		// geneExpressionData.setGroupingPath(MGLU2_EXPERIMENT_CHEM_CLUSTER_GROUPING);
//		expertimentBiClusterData.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED
//				.name());
//
//		DataDomainConfiguration dataConfiguration = new DataDomainConfiguration();
//		dataConfiguration.setRecordIDCategory("SAMPLE");
//		dataConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
//		dataConfiguration.setHumanReadableRecordIDType("SAMPLE");
//		dataConfiguration.setRecordDenominationPlural("samples");
//		dataConfiguration.setRecordDenominationSingular("sample");
//
//		dataConfiguration.setDimensionIDCategory("BICLUSTER");
//		// dataConfiguration.setPrimaryDimensionMappingType("DAVID");
//		dataConfiguration.setHumanReadableDimensionIDType("BICLUSTER");
//		dataConfiguration.setDimensionDenominationPlural("biclusters");
//		dataConfiguration.setDimensionDenominationSingular("bicluster");
//
//		expertimentBiClusterData.setDataDomainConfiguration(dataConfiguration);
//
//		String delimiter = "\t";
//
//		LoadDataParameters loadDataParameters = new LoadDataParameters();
//		loadDataParameters.setLabel(expertimentBiClusterData.getName());
//		loadDataParameters.setFileName(expertimentBiClusterData.getDataPath());
//		loadDataParameters.setDelimiter(delimiter);
//
//		loadDataParameters.setMathFilterMode("Normal");
//		loadDataParameters.setIsDataHomogeneous(true);
//		loadDataParameters.setColumnDimension(false);
//
//		// geneExpressionData.setRunClusteringOnRows(true);
//		// geneExpressionData.setCreateGeneSamples(true);
//
//		try {
//			loadDataParameters.setStartParseFileAtLine(1);
//
//			BufferedReader reader = new BufferedReader(new FileReader(
//					expertimentBiClusterData.getDataPath()));
//
//			// read dimensions of data matrix
//			String dimensionString = reader.readLine();
//
//			String[] dimensions = dimensionString
//					.split(loadDataParameters.getDelimiter());
//
//			int columns = dimensions.length - 1;
//
//			// loadDataParameters.setMinDefined(true);
//			// loadDataParameters.setMin(min);
//			// loadDataParameters.setMaxDefined(true);
//			// loadDataParameters.setMax(max);
//
//			StringBuffer inputPattern = new StringBuffer("SKIP;");
//
//			// list to store column labels
//			List<String> columnLabels = new ArrayList<String>();
//
//			for (int i = 0; i < columns; ++i) {
//				inputPattern.append("FLOAT;");
//				columnLabels.add(dimensions[i + 1]);
//			}
//
//			loadDataParameters.setInputPattern(inputPattern.toString());
//			// loadDataParameters
//			// .setColumnHeaderStringConverter(new TCGAIDStringConverter());
//			loadDataParameters.setColumnLabels(columnLabels);
//
//			expertimentBiClusterData.setLoadDataParameters(loadDataParameters);
//
//		} catch (Exception e) {
//			throw new IllegalStateException(e);
//		}
//
//		return expertimentBiClusterData;
//	}

//	private DataSetMetaInfo setUpXMGLU2Pic50Data() {
//		DataSetMetaInfo pic50Data = new DataSetMetaInfo();
//		pic50Data.setName("mGlu2 pic50");
//		pic50Data.setDataDomainType("org.caleydo.datadomain.generic");
//		pic50Data.setDataPath(MGLU2_PIC50_DATA);
//		pic50Data.setColorScheme(EDefaultColorSchemes.BLUE_WHITE_RED.name());
//
//		DataDomainConfiguration dataConfiguration = new DataDomainConfiguration();
//		// dataConfiguration.setMappingFile("data/bootstrap/bootstrap.xml");
//
//		dataConfiguration.setRecordIDCategory("SAMPLE");
//		dataConfiguration.setPrimaryRecordMappingType("SAMPLE_INT");
//		dataConfiguration.setHumanReadableRecordIDType("SAMPLE");
//		dataConfiguration.setRecordDenominationPlural("samples");
//		dataConfiguration.setRecordDenominationSingular("sample");
//
//		dataConfiguration.setDimensionIDCategory("PIC50ID");
//		// dataConfiguration.setPrimaryDimensionMappingType("DAVID");
//		dataConfiguration.setHumanReadableDimensionIDType("PIC50ID");
//		dataConfiguration.setDimensionDenominationPlural("pic50IDs");
//		dataConfiguration.setDimensionDenominationSingular("pic50");
//
//		pic50Data.setDataDomainConfiguration(dataConfiguration);
//
//		String delimiter = "\t";
//
//		LoadDataParameters loadDataParameters = new LoadDataParameters();
//		loadDataParameters.setLabel(pic50Data.getName());
//		loadDataParameters.setFileName(pic50Data.getDataPath());
//		loadDataParameters.setDelimiter(delimiter);
//
//		loadDataParameters.setMathFilterMode("Normal");
//		loadDataParameters.setIsDataHomogeneous(true);
//		loadDataParameters.setColumnDimension(false);
//
//		// geneExpressionData.setRunClusteringOnRows(true);
//		// geneExpressionData.setCreateGeneSamples(true);
//
//		try {
//			loadDataParameters.setStartParseFileAtLine(1);
//
//			BufferedReader reader = new BufferedReader(new FileReader(
//					pic50Data.getDataPath()));
//
//			// read dimensions of data matrix
//			String dimensionString = reader.readLine();
//
//			String[] dimensions = dimensionString
//					.split(loadDataParameters.getDelimiter());
//
//			int columns = dimensions.length - 1;
//
//			// loadDataParameters.setMinDefined(true);
//			// loadDataParameters.setMin(min);
//			// loadDataParameters.setMaxDefined(true);
//			// loadDataParameters.setMax(max);
//
//			StringBuffer inputPattern = new StringBuffer("SKIP;");
//
//			// list to store column labels
//			List<String> columnLabels = new ArrayList<String>();
//
//			for (int i = 0; i < columns; ++i) {
//				inputPattern.append("FLOAT;");
//				columnLabels.add(dimensions[i + 1]);
//			}
//
//			loadDataParameters.setInputPattern(inputPattern.toString());
//			// loadDataParameters
//			// .setColumnHeaderStringConverter(new TCGAIDStringConverter());
//			loadDataParameters.setColumnLabels(columnLabels);
//
//			pic50Data.setLoadDataParameters(loadDataParameters);
//
//		} catch (Exception e) {
//			throw new IllegalStateException(e);
//		}
//
//		return pic50Data;
//	}
}
