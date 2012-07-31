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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataProcessingDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.data.importer.setupgenerator.DataSetDescriptionSerializer;

/**
 * Generator class that writes the loading information of a series of TCGA data
 * sets to an XML file.
 * 
 * @author Nils Gehlenborg
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGADataXMLGenerator
	extends DataSetDescriptionSerializer {

	private static String FIREHOSE_URL_PREFIX = "http://gdac.broadinstitute.org/runs/analyses__";
	private static String FIREHOSE_TAR_NAME_PREFIX = "gdac.broadinstitute.org_";

	// protected String tumorName;
	protected String tumorAbbreviation;

	protected String analysisRunIdentifier;
	protected String analysisRunIdentifierWithoutUnderscore;
	
	protected String dataRunIdentifier;
	protected String dataRunIdentifierWithoutUnderscore;
	
	protected String tmpOutputDirectoryPath;
	protected String remoteArchiveDirectory;

	public static final String TCGA_ID_SUBSTRING_REGEX = "TCGA\\-|\\-...\\-";

	private IDSpecification sampleIDSpecification;

	public static void main(String[] args) {

		TCGADataXMLGenerator generator = new TCGADataXMLGenerator(args);
		generator.run();
	}

	public TCGADataXMLGenerator(String[] arguments) {
		super(arguments);

		// this.tumorName = "Glioblastoma Multiforme";
		this.tumorAbbreviation = "STAD";
		this.analysisRunIdentifier = "2012_05_25";
		this.dataRunIdentifier = "2012_07_07";
		this.tmpOutputDirectoryPath = GeneralManager.CALEYDO_HOME_PATH + "TCGA/tmp";
		this.outputXMLFilePath = this.tmpOutputDirectoryPath + System.getProperty("file.separator")
				+ tumorAbbreviation + "_" + this.analysisRunIdentifierWithoutUnderscore + "_caleydo.xml";

		init();
	}

	public TCGADataXMLGenerator(String tumorAbbreviation, String runIdentifierUnderscore,
			String dataRunIdentifier, String outputXMLFilePath, String tmpOutputDirectoryPath) {

		super(null);

		this.tumorAbbreviation = tumorAbbreviation;
		this.analysisRunIdentifier = runIdentifierUnderscore;
		this.dataRunIdentifier = dataRunIdentifier;
		this.outputXMLFilePath = outputXMLFilePath;
		this.tmpOutputDirectoryPath = tmpOutputDirectoryPath;

		init();
	}

	private void init() {
		this.analysisRunIdentifierWithoutUnderscore = analysisRunIdentifier.replace("_", "");
		this.dataRunIdentifierWithoutUnderscore = dataRunIdentifier.replace("_", "");

		// create path of archive search directory
		this.remoteArchiveDirectory = FIREHOSE_URL_PREFIX + analysisRunIdentifier
				+ "/data/" + tumorAbbreviation + "/" + analysisRunIdentifierWithoutUnderscore + "/";
	}

	protected String extractFileFromTarGzArchive(String archiveName, String fileName,
			String outputDirectoryName) {
		String outputFileName = null;

		try {
			byte[] buf = new byte[1024];
			TarInputStream tarInputStream = null;
			TarEntry tarEntry;

			tarInputStream = new TarInputStream(new GZIPInputStream(new URL(
					this.remoteArchiveDirectory + System.getProperty("file.separator")
							+ archiveName).openStream()));

			tarEntry = tarInputStream.getNextEntry();
			while (tarEntry != null) {
				// for each entry to be extracted
				String entryName = tarEntry.getName();

				// only continue if the this entry is the one we need to extract
				if (!entryName.endsWith(fileName)) {
					tarEntry = tarInputStream.getNextEntry();
					continue;
				}

				int n;
				FileOutputStream fileoutputstream;
				File newFile = new File(entryName);
				String directory = newFile.getParent();

				if (directory == null) {
					if (newFile.isDirectory())

						break;
				}

				outputDirectoryName += System.getProperty("file.separator")
						+ this.analysisRunIdentifierWithoutUnderscore + System.getProperty("file.separator")
						+ this.tumorAbbreviation + System.getProperty("file.separator")
						+ archiveName;

				if (!(new File(outputDirectoryName)).exists()) {
					if (!(new File(outputDirectoryName)).mkdirs()) {
						// Directory creation failed
						throw new RuntimeException("Unable to create output directory "
								+ outputDirectoryName + " for " + fileName + ".");
					}
				}

				outputFileName = outputDirectoryName + System.getProperty("file.separator")
						+ fileName;

				fileoutputstream = new FileOutputStream(outputFileName);

				while ((n = tarInputStream.read(buf, 0, 1024)) > -1)
					fileoutputstream.write(buf, 0, n);

				fileoutputstream.close();
				tarInputStream.close();

				break;
			}// while

		}
		catch (Exception e) {
			throw new RuntimeException("Unable to extract " + fileName + " from "
					+ archiveName + ".");
		}

		if (outputFileName == null) {
			throw new RuntimeException("File " + fileName + " not found in " + archiveName
					+ ".");
		}

		return outputFileName;
	}

	// find Firehose archive in Firehose_get output directory and extract file
	// from archive to temp directory
	// return path to file in temp directory
	protected String extractFile(String fileName, String pipelineName) {
		// check if exactly one archive exists, if not return null
		// String[] archiveNames = new java.io.File(this.archiveDirectory)
		// .list(new PipelineNameFilter(pipelineName));

		// if (archiveNames.length == 0) {
		// throw new RuntimeException("No archive found for pipeline " +
		// pipelineName
		// + " in " + this.archiveDirectory);
		// }
		//
		// if (archiveNames.length > 1) {
		// throw new RuntimeException("Multiple archives found for pipeline " +
		// pipelineName
		// + " in " + this.archiveDirectory);
		// }
		//
		// String archiveName = archiveNames[0];

		// gdac.broadinstitute.org_GBM.Methylation_Clustering_CNMF.Level_4.2012052500.0.0.tar.gz
		String archiveName = FIREHOSE_TAR_NAME_PREFIX + tumorAbbreviation + "." + pipelineName
				+ ".Level_4." + analysisRunIdentifierWithoutUnderscore + "00.0.0.tar.gz";

		// extract file to temp directory and return path to file
		return extractFileFromTarGzArchive(archiveName, fileName, tmpOutputDirectoryPath);
	}

	@Override
	protected void setUpDataSetDescriptions() {

		sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		sampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setReplacementExpression("\\.", "-");
		idTypeParsingRules.setSubStringExpression(TCGA_ID_SUBSTRING_REGEX);
		sampleIDSpecification.setIdTypeParsingRules(idTypeParsingRules);
		IDSpecification rowIDSpecification;
		
		
		// ====== mRNA ============================================================================
				
		rowIDSpecification = null; // uses genes
		try {
			dataSetDescriptionCollection.add(setUpClusteredMatrixData("mRNA_Clustering_CNMF",
					"mRNA_Clustering_Consensus", "outputprefix.expclu.gct", "mRNA", rowIDSpecification, true));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		try {
			dataSetDescriptionCollection.add(setUpClusteredMatrixData("mRNAseq_Clustering_CNMF",
					"mRNAseq_Clustering_Consensus", "outputprefix.expclu.gct", "mRNA-seq", rowIDSpecification, true));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}


		// ====== microRNA ========================================================================

		rowIDSpecification = new IDSpecification();
		rowIDSpecification.setIdType("microRNA");
		rowIDSpecification.setIdCategory("microRNA");

		try {
			dataSetDescriptionCollection.add(setUpClusteredMatrixData("miR_Clustering_CNMF",
					"miR_Clustering_Consensus", "cnmf.normalized.gct", "microRNA", rowIDSpecification, false));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		try {
			dataSetDescriptionCollection.add(setUpClusteredMatrixData(
					"miRseq_Clustering_CNMF", "miRseq_Clustering_Consensus",
					"cnmf.normalized.gct", "microRNA-seq", rowIDSpecification, false));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		
		// ====== methylation =====================================================================
		
		rowIDSpecification = null; // uses genes

		try {
			dataSetDescriptionCollection.add(setUpClusteredMatrixData(
					"Methylation_Clustering_CNMF", "Methylation_Clustering_Consensus",
					"cnmf.normalized.gct", "methylation", rowIDSpecification, true));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}		
		
		
		// ====== reverse-phase protein arrays ====================================================

		rowIDSpecification = new IDSpecification();
		rowIDSpecification.setIdType("protein");
		rowIDSpecification.setIdCategory("protein");

		
		try {
			dataSetDescriptionCollection.add(setUpClusteredMatrixData("RPPA_Clustering_CNMF",
					"RPPA_Clustering_Consensus", "cnmf.normalized.gct", "RPPA", rowIDSpecification, false));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		
		// ====== copy number =====================================================================

		try {
			dataSetDescriptionCollection.add(setUpCopyNumberData("CopyNumber_Gistic2",
					"Copy Number"));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
		

		// ====== mutation ========================================================================

		try {
			dataSetDescriptionCollection.add(setUpMutationData("Mutation_Significance",
					"Mutations"));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		
	}

	private DataSetDescription setUpClusteredMatrixData(String cnmfArchiveName,
			String hierarchicalArchiveName, String matrixFileName, String dataSetName, IDSpecification rowIDSpecification,
			boolean isGeneIdType) {
		String matrixFile = this.extractFile(matrixFileName, cnmfArchiveName);
		String cnmfGroupingFile = this.extractFile("cnmf.membership.txt", cnmfArchiveName);

		DataSetDescription matrixData = new DataSetDescription();
		matrixData.setDataSetName(dataSetName);

		matrixData.setDataSourcePath(matrixFile);
		matrixData.setNumberOfHeaderLines(3);

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
			if ( rowIDSpecification == null ) {
				rowIDSpecification = new IDSpecification();
				rowIDSpecification.setIdType("unknown");				
				rowIDSpecification.setIdCategory("unknown");				
			}				
			matrixData.setRowIDSpecification(rowIDSpecification);				
		}

		matrixData.setColumnIDSpecification(sampleIDSpecification);

		GroupingParseSpecification firehoseCnmfClustering = new GroupingParseSpecification(
				cnmfGroupingFile);
		firehoseCnmfClustering.setContainsColumnIDs(false);
		firehoseCnmfClustering.setRowIDSpecification(sampleIDSpecification);
		firehoseCnmfClustering.setGroupingName( "NMF Cluster" );
		matrixData.addColumnGroupingSpecification(firehoseCnmfClustering);

		try {
			String hierarchicalGroupingFile = this.extractFile(this.tumorAbbreviation
					+ ".allclusters.txt", hierarchicalArchiveName); // e.g.
																	// GBM.allclusters.txt

			GroupingParseSpecification firehoseHierarchicalClustering = new GroupingParseSpecification(
					hierarchicalGroupingFile);
			firehoseHierarchicalClustering.setContainsColumnIDs(false);
			firehoseHierarchicalClustering.setRowIDSpecification(sampleIDSpecification);
			firehoseHierarchicalClustering.setGroupingName( "Hier. Cluster" );
			matrixData.addColumnGroupingSpecification(firehoseHierarchicalClustering);
		}
		catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}

		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		KMeansClusterConfiguration clusterConfiguration = new KMeansClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		clusterConfiguration.setNumberOfClusters(5);
		dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);
		matrixData.setDataProcessingDescription(dataProcessingDescription);

		return matrixData;
	}

	
	private DataSetDescription setUpMutationData(String archiveName, String dataSetName) {
		IDSpecification mutationSampleIDSpecification = new IDSpecification();
		mutationSampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		mutationSampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules mutationIDTypeParsingRules = new IDTypeParsingRules();
		mutationIDTypeParsingRules.setSubStringExpression(this.tumorAbbreviation + "\\_|\\_...");
		mutationSampleIDSpecification.setIdTypeParsingRules(mutationIDTypeParsingRules);
				
		String mutationFile = this.extractFile( this.tumorAbbreviation + ".per_gene.mutation_counts.txt", archiveName);

		DataSetDescription mutationData = new DataSetDescription();
		mutationData.setDataSetName(dataSetName);

		mutationData.setDataSourcePath(mutationFile);
		mutationData.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(8);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT",
				ColumnDescription.NOMINAL));
		mutationData.addParsingRule(parsingRule);
		mutationData.setTransposeMatrix(true);

		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		mutationData.setRowIDSpecification(geneIDSpecification);
		mutationData.setColumnIDSpecification(mutationSampleIDSpecification);

		return mutationData;
	}

	
	private DataSetDescription setUpCopyNumberData(String archiveName, String dataType) {
		String copyNumberFile = this.extractFile(  "all_thresholded.by_genes.txt", archiveName);

		DataSetDescription copyNumberData = new DataSetDescription();
		copyNumberData.setDataSetName(dataType);

		copyNumberData.setDataSourcePath(copyNumberFile);
		copyNumberData.setNumberOfHeaderLines(1);

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

		return copyNumberData;
	}
	

	// find pipeline archive name filter (filename pattern matcher)
	// TODO: replace with PathMatcher in Java 7
	class PipelineNameFilter
		implements FilenameFilter {

		protected String pipelineName;

		public PipelineNameFilter(String pipelineName) {
			this.pipelineName = pipelineName;
		}

		public boolean accept(File directory, String fileName) {
			if (fileName.contains(this.pipelineName + "." + "Level_4")) {
				if (fileName.endsWith(".tar.gz")) {
					return true;
				}
			}

			return false;
		}
	}
}
