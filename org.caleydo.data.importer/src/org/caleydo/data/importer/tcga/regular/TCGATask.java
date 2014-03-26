/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.regular;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.io.HTMLFormatter;
import org.caleydo.core.io.MetaDataElement;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.data.importer.tcga.ATCGATask;
import org.caleydo.data.importer.tcga.Settings;
import org.caleydo.data.importer.tcga.model.TCGADataSet;
import org.caleydo.data.importer.tcga.model.TCGADataSets;
import org.caleydo.data.importer.tcga.model.TumorType;
import org.caleydo.view.tourguide.api.external.ExternalIDTypeScoreParser;
import org.caleydo.view.tourguide.api.external.ScoreParseSpecification;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.api.score.Scores;

import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This class handles the whole workflow of creating a Caleydo project from TCGA data.
 *
 * @author Marc Streit
 *
 */
public class TCGATask extends ATCGATask {
	private static final Logger log = Logger.getLogger(TCGATask.class.getName());
	private static final long serialVersionUID = 7378867458430247164L;

	private final TumorType tumorType;
	private final Date analysisRun;
	private final Date dataRun;
	private final TCGASettings settings;
	private final String id;

	public TCGATask(TumorType tumorType, Date analysisRun, Date dataRun, TCGASettings settings) {
		this.tumorType = tumorType;
		this.analysisRun = analysisRun;
		this.dataRun = dataRun;
		this.settings = settings;

		this.id = String.format("%s@%s", tumorType, Settings.format(analysisRun));
	}

	@Override
	protected JsonElement compute() {
		Stopwatch w = new Stopwatch().start();
		log.info(id + " start downloading");

		String run = Settings.format(analysisRun);
		String runSpecificOutputPath = settings.getDataDirectory(run);

		TCGADataSets project = new TCGADataSetGenerator(tumorType, settings.createFirehoseProvider(tumorType,
				analysisRun, dataRun), settings).invoke();

		if (project.isEmpty()) {
			log.warning(id + " no datasets were created, skipping");
			return null;
		}

		if (settings.isDownloadOnly()) {
			log.fine(id + " no project generation just downloading data");
			return null;
		}

		log.info(id + " loading project");

		Collection<ATableBasedDataDomain> dataDomains = loadProject(project);
		if (dataDomains.isEmpty()) {
			log.severe(id + " no datadomains were loaded, skipping");
			return null;
		}

		log.fine(id + " start post processing");
		for (TCGADataSet set : project) {
			new TCGAPostprocessingTask(set).invoke();
		}

		if (project.getMutsigParser() != null) {
			log.info(id + " start loading mutsig scores");
			loadExternalScores(project.getMutsigParser(), dataDomains);
		}

		final String projectOutputPath = runSpecificOutputPath + run + "_" + tumorType + ".cal";

		ProjectMetaData metaData = ProjectMetaData.createDefault();
		metaData.setName("TCGA " + tumorType.getName() + " Package");
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
		metaData.set("Analysis Run", df.format(analysisRun));
		metaData.set("Data Run", df.format(dataRun));
		metaData.set("Tumor", tumorType.getLabel());
		metaData.set("Report URL", settings.getReportUrl(analysisRun, tumorType));

		log.info(id + " saving project");
		if (!saveProject(dataDomains, projectOutputPath, metaData)) {
			log.severe(id + " saving error, skipping");
			return null;
		}

		saveProjectSpecificReport(dataDomains, tumorType, runSpecificOutputPath, run);


		project = null;

		String projectRemoteOutputURL = settings.getTcgaServerURL() + run + "/" + run + "_" + tumorType + ".cal";

		JsonObject report = generateTumorReportLine(dataDomains, tumorType, analysisRun, projectRemoteOutputURL);

		log.fine(id + " cleanup up datadomains: " + dataDomains);
		cleanUp(dataDomains);

		log.info(id + " done in " + w);
		return report;
	}


