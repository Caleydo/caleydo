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
package org.caleydo.data.importer.tcga;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataProcessingDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.system.FileOperations;
import org.caleydo.data.importer.setupgenerator.DataSetDescriptionSerializer;
import org.caleydo.data.importer.tcga.utils.ArchiveExtractionUtils;

/**
 * Generator class that writes the loading information of a series of TCGA data
 * sets to an XML file.
 * 
 * @author Nils Gehlenborg
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGAXMLGenerator
	extends DataSetDescriptionSerializer {

	public static String FIREHOSE_URL_PREFIX = "http://gdac.broadinstitute.org/runs/";
	private static String FIREHOSE_TAR_NAME_PREFIX = "gdac.broadinstitute.org_";

	private String tumorAbbreviation;

	private String analysisRunIdentifier;
	private String analysisRunIdentifierWithoutUnderscore;

	private String dataRunIdentifier;
	private String dataRunIdentifierWithoutUnderscore;

	private String tmpOutputDirectoryPath;
	private String remoteAnalysisRunArchiveDirectory;
	private String remoteDataRunArchiveDirectory;

	public static final String TCGA_ID_SUBSTRING_REGEX = "tcga\\-|\\-...\\-";

	private IDSpecification sampleIDSpecification;

	private boolean loadSampledGenes = false;

	public TCGAXMLGenerator(String tumorAbbreviation, String runIdentifierUnderscore,
			String dataRunIdentifier, String outputXMLFilePath, String outputFolderPath,
			String tmpOutputFolderPath, boolean loadSampledGenes) {

		super(null);

		this.tumorAbbreviation = tumorAbbreviation;
		this.analysisRunIdentifier = runIdentifierUnderscore;
		this.dataRunIdentifier = dataRunIdentifier;
		this.outputXMLFilePath = outputXMLFilePath;
		this.tmpOutputDirectoryPath = tmpOutputFolderPath;
		this.loadSampledGenes = loadSampledGenes;

		init();
	}

	private void init() {
		this.analysisRunIdentifierWithoutUnderscore = analysisRunIdentifier.replace("_", "");
		this.dataRunIdentifierWithoutUnderscore = dataRunIdentifier.replace("_", "");

		// create path of archive search directory
		this.remoteAnalysisRunArchiveDirectory = FIREHOSE_URL_PREFIX + "analyses__"
				+ analysisRunIdentifier + "/data/" + tumorAbbreviation + "/"
				+ analysisRunIdentifierWithoutUnderscore + "/";

		this.remoteDataRunArchiveDirectory = FIREHOSE_URL_PREFIX + "stddata__"
				+ dataRunIdentifier + "/data/" + tumorAbbreviation + "/"
				+ dataRunIdentifierWithoutUnderscore + "/";
	}

	// find Firehose archive in Firehose_get output directory and extract file
	// from archive to temp directory
	// return path to file in temp directory
	protected String extractFile(String fileName, String pipelineName,
			String runIdentifierWithoutUnderscore, String remoteArchiveDirectory, int level) {

		// gdac.broadinstitute.org_GBM.Methylation_Clustering_CNMF.Level_4.2012052500.0.0.tar.gz
		String archiveName = FIREHOSE_TAR_NAME_PREFIX + tumorAbbreviation + "." + pipelineName
				+ ".Level_" + level + "." + runIdentifierWithoutUnderscore + "00.0.0.tar.gz";

		String outputDirectoryName = tmpOutputDirectoryPath + runIdentifierWithoutUnderscore
				+ System.getProperty("file.separator") + tumorAbbreviation
				+ System.getProperty("file.separator") + archiveName;

		// extract file to temp directory and return path to file
		return ArchiveExtractionUtils.extractFileFromTarGzArchive(archiveName, fileName,
				outputDirectoryName, remoteArchiveDirectory);
	}

	@Override
	protected void setUpDataSetDescriptions() {

		String matrixArchiveName = null;
		String matrixFileName = null;

		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		sampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setReplacementExpression("\\.", "-");
		idTypeParsingRules.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);
		idTypeParsingRules.setToLowerCase(true);
		idTypeParsingRules.setDefault(true);
		sampleIDSpecification.setIdTypeParsingRules(idTypeParsingRules);

		IDSpecification rowIDSpecification;

		// ====== mRNA ======

		rowIDSpecification = null; // uses genes
		try {

			if (loadSampledGenes) {
				matrixArchiveName = "mRNA_Clustering_CNMF";
				matrixFileName = "cnmf.normalized.gct";
			}
			else {
				matrixArchiveName = "mRNA_Preprocess_Median";
				matrixFileName = tumorAbbreviation + ".medianexp.txt";
			}

			projectDescription.add(setUpClusteredMatrixData("mRNA_Clustering_CNMF",
					"mRNA_Clustering_Consensus", matrixArchiveName, matrixFileName, "mRNA",
					rowIDSpecification, sampleIDSpecification, true,
					EDataSetType.mRNA.getColor()));

		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== microRNA ======

		rowIDSpecification = new IDSpecification();
		rowIDSpecification.setIdType("microRNA");
		rowIDSpecification.setIdCategory("microRNA");

		try {

			if (loadSampledGenes) {
				matrixArchiveName = "miR_Clustering_CNMF";
				matrixFileName = "cnmf.normalized.gct";
			}
			else {
				matrixArchiveName = "miR_Preprocess";
				matrixFileName = tumorAbbreviation + ".miR_expression.txt";
			}

			projectDescription.add(setUpClusteredMatrixData("miR_Clustering_CNMF",
					"miR_Clustering_Consensus", matrixArchiveName, matrixFileName, "microRNA",
					rowIDSpecification, sampleIDSpecification, false,
					EDataSetType.microRNA.getColor()));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// TCGA SAMPLE IDs look different for seq data (an "-01" is attached)

		IDSpecification seqSampleIDSpecification = new IDSpecification();
		seqSampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		seqSampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules seqSampleIDTypeParsingRules = new IDTypeParsingRules();
		seqSampleIDTypeParsingRules.setSubStringExpression("tcga\\-|\\-..\\z");
		seqSampleIDTypeParsingRules.setReplacementExpression("\\.", "-");
		seqSampleIDTypeParsingRules.setToLowerCase(true);
		seqSampleIDSpecification.setIdTypeParsingRules(seqSampleIDTypeParsingRules);

		// ====== mRNAseq ======

		try {
			if (loadSampledGenes) {
				matrixArchiveName = "mRNAseq_Clustering_CNMF";
				matrixFileName = "cnmf.normalized.gct";
			}
			else {
				matrixArchiveName = "mRNAseq_Preprocess";
				matrixFileName = tumorAbbreviation + ".mRNAseq_RPKM_log2.txt";
			}

			projectDescription.add(setUpClusteredMatrixData("mRNAseq_Clustering_CNMF",
					"mRNAseq_Clustering_Consensus", matrixArchiveName, matrixFileName,
					"mRNA-seq", rowIDSpecification, seqSampleIDSpecification, true,
					EDataSetType.mRNAseq.getColor()));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== microRNAseq ======

		try {

			if (loadSampledGenes) {
				matrixArchiveName = "miRseq_Clustering_CNMF";
				matrixFileName = "cnmf.normalized.gct";
			}
			else {
				matrixArchiveName = "miRseq_Preprocess";
				matrixFileName = tumorAbbreviation + ".miRseq_RPKM_log2.txt";
			}

			projectDescription.add(setUpClusteredMatrixData("miRseq_Clustering_CNMF",
					"miRseq_Clustering_Consensus", matrixArchiveName, matrixFileName,
					"microRNA-seq", rowIDSpecification, seqSampleIDSpecification, false,
					EDataSetType.microRNAseq.getColor()));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== methylation ======

		rowIDSpecification = null; // uses genes

		try {
			projectDescription.add(setUpClusteredMatrixData("Methylation_Clustering_CNMF",
					"Methylation_Clustering_Consensus", "Methylation_Clustering_CNMF",
					"cnmf.normalized.gct", "Methylation", rowIDSpecification,
					sampleIDSpecification, true, EDataSetType.methylation.getColor()));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== RPPA (reverse-phase protein arrays) ======

		rowIDSpecification = new IDSpecification();
		rowIDSpecification.setIdType("protein");
		rowIDSpecification.setIdCategory("protein");

		try {
			projectDescription.add(setUpClusteredMatrixData("RPPA_Clustering_CNMF",
					"RPPA_Clustering_Consensus", "RPPA_Clustering_CNMF",
					"cnmf.normalized.gct", "RPPA", rowIDSpecification, sampleIDSpecification,
					false, EDataSetType.RPPA.getColor()));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== copy number ======

		try {
			projectDescription.add(setUpCopyNumberData("CopyNumber_Gistic2", "Copy Number",
					EDataSetType.copyNumber.getColor()));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== mutation ======

		try {
			projectDescription.add(setUpMutationData("Mutation_Significance", "Mutations",
					EDataSetType.mutation.getColor()));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== clinical ======

		try {
			projectDescription.add(setUpClinicalData("Clinical_Pick_Tier1", "Clinical",
					EDataSetType.clinical.getColor()));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private DataSetDescription setUpClusteredMatrixData(String cnmfArchiveName,
			String hierarchicalArchiveName, String matrixArchiveName, String matrixFileName,
			String dataSetName, IDSpecification rowIDSpecification,
			IDSpecification columnIDSpecification, boolean isGeneIdType, Color color) {

		String matrixFile = this.extractFile(matrixFileName, matrixArchiveName,
				analysisRunIdentifierWithoutUnderscore, remoteAnalysisRunArchiveDirectory, 4);
		String cnmfGroupingFile = this.extractFile("cnmf.membership.txt", cnmfArchiveName,
				analysisRunIdentifierWithoutUnderscore, remoteAnalysisRunArchiveDirectory, 4);

		DataSetDescription matrixData = new DataSetDescription();
		matrixData.setDataSetName(dataSetName);
		matrixData.setDataSourcePath(matrixFile);
		matrixData.setNumberOfHeaderLines(3);
		matrixData.setColor(color);
		matrixData.setDataCenteredAtZero(true);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.CONTINUOUS));
		matrixData.addParsingRule(parsingRule);
		matrixData.setTransposeMatrix(true);

		if (isGeneIdType) {
			IDSpecification geneIDSpecification = new IDSpecification();
			geneIDSpecification.setIDTypeGene(true);
			geneIDSpecification.setIdType("GENE_SYMBOL");
			matrixData.setRowIDSpecification(geneIDSpecification);
		}
		else {
			if (rowIDSpecification == null) {
				rowIDSpecification = new IDSpecification();
				rowIDSpecification.setIdType("unknown");
				rowIDSpecification.setIdCategory("unknown");
			}
			matrixData.setRowIDSpecification(rowIDSpecification);
		}

		matrixData.setColumnIDSpecification(columnIDSpecification);

		GroupingParseSpecification firehoseCnmfClustering = new GroupingParseSpecification(
				cnmfGroupingFile);
		firehoseCnmfClustering.setContainsColumnIDs(false);
		firehoseCnmfClustering.setRowIDSpecification(columnIDSpecification);
		firehoseCnmfClustering.setGroupingName("CNMF Clustering");
		matrixData.addColumnGroupingSpecification(firehoseCnmfClustering);

		try {
			String hierarchicalGroupingFile = this.extractFile(this.tumorAbbreviation
					+ ".allclusters.txt", hierarchicalArchiveName,
					analysisRunIdentifierWithoutUnderscore, remoteAnalysisRunArchiveDirectory,
					4);

			GroupingParseSpecification firehoseHierarchicalClustering = new GroupingParseSpecification(
					hierarchicalGroupingFile);
			firehoseHierarchicalClustering.setContainsColumnIDs(false);
			firehoseHierarchicalClustering.setRowIDSpecification(columnIDSpecification);
			firehoseHierarchicalClustering.setGroupingName("Hierchical Clustering");
			matrixData.addColumnGroupingSpecification(firehoseHierarchicalClustering);
		}
		catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}

		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		KMeansClusterConfiguration kMeansAlgo = new KMeansClusterConfiguration();
		kMeansAlgo.setNumberOfClusters(5);
		clusterConfiguration.setClusterAlgorithmConfiguration(kMeansAlgo);
		dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);
		matrixData.setDataProcessingDescription(dataProcessingDescription);

		return matrixData;
	}

	private DataSetDescription setUpMutationData(String archiveName, String dataSetName,
			Color color) {

		String mutationFile = this.extractFile(this.tumorAbbreviation
				+ ".per_gene.mutation_counts.txt", archiveName,
				analysisRunIdentifierWithoutUnderscore, remoteAnalysisRunArchiveDirectory, 4);

		DataSetDescription mutationData = new DataSetDescription();
		mutationData.setDataSetName(dataSetName);
		mutationData.setDataSourcePath(mutationFile);
		mutationData.setNumberOfHeaderLines(1);
		mutationData.setMax((float) 1);
		mutationData.setColor(color);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(8);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.NOMINAL));
		mutationData.addParsingRule(parsingRule);
		mutationData.setTransposeMatrix(true);

		IDSpecification mutationSampleIDSpecification = new IDSpecification();
		mutationSampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		mutationSampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules mutationIDTypeParsingRules = new IDTypeParsingRules();
		mutationIDTypeParsingRules.setReplacementExpression("\\_", "-");
		mutationIDTypeParsingRules.setSubStringExpression("^[a-z]+\\-");
		mutationIDTypeParsingRules.setToLowerCase(true);
		mutationSampleIDSpecification.setIdTypeParsingRules(mutationIDTypeParsingRules);
		mutationData.setColumnIDSpecification(mutationSampleIDSpecification);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mutationData.setRowIDSpecification(geneIDSpecification);

		return mutationData;
	}

	private DataSetDescription setUpCopyNumberData(String archiveName, String dataType,
			Color color) {
		String copyNumberFile = this.extractFile("all_thresholded.by_genes.txt", archiveName,
				analysisRunIdentifierWithoutUnderscore, remoteAnalysisRunArchiveDirectory, 4);

		DataSetDescription copyNumberData = new DataSetDescription();
		copyNumberData.setDataSetName(dataType);
		copyNumberData.setDataSourcePath(copyNumberFile);
		copyNumberData.setNumberOfHeaderLines(1);
		copyNumberData.setColor(color);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.ORDINAL));
		copyNumberData.addParsingRule(parsingRule);
		copyNumberData.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		copyNumberData.setRowIDSpecification(geneIDSpecification);
		copyNumberData.setColumnIDSpecification(sampleIDSpecification);

		return copyNumberData;
	}

	private DataSetDescription setUpClinicalData(String archiveName, String dataType,
			Color color) {

		String clinicalFile = this.extractFile(this.tumorAbbreviation
				+ ".clin.merged.picked.txt", archiveName, dataRunIdentifierWithoutUnderscore,
				remoteDataRunArchiveDirectory, 4);

		transposeCSV(clinicalFile);

		DataSetDescription clinicalData = new DataSetDescription();
		clinicalData.setDataSetName("Clinical");
		clinicalData.setDataHomogeneous(false);
		clinicalData.setDataSourcePath(clinicalFile);
		clinicalData.setNumberOfHeaderLines(1);
		clinicalData.setColor(color);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setToColumn(4);
		parsingRule.setColumnDescripton(new ColumnDescription());
		clinicalData.addParsingRule(parsingRule);

		IDSpecification clinicalIdSpecification = new IDSpecification();
		clinicalIdSpecification.setIdType("clinical");
		clinicalData.setColumnIDSpecification(clinicalIdSpecification);

		IDSpecification clinicalSampleIDSpecification = new IDSpecification();
		clinicalSampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		clinicalSampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules clinicalSampleIDTypeParsingRules = new IDTypeParsingRules();
		clinicalSampleIDTypeParsingRules.setSubStringExpression("tcga\\-");
		clinicalSampleIDTypeParsingRules.setToLowerCase(true);
		clinicalSampleIDSpecification.setIdTypeParsingRules(clinicalSampleIDTypeParsingRules);
		clinicalData.setRowIDSpecification(clinicalSampleIDSpecification);

		return clinicalData;
	}

	private void transposeCSV(String fileName) {

		// Tmp file needed because script cannot read and write from same file
		// simultaneously
		String tmpFile = fileName + "tmp";

		Runtime rt = Runtime.getRuntime();
		Process p;
		try {

			new File(tmpFile).delete();

			p = rt.exec(new String[] { "resources/transpose_csv.sh", fileName, tmpFile });
			p.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = buf.readLine()) != null) {
				System.out.println(line);
			}

			FileOperations.copyFolder(new File(tmpFile), new File(fileName));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
