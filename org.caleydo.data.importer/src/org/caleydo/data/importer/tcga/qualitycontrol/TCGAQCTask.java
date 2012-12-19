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
import org.caleydo.core.io.ProjectDescription;
import org.caleydo.data.importer.XMLToProjectBuilder;
import org.caleydo.data.importer.tcga.ATCGATask;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.caleydo.data.importer.tcga.ETumorType;

/**
 * This class handles the whole workflow of creating a Caleydo project from TCGA
 * data.
 *
 * @author Marc Streit
 *
 */
public class TCGAQCTask extends ATCGATask {
	private static final long serialVersionUID = -3231766030581533359L;

	private final String tumorType;
	private final TCGAQCSettings settings;
	private final EDataSetType dataSetType;

	public TCGAQCTask(String tumorType, EDataSetType dataSetType, TCGAQCSettings settings) {
		this.tumorType = tumorType;
		this.dataSetType = dataSetType;
		this.settings = settings;
	}

	@Override
	public String compute() {
		String jnlpOutputFolder = settings.getJNLPOutputDirectory();
		String jnlpFileName = dataSetType + "_" + tumorType + ".jnlp";

		String projectOutputPath = settings.getDataDirectory(dataSetType.name()) + dataSetType + "_" + tumorType
				+ ".cal";

		System.out.println("Downloading " + dataSetType + " data for tumor type " + tumorType);

		ProjectDescription project = new TCGAInterAnalysisRunXMLGenerator(tumorType, dataSetType, settings).invoke();

		if (project.getDataSetDescriptionCollection().isEmpty())
			return null;

		Collection<ATableBasedDataDomain> dataDomains = new XMLToProjectBuilder().buildProject(project,
				projectOutputPath);

		String projectRemoteOutputURL = settings.getTcgaServerURL() + dataSetType + "/" + dataSetType + "_" + tumorType
				+ ".cal";

		StringBuilder report = new StringBuilder();
		generateTumorReportLine(report, dataDomains, tumorType, jnlpFileName, projectRemoteOutputURL);

		generateJNLP(new File(jnlpOutputFolder, jnlpFileName), projectRemoteOutputURL);

		cleanUp(dataDomains);

		return report.toString();
	}

	protected void generateTumorReportLine(StringBuilder report, Collection<ATableBasedDataDomain> dataDomains,
			String tumorAbbreviation, String jnlpFileName, String projectOutputPath) {

		String jnlpURL = settings.getJNLPURL(jnlpFileName);

		String tumorName = ETumorType.valueOf(tumorAbbreviation).getTumorName();

		report.append("{\"tumorAbbreviation\":\"").append(tumorAbbreviation)
				.append("\",\"tumorName\":\"").append(tumorName).append("\",\"genomic\":{");

		for (ATableBasedDataDomain dataDomain : dataDomains) {

			String analysisRunName = dataDomain.getDataSetDescription().getDataSetName();

			report.append("\"").append(analysisRunName).append("\":")
					.append(getAdditionalInfo(dataDomain)).append(",");
		}

		// remove last comma
		if (!dataDomains.isEmpty())
			report.setLength(report.length() - 1);

		report.append("},\"Caleydo JNLP\":\"").append(jnlpURL).append("\",\"Caleydo Project\":\"");
		report.append(projectOutputPath).append("\"}\n");
	}
}
