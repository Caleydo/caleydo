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
package org.caleydo.data.importer.tcga.regular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;

import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.ProjectDescription;
import org.caleydo.data.importer.tcga.ATCGAProjectBuilder;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.caleydo.data.importer.tcga.provider.AFirehoseProvider;
import org.caleydo.datadomain.genetic.TCGADefinitions;


/**
 * Generator class that writes the loading information of a series of TCGA data sets to an XML file.
 *
 * @author Nils Gehlenborg
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGAXMLGenerator extends ATCGAProjectBuilder {

	public static final String TCGA_ID_SUBSTRING_REGEX = "tcga\\-|\\-...\\-";

	private final boolean loadSampledGenes;

	private final AFirehoseProvider fileProvider;

	public TCGAXMLGenerator(String tumorAbbreviation, AFirehoseProvider fileProvider, TCGASettings settings) {
		super(tumorAbbreviation);
		this.fileProvider = fileProvider;
		this.loadSampledGenes = settings.isSampleGenes();
	}

	@Override
	protected ProjectDescription compute() {
		ProjectDescription projectDescription = new ProjectDescription();

		String matrixArchiveName = null;
		String matrixFileName = null;

		IDSpecification sampleIDSpecification = new IDSpecification();
		sampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		sampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setReplacementExpression(
				TCGADefinitions.TCGA_REPLACEMENT_STRING,
				TCGADefinitions.TCGA_REPLACING_EXPRESSIONS);
		idTypeParsingRules
				.setSubStringExpression(TCGADefinitions.TCGA_ID_SUBSTRING_REGEX);
		idTypeParsingRules.setToLowerCase(true);
		idTypeParsingRules.setDefault(true);
		sampleIDSpecification.setIdTypeParsingRules(idTypeParsingRules);

		IDSpecification rowIDSpecification;

		Collection<ForkJoinTask<DataSetDescription>> tasks = new ArrayList<>();

		// ====== mRNA ======

		rowIDSpecification = null; // uses genes
		try {

			if (loadSampledGenes) {
				matrixArchiveName = "mRNA_Clustering_CNMF";
				matrixFileName = "outputprefix.expclu.gct";
			} else {
				matrixArchiveName = "mRNA_Preprocess_Median";
				matrixFileName = tumorAbbreviation + ".medianexp.txt";
			}

			tasks.add(adapt(setUpClusteredMatrixData("mRNA_Clustering_CNMF", "mRNA_Clustering_Consensus",
					matrixArchiveName, matrixFileName, rowIDSpecification, sampleIDSpecification, true, createTemplate(EDataSetType.mRNA),
 fileProvider)));

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== microRNA ======

		rowIDSpecification = new IDSpecification();
		rowIDSpecification.setIdType("microRNA");
		rowIDSpecification.setIdCategory("microRNA");

		try {

			if (loadSampledGenes) {
				matrixArchiveName = "miR_Clustering_CNMF";
				matrixFileName = "outputprefix.expclu.gct";
			} else {
				matrixArchiveName = "miR_Preprocess";
				matrixFileName = tumorAbbreviation + ".miR_expression.txt";
			}

			tasks.add(adapt(setUpClusteredMatrixData("miR_Clustering_CNMF", "miR_Clustering_Consensus",
					matrixArchiveName, matrixFileName, rowIDSpecification, sampleIDSpecification, false, createTemplate(EDataSetType.microRNA),
 fileProvider)));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// TCGA SAMPLE IDs look different for seq data (an "-01" is attached)

		IDSpecification seqSampleIDSpecification = new IDSpecification();
		seqSampleIDSpecification.setIdCategory("TCGA_SAMPLE");
		seqSampleIDSpecification.setIdType("TCGA_SAMPLE");
		IDTypeParsingRules seqSampleIDTypeParsingRules = new IDTypeParsingRules();
		seqSampleIDTypeParsingRules
				.setSubStringExpression(TCGADefinitions.TCGA_ID_SUBSTRING_REGEX);
		seqSampleIDTypeParsingRules.setReplacementExpression(
				TCGADefinitions.TCGA_REPLACEMENT_STRING,
				TCGADefinitions.TCGA_REPLACING_EXPRESSIONS);
		seqSampleIDTypeParsingRules.setToLowerCase(true);
		seqSampleIDSpecification.setIdTypeParsingRules(seqSampleIDTypeParsingRules);

		// ====== mRNAseq ======

		try {
			if (loadSampledGenes) {
				matrixArchiveName = "mRNAseq_Clustering_CNMF";
				matrixFileName = "outputprefix.expclu.gct";
			} else {
				matrixArchiveName = "mRNAseq_Preprocess";
				matrixFileName = tumorAbbreviation + ".mRNAseq_RPKM_log2.txt";
			}

			tasks.add(adapt(setUpClusteredMatrixData("mRNAseq_Clustering_CNMF", "mRNAseq_Clustering_Consensus",
					matrixArchiveName, matrixFileName, rowIDSpecification, seqSampleIDSpecification, true,
					createTemplate(EDataSetType.mRNAseq), fileProvider)));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== microRNAseq ======

		try {

			if (loadSampledGenes) {
				matrixArchiveName = "miRseq_Clustering_CNMF";
				matrixFileName = "outputprefix.expclu.gct";
			} else {
				matrixArchiveName = "miRseq_Preprocess";
				matrixFileName = tumorAbbreviation + ".miRseq_RPKM_log2.txt";
			}

			tasks.add(adapt(setUpClusteredMatrixData("miRseq_Clustering_CNMF", "miRseq_Clustering_Consensus",
					matrixArchiveName, matrixFileName, rowIDSpecification, seqSampleIDSpecification, false,
					createTemplate(EDataSetType.microRNAseq), fileProvider)));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== methylation ======

		rowIDSpecification = null; // uses genes

		try {
			tasks.add(adapt(setUpClusteredMatrixData("Methylation_Clustering_CNMF",
					"Methylation_Clustering_Consensus", "Methylation_Clustering_CNMF", "outputprefix.expclu.gct",
					rowIDSpecification, sampleIDSpecification, true, createTemplate(EDataSetType.methylation),
 fileProvider)));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== RPPA (reverse-phase protein arrays) ======

		rowIDSpecification = new IDSpecification();
		rowIDSpecification.setIdType("protein");
		rowIDSpecification.setIdCategory("protein");

		try {
			tasks.add(adapt(setUpClusteredMatrixData("RPPA_Clustering_CNMF", "RPPA_Clustering_Consensus",
					"RPPA_Clustering_CNMF", "outputprefix.expclu.gct", rowIDSpecification, sampleIDSpecification,
					false, createTemplate(EDataSetType.RPPA), fileProvider)));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== copy number ======

		try {
			tasks.add(adapt(setUpCopyNumberData("CopyNumber_Gistic2", createTemplate(EDataSetType.copyNumber),
					sampleIDSpecification, fileProvider)));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== mutation ======

		try {
			tasks.add(adapt(setUpMutationData("Mutation_Significance", createTemplate(EDataSetType.mutation),
					sampleIDSpecification, fileProvider)));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		// ====== clinical ======

		try {
			tasks.add(adapt(setUpClinicalData("Clinical_Pick_Tier1", createTemplate(EDataSetType.clinical),
					fileProvider)));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		invokeAll(tasks); // fork and wait

		for (ForkJoinTask<DataSetDescription> task : tasks) {
			try {
				DataSetDescription ds = task.get();
				if (ds == null)
					continue;
				projectDescription.add(ds);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			} catch (ExecutionException e) {
				System.err.println(e.getMessage());
			}
		}

		return projectDescription;
	}

}
