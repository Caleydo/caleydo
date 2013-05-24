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

import java.io.File;
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
		String jnlpOutputFolder = settings.getJNLPOutputDirectory();

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

		String jnlpFileName = dataSetType + "_" + tumor + ".jnlp";
		JsonElement report = generateTumorReportLine(dataDomains, projectRemoteOutputURL);

		generateJNLP(new File(jnlpOutputFolder, jnlpFileName), projectRemoteOutputURL);

		cleanUp(dataDomains);

		return report;
	}

	protected JsonElement generateTumorReportLine(Collection<ATableBasedDataDomain> dataDomains,
			String projectOutputPath) {

		String jnlpURL = settings.getJNLPURL(dataSetType.toString(), tumor);

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

		report.addProperty("Caleydo JNLP", jnlpURL);
		report.addProperty("Caleydo Project", projectOutputPath);

		return report;
	}
}
