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
import java.util.Collection;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.io.ProjectDescription;
import org.caleydo.data.importer.XMLToProjectBuilder;
import org.caleydo.data.importer.tcga.ATCGATask;
import org.caleydo.data.importer.tcga.ETumorType;

/**
 * This class handles the whole workflow of creating a Caleydo project from TCGA
 * data.
 *
 * @author Marc Streit
 *
 */
public class TCGATask extends ATCGATask {
	private static final long serialVersionUID = 7378867458430247164L;

	private final String tumorType;
	private final String analysisRun;
	private final String dataRun;
	private TCGASettings settings;

	public TCGATask(String tumorType, String analysisRun, String dataRun, TCGASettings settings) {
		this.tumorType = tumorType;
		this.analysisRun = analysisRun;
		this.dataRun = dataRun;
		this.settings = settings;
	}

	@Override
	protected String compute() {
		System.out.println("Downloading data for tumor type " + tumorType + " for analysis run " + analysisRun);

		String runSpecificOutputPath = settings.getDataDirectory(analysisRun);

		ProjectDescription project = new TCGAXMLGenerator(tumorType, settings.createFirehoseProvider(tumorType,
				analysisRun, dataRun), settings).invoke();

		if (project.getDataSetDescriptionCollection().isEmpty())
			return null;

		if (settings.isDownloadOnly())
			return null;
		System.out.println("Building project file for tumor type " + tumorType + " for analysis run " + analysisRun);
		String projectOutputPath = runSpecificOutputPath + analysisRun + "_" + tumorType + ".cal";
		Collection<ATableBasedDataDomain> dataDomains = new XMLToProjectBuilder().buildProject(project,
				projectOutputPath);

		project = null;

		String projectRemoteOutputURL = settings.getTcgaServerURL() + analysisRun + "/" + analysisRun + "_" + tumorType
				+ ".cal";

		StringBuilder report = new StringBuilder();

		String jnlpFileName = analysisRun + "_" + tumorType + ".jnlp";

		generateTumorReportLine(report, dataDomains, tumorType, analysisRun, jnlpFileName, projectRemoteOutputURL);

		generateJNLP(new File(settings.getJNLPOutputDirectory(), jnlpFileName), projectRemoteOutputURL);

		cleanUp(dataDomains);

		return report.toString();
	}

	protected void generateTumorReportLine(StringBuilder report, Collection<ATableBasedDataDomain> dataDomains,
			String tumorAbbreviation, String analysisRun,
			String jnlpFileName, String projectOutputPath) {

		String addInfoMRNA = "null";
		String addInfoMRNASeq = "null";
		String addInfoMicroRNA = "null";
		String addInfoMicroRNASeq = "null";
		String addInfoClinical = "null";
		String addInfoMutations = "null";
		String addInfoCopyNumber = "null";
		String addInfoMethylation = "null";
		String addInfoRPPA = "null";

		String jnlpURL = settings.getJNLPURL(jnlpFileName);

		String firehoseReportURL = settings.getReportUrl(analysisRun, tumorAbbreviation);

		String tumorName = ETumorType.valueOf(tumorAbbreviation).getTumorName();

		for (ATableBasedDataDomain dataDomain : dataDomains) {

			String dataSetName = dataDomain.getDataSetDescription().getDataSetName();

			if (dataSetName.equals("mRNA")) {
				addInfoMRNA = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("mRNA-seq")) {
				addInfoMRNASeq = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("microRNA")) {
				addInfoMicroRNA = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("microRNA-seq")) {
				addInfoMicroRNASeq = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("Clinical")) {
				addInfoClinical = getClinicalInfo(dataDomain);
			}
			else if (dataSetName.equals("Mutations")) {
				addInfoMutations = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("Copy Number")) {
				addInfoCopyNumber = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("Methylation")) {
				addInfoMethylation = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("RPPA")) {
				addInfoRPPA = getAdditionalInfo(dataDomain);
			}
		}


		report.append("{\"tumorAbbreviation\":\"").append(tumorAbbreviation);
		report.append("\",\"tumorName\":\"").append(tumorName);
		report.append("\",\"genomic\":{");
		report.append("\"mRNA\":").append(addInfoMRNA);
		report.append(",\"mRNA-seq\":").append(addInfoMRNASeq);
		report.append(",\"microRNA\":").append(addInfoMicroRNA);
		report.append(",\"microRNA-seq\":").append(addInfoMicroRNASeq);
		report.append(",\"Mutations\":" + addInfoMutations);
		report.append(",\"Copy Number\":").append(addInfoCopyNumber);
		report.append(",\"Methylation\":" + addInfoMethylation);
		report.append(",\"RPPA\":").append(addInfoRPPA);
		report.append("},\"nonGenomic\":");
		report.append("{\"Clinical\":").append(addInfoClinical);
		report.append("},\"Caleydo JNLP\":\"").append(jnlpURL);
		report.append("\",\"Caleydo Project\":\"").append(projectOutputPath);
		report.append("\",\"Firehose Report\":\"").append(firehoseReportURL).append("\"}\n");
	}


	private String getClinicalInfo(ATableBasedDataDomain dataDomain) {
		String clinicalParameters = "";
		DimensionVirtualArray dimensionVA = dataDomain.getTable()
				.getDefaultDimensionPerspective().getVirtualArray();
		for (int dimensionID : dimensionVA) {
			clinicalParameters += "\"" + dataDomain.getDimensionLabel(dimensionID) + "\",";
		}

		// remove last comma
		if (clinicalParameters.length() > 1)
			clinicalParameters = clinicalParameters.substring(0,
					clinicalParameters.length() - 1);

		return "{\"count\":\"" + dataDomain.getTable().getMetaData().depth()
				+ "\",\"parameters\":[" + clinicalParameters + "]}";
	}

}