	private void loadExternalScores(ScoreParseSpecification spec, Collection<ATableBasedDataDomain> dataDomains) {
		// spec.set
		// find mutation or copy number for the target type
		ATableBasedDataDomain target = null;
		for (ATableBasedDataDomain dataDomain : dataDomains) {
			if (dataDomain.getLabel().contains("Mutation")) {
				target = dataDomain;
				break;
			}
		}
		if (target == null) {
			log.warning(id + " skipping loading mutsig values as there is no mutation data domain");
			return;
		}

		ExternalIDTypeScoreParser parser = new ExternalIDTypeScoreParser(spec, target.getDimensionIDType(), target);
		Collection<ISerializeableScore> scores = parser.call();
		final Scores s = Scores.get();
		for (ISerializeableScore score : scores) {
			s.addPersistentScoreIfAbsent(score);
		}
		log.fine(id + " loaded external scores: " + scores);
	}

	protected void saveProjectSpecificReport(Collection<ATableBasedDataDomain> dataDomains, TumorType tumorType,
			String outputPath, String run) {
		MetaDataElement rootElement = new MetaDataElement();
		for (ATableBasedDataDomain dataDomain : dataDomains) {
			MetaDataElement ddElement = new MetaDataElement(dataDomain.getLabel());
			MetaDataElement md = dataDomain.getDataSetDescription().getMetaData();
			if (md != null) {
				ddElement.addElement(md);
			}
			rootElement.addElement(ddElement);
		}
		String projectReport = new HTMLFormatter().format(rootElement, "Report " + tumorType.getLabel());
		String fileName = run + "_" + tumorType + "_report.html";
		try {
			Files.write(projectReport, new File(outputPath, fileName), Charset.defaultCharset());
		} catch (IOException e) {
			log.log(Level.WARNING, id + "Could not save project report to " + outputPath + "/" + fileName, e);
		}
	}

	protected JsonObject generateTumorReportLine(Collection<ATableBasedDataDomain> dataDomains, TumorType tumor,
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

		String firehoseReportURL = settings.getReportUrl(analysisRun, tumor);

		for (ATableBasedDataDomain dataDomain : dataDomains) {

			String dataSetName = dataDomain.getDataSetDescription().getDataSetName();

			if (dataSetName.equals("mRNA")) {
				addInfoMRNA = new AdditionalInfo(dataDomain);
			} else if (dataSetName.equals("mRNA-seq")) {
				addInfoMRNASeq = new AdditionalInfo(dataDomain);
			} else if (dataSetName.equals("microRNA")) {
				addInfoMicroRNA = new AdditionalInfo(dataDomain);
			} else if (dataSetName.equals("microRNA-seq")) {
				addInfoMicroRNASeq = new AdditionalInfo(dataDomain);
			} else if (dataSetName.equals("Clinical")) {
				addInfoClinical = new ClinicalInfos(dataDomain);
			} else if (dataSetName.equals("Mutations")) {
				addInfoMutations = new AdditionalInfo(dataDomain);
			} else if (dataSetName.equals("Copy Number")) {
				addInfoCopyNumber = new AdditionalInfo(dataDomain);
			} else if (dataSetName.equals("Methylation")) {
				addInfoMethylation = new AdditionalInfo(dataDomain);
			} else if (dataSetName.equals("RPPA")) {
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
		report.addProperty("Caleydo Project", projectOutputPath);
		report.addProperty("Firehose Report", firehoseReportURL);

		return report;
	}

	public static class ClinicalInfos {
		private int count;
		private List<String> parameters = new ArrayList<>();

		public ClinicalInfos(ATableBasedDataDomain dataDomain) {
			count = dataDomain.getTable().depth();
			for (String id : dataDomain.getTable().getDimensionPerspectiveIDs()) {
				Perspective p = dataDomain.getTable().getDimensionPerspective(id);
				if (p.isDefault())
					continue;
				parameters.add(p.getLabel());
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
