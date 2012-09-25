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
package org.caleydo.data.importer.tcga.qualitycontrol;

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
import org.caleydo.core.util.color.ColorManager;
import org.caleydo.data.importer.setupgenerator.DataSetDescriptionSerializer;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.caleydo.data.importer.tcga.utils.ArchiveExtractionUtils;

/**
 * Generator class that writes the loading information of a series of TCGA data
 * sets to an XML file.
 * 
 * @author Nils Gehlenborg
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGAInterAnalysisRunXMLGenerator
	extends DataSetDescriptionSerializer {

	public static String FIREHOSE_URL_PREFIX = "http://gdac.broadinstitute.org/runs/";
	private static String FIREHOSE_TAR_NAME_PREFIX = "gdac.broadinstitute.org_";

	private String tumorAbbreviation;

	private String[] analysisRuns;

	private String tmpOutputDirectoryPath;

	public static final String TCGA_ID_SUBSTRING_REGEX = "tcga\\-|\\-...\\-";

	private IDSpecification sampleIDSpecification;

	public EDataSetType dataSetType;

	public TCGAInterAnalysisRunXMLGenerator(String tumorAbbreviation, String[] analysisRuns,
			EDataSetType dataSetType, String outputXMLFilePath, String outputFolderPath,
			String tmpOutputFolderPath) {

		super(null);

		this.tumorAbbreviation = tumorAbbreviation;
		this.analysisRuns = analysisRuns;
		this.outputXMLFilePath = outputXMLFilePath;
		this.tmpOutputDirectoryPath = tmpOutputFolderPath;
		this.dataSetType = dataSetType;
	}

	@Override
	protected void setUpDataSetDescriptions() {

		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		sampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setReplacementExpression("\\.", "-");
		idTypeParsingRules.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);
		idTypeParsingRules.setToLowerCase(true);
		idTypeParsingRules.setDefault(true);
		sampleIDSpecification.setIdTypeParsingRules(idTypeParsingRules);

		// TCGA SAMPLE IDs look different for seq data (an "-01" is attached)
		IDSpecification seqSampleIDSpecification = new IDSpecification();
		seqSampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		seqSampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules seqSampleIDTypeParsingRules = new IDTypeParsingRules();
		seqSampleIDTypeParsingRules.setSubStringExpression("tcga\\-|\\-..\\z");
		seqSampleIDTypeParsingRules.setReplacementExpression("\\.", "-");
		seqSampleIDTypeParsingRules.setToLowerCase(true);
		seqSampleIDSpecification.setIdTypeParsingRules(seqSampleIDTypeParsingRules);

		for (int analysisRunIndex = 0; analysisRunIndex < analysisRuns.length; analysisRunIndex++) {

			String analysisRun = analysisRuns[analysisRunIndex];
			String analysisRunWithoutUnderscore = analysisRun.replace("_", "");

			IDSpecification rowIDSpecification = null; // uses genes

			try {
				switch (dataSetType) {

					case mRNA:

						projectDescription.add(setUpClusteredMatrixData(
								"mRNA_Clustering_CNMF", "mRNA_Clustering_Consensus",
								"outputprefix.expclu.gct", "mRNA", rowIDSpecification,
								sampleIDSpecification, true, dataSetType.getColor(),
								analysisRun, analysisRunWithoutUnderscore));
						break;
					case mRNAseq:

						projectDescription.add(setUpClusteredMatrixData(
								"mRNAseq_Clustering_CNMF", "mRNAseq_Clustering_Consensus",
								"outputprefix.expclu.gct", "mRNA-seq", rowIDSpecification,
								seqSampleIDSpecification, true, dataSetType.getColor(),
								analysisRun, analysisRunWithoutUnderscore));

						break;
					case microRNA:

						rowIDSpecification = new IDSpecification();
						rowIDSpecification.setIdType("microRNA");
						rowIDSpecification.setIdCategory("microRNA");

						projectDescription.add(setUpClusteredMatrixData("miR_Clustering_CNMF",
								"miR_Clustering_Consensus", "cnmf.normalized.gct", "microRNA",
								rowIDSpecification, sampleIDSpecification, false,
								dataSetType.getColor(), analysisRun,
								analysisRunWithoutUnderscore));

						break;
					case microRNAseq:

						rowIDSpecification = new IDSpecification();
						rowIDSpecification.setIdType("microRNA");
						rowIDSpecification.setIdCategory("microRNA");

						projectDescription.add(setUpClusteredMatrixData(
								"miRseq_Clustering_CNMF", "miRseq_Clustering_Consensus",
								"cnmf.normalized.gct", "microRNA-seq", rowIDSpecification,
								seqSampleIDSpecification, false, dataSetType.getColor(),
								analysisRun, analysisRunWithoutUnderscore));

						break;
					case methylation:

						projectDescription.add(setUpClusteredMatrixData(
								"Methylation_Clustering_CNMF",
								"Methylation_Clustering_Consensus", "cnmf.normalized.gct",
								"Methylation", rowIDSpecification, sampleIDSpecification,
								true, dataSetType.getColor(), analysisRun,
								analysisRunWithoutUnderscore));

						break;
					case RPPA:

						rowIDSpecification = new IDSpecification();
						rowIDSpecification.setIdType("protein");
						rowIDSpecification.setIdCategory("protein");

						projectDescription.add(setUpClusteredMatrixData(
								"RPPA_Clustering_CNMF", "RPPA_Clustering_Consensus",
								"cnmf.normalized.gct", "RPPA", rowIDSpecification,
								sampleIDSpecification, false, dataSetType.getColor(),
								analysisRun, analysisRunWithoutUnderscore));

						break;

				}
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

		ColorManager.get().unmarkAllColors(ColorManager.QUALITATIVE_COLORS);
	}

	private Color getNextDataSetColor() {

		Color color = ColorManager.get().getFirstMarkedColorOfList(
				ColorManager.QUALITATIVE_COLORS, false);
		ColorManager.get().markColor(ColorManager.QUALITATIVE_COLORS, color, true);
		return color;
	}

	private DataSetDescription setUpClusteredMatrixData(String cnmfArchiveName,
			String hierarchicalArchiveName, String matrixFileName, String dataSetName,
			IDSpecification rowIDSpecification, IDSpecification columnIDSpecification,
			boolean isGeneIdType, Color color, String analysisRun,
			String analysisRunWithoutUnderscore) {

		// create path of archive search directory
		String remoteAnalysisRunArchiveDirectory = FIREHOSE_URL_PREFIX + "analyses__"
				+ analysisRun + "/data/" + tumorAbbreviation + "/"
				+ analysisRunWithoutUnderscore + "/";

		String matrixFile = this.extractFile(matrixFileName, cnmfArchiveName,
				analysisRunWithoutUnderscore, remoteAnalysisRunArchiveDirectory, 4);
		String cnmfGroupingFile = this.extractFile("cnmf.membership.txt", cnmfArchiveName,
				analysisRunWithoutUnderscore, remoteAnalysisRunArchiveDirectory, 4);

		DataSetDescription matrixData = new DataSetDescription();
		matrixData.setDataSetName(analysisRun);
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
		firehoseCnmfClustering.setGroupingName("CNMF Cluster");
		matrixData.addColumnGroupingSpecification(firehoseCnmfClustering);

		try {
			String hierarchicalGroupingFile = this.extractFile(this.tumorAbbreviation
					+ ".allclusters.txt", hierarchicalArchiveName,
					analysisRunWithoutUnderscore, remoteAnalysisRunArchiveDirectory, 4);

			GroupingParseSpecification firehoseHierarchicalClustering = new GroupingParseSpecification(
					hierarchicalGroupingFile);
			firehoseHierarchicalClustering.setContainsColumnIDs(false);
			firehoseHierarchicalClustering.setRowIDSpecification(columnIDSpecification);
			firehoseHierarchicalClustering.setGroupingName("Hierarchical");
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
}
