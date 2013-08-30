/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.regular;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.data.importer.tcga.ATCGATask;
import org.caleydo.data.importer.tcga.Settings;
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
	private final Date analysisRun;
	private final Date dataRun;
	private final TCGASettings settings;

	public TCGATask(TumorType tumorType, Date analysisRun, Date dataRun, TCGASettings settings) {
		this.tumorType = tumorType;
		this.analysisRun = analysisRun;
		this.dataRun = dataRun;
		this.settings = settings;
	}

	@Override
	protected JsonElement compute() {
		log.info("Downloading data for tumor type " + tumorType + " for analysis run " + analysisRun);

		String run = Settings.format(analysisRun);
		String runSpecificOutputPath = settings.getDataDirectory(run);

		TCGADataSets project = new TCGADataSetGenerator(tumorType, settings.createFirehoseProvider(tumorType,
				analysisRun, dataRun), settings).invoke();

		if (project.isEmpty())
			return null;

		if (settings.isDownloadOnly())
			return null;

		log.info("Building project file for tumor type " + tumorType + " for analysis run " + run);

		Collection<ATableBasedDataDomain> dataDomains = loadProject(project);
		if (dataDomains.isEmpty()) {
			log.warning("No Data Domains loaded for tumor type " + tumorType + " for analysis run " + run
					+ " -> skipping rest");
			return null;
		}

		log.info("Post Processing project file for tumor type " + tumorType + " for analysis run " + run);
		for (TCGADataSet set : project) {
			new TCGAPostprocessingTask(set).invoke();
		}

		final String projectOutputPath = runSpecificOutputPath + run + "_" + tumorType + ".cal";

		ProjectMetaData metaData = ProjectMetaData.createDefault();
		metaData.setName("TCGA " + tumorType.getName() + " Package");
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
		metaData.set("Analysis Run", df.format(analysisRun));
		metaData.set("Data Run", df.format(dataRun));
		metaData.set("Tumor", tumorType.getLabel());
		metaData.set("Report URL", settings.getReportUrl(analysisRun, tumorType));

		if (!saveProject(dataDomains, projectOutputPath, metaData)) {
			log.severe("Saving Project failed for tumor type " + tumorType + " for analysis run " + run);
			return null;
		}

		log.info("Built project file for tumor type " + tumorType + " for analysis run " + run);

		project = null;

		String projectRemoteOutputURL = settings.getTcgaServerURL() + run + "/" + run + "_" + tumorType
				+ ".cal";

		String jnlpFileName = run + "_" + tumorType + ".jnlp";

		generateJNLP(new File(settings.getJNLPOutputDirectory(), jnlpFileName), projectRemoteOutputURL);

		JsonObject report = generateTumorReportLine(dataDomains, tumorType, analysisRun, projectRemoteOutputURL);

		cleanUp(dataDomains);

		return report;
	}



	protected JsonObject generateTumorReportLine(Collection<ATableBasedDataDomain> dataDomains,
 TumorType tumor,
			Date analysisRun, String projectOutputPath) {

		AdditionalInfo addInfoMRNA = null;
		AdditionalInfo addInfoMRNASeq = null;
		AdditionalInfo addInfoMicroRNA = null;
		AdditionalInfo addInfoMicroRNASeq = null;
		ClinicalInfos addInfoClinical = null;
		AdditionalInfo addInfoMutations = null;
		AdditionalInfo addInfoCopyNumber = null;
		AdditionalInfo addInfoMethylation = null;
		AdditionalInfo addInfoRPPA = null;

		String jnlpURL = settings.getJNLPURL(Settings.format(analysisRun), tumor);

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
			VirtualArray dimensionVA = dataDomain.getTable().getDefaultDimensionPerspective(false).getVirtualArray();
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
