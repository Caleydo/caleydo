/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataProcessingDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescription.ECreateDefaultProperties;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.KNNImputeDescription;
import org.caleydo.core.io.MetaDataElement;
import org.caleydo.core.io.MetaDataElement.AttributeType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.core.util.clusterer.algorithm.affinity.AffinityClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.tree.ETreeClustererAlgo;
import org.caleydo.core.util.clusterer.algorithm.tree.TreeClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.data.importer.tcga.model.ClinicalMapping;
import org.caleydo.data.importer.tcga.model.TCGADataSet;
import org.caleydo.datadomain.genetic.TCGADefinitions;

public class TCGADataSetBuilder extends RecursiveTask<TCGADataSet> {
	private static final Logger log = Logger.getLogger(TCGADataSetBuilder.class.getName());
	private static final long serialVersionUID = 6468622325177694143L;

	private final String dataSetName;
	private final EDataSetType dataSetType;
	private boolean loadFullGenes;

	private final FirehoseProvider fileFinder;

	private final Settings settings;
	private final String id;

	private TCGADataSetBuilder(EDataSetType datasetType, String dataSetName, FirehoseProvider fileProvider,
			boolean loadSampledGenes, Settings settings) {
		this.dataSetType = datasetType;
		this.dataSetName = dataSetName;
		this.loadFullGenes = !loadSampledGenes;
		this.fileFinder = fileProvider;
		this.settings = settings;

		this.id = String.format("%s(%s)[%s] %s", dataSetType, dataSetName, loadFullGenes ? "F" : "S", fileProvider);

	}

	public static ForkJoinTask<TCGADataSet> create(EDataSetType datasetType, FirehoseProvider fileProvider,
			boolean loadSampledGenes, Settings settings) {
		return create(datasetType, datasetType.getName(), fileProvider, loadSampledGenes, settings);
	}

	public static ForkJoinTask<TCGADataSet> create(EDataSetType datasetType, String dataSetName,
			FirehoseProvider fileProvider, boolean loadSampledGenes, Settings settings) {
		return new TCGADataSetBuilder(datasetType, dataSetName, fileProvider, loadSampledGenes, settings);
	}

