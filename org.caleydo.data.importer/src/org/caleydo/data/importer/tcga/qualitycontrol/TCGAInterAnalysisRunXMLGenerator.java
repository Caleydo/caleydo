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
 * Generator class that writes the loading information of a series of TCGA data
 * sets to an XML file.
 *
 * @author Nils Gehlenborg
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGAInterAnalysisRunXMLGenerator extends ATCGAProjectBuilder {

	public static final String TCGA_ID_SUBSTRING_REGEX = "tcga\\-|\\-...\\-";

	private final EDataSetType dataSetType;
	private final TCGAQCSettings settings;

	public TCGAInterAnalysisRunXMLGenerator(String tumorAbbreviation, EDataSetType dataSetType, TCGAQCSettings settings) {
		super(tumorAbbreviation);
		this.settings = settings;
		this.dataSetType = dataSetType;
	}

	@Override
	public ProjectDescription compute() {
		ProjectDescription projectDescription = new ProjectDescription();

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

		IDSpecification rowIDSpecification = createIDSpecification(dataSetType); // uses genes

		Collection<ForkJoinTask<DataSetDescription>> tasks = new ArrayList<>();

		for (String analysisRun : settings.getAnalysisRuns()) {
			AFirehoseProvider fileProvider = settings.createFirehoseProvider(tumorAbbreviation, analysisRun);

			DataSetDescription datasetDescription = createTemplate(analysisRun, dataSetType);
			try {
				switch (dataSetType) {
				case mRNA:
					tasks.add(adapt(setUpClusteredMatrixData("mRNA_Clustering_CNMF",
							"mRNA_Clustering_Consensus", "outputprefix.expclu.gct", analysisRun, rowIDSpecification,
 sampleIDSpecification, true,
							datasetDescription, fileProvider)));
					break;
				case mRNAseq:
					tasks.add(adapt(setUpClusteredMatrixData("mRNAseq_Clustering_CNMF",
							"mRNAseq_Clustering_Consensus", "outputprefix.expclu.gct", rowIDSpecification,
 seqSampleIDSpecification, true,
							datasetDescription, fileProvider)));

					break;
				case microRNA:
					tasks.add(adapt(setUpClusteredMatrixData("miR_Clustering_CNMF", "miR_Clustering_Consensus",
							"cnmf.normalized.gct", rowIDSpecification, sampleIDSpecification, false,
							datasetDescription, fileProvider)));

					break;
				case microRNAseq:
					tasks.add(adapt(setUpClusteredMatrixData("miRseq_Clustering_CNMF",
							"miRseq_Clustering_Consensus", "cnmf.normalized.gct", rowIDSpecification,
 seqSampleIDSpecification, false,
							datasetDescription, fileProvider)));

					break;
				case methylation:
					tasks.add(adapt(setUpClusteredMatrixData("Methylation_Clustering_CNMF",
							"Methylation_Clustering_Consensus", "cnmf.normalized.gct", rowIDSpecification,
							sampleIDSpecification, true, datasetDescription, fileProvider)));

					break;
				case RPPA:
					tasks.add(adapt(setUpClusteredMatrixData("RPPA_Clustering_CNMF",
							"RPPA_Clustering_Consensus", "cnmf.normalized.gct", rowIDSpecification,
 sampleIDSpecification, false,
							datasetDescription, fileProvider)));

					break;
				}
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

		for (ForkJoinTask<DataSetDescription> task : invokeAll(tasks)) {
			try {
				DataSetDescription ds = task.get();
				if (ds == null)
					continue;
				projectDescription.add(ds);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return projectDescription;
	}

	private static IDSpecification createIDSpecification(EDataSetType dataSetType) {
		switch (dataSetType) {
		case mRNA:
		case mRNAseq:
		case RPPA:
			return null;
		case microRNA:
		case microRNAseq:
			return new IDSpecification("microRNA", "microRNA");
		case methylation:
			return new IDSpecification("protein", "protein");
		default:
			return null;
		}
	}
}
