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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

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
import org.caleydo.core.util.collection.Pair;
import org.caleydo.datadomain.genetic.TCGADefinitions;

public class TCGADataSetBuilder extends RecursiveTask<DataSetDescription> {
	private static final long serialVersionUID = 6468622325177694143L;

	private final String dataSetName;
	private final EDataSetType dataSetType;
	private final boolean loadSampledGenes;

	private final FirehoseProvider fileFinder;

	private TCGADataSetBuilder(EDataSetType datasetType, String dataSetName, FirehoseProvider fileFinder,
			boolean loadSampledGenes) {
		this.dataSetName = dataSetName;
		this.dataSetType = datasetType;
		this.loadSampledGenes = loadSampledGenes;
		this.fileFinder = fileFinder;

	}

	public static ForkJoinTask<DataSetDescription> create(EDataSetType datasetType,
			FirehoseProvider fileProvider) {
		return create(datasetType, fileProvider, true);
	}

	public static ForkJoinTask<DataSetDescription> create(EDataSetType datasetType,
			String dataSetName, FirehoseProvider fileProvider) {
		return create(datasetType, dataSetName, fileProvider, true);
	}

	public static ForkJoinTask<DataSetDescription> create(EDataSetType datasetType,
			FirehoseProvider fileProvider, boolean loadSampledGenes) {
		return create(datasetType, datasetType.getName(), fileProvider, loadSampledGenes);
	}

	public static ForkJoinTask<DataSetDescription> create(EDataSetType datasetType,
			String dataSetName, FirehoseProvider fileProvider, boolean loadSampledGenes) {
		return new TCGADataSetBuilder(datasetType, dataSetName, fileProvider, loadSampledGenes);
	}

	@Override
	public DataSetDescription compute() {
		final IDSpecification sampleID = TCGADefinitions.createSampleIDSpecification(true);

		// TCGA SAMPLE IDs look different for seq data (an "-01" is attached)
		final IDSpecification seqSampleID = TCGADefinitions.createSampleIDSpecification(false);

		final IDSpecification clinicalColumnID = new IDSpecification();
		clinicalColumnID.setIdType("clinical");

		final IDSpecification geneRowID = TCGADefinitions.createGeneIDSpecificiation();

		final IDSpecification proteinRowID = new IDSpecification("protein", "protein");
		final IDSpecification microRNARowID = new IDSpecification("microRNA", "microRNA");
		final IDSpecification clinicalRowID = new IDSpecification("TCGA_SAMPLE", "TCGA_SAMPLE");

		IDTypeParsingRules clinicalSampleIDTypeParsingRules = new IDTypeParsingRules();
		clinicalSampleIDTypeParsingRules.setSubStringExpression("tcga\\-");
		clinicalSampleIDTypeParsingRules.setToLowerCase(true);
		clinicalRowID.setIdTypeParsingRules(clinicalSampleIDTypeParsingRules);

		switch (dataSetType) {
		case mRNA:
			return setUpClusteredMatrixData(dataSetType, geneRowID, sampleID,
					fileFinder.findmRNAMatrixFile(loadSampledGenes));
		case mRNAseq:
			return setUpClusteredMatrixData(dataSetType, geneRowID, sampleID,
					fileFinder.findmRNAseqMatrixFile(loadSampledGenes));
		case microRNA:
			return setUpClusteredMatrixData(dataSetType, microRNARowID, sampleID,
					fileFinder.findmicroRNAMatrixFile(loadSampledGenes));
		case microRNAseq:
			return setUpClusteredMatrixData(dataSetType, microRNARowID, seqSampleID,
					fileFinder.findmicroRNAseqMatrixFile(loadSampledGenes));
		case methylation:
			return setUpClusteredMatrixData(dataSetType, geneRowID, sampleID, fileFinder.findMethylationMatrixFile());
		case RPPA:
			return setUpClusteredMatrixData(dataSetType, proteinRowID, sampleID, fileFinder.findRPPAMatrixFile());
		case clinical:
			return setUpClinicalData(clinicalRowID, clinicalColumnID);
		case mutation:
			return setUpMutationData(geneRowID, sampleID);
		case copyNumber:
			return setUpCopyNumberData(geneRowID, sampleID);
		}
		throw new IllegalStateException("uknown data set type: " + dataSetType);
	}

