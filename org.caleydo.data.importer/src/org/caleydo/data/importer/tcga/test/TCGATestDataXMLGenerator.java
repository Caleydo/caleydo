/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.test;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataProcessingDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescription.ECreateDefaultProperties;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.core.io.ProjectDescription;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.color.Color;
import org.caleydo.data.importer.setupgenerator.DataSetDescriptionSerializer;
import org.caleydo.datadomain.genetic.TCGADefinitions;

/**
 * Creates and parameterizes a series of {@link DataSetDescription}s for the TCGA dataset. Writes the result to an xml
 * file, which can then be loaded by the importer plugin, which creates and caleydo project file.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGATestDataXMLGenerator extends DataSetDescriptionSerializer {

	public static final String DROPBOX_GBM_FOLDER = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "Dropbox/Caleydo/data/tcga/20110728/gbm/";

	public static final String MRNA = DROPBOX_GBM_FOLDER + "mrna_cnmf/outputprefix.expclu.gct";
	public static final String MRNA_GROUPING = DROPBOX_GBM_FOLDER + "mrna_cnmf/cnmf.membership.txt";

	public static final String MIRNA = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.normalized.gct";
	public static final String MIRNA_GROUPING = DROPBOX_GBM_FOLDER + "mir_cnmf/cnmf.membership.txt";

	public static final String METHYLATION = DROPBOX_GBM_FOLDER + "methylation_cnmf/cnmf.normalized.gct";
	public static final String METHYLATION_GROUPING = DROPBOX_GBM_FOLDER + "methylation_cnmf/cnmf.membership.txt";

	public static final String COPY_NUMBER = DROPBOX_GBM_FOLDER + "copy_number/all_thresholded_by_genes.txt";

	public static final String GROUND_TRUTH_GROUPING = DROPBOX_GBM_FOLDER + "ground_truth/2011_exp_assignments.txt";

	public static final String CLINICAL = DROPBOX_GBM_FOLDER + "clinical/clinical_patient_public_GBM.txt";

	public static final String MUTATION = DROPBOX_GBM_FOLDER + "mutation/gbm_mutation.csv";

	private IDSpecification sampleIDSpecification;

	public static void main(String[] args) {

		TCGATestDataXMLGenerator generator = new TCGATestDataXMLGenerator(args);
		generator.run();
	}

	/**
	 *
	 */
	public TCGATestDataXMLGenerator(String[] arguments) {
		super(arguments);
	}

	@Override
	protected void setUpDataSetDescriptions() {
		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		sampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setReplacementExpression(TCGADefinitions.TCGA_REPLACEMENT_STRING,
				TCGADefinitions.TCGA_REPLACING_EXPRESSIONS);
		idTypeParsingRules.setSubStringExpression(TCGADefinitions.TCGA_ID_SUBSTRING_REGEX);
		idTypeParsingRules.setDefault(true);
		sampleIDSpecification.setIdTypeParsingRules(idTypeParsingRules);

		projectDescription = new ProjectDescription();

		projectDescription.add(setUpMRNAData());
		projectDescription.add(setUpMutationData());
		projectDescription.add(setUpMiRNAData());
		projectDescription.add(setUpMethylationData());
		projectDescription.add(setUpCopyNumberData());
		projectDescription.add(setUpClinicalData());

	}

	private DataSetDescription setUpMRNAData() {
		DataSetDescription mrnaData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		mrnaData.setDataSetName("mRNA");

		mrnaData.setDataSourcePath(MRNA);
		mrnaData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mrnaData.addParsingRule(parsingRule);
		mrnaData.setTransposeMatrix(true);

		mrnaData.getDataDescription().getNumericalProperties().setDataCenter(0d);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mrnaData.setRowIDSpecification(geneIDSpecification);
		mrnaData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseClustering = new GroupingParseSpecification(MRNA_GROUPING);
		firehoseClustering.setContainsColumnIDs(true);
		// firehoseClustering.setGroupingName("hierarchical");
		firehoseClustering.setRowIDSpecification(sampleIDSpecification);
		mrnaData.addColumnGroupingSpecification(firehoseClustering);

		GroupingParseSpecification groundTruthGrouping = new GroupingParseSpecification();
		groundTruthGrouping.setDataSourcePath(GROUND_TRUTH_GROUPING);
		groundTruthGrouping.addColum(2);
		groundTruthGrouping.setRowIDSpecification(sampleIDSpecification);
		mrnaData.addColumnGroupingSpecification(groundTruthGrouping);

		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		KMeansClusterConfiguration kMeansAlgo = new KMeansClusterConfiguration();
		kMeansAlgo.setNumberOfClusters(5);
		clusterConfiguration.setClusterAlgorithmConfiguration(kMeansAlgo);
		dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);

		clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		kMeansAlgo = new KMeansClusterConfiguration();
		kMeansAlgo.setNumberOfClusters(5);
		clusterConfiguration.setClusterAlgorithmConfiguration(kMeansAlgo);

		dataProcessingDescription.addColumnClusterConfiguration(clusterConfiguration);

		mrnaData.setDataProcessingDescription(dataProcessingDescription);

		return mrnaData;
	}

	private DataSetDescription setUpMiRNAData() {
		DataSetDescription mirnaData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		mirnaData.setDataSetName("miRNA");

		mirnaData.setDataSourcePath(MIRNA);
		mirnaData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mirnaData.addParsingRule(parsingRule);

		// IDSpecification mirnaIDSpecification = new IDSpecification();
		// mirnaIDSpecification.setIdType("miRNA");
		// mirnaData.setRowIDSpecification(mirnaIDSpecification);
		mirnaData.setTransposeMatrix(true);
		mirnaData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseClustering = new GroupingParseSpecification(MIRNA_GROUPING);
		firehoseClustering.setContainsColumnIDs(false);
		firehoseClustering.setRowIDSpecification(sampleIDSpecification);
		mirnaData.addColumnGroupingSpecification(firehoseClustering);

		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		KMeansClusterConfiguration kMeansAlgo = new KMeansClusterConfiguration();
		kMeansAlgo.setNumberOfClusters(5);
		clusterConfiguration.setClusterAlgorithmConfiguration(kMeansAlgo);
		dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);
		mirnaData.setDataProcessingDescription(dataProcessingDescription);

		return mirnaData;
	}

	private DataSetDescription setUpMethylationData() {
		DataSetDescription methylationData = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		methylationData.setDataSetName("Methylation");

		methylationData.setDataSourcePath(METHYLATION);
		methylationData.setNumberOfHeaderLines(3);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		methylationData.addParsingRule(parsingRule);
		methylationData.setTransposeMatrix(true);

		// IDSpecification methylationIDSpecification = new IDSpecification();
		// methylationIDSpecification.setIdType("methylation");
		// methylationData.setRowIDSpecification(methylationIDSpecification);
		methylationData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseClustering = new GroupingParseSpecification(METHYLATION_GROUPING);
		firehoseClustering.setContainsColumnIDs(false);
		firehoseClustering.setRowIDSpecification(sampleIDSpecification);
		methylationData.addColumnGroupingSpecification(firehoseClustering);

		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		KMeansClusterConfiguration kMeansAlgo = new KMeansClusterConfiguration();
		kMeansAlgo.setNumberOfClusters(5);
		clusterConfiguration.setClusterAlgorithmConfiguration(kMeansAlgo);
		dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);
		methylationData.setDataProcessingDescription(dataProcessingDescription);

		return methylationData;
	}

	private DataSetDescription setUpCopyNumberData() {
		DataSetDescription copyNumberData = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
		copyNumberData.setDataSetName("Copy number");

		copyNumberData.setDataSourcePath(COPY_NUMBER);
		copyNumberData.setNumberOfHeaderLines(1);

		@SuppressWarnings("unchecked")
		CategoricalClassDescription<Integer> categoricalClassDescription = (CategoricalClassDescription<Integer>) copyNumberData
				.getDataDescription().getCategoricalClassDescription();
		categoricalClassDescription.setCategoryType(ECategoryType.ORDINAL);
		categoricalClassDescription.setRawDataType(EDataType.INTEGER);
		categoricalClassDescription.addCategoryProperty(-2, "Homozygous deletion", Color.BLUE);
		categoricalClassDescription.addCategoryProperty(-1, "Heterozygous deletion",
				Color.BLUE.getColorWithSpecificBrighness(0.5f));
		categoricalClassDescription.addCategoryProperty(0, "NORMAL", Color.NEUTRAL_GREY);
		categoricalClassDescription.addCategoryProperty(1, "Low level amplification",
				Color.RED.getColorWithSpecificBrighness(0.5f));
		categoricalClassDescription.addCategoryProperty(2, "High level amplification", Color.RED);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		copyNumberData.addParsingRule(parsingRule);
		copyNumberData.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		copyNumberData.setRowIDSpecification(geneIDSpecification);
		copyNumberData.setColumnIDSpecification(sampleIDSpecification);

		return copyNumberData;

		// copyNumberData.setColorScheme(EDefaultColorSchemes.RED_YELLOW_BLUE_DIVERGING
		// .name());
	}

	//
	private DataSetDescription setUpClinicalData() {
		DataSetDescription clinicalData = new DataSetDescription();
		clinicalData.setDataSetName("Clinical");

		clinicalData.setDataSourcePath(CLINICAL);
		clinicalData.setNumberOfHeaderLines(1);

		DataDescription dataDescription = new DataDescription(EDataClass.NATURAL_NUMBER, EDataType.INTEGER,
				new NumericalProperties());
		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(10);
		parsingRule.setToColumn(11);
		parsingRule.setColumnDescripton(new ColumnDescription(dataDescription));
		clinicalData.addParsingRule(parsingRule);
		parsingRule = new ParsingRule();
		parsingRule.setFromColumn(13);
		parsingRule.setToColumn(15);
		parsingRule.setColumnDescripton(new ColumnDescription(dataDescription));
		clinicalData.addParsingRule(parsingRule);

		IDSpecification clinicalIdSpecification = new IDSpecification();
		clinicalIdSpecification.setIdType("clinical");

		clinicalData.setColumnIDSpecification(clinicalIdSpecification);
		clinicalData.setRowIDSpecification(sampleIDSpecification);

		// columnLabels.add("Days to birth");
		// columnLabels.add("Days to death");
		// columnLabels.add("Days to last followup");
		// columnLabels.add("Days to tumor progression");
		// columnLabels.add("Days to tumor recurrence");

		return clinicalData;
	}

	private DataSetDescription setUpMutationData() {
		DataSetDescription mutationDataDescription = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
		mutationDataDescription.setDataSetName("Mutation Status");
		mutationDataDescription.setDataSourcePath(MUTATION);

		@SuppressWarnings("unchecked")
		CategoricalClassDescription<Integer> categoricalClassDescription = (CategoricalClassDescription<Integer>) mutationDataDescription
				.getDataDescription().getCategoricalClassDescription();
		categoricalClassDescription.addCategoryProperty(0, "Not Mutated", Color.NEUTRAL_GREY);
		categoricalClassDescription.addCategoryProperty(1, "Mutated", Color.RED);

		mutationDataDescription.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(1);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		mutationDataDescription.addParsingRule(parsingRule);
		mutationDataDescription.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mutationDataDescription.setRowIDSpecification(geneIDSpecification);
		mutationDataDescription.setColumnIDSpecification(sampleIDSpecification);

		return mutationDataDescription;
	}

}