	@Override
	public TCGADataSet compute() {
		log.info(id + " start");
		final IDSpecification sampleID = TCGADefinitions.createSampleIDSpecification(true);

		// TCGA SAMPLE IDs look different for seq data (an "-01" is attached)
		final IDSpecification seqSampleID = TCGADefinitions.createSampleIDSpecification(false);

		final IDSpecification clinicalColumnID = new IDSpecification();
		clinicalColumnID.setIdType("clinical");

		final IDSpecification geneRowID = TCGADefinitions.createGeneIDSpecificiation();

		final IDSpecification proteinRowID = geneRowID; // ticket #1497 new IDSpecification("protein", "protein");
		final IDSpecification microRNARowID = new IDSpecification("microRNA", "microRNA");
		final IDSpecification clinicalRowID = new IDSpecification("TCGA_SAMPLE", "TCGA_SAMPLE");

		IDTypeParsingRules clinicalSampleIDTypeParsingRules = new IDTypeParsingRules();
		clinicalSampleIDTypeParsingRules.setSubStringExpression("TCGA\\-");
		clinicalSampleIDTypeParsingRules.setToUpperCase(true);
		clinicalRowID.setIdTypeParsingRules(clinicalSampleIDTypeParsingRules);

		DataSetDescription desc = null;
		switch (dataSetType) {
		case mRNA:
			desc = setUpClusteredMatrixData(dataSetType, geneRowID, sampleID,
					fileFinder.findmRNAMatrixFile(loadFullGenes));
			break;
		case mRNAseq:
			desc = setUpClusteredMatrixData(dataSetType, geneRowID, sampleID,
					fileFinder.findmRNAseqMatrixFile(loadFullGenes));
			break;
		case microRNA:
			desc = setUpClusteredMatrixData(dataSetType, microRNARowID, sampleID,
					fileFinder.findmicroRNAMatrixFile(loadFullGenes));
			break;
		case microRNAseq:
			desc = setUpClusteredMatrixData(dataSetType, microRNARowID, seqSampleID,
					fileFinder.findmicroRNAseqMatrixFile(loadFullGenes));
			break;
		case methylation:
			desc = setUpClusteredMatrixData(dataSetType, geneRowID, sampleID,
					fileFinder.findMethylationMatrixFile(loadFullGenes));
			break;
		case RPPA:
			desc = setUpClusteredMatrixData(dataSetType, proteinRowID, sampleID,
					fileFinder.findRPPAMatrixFile(loadFullGenes));
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
		if (desc == null) {
			log.warning(id + " invalid data description");
			return null;
		}
		return new TCGADataSet(desc, this.dataSetType);
	}

	private DataSetDescription setUpClusteredMatrixData(EDataSetType type, IDSpecification rowIDSpecification,
			IDSpecification columnIDSpecification, Pair<TCGAFileInfo, Boolean> pair) {
		if (pair == null || pair.getFirst() == null) {
			log.warning(id + " no data found");
			return null;
		}
		TCGAFileInfo fileInfo = pair.getFirst();
		final File matrixFile = fileInfo.getFile();
		final boolean loadFullGenes = pair.getSecond();
		log.fine(id + " data found: " + (loadFullGenes ? "F" : "S") + " " + matrixFile);

		MetaDataElement metaData = new MetaDataElement();
		DataSetDescription dataSet = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataSourcePath(matrixFile.getPath());
		dataSet.setMetaData(metaData);

		MetaDataElement dataset = new MetaDataElement("Dataset");
		metaData.addElement(dataset);
		addDataSourceMetaData(dataset, fileInfo);
		dataset.addAttribute("Full Genes", new Boolean(loadFullGenes).toString());

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription());

		if (!loadFullGenes) {
			// the first column contains the gene ids and the second column contains additional (or the same) identifiers
			parsingRule.setFromColumn(2);

			// the gct files have 3 header lines and are centered
			dataSet.setNumberOfHeaderLines(3);
			dataSet.getDataDescription().getNumericalProperties().setDataCenter(0d);
		} else {
			// full matrices don't have the extra column before the actual data that gct files have
			parsingRule.setFromColumn(1);

			// the files with all the genes have the ids in the first row, then a row with "signal" and then the data
			// NG: I can't confirm the above and I found counter examples, e.g. in mRNA-seq for THCA
			// in the 2014 run, there is a new additional row in the data
			dataSet.setNumberOfHeaderLines(this.fileFinder.is2014Run() ? 2 : 1);
			dataSet.setRowOfColumnIDs(0);
		}

		dataSet.addParsingRule(parsingRule);
		dataSet.setTransposeMatrix(true);

		dataSet.setRowIDSpecification(rowIDSpecification);

		dataSet.setColumnIDSpecification(columnIDSpecification);

		MetaDataElement columnGroupings = new MetaDataElement("Loaded " + columnIDSpecification.getIdCategory()
				+ " Groupings");

		TCGAFileInfo cnmfGroupingFile = fileFinder.findCNMFGroupingFile(type);
		if (cnmfGroupingFile != null) {
			log.fine(id + " add CNMF grouping file " + cnmfGroupingFile);
			GroupingParseSpecification grouping = new GroupingParseSpecification(cnmfGroupingFile.getFile().getPath());
			grouping.setContainsColumnIDs(false);
			grouping.setRowIDSpecification(columnIDSpecification);
			grouping.setGroupingName("CNMF Clustering");
			dataSet.addColumnGroupingSpecification(grouping);

			MetaDataElement cnmfClustering = new MetaDataElement("CNMF Clustering");
			addDataSourceMetaData(cnmfClustering, cnmfGroupingFile);
			columnGroupings.addElement(cnmfClustering);
		} else {
			log.warning(id + " can't find CNMF grouping file");
		}

		TCGAFileInfo hierarchicalGroupingFile = fileFinder.findHiearchicalGrouping(type);
		if (hierarchicalGroupingFile != null) {
			log.fine(id + " add hierarchical grouping file " + hierarchicalGroupingFile);
			GroupingParseSpecification grouping = new GroupingParseSpecification(hierarchicalGroupingFile.getFile()
					.getPath());
			grouping.setContainsColumnIDs(false);
			grouping.setRowIDSpecification(columnIDSpecification);
			grouping.setGroupingName("Hierarchical Clustering");
			dataSet.addColumnGroupingSpecification(grouping);

			MetaDataElement hierarchicalClustering = new MetaDataElement("Hierarchical Clustering");
			addDataSourceMetaData(hierarchicalClustering, hierarchicalGroupingFile);
			columnGroupings.addElement(hierarchicalClustering);
		} else {
			log.warning(id + " can't find hierarchical grouping file");
		}

		if (!columnGroupings.getElements().isEmpty()) {
			metaData.addElement(columnGroupings);
		}

		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		dataSet.setDataProcessingDescription(dataProcessingDescription);

		MetaDataElement rowGroupings = new MetaDataElement("Computed " + rowIDSpecification.getIdCategory()
				+ " Groupings");

		ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		switch (settings.getCluster()) {
		case NONE:
			break;
		case AFFINITY:
			AffinityClusterConfiguration affinityAlgo = new AffinityClusterConfiguration();
			int clusterFactor = 9;
			log.fine(id + " add affinity clustering with factor " + clusterFactor);
			affinityAlgo.setClusterFactor(clusterFactor);
			affinityAlgo.setCacheVectors(true);
			clusterConfiguration.setClusterAlgorithmConfiguration(affinityAlgo);
			dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);

			MetaDataElement affinityCluster = new MetaDataElement("Affinity Clustering");
			affinityCluster.addAttribute("Cluster Factor", String.valueOf(clusterFactor));
			rowGroupings.addElement(affinityCluster);
			break;
		case KMEANS:
			KMeansClusterConfiguration kMeansAlgo = new KMeansClusterConfiguration();
			Integer numClusters = 5;
			log.fine(id + " add kmeans clustering with factor " + numClusters);
			kMeansAlgo.setNumberOfClusters(numClusters);
			kMeansAlgo.setCacheVectors(true);
			clusterConfiguration.setClusterAlgorithmConfiguration(kMeansAlgo);
			dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);

