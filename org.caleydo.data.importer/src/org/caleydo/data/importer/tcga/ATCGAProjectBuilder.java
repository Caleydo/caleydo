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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveTask;

import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataProcessingDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.ParsingRule;
import org.caleydo.core.io.ProjectDescription;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.data.importer.tcga.provider.AFirehoseProvider;

public abstract class ATCGAProjectBuilder extends RecursiveTask<ProjectDescription> {
	private static final long serialVersionUID = 6468622325177694143L;
	protected final String tumorAbbreviation;

	public ATCGAProjectBuilder(String tumorAbbreviation) {
		this.tumorAbbreviation = tumorAbbreviation;
	}

	protected static DataSetDescription createTemplate(EDataSetType type) {
		DataSetDescription d = new DataSetDescription();
		d.setDataSetName(type.getName());
		d.setColor(type.getColor());
		return d;
	}

	protected static DataSetDescription createTemplate(String name, EDataSetType type) {
		DataSetDescription d = new DataSetDescription();
		d.setDataSetName(name);
		d.setColor(type.getColor());
		return d;
	}

	protected Callable<DataSetDescription> setUpClusteredMatrixData(String cnmfArchiveName,
			String hierarchicalArchiveName, String matrixFileName, IDSpecification rowIDSpecification,
			IDSpecification columnIDSpecification, boolean isGeneIdType, DataSetDescription template,
			AFirehoseProvider fileProvider) {
		return setUpClusteredMatrixData(cnmfArchiveName, hierarchicalArchiveName, cnmfArchiveName, matrixFileName,
 rowIDSpecification,
				columnIDSpecification, isGeneIdType, template, fileProvider);
	}

