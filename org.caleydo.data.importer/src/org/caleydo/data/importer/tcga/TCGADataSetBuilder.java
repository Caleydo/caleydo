/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.data.importer.tcga;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;

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
import org.caleydo.core.io.ParsingRule;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.color.Colors;
import org.caleydo.data.importer.tcga.model.ClinicalMapping;
import org.caleydo.data.importer.tcga.model.TCGADataSet;
import org.caleydo.data.importer.tcga.model.TumorType;
import org.caleydo.datadomain.genetic.TCGADefinitions;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class TCGADataSetBuilder extends RecursiveTask<TCGADataSet> {
	private static final Logger log = Logger.getLogger(TCGADataSetBuilder.class.getSimpleName());
	private static final long serialVersionUID = 6468622325177694143L;

	private static final String CLUSTER_FILE = "outputprefix.expclu.gct";
	private static final int LEVEL = 4;

	private final EDataSetType dataSetType;
	private final TumorType tumorAbbreviation;
	private boolean loadSampledGenes;

	private final FirehoseProvider fileProvider;

	private final String dataSetName;
	private final Settings settings;

	private TCGADataSetBuilder(TumorType tumorAbbreviation, EDataSetType datasetType, String dataSetName,
			FirehoseProvider fileProvider, boolean loadSampledGenes, Settings settings) {
		this.tumorAbbreviation = tumorAbbreviation;
		this.dataSetType = datasetType;
		this.fileProvider = fileProvider;
		this.loadSampledGenes = loadSampledGenes;
		this.dataSetName = dataSetName;
		this.settings = settings;

	}

	public static ForkJoinTask<TCGADataSet> create(TumorType tumorAbbreviation, EDataSetType datasetType,
			FirehoseProvider fileProvider, boolean loadSampledGenes, Settings settings) {
		return create(tumorAbbreviation, datasetType, datasetType.getName(), fileProvider, loadSampledGenes, settings);
	}

	public static ForkJoinTask<TCGADataSet> create(TumorType tumorAbbreviation, EDataSetType datasetType,
			String dataSetName, FirehoseProvider fileProvider, boolean loadSampledGenes, Settings settings) {
		return new TCGADataSetBuilder(tumorAbbreviation, datasetType, dataSetName, fileProvider, loadSampledGenes,
				settings);
	}

	@Override
	public TCGADataSet compute() {
		final IDSpecification sampleID = TCGADefinitions.createIDSpecification(true);

		// TCGA SAMPLE IDs look different for seq data (an "-01" is attached)
		final IDSpecification seqSampleID = TCGADefinitions.createIDSpecification(false);

		final IDSpecification clinicalColumnID = new IDSpecification();
		clinicalColumnID.setIdType("clinical");

		final IDSpecification geneRowID = IDSpecification.createGene();
		final IDSpecification proteinRowID = new IDSpecification("protein", "protein");
		final IDSpecification microRNARowID = new IDSpecification("microRNA", "microRNA");
		final IDSpecification clinicalRowID = new IDSpecification("TCGA_SAMPLE", "TCGA_SAMPLE");

		IDTypeParsingRules clinicalSampleIDTypeParsingRules = new IDTypeParsingRules();
		clinicalSampleIDTypeParsingRules.setSubStringExpression("tcga\\-");
		clinicalSampleIDTypeParsingRules.setToLowerCase(true);
		clinicalRowID.setIdTypeParsingRules(clinicalSampleIDTypeParsingRules);

		DataSetDescription desc = null;
		switch (dataSetType) {
		case mRNA:
			if (loadSampledGenes) {
				desc = setUpClusteredMatrixData(geneRowID, sampleID);
			} else {
				desc = setUpClusteredMatrixData("mRNA_Preprocess_Median", tumorAbbreviation + ".medianexp.txt",
						geneRowID, sampleID);
			}
			break;
		case mRNAseq:
			if (loadSampledGenes) {
				desc = setUpClusteredMatrixData(geneRowID, seqSampleID);
			} else {
				desc = setUpClusteredMatrixData("mRNAseq_Preprocess", tumorAbbreviation + ".mRNAseq_RPKM_log2.txt",
						geneRowID, seqSampleID);
			}
			break;
		case microRNA:
			if (loadSampledGenes) {
				desc = setUpClusteredMatrixData(microRNARowID, sampleID);
			} else {
				desc = setUpClusteredMatrixData("miR_Preprocess", tumorAbbreviation + ".miR_expression.txt",
						microRNARowID, sampleID);
			}
			break;
		case microRNAseq:
			if (loadSampledGenes) {
				desc = setUpClusteredMatrixData(microRNARowID, seqSampleID);
			} else {
				desc = setUpClusteredMatrixData("miRseq_Preprocess", tumorAbbreviation + ".miRseq_RPKM_log2.txt",
						microRNARowID, seqSampleID);
			}
			break;
		case methylation:
			desc = setUpClusteredMatrixData(geneRowID, sampleID);
			break;
		case RPPA:
			desc = setUpClusteredMatrixData(proteinRowID, sampleID);
			break;
		case clinical:
			desc = setUpClinicalData(clinicalRowID, clinicalColumnID);
			break;
		case mutation:
			desc = setUpMutationData(geneRowID, sampleID);
			break;
		case copyNumber:
			desc = setUpCopyNumberData(geneRowID, sampleID);
			break;
		}
		if (desc == null)
			return null;
		return new TCGADataSet(desc, this.dataSetType);
	}

	private DataSetDescription setUpClusteredMatrixData(IDSpecification rowIDSpecification,
			IDSpecification sampleIDSpecification) {
		return setUpClusteredMatrixData(dataSetType.getTCGAAbbr() + "_Clustering_CNMF", CLUSTER_FILE,
				rowIDSpecification, sampleIDSpecification);
	}

	private DataSetDescription setUpClusteredMatrixData(String matrixArchiveName, String matrixFileName,
			IDSpecification rowIDSpecification, IDSpecification columnIDSpecification) {
		String cnmfArchiveName = dataSetType.getTCGAAbbr() + "_Clustering_CNMF";
		String hierarchicalArchiveName = dataSetType.getTCGAAbbr() + "_Clustering_Consensus";

		File matrixFile = fileProvider.extractAnalysisRunFile(matrixFileName, matrixArchiveName, LEVEL);
		if (matrixFile == null)
			return null;
		File cnmfGroupingFile = fileProvider.extractAnalysisRunFile("cnmf.membership.txt", cnmfArchiveName, LEVEL);
		if (cnmfGroupingFile == null)
			return null;

		DataSetDescription dataSet = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataSourcePath(matrixFile.getPath());
		if (loadSampledGenes) {
			// the gct files have 3 header lines and are centered<
			dataSet.setNumberOfHeaderLines(3);
			dataSet.getDataDescription().getNumericalProperties().setDataCenter(0d);
		} else {
			// the files with all the genes have the ids in the first row, then a row with "signal" and then the data
			dataSet.setNumberOfHeaderLines(2);
			dataSet.setRowOfColumnIDs(0);

		}

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		dataSet.addParsingRule(parsingRule);
		dataSet.setTransposeMatrix(true);

		dataSet.setRowIDSpecification(rowIDSpecification);

		dataSet.setColumnIDSpecification(columnIDSpecification);

		GroupingParseSpecification firehoseCnmfClustering = new GroupingParseSpecification(cnmfGroupingFile.getPath());
		firehoseCnmfClustering.setContainsColumnIDs(false);
		firehoseCnmfClustering.setRowIDSpecification(columnIDSpecification);
		firehoseCnmfClustering.setGroupingName("CNMF Clustering");
		dataSet.addColumnGroupingSpecification(firehoseCnmfClustering);

		try {
			File hierarchicalGroupingFile = fileProvider.extractAnalysisRunFile(tumorAbbreviation + ".allclusters.txt",
					hierarchicalArchiveName, LEVEL);
			if (hierarchicalGroupingFile == null)
				throw new IllegalStateException("can't extract: " + tumorAbbreviation + ".allclusters.txt");
			GroupingParseSpecification firehoseHierarchicalClustering = new GroupingParseSpecification(
					hierarchicalGroupingFile.getPath());
			firehoseHierarchicalClustering.setContainsColumnIDs(false);
			firehoseHierarchicalClustering.setRowIDSpecification(columnIDSpecification);
			firehoseHierarchicalClustering.setGroupingName("Hierarchical Clustering");
			dataSet.addColumnGroupingSpecification(firehoseHierarchicalClustering);
		} catch (RuntimeException e) {
			System.err.println("can't extract hierarchical information " + e.getMessage());
		}

		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		KMeansClusterConfiguration kMeansAlgo = new KMeansClusterConfiguration();
		kMeansAlgo.setNumberOfClusters(5);
		clusterConfiguration.setClusterAlgorithmConfiguration(kMeansAlgo);
		dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);
		dataSet.setDataProcessingDescription(dataProcessingDescription);

		if (loadSampledGenes) {
			// here we turn on sampling to 1500
			dataProcessingDescription.setNrRowsInSample(1500);
		}

		return dataSet;
	}

	private DataSetDescription setUpMutationData(IDSpecification geneIDSpecification,
			IDSpecification sampleIDSpecification) {
		int startColumn = 8;
		File mutationFile = fileProvider.extractAnalysisRunFile(tumorAbbreviation + ".per_gene.mutation_counts.txt",
				"Mutation_Significance", LEVEL);
		if (mutationFile == null)
			mutationFile = fileProvider.extractAnalysisRunFile(tumorAbbreviation + ".per_gene.mutation_counts.txt",
					"MutSigRun2.0", LEVEL);

		if (mutationFile == null) {
			File maf = fileProvider.extractAnalysisRunFile(tumorAbbreviation + "-TP.final_analysis_set.maf",
					"MutSigNozzleReport2.0", LEVEL);
			if (maf != null) {
				mutationFile = parseMAF(maf);
				startColumn = 1;
			}
		}
		if (mutationFile != null) {
			DataSetDescription dataSet = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
			dataSet.setDataSetName(dataSetName);
			dataSet.setColor(dataSetType.getColor());

			dataSet.setDataSourcePath(mutationFile.getPath());
			dataSet.setNumberOfHeaderLines(1);
			ParsingRule parsingRule = new ParsingRule();
			parsingRule.setFromColumn(startColumn);
			parsingRule.setParseUntilEnd(true);
			// TODO: review ordinale/integer
			parsingRule.setColumnDescripton(new ColumnDescription());
			dataSet.addParsingRule(parsingRule);
			dataSet.setTransposeMatrix(true);
			dataSet.setColumnIDSpecification(sampleIDSpecification);
			dataSet.setRowIDSpecification(geneIDSpecification);

			@SuppressWarnings("unchecked")
			CategoricalClassDescription<Integer> cats = (CategoricalClassDescription<Integer>) dataSet
					.getDataDescription().getCategoricalClassDescription();
			cats.setCategoryType(ECategoryType.ORDINAL);
			cats.setRawDataType(EDataType.INTEGER);
			cats.addCategoryProperty(0, "Not Mutated", Colors.NEUTRAL_GREY);
			cats.addCategoryProperty(1, "Mutated", Colors.RED);
			return dataSet;
		}
		return null;


		// IDSpecification mutationSampleIDSpecification = new
		// IDSpecification();
		// mutationSampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		// mutationSampleIDSpecification.setIdType("TCGA_SAMPLE");

		// Mutation uses a different ID convention, the source looks like this:
		// OV_20_0990
		// IDTypeParsingRules mutationSampleIDTypeParsingRules = new
		// IDTypeParsingRules();
		// mutationSampleIDTypeParsingRules.setReplacementExpression("-",
		// "\\_");
		// mutationSampleIDTypeParsingRules.setSubStringExpression("^[a-z]+\\-");
		// mutationSampleIDTypeParsingRules.setToLowerCase(true);
		// mutationSampleIDSpecification
		// .setIdTypeParsingRules(mutationSampleIDTypeParsingRules);
	}

	private File parseMAF(File maf) {
		final String TAB = "\t";

		try {
			List<String> lines = Files.readAllLines(maf.toPath(), Charset.defaultCharset());
			List<String> header = Arrays.asList(lines.get(0).split(TAB));
			lines = lines.subList(1, lines.size());
			int geneIndex = header.indexOf("Hugo_Symbol");
			int sampleIndex = header.indexOf("Tumor_Sample_Barcode");
			// gene x sample x mutated
			Table<String, String, Boolean> mutated = TreeBasedTable.create();
			for (String line : lines) {
				String[] columns = line.split(TAB);
				mutated.put(columns[geneIndex], columns[sampleIndex], Boolean.TRUE);
			}

			File out = new File(maf.getParentFile(), "P" + maf.getName());
			PrintWriter w = new PrintWriter(out);
			w.append("Hugo_Symbol");
			for (String sample : mutated.columnKeySet()) {
				w.append(TAB).append(sample);
			}
			w.println();
			for (String gene : mutated.rowKeySet()) {
				w.append(gene);
				for (String sample : mutated.columnKeySet()) {
					w.append(TAB).append(mutated.contains(gene, sample) ? "1" : "0");
				}
				w.println();
			}
			w.close();
			return out;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private DataSetDescription setUpCopyNumberData(IDSpecification rwoIDSpecification,
			IDSpecification sampleIDSpecification) {
		File copyNumberFile = fileProvider.extractAnalysisRunFile("all_thresholded.by_genes.txt", "CopyNumber_Gistic2",
				LEVEL);
		if (copyNumberFile == null)
			return null;

		DataSetDescription dataSet = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataSourcePath(copyNumberFile.getPath());
		dataSet.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());
		dataSet.addParsingRule(parsingRule);
		dataSet.setTransposeMatrix(true);

		dataSet.setRowIDSpecification(rwoIDSpecification);
		dataSet.setColumnIDSpecification(sampleIDSpecification);

		@SuppressWarnings("unchecked")
		CategoricalClassDescription<Integer> cats = (CategoricalClassDescription<Integer>) dataSet.getDataDescription()
				.getCategoricalClassDescription();
		cats.setCategoryType(ECategoryType.ORDINAL);
		cats.setRawDataType(EDataType.INTEGER);
		cats.addCategoryProperty(-2, "Homozygous deletion", Colors.BLUE);
		cats.addCategoryProperty(-1, "Heterozygous deletion", Colors.BLUE.getColorWithSpecificBrighness(0.5f));
		cats.addCategoryProperty(0, "NORMAL", Colors.NEUTRAL_GREY);
		cats.addCategoryProperty(1, "Low level amplification", Colors.RED.getColorWithSpecificBrighness(0.5f));
		cats.addCategoryProperty(2, "High level amplification", Colors.RED);

		// File cnmfGroupingFile = fileProvider.extractAnalysisRunFile("cnmf.membership.txt",
		// "CopyNumber_Clustering_CNMF", LEVEL);
		// if (cnmfGroupingFile != null) {
		// GroupingParseSpecification firehoseCnmfClustering = new GroupingParseSpecification(
		// cnmfGroupingFile.getPath());
		// firehoseCnmfClustering.setContainsColumnIDs(false);
		// firehoseCnmfClustering.setRowIDSpecification(sampleIDSpecification);
		// firehoseCnmfClustering.setGroupingName("CNMF Clustering");
		// dataSet.addColumnGroupingSpecification(firehoseCnmfClustering);
		// }
		return dataSet;
	}

	private DataSetDescription setUpClinicalData(IDSpecification rowIdSpecification,
			IDSpecification columnIdSpecification) {
		File clinicalFile = fileProvider.extractDataRunFile(tumorAbbreviation + ".clin.merged.picked.txt",
				"Clinical_Pick_Tier1", LEVEL);
		if (clinicalFile == null)
			return null;

		File out = new File(clinicalFile.getParentFile(), "T" + clinicalFile.getName());
		transposeCSV(clinicalFile.getPath(), out.getPath());

		DataSetDescription dataSet = new DataSetDescription();
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataSourcePath(out.getPath());
		dataSet.setNumberOfHeaderLines(1);

		dataSet.setRowIDSpecification(rowIdSpecification);
		dataSet.setColumnIDSpecification(columnIdSpecification);

		String header = readFirstLine(out);
		assert header != null;
		List<String> columns = Arrays.asList(header.split("\t"));

		Collection<String> toInclude = settings.getClinicalVariables();
		for (int i = 2; i < columns.size(); ++i) {
			String name = columns.get(i).toLowerCase();
			boolean found = toInclude.isEmpty();
			for (String inc : toInclude) {
				if (inc.equals(name) || inc.startsWith(name)) {
					found = true;
					break;
				}
			}
			if (found) {
				ClinicalMapping mapping = ClinicalMapping.byName(name);
				if (mapping == null && !toInclude.isEmpty()) {
					log.warning("activly selected clinicial variable: " + name + " is not known using default");
					dataSet.addParsingPattern(new ColumnDescription(i, new DataDescription(EDataClass.CATEGORICAL,
							EDataType.STRING)));
				} else if (mapping != null) {
					dataSet.addParsingPattern(new ColumnDescription(i, new DataDescription(mapping.getDataClass(),
							mapping.getDataType())));
				} else {
					continue;
				}
			}
		}
		return dataSet;
	}

	private String readFirstLine(File file) {
		try (BufferedReader r = new BufferedReader(new FileReader(file))) {
			return r.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void transposeCSV(String fileName, String fileNameOut) {
		log.info("tranposing: " + fileName);
		File in = new File(fileName);

		List<String> data;
		try {
			data = Files.readAllLines(in.toPath(), Charset.defaultCharset());
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		// split into parts
		String[][] parts = new String[data.size()][];
		int maxCol = -1;
		for (int i = 0; i < data.size(); ++i) {
			parts[i] = data.get(i).split("\t");
			if (parts[i].length > maxCol)
				maxCol = parts[i].length;
		}
		data = null;

		try (BufferedWriter writer = Files.newBufferedWriter(new File(fileNameOut).toPath(), Charset.defaultCharset())) {
			for (int c = 0; c < maxCol; ++c) {
				for (int i = 0; i < parts.length; ++i) {
					if (i > 0)
						writer.append('\t');
					String[] p = parts[i];
					if (p.length >= c)
						writer.append(p[c]);
				}
				writer.newLine();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