			MetaDataElement kmeansCluster = new MetaDataElement("KMeans Clustering");
			kmeansCluster.addAttribute("Number of Clusters", numClusters.toString());
			rowGroupings.addElement(kmeansCluster);
			break;
		case TREE:
			TreeClusterConfiguration treeAlgo = new TreeClusterConfiguration();
			ETreeClustererAlgo algo = ETreeClustererAlgo.AVERAGE_LINKAGE;
			log.fine(id + " add tree clustering using " + algo);
			treeAlgo.setTreeClustererAlgo(algo);
			clusterConfiguration.setClusterAlgorithmConfiguration(treeAlgo);
			dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);

			MetaDataElement treeCluster = new MetaDataElement("Hierarchical Tree Clustering");
			treeCluster.addAttribute("Algorithm", algo.getName());
			rowGroupings.addElement(treeCluster);
			break;
		default:
			log.severe(id + " unknown cluster specificiation: " + settings.getCluster());
			break;
		}

		if (!rowGroupings.getElements().isEmpty()) {
			metaData.addElement(rowGroupings);
		}

		if (loadFullGenes) {
			NumericalProperties numProp = dataSet.getDataDescription().getNumericalProperties();
			// run z-score normalization on the rows
			numProp.setzScoreNormalization(NumericalProperties.ZSCORE_ROWS);
			// clip to 4 std-devs
			Float stdDevFactor = 4f;
			numProp.setClipToStdDevFactor(stdDevFactor);
			// here we turn on sampling to 1500
			Integer numRowsInSample = 1500;
			dataProcessingDescription.setNrRowsInSample(numRowsInSample);

			log.fine(id + " add preprocessing steps: z-normalization, clipping, sampling, imputation");
			MetaDataElement processing = new MetaDataElement("Data Pre-Processing");
			processing
					.addElement(new MetaDataElement("Z-Score Normalization per " + rowIDSpecification.getIdCategory()));
			processing.addElement(new MetaDataElement("Clip to " + stdDevFactor.toString() + " Standard Deviation"));
			processing.addElement(new MetaDataElement("Sampling for " + rowIDSpecification.getIdCategory() + " to "
					+ numRowsInSample.toString()
					+ " using most variable Elements according to Median Absolute Deviation"));
			metaData.addElement(processing);
			dataProcessingDescription.setNrRowsInSample(1500);

			numProp.setImputeDescription(new KNNImputeDescription());
			processing.addElement(new MetaDataElement("Data is imputed for NaN Values"));
		}

		return dataSet;
	}

	private DataSetDescription setUpMutationData(IDSpecification geneIDSpecification,
			IDSpecification sampleIDSpecification) {
		Pair<TCGAFileInfo, Integer> p = fileFinder.findMutationFile();
		int startColumn = p.getSecond();
		TCGAFileInfo mutationFile = p.getFirst();

		if (mutationFile == null) {
			log.warning(id + " invalid file");
			return null;
		}
		log.fine(id + " data found: (startColumn " + startColumn + ") + mutationFile");
		MetaDataElement metaData = new MetaDataElement();
		DataSetDescription dataSet = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());

		MetaDataElement dataset = new MetaDataElement("Dataset");
		metaData.addElement(dataset);
		addDataSourceMetaData(dataset, mutationFile);
		dataSet.setMetaData(metaData);

		if (mutationFile.getSourceFileName().toLowerCase().endsWith(".maf")) {
			MetaDataElement processing = new MetaDataElement("Data Pre-Processing");
			processing.addElement(new MetaDataElement(
					"Converted MAF file to binary map with Tumor_Sample_Barcode x Hugo_Symbol"));
			metaData.addElement(processing);
		}

		dataSet.setDataSourcePath(mutationFile.getFile().getPath());
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
		CategoricalClassDescription<Integer> cats = (CategoricalClassDescription<Integer>) dataSet.getDataDescription()
				.getCategoricalClassDescription();
		cats.setCategoryType(ECategoryType.ORDINAL);
		cats.setRawDataType(EDataType.INTEGER);
		cats.addCategoryProperty(0, "Not Mutated", Color.NEUTRAL_GREY);
		cats.addCategoryProperty(1, "Mutated", Color.RED);
		return dataSet;

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

	private DataSetDescription setUpCopyNumberData(IDSpecification rwoIDSpecification,
			IDSpecification sampleIDSpecification) {
		TCGAFileInfo copyNumberFile = fileFinder.findCopyNumberFile();
		if (copyNumberFile == null) {
			log.warning(id + " invalid file");
			return null;
		}
		log.fine(id + " data found: " + copyNumberFile);

		MetaDataElement metaData = new MetaDataElement();
		DataSetDescription dataSet = new DataSetDescription(ECreateDefaultProperties.CATEGORICAL);
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataSourcePath(copyNumberFile.getFile().getPath());
		dataSet.setNumberOfHeaderLines(1);
		dataSet.setMetaData(metaData);

		MetaDataElement dataset = new MetaDataElement("Dataset");
		metaData.addElement(dataset);
		addDataSourceMetaData(dataset, copyNumberFile);

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
		// Use ColorBrewer color map and replace salmon with light red
		cats.addCategoryProperty(-2, "Homozygous deletion", ColorBrewer.RdBu.get(4).get(3));
		cats.addCategoryProperty(-1, "Heterozygous deletion", ColorBrewer.RdBu.get(4).get(2));
		cats.addCategoryProperty(0, "NORMAL", Color.NEUTRAL_GREY);
		cats.addCategoryProperty(1, "Low level amplification", Color.LIGHT_RED);
		cats.addCategoryProperty(2, "High level amplification", ColorBrewer.RdBu.get(4).get(0));

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
		TCGAFileInfo clinicalFileInfo = fileFinder.findClinicalDataFile();
		if (clinicalFileInfo == null) {
			log.warning(id + " invalid file");
			return null;
		}
		log.fine(id + " data found: " + clinicalFileInfo);
		File clinicalFile = clinicalFileInfo.getFile();

		// new key for force an update
		File out = new File(clinicalFile.getParentFile(), "TR" + clinicalFile.getName());

		Collection<String> toInclude = settings.getClinicalVariables();

		transposeCSV(clinicalFile.getPath(), out.getPath(), ClinicalMapping.getAliasMap(toInclude));
		log.fine(id + " transposed file to " + out);

		MetaDataElement metaData = new MetaDataElement();

		DataSetDescription dataSet = new DataSetDescription();
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataSourcePath(out.getPath());
		dataSet.setNumberOfHeaderLines(1);

		MetaDataElement dataset = new MetaDataElement("Dataset");
		metaData.addElement(dataset);
		addDataSourceMetaData(dataset, clinicalFileInfo);
		dataSet.setMetaData(metaData);

		dataSet.setRowIDSpecification(rowIdSpecification);
		dataSet.setColumnIDSpecification(columnIdSpecification);
		dataSet.setRowOfColumnIDs(0);

		String header = readFirstLine(out);
		assert header != null;
		List<String> columns = Arrays.asList(header.split("\t"));

		final String idColumnLabel = "patient.bcrpatientbarcode";
		if (columns.contains(idColumnLabel)) {
			int index = columns.indexOf(idColumnLabel);
			log.info(id + " found id column: " + idColumnLabel + " at index " + index);
			dataSet.setColumnOfRowIds(index); // "patient.bcrpatientbarcode"
		} else {
			log.warning(id + " can't find column: " + idColumnLabel + " assuming ids are at column 12");
			dataSet.setColumnOfRowIds(12);
		}

		int counter = 0;
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
					log.warning(id + " activly selected clinicial variable: " + name + " is not known using default");
					dataSet.addParsingPattern(ClinicalMapping.createDefault(i));
					counter++;
				} else if (mapping != null) {
					log.info(id + " adding clinicial variable: " + name);
					dataSet.addParsingPattern(mapping.create(i));
					counter++;
				} else {
					continue;
				}
			}
		}
		if (counter == 0) {// empty file
			log.warning(id + " no defined columns, skipping dataset");
			return null;
		} else {
			log.fine(id + " added " + counter + " clinicial variables");
		}

		return dataSet;
	}

	private void addDataSourceMetaData(MetaDataElement metaData, TCGAFileInfo fileInfo) {
		metaData.addAttribute("Archive", fileInfo.getArchiveURL().toExternalForm(), AttributeType.URL);
		metaData.addAttribute("Source File", fileInfo.getSourceFileName());
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

	private void transposeCSV(String fileName, String fileNameOut, Map<String, String> lookupMap) {
		// File in = new File(fileName);
		File out = new File(fileNameOut);

		if (out.exists() && !settings.isCleanCache())
			return;

		transposeCSV(fileName, fileNameOut, "\t", lookupMap);
	}

	public static void transposeCSV(String fileName, String fileNameOut, String delimiter, Map<String, String> lookupMap) {
		// log.info("tranposing: " + fileName);
		File in = new File(fileName);
		File out = new File(fileNameOut);

		// if (out.exists() && !settings.isCleanCache())
		// return;

		List<String> data;
		try {
			data = Files.readAllLines(in.toPath(), Charset.forName("UTF-8"));
		} catch (IOException e2) {
			log.log(Level.SEVERE, "can' read all of " + in, e2);
			return;
		}
		// split into parts
		String[][] parts = new String[data.size()][];
		int maxCol = -1;
		for (int i = 0; i < data.size(); ++i) {
			parts[i] = data.get(i).split(delimiter, -1);
			if (parts[i].length > maxCol)
				maxCol = parts[i].length;
		}
		data = null;

		try (BufferedWriter writer = Files.newBufferedWriter(out.toPath(), Charset.forName("UTF-8"))) {
			for (int c = 0; c < maxCol; ++c) {
				for (int i = 0; i < parts.length; ++i) {
					if (i > 0)
						writer.append(delimiter);
					String[] p = parts[i];
					if (p.length > c) {
						String v = p[c];
						if (lookupMap.containsKey(v))
							v = lookupMap.get(v);
						writer.append(v);
					}
				}
				writer.newLine();
			}
		} catch (IOException e1) {
			log.log(Level.SEVERE, "can' write all of " + in + " to " + out, e1);
		}
	}
}
