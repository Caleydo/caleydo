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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.data.importer.tcga.ATCGATask;
import org.caleydo.data.importer.tcga.model.TCGADataSet;
import org.caleydo.data.importer.tcga.model.TCGADataSets;
import org.caleydo.data.importer.tcga.model.TumorType;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This class handles the whole workflow of creating a Caleydo project from TCGA
 * data.
 *
 * @author Marc Streit
 *
 */
public class TCGATask extends ATCGATask {
	private static final Logger log = Logger.getLogger(TCGATask.class.getSimpleName());
	private static final long serialVersionUID = 7378867458430247164L;

	private final TumorType tumorType;
	private final String analysisRun;
	private final String dataRun;
	private final TCGASettings settings;

	public TCGATask(TumorType tumorType, String analysisRun, String dataRun, TCGASettings settings) {
		this.tumorType = tumorType;
		this.analysisRun = analysisRun;
		this.dataRun = dataRun;
		this.settings = settings;
	}

	@Override
	protected JsonElement compute() {
		log.info("Downloading data for tumor type " + tumorType + " for analysis run " + analysisRun);

		String runSpecificOutputPath = settings.getDataDirectory(analysisRun);

		TCGADataSets project = new TCGADataSetGenerator(tumorType, settings.createFirehoseProvider(tumorType,
				analysisRun, dataRun), settings).invoke();

		if (project.isEmpty())
			return null;

		log.info("Building project file for tumor type " + tumorType + " for analysis run " + analysisRun);

		Collection<ATableBasedDataDomain> dataDomains = loadProject(project);
		if (dataDomains.isEmpty()) {
			log.warning("No Data Domains loaded for tumor type " + tumorType + " for analysis run " + analysisRun
					+ " -> skipping rest");
			return null;
		}

		log.info("Post Processing project file for tumor type " + tumorType + " for analysis run " + analysisRun);
		for (TCGADataSet set : project) {
			new TCGAPostprocessingTask(set, settings).invoke();
		}

		final String projectOutputPath = runSpecificOutputPath + analysisRun + "_" + tumorType + ".cal";
		if (!saveProject(dataDomains, projectOutputPath)) {
			log.severe("Saving Project failed for tumor type " + tumorType + " for analysis run " + analysisRun);
			return null;
		}

		log.info("Built project file for tumor type " + tumorType + " for analysis run " + analysisRun);

		project = null;

		String projectRemoteOutputURL = settings.getTcgaServerURL() + analysisRun + "/" + analysisRun + "_" + tumorType
				+ ".cal";

		String jnlpFileName = analysisRun + "_" + tumorType + ".jnlp";

		JsonObject report = generateTumorReportLine(dataDomains, tumorType, analysisRun, jnlpFileName,
				projectRemoteOutputURL);

		generateJNLP(new File(settings.getJNLPOutputDirectory(), jnlpFileName), projectRemoteOutputURL);

		cleanUp(dataDomains);

		return report;
	}



	protected JsonObject generateTumorReportLine(Collection<ATableBasedDataDomain> dataDomains,
			TumorType tumor, String analysisRun,
			String jnlpFileName, String projectOutputPath) {

		AdditionalInfo addInfoMRNA = null;
		AdditionalInfo addInfoMRNASeq = null;
		AdditionalInfo addInfoMicroRNA = null;
		AdditionalInfo addInfoMicroRNASeq = null;
		ClinicalInfos addInfoClinical = null;
		AdditionalInfo addInfoMutations = null;
		AdditionalInfo addInfoCopyNumber = null;
		AdditionalInfo addInfoMethylation = null;
		AdditionalInfo addInfoRPPA = null;

		String jnlpURL = settings.getJNLPURL(jnlpFileName);

		String firehoseReportURL = settings.getReportUrl(analysisRun, tumor);

		for (ATableBasedDataDomain dataDomain : dataDomains) {

			String dataSetName = dataDomain.getDataSetDescription().getDataSetName();

			if (dataSetName.equals("mRNA")) {
				addInfoMRNA = new AdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("mRNA-seq")) {
				addInfoMRNASeq = new AdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("microRNA")) {
				addInfoMicroRNA = new AdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("microRNA-seq")) {
				addInfoMicroRNASeq = new AdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("Clinical")) {
				addInfoClinical = new ClinicalInfos(dataDomain);
			}
			else if (dataSetName.equals("Mutations")) {
				addInfoMutations = new AdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("Copy Number")) {
				addInfoCopyNumber = new AdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("Methylation")) {
				addInfoMethylation = new AdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("RPPA")) {
				addInfoRPPA = new AdditionalInfo(dataDomain);
			}
		}
		Gson gson = settings.getGson();

		JsonObject report = new JsonObject();
		report.addProperty("tumorAbbreviation", tumor.getName());
		report.addProperty("tumorName", tumor.getLabel());
		{
			JsonObject genomic = new JsonObject();
			report.add("genomic", genomic);
			genomic.add("mRNA", gson.toJsonTree(addInfoMRNA));
			genomic.add("mRNA-seq", gson.toJsonTree(addInfoMRNASeq));
			genomic.add("microRNA", gson.toJsonTree(addInfoMicroRNA));
			genomic.add("microRNA-seq", gson.toJsonTree(addInfoMicroRNASeq));
			genomic.add("Mutations", gson.toJsonTree(addInfoMutations));
			genomic.add("Copy Number", gson.toJsonTree(addInfoCopyNumber));
			genomic.add("Methylation", gson.toJsonTree(addInfoMethylation));
			genomic.add("RPPA", gson.toJsonTree(addInfoRPPA));
		}
		{
			JsonObject nonGenomic = new JsonObject();
			report.add("nonGenomic", nonGenomic);
			nonGenomic.add("Clinical", gson.toJsonTree(addInfoClinical));
		}

		report.addProperty("Caleydo JNLP", jnlpURL);
		report.addProperty("Caleydo Project", projectOutputPath);
		report.addProperty("Firehose Report", firehoseReportURL);

		return report;
	}

	public static class ClinicalInfos {
		private int count;
		private List<String> parameters = new ArrayList<>();

		public ClinicalInfos(ATableBasedDataDomain dataDomain) {
			count = dataDomain.getTable().depth();
			VirtualArray dimensionVA = dataDomain.getTable().getDefaultDimensionPerspective().getVirtualArray();
			for (int dimensionID : dimensionVA) {
				parameters.add(dataDomain.getDimensionLabel(dimensionID));
			}
		}

		public int getCount() {
			return count;
		}

		public List<String> getParameters() {
			return parameters;
		}
	}

}