	protected Callable<DataSetDescription> setUpClusteredMatrixData(final String cnmfArchiveName,
			final String hierarchicalArchiveName, final String matrixArchiveName, final String matrixFileName,
			final IDSpecification rowIDSpecification,
 final IDSpecification columnIDSpecification,
			final boolean isGeneIdType, final DataSetDescription template,
			final AFirehoseProvider fileProvider) {
		return new Callable<DataSetDescription>() {
			@Override
			public DataSetDescription call() throws Exception {

				File matrixFile = fileProvider.extractAnalysisRunFile(matrixFileName, matrixArchiveName, 4);
				if (matrixFile == null)
					return null;
				File cnmfGroupingFile = fileProvider.extractAnalysisRunFile("cnmf.membership.txt", cnmfArchiveName, 4);
				if (cnmfGroupingFile == null)
					return null;

				DataSetDescription matrixData = template;
				matrixData.setDataSourcePath(matrixFile.getPath());
				matrixData.setNumberOfHeaderLines(3);
				matrixData.setDataCenter(0d);

				ParsingRule parsingRule = new ParsingRule();
				parsingRule.setFromColumn(2);
				parsingRule.setParseUntilEnd(true);
				parsingRule.setColumnDescripton(new ColumnDescription("FLOAT", ColumnDescription.CONTINUOUS));
				matrixData.addParsingRule(parsingRule);
				matrixData.setTransposeMatrix(true);

				if (isGeneIdType) {
					IDSpecification geneIDSpecification = new IDSpecification();
					geneIDSpecification.setIDTypeGene(true);
					geneIDSpecification.setIdType("GENE_SYMBOL");
					matrixData.setRowIDSpecification(geneIDSpecification);
				} else {
					if (rowIDSpecification == null) {
						matrixData.setRowIDSpecification(new IDSpecification("unknown", "unknown"));
					} else {
						matrixData.setRowIDSpecification(rowIDSpecification);
					}
				}

				matrixData.setColumnIDSpecification(columnIDSpecification);

				GroupingParseSpecification firehoseCnmfClustering = new GroupingParseSpecification(
						cnmfGroupingFile.getPath());
				firehoseCnmfClustering.setContainsColumnIDs(false);
				firehoseCnmfClustering.setRowIDSpecification(columnIDSpecification);
				firehoseCnmfClustering.setGroupingName("CNMF Clustering");
				matrixData.addColumnGroupingSpecification(firehoseCnmfClustering);

				try {
					File hierarchicalGroupingFile = fileProvider.extractAnalysisRunFile(tumorAbbreviation
							+ ".allclusters.txt", hierarchicalArchiveName, 4);
					GroupingParseSpecification firehoseHierarchicalClustering = new GroupingParseSpecification(
							hierarchicalGroupingFile.getPath());
					firehoseHierarchicalClustering.setContainsColumnIDs(false);
					firehoseHierarchicalClustering.setRowIDSpecification(columnIDSpecification);
					firehoseHierarchicalClustering.setGroupingName("Hierchical Clustering");
					matrixData.addColumnGroupingSpecification(firehoseHierarchicalClustering);
				} catch (RuntimeException e) {
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
				// here we turn on sampling to 1500
				dataProcessingDescription.setNrRowsInSample(1500);

				return matrixData;
			}
		};
	}

	protected Callable<DataSetDescription> setUpMutationData(final String archiveName,
			final DataSetDescription template, final IDSpecification sampleIDSpecification,
			final AFirehoseProvider fileProvider) {
		return new Callable<DataSetDescription>() {
			@Override
			public DataSetDescription call() throws Exception {
				File mutationFile = fileProvider.extractAnalysisRunFile(tumorAbbreviation
						+ ".per_gene.mutation_counts.txt", archiveName, 4);
				if (mutationFile == null)
					return null;

				DataSetDescription mutationData = template;
				mutationData.setDataSourcePath(mutationFile.getPath());
				mutationData.setNumberOfHeaderLines(1);
				mutationData.setMax((float) 1);

				ParsingRule parsingRule = new ParsingRule();
				parsingRule.setFromColumn(8);
				parsingRule.setParseUntilEnd(true);
				parsingRule.setColumnDescripton(new ColumnDescription("FLOAT", ColumnDescription.NOMINAL));
				mutationData.addParsingRule(parsingRule);
				mutationData.setTransposeMatrix(true);

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
				mutationData.setColumnIDSpecification(sampleIDSpecification);

				IDSpecification geneIDSpecification = new IDSpecification();
				geneIDSpecification.setIDTypeGene(true);
				geneIDSpecification.setIdType("GENE_SYMBOL");
				mutationData.setRowIDSpecification(geneIDSpecification);

				return mutationData;
			}
		};

	}

	protected Callable<DataSetDescription> setUpCopyNumberData(final String archiveName,
			final DataSetDescription template,
			final IDSpecification sampleIDSpecification, final AFirehoseProvider fileProvider) {
		return new Callable<DataSetDescription>() {
			@Override
			public DataSetDescription call() throws Exception {
				File copyNumberFile = fileProvider.extractAnalysisRunFile("all_thresholded.by_genes.txt", archiveName,
						4);
				if (copyNumberFile == null)
					return null;

				DataSetDescription copyNumberData = template;
				copyNumberData.setDataSourcePath(copyNumberFile.getPath());
				copyNumberData.setNumberOfHeaderLines(1);

				ParsingRule parsingRule = new ParsingRule();
				parsingRule.setFromColumn(3);
				parsingRule.setParseUntilEnd(true);
				parsingRule.setColumnDescripton(new ColumnDescription("FLOAT", ColumnDescription.ORDINAL));
				copyNumberData.addParsingRule(parsingRule);
				copyNumberData.setTransposeMatrix(true);

				IDSpecification geneIDSpecification = new IDSpecification();
				geneIDSpecification.setIDTypeGene(true);
				geneIDSpecification.setIdType("GENE_SYMBOL");
				copyNumberData.setRowIDSpecification(geneIDSpecification);
				copyNumberData.setColumnIDSpecification(sampleIDSpecification);

				return copyNumberData;
			}
		};

	}

	protected Callable<DataSetDescription> setUpClinicalData(final String archiveName,
			final DataSetDescription template,
			final AFirehoseProvider fileProvider) {

		return new Callable<DataSetDescription>() {
			@Override
			public DataSetDescription call() throws Exception {
				File clinicalFile = fileProvider.extractDataRunFile(tumorAbbreviation + ".clin.merged.picked.txt",
				archiveName, 4);
				if (clinicalFile == null)
					return null;

				File out = new File(clinicalFile.getParentFile(), "T" + clinicalFile.getName());
				transposeCSV(clinicalFile.getPath(), out.getPath());

				DataSetDescription clinicalData = template;
				clinicalData.setDataHomogeneous(false);
				clinicalData.setDataSourcePath(out.getPath());
				clinicalData.setNumberOfHeaderLines(1);

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
		};

	}

	private static void transposeCSV(String fileName, String fileNameOut) {
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
		for(int i = 0; i < data.size(); ++i) {
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
		// // Tmp file needed because script cannot read and write from same file
		// // simultaneously
		// String tmpFile = fileName + "tmp";
		//
		// Runtime rt = Runtime.getRuntime();
		// Process p;
		// try {
		// new File(tmpFile).delete();
		//
		// p = rt.exec(new String[] { "resources/transpose_csv.sh", fileName, tmpFile });
		// p.waitFor();
		// BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
		// String line = "";
		// while ((line = buf.readLine()) != null) {
		// System.out.println(line);
		// }
		//
		// FileOperations.copyFolder(new File(tmpFile), new File(fileName));
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}
}
