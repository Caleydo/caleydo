/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.qualitycontrol;

import java.util.Collection;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.data.importer.tcga.ATCGATask;
import org.caleydo.data.importer.tcga.EDataSetType;
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
public class TCGAQCTask extends ATCGATask {
	private static final long serialVersionUID = -3231766030581533359L;

	private final TumorType tumor;
	private final TCGAQCSettings settings;
	private final EDataSetType dataSetType;

	public TCGAQCTask(TumorType tumor, EDataSetType dataSetType, TCGAQCSettings settings) {
		this.tumor = tumor;
		this.dataSetType = dataSetType;
		this.settings = settings;
	}

	@Override
	public JsonElement compute() {
		String projectOutputPath = settings.getDataDirectory(dataSetType.name()) + dataSetType + "_" + tumor
				+ ".cal";

		System.out.println("Downloading " + dataSetType + " data for tumor type " + tumor);

		TCGADataSets project = new TCGAInterAnalysisRunXMLGenerator(tumor, dataSetType, settings).invoke();

		if (project.isEmpty())
			return null;

		Collection<ATableBasedDataDomain> dataDomains = loadProject(project);
		project = null;

		if (dataDomains.isEmpty()) {
			return null;
		}

		ProjectMetaData metaData = ProjectMetaData.createDefault();

		if (!saveProject(dataDomains, projectOutputPath, metaData)) {
			return null;
		}

		String projectRemoteOutputURL = settings.getTcgaServerURL() + dataSetType + "/" + dataSetType + "_" + tumor
				+ ".cal";

		JsonElement report = generateTumorReportLine(dataDomains, projectRemoteOutputURL);

		cleanUp(dataDomains);

		return report;
	}

	protected JsonElement generateTumorReportLine(Collection<ATableBasedDataDomain> dataDomains,
			String projectOutputPath) {
		JsonObject report = new JsonObject();
		report.addProperty("tumorAbbreviation", tumor.getName());
		report.addProperty("tumorName", tumor.getLabel());

		Gson gson = settings.getGson();
		{
			JsonObject genomic = new JsonObject();
			report.add("genomic", genomic);
			for (ATableBasedDataDomain dataDomain : dataDomains) {
				String analysisRunName = dataDomain.getDataSetDescription().getDataSetName();
				report.add(analysisRunName, gson.toJsonTree(new AdditionalInfo(dataDomain)));
			}
		}
		report.addProperty("Caleydo Project", projectOutputPath);

		return report;
	}
}