	private DataSetDescription setUpClusteredMatrixData(EDataSetType type, IDSpecification rowIDSpecification,
			IDSpecification columnIDSpecification, File matrixFile) {
		if (matrixFile == null)
			return null;

		DataSetDescription dataSet = new DataSetDescription();
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataSourcePath(matrixFile.getPath());
		if (loadSampledGenes) {
			// the gct files have 3 header lines and are centered<
			dataSet.setNumberOfHeaderLines(3);
			dataSet.setDataCenter(0d);
		} else {
			// the files with all the genes have the ids in the first row, then a row with "signal" and then the data
			dataSet.setNumberOfHeaderLines(2);
			dataSet.setRowOfColumnIDs(0);

		}

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT", ColumnDescription.CONTINUOUS));
		dataSet.addParsingRule(parsingRule);
		dataSet.setTransposeMatrix(true);

		dataSet.setRowIDSpecification(rowIDSpecification);

		dataSet.setColumnIDSpecification(columnIDSpecification);

		File cnmfGroupingFile = fileFinder.findCNMFGroupingFile(type);
		if (cnmfGroupingFile != null) {
			GroupingParseSpecification grouping = new GroupingParseSpecification(
					cnmfGroupingFile.getPath());
			grouping.setContainsColumnIDs(false);
			grouping.setRowIDSpecification(columnIDSpecification);
			grouping.setGroupingName("CNMF Clustering");
			dataSet.addColumnGroupingSpecification(grouping);
		} else {
			System.out.println("Warning can't find cnmf grouping file");
		}

		File hierarchicalGroupingFile = fileFinder.findHiearchicalGrouping(type);
		if (hierarchicalGroupingFile != null) {
			GroupingParseSpecification grouping = new GroupingParseSpecification(
					hierarchicalGroupingFile.getPath());
			grouping.setContainsColumnIDs(false);
			grouping.setRowIDSpecification(columnIDSpecification);
			grouping.setGroupingName("Hierarchical Clustering");
			dataSet.addColumnGroupingSpecification(grouping);
		} else {
			System.out.println("Warning can't find hierarchical grouping file");
		}

		DataProcessingDescription dataProcessingDescription = new DataProcessingDescription();
		{
			ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
			clusterConfiguration.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
			KMeansClusterConfiguration kMeansAlgo = new KMeansClusterConfiguration();
			kMeansAlgo.setNumberOfClusters(5);
			clusterConfiguration.setClusterAlgorithmConfiguration(kMeansAlgo);
			dataProcessingDescription.addRowClusterConfiguration(clusterConfiguration);
		}
		dataSet.setDataProcessingDescription(dataProcessingDescription);

		if (loadSampledGenes) {
			// here we turn on sampling to 1500
			dataProcessingDescription.setNrRowsInSample(1500);
		}

		return dataSet;
	}



	private DataSetDescription setUpMutationData(IDSpecification rowIDSpecification,
			IDSpecification sampleIDSpecification) {

		Pair<File, Integer> p = fileFinder.findMutationFile();
		int startColumn = p.getSecond();
		File mutationFile = p.getFirst();

		if (mutationFile == null)
			return null;
		DataSetDescription dataSet = new DataSetDescription();
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataSourcePath(mutationFile.getPath());
		dataSet.setNumberOfHeaderLines(1);
		dataSet.setMax(1.f);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(startColumn);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT", ColumnDescription.NOMINAL));
		dataSet.addParsingRule(parsingRule);
		dataSet.setTransposeMatrix(true);

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
		dataSet.setColumnIDSpecification(sampleIDSpecification);
		dataSet.setRowIDSpecification(rowIDSpecification);

		return dataSet;
	}


	private DataSetDescription setUpCopyNumberData(IDSpecification rwoIDSpecification,
			IDSpecification sampleIDSpecification) {
		File copyNumberFile = fileFinder.findCopyNumberFile();
		if (copyNumberFile == null)
			return null;

		DataSetDescription dataSet = new DataSetDescription();
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataSourcePath(copyNumberFile.getPath());
		dataSet.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(3);
		parsingRule.setParseUntilEnd(true);
		parsingRule.setColumnDescripton(new ColumnDescription("FLOAT", ColumnDescription.ORDINAL));
		dataSet.addParsingRule(parsingRule);
		dataSet.setTransposeMatrix(true);

		dataSet.setRowIDSpecification(rwoIDSpecification);
		dataSet.setColumnIDSpecification(sampleIDSpecification);

		return dataSet;
	}

	private DataSetDescription setUpClinicalData(IDSpecification rowIdSpecification,
			IDSpecification columnIdSpecification) {
		File clinicalFile = fileFinder.findClinicalDataFile();
		if (clinicalFile == null)
			return null;

		File out = new File(clinicalFile.getParentFile(), "T" + clinicalFile.getName());
		transposeCSV(clinicalFile.getPath(), out.getPath());

		DataSetDescription dataSet = new DataSetDescription();
		dataSet.setDataSetName(dataSetName);
		dataSet.setColor(dataSetType.getColor());
		dataSet.setDataHomogeneous(false);
		dataSet.setDataSourcePath(out.getPath());
		dataSet.setNumberOfHeaderLines(1);

		ParsingRule parsingRule = new ParsingRule();
		parsingRule.setFromColumn(2);
		parsingRule.setToColumn(4);
		parsingRule.setColumnDescripton(new ColumnDescription());
		dataSet.addParsingRule(parsingRule);

		dataSet.setRowIDSpecification(rowIdSpecification);
		dataSet.setColumnIDSpecification(columnIdSpecification);

		return dataSet;
	}

	private static void transposeCSV(String fileName, String fileNameOut) {
		File in = new File(fileName);
		File out = new File(fileNameOut);
		if (out.exists())
			return;

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
					if (p.length > c)
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
