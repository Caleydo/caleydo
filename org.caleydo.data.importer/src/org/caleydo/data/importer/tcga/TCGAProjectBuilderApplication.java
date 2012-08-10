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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.system.FileOperations;
import org.caleydo.data.importer.XMLToProjectBuilder;
import org.caleydo.data.importer.tcga.utils.GroupingListCreator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
 * This class handles the whole workflow of creating a Caleydo project from TCGA
 * data.
 * 
 * @author Marc Streit
 * 
 */
public class TCGAProjectBuilderApplication
	implements IApplication {

	public static String DEFAULT_TCGA_SERVER_URL = "http://compbio.med.harvard.edu/tcga/stratomex/data/";
	public static String CALEYDO_WEBSTART_URL = "http://data.icg.tugraz.at/caleydo/download/webstart_"
			+ GeneralManager.VERSION + "/";
	public static String DEFAULT_OUTPUT_FOLDER_PATH = GeneralManager.CALEYDO_HOME_PATH
			+ "TCGA/";

	private String[] tumorTypes = null;
	private String[] analysisRuns = null;
	private String[] dataRuns = null;
	private String outputPath = "";
	private String tcgaServerURL = "";

	private String reportJSONGenomicData = "";

	@Override
	public Object start(IApplicationContext context) throws Exception {

		handleProgramArguments(context);

		generateTCGAProjectFiles();

		// FileOperations.deleteDirectory(tmpDataOutputPath);

		return context;
	}

	private void handleProgramArguments(IApplicationContext context) {
		String[] runConfigParameters = (String[]) context.getArguments().get(
				"application.args");

		JSAP jsap = new JSAP();
		try {

			FlaggedOption tumorOpt = new FlaggedOption("tumor")
					.setStringParser(JSAP.STRING_PARSER).setDefault(JSAP.NO_DEFAULT)
					.setRequired(true).setShortFlag('t').setLongFlag(JSAP.NO_LONGFLAG);
			tumorOpt.setList(true);
			tumorOpt.setListSeparator(',');
			tumorOpt.setHelp("Tumor abbreviation");
			jsap.registerParameter(tumorOpt);

			FlaggedOption analysisRunIdentifierOpt = new FlaggedOption("analysis_runs")
					.setStringParser(JSAP.STRING_PARSER).setDefault(JSAP.NO_DEFAULT)
					.setRequired(true).setShortFlag('a').setLongFlag(JSAP.NO_LONGFLAG);
			analysisRunIdentifierOpt.setList(true);
			analysisRunIdentifierOpt.setListSeparator(',');
			analysisRunIdentifierOpt.setHelp("Analysis run identifiers");
			jsap.registerParameter(analysisRunIdentifierOpt);

			FlaggedOption dataRunIdentifierOpt = new FlaggedOption("data_runs")
					.setStringParser(JSAP.STRING_PARSER).setDefault(JSAP.NO_DEFAULT)
					.setRequired(true).setShortFlag('d').setLongFlag(JSAP.NO_LONGFLAG);
			dataRunIdentifierOpt.setList(true);
			dataRunIdentifierOpt.setListSeparator(',');
			dataRunIdentifierOpt.setHelp("Data run identifiers");
			jsap.registerParameter(dataRunIdentifierOpt);

			FlaggedOption outputFolderOpt = new FlaggedOption("output-folder")
					.setStringParser(JSAP.STRING_PARSER)
					.setDefault(DEFAULT_OUTPUT_FOLDER_PATH).setRequired(false)
					.setShortFlag('o').setLongFlag(JSAP.NO_LONGFLAG);
			outputFolderOpt.setHelp("Output folder (full path)");
			jsap.registerParameter(outputFolderOpt);

			FlaggedOption tcgaServerURLOpt = new FlaggedOption("server")
					.setStringParser(JSAP.STRING_PARSER).setDefault(DEFAULT_TCGA_SERVER_URL)
					.setRequired(false).setShortFlag('s').setLongFlag(JSAP.NO_LONGFLAG);
			tcgaServerURLOpt.setHelp("TCGA Server URL that hosts TCGA Caleydo project files");
			jsap.registerParameter(tcgaServerURLOpt);

			JSAPResult config = jsap.parse(runConfigParameters);

			// check whether the command line was valid, and if it wasn't,
			// display usage information and exit.
			if (!config.success()) {
				handleJSAPError(jsap);
			}

			tumorTypes = config.getStringArray("tumor");

			analysisRuns = config.getStringArray("analysis_runs");
			dataRuns = config.getStringArray("data_runs");

			if (analysisRuns.length != dataRuns.length) {
				System.err
						.println("Error during parsing of program arguments. You need to provide a corresponding data run for each analysis run. Closing program.");
				System.err.println("Usage: Caleydo");
				System.err.println(jsap.getUsage());
				System.err.println();
				System.exit(1);
			}
			outputPath = config.getString("output-folder");
			tcgaServerURL = config.getString("server");
		}
		catch (JSAPException e) {
			handleJSAPError(jsap);
		}
	}

	private void generateTCGAProjectFiles() {
		String tmpDataOutputPath = outputPath + "tmp/";
		String jnlpOutputFolder = outputPath + "jnlp/";
		FileOperations.createDirectory(jnlpOutputFolder);

		for (int analysisRunIndex = 0; analysisRunIndex < analysisRuns.length; analysisRunIndex++) {

			String analysisRun = analysisRuns[analysisRunIndex];
			String dataRun = dataRuns[analysisRunIndex];

			FileOperations.createDirectory(outputPath + "data/");
			String runSpecificOutputPath = outputPath + "data/" + analysisRun + "/";
			FileOperations.createDirectory(runSpecificOutputPath);

			for (int tumorIndex = 0; tumorIndex < tumorTypes.length; tumorIndex++) {

				String tumorType = tumorTypes[tumorIndex];

				String xmlFilePath = tmpDataOutputPath + analysisRun + "_" + tumorType
						+ ".xml";

				String projectOutputPath = runSpecificOutputPath + analysisRun + "_"
						+ tumorType + ".cal";

				String jnlpFileName = analysisRun + "_" + tumorType + ".jnlp";
				String jnlpOutputPath = jnlpOutputFolder + jnlpFileName;

				String projectRemoteOutputURL = tcgaServerURL + analysisRun + "/"
						+ analysisRun + "_" + tumorType + ".cal";

				System.out.println("Downloading data for tumor type " + tumorType
						+ " for analysis run " + analysisRun);

				TCGAXMLGenerator generator = new TCGAXMLGenerator(tumorType, analysisRun,
						dataRun, xmlFilePath, runSpecificOutputPath, tmpDataOutputPath);

				generator.run();

				System.out.println("Building project file for tumor type " + tumorType
						+ " for analysis run " + analysisRun);

				XMLToProjectBuilder xmlToProjectBuilder = new XMLToProjectBuilder();
				xmlToProjectBuilder.buildProject(xmlFilePath, projectOutputPath);

				generateTumorReportLine(tumorType, analysisRun, jnlpFileName,
						projectRemoteOutputURL);

				if (tumorIndex < tumorTypes.length - 1)
					reportJSONGenomicData += ",";

				cleanUp(xmlFilePath, jnlpOutputPath, jnlpFileName, projectRemoteOutputURL);
			}

			generateJSONReport(analysisRun, dataRun, runSpecificOutputPath);
		}
	}

	private void handleJSAPError(JSAP jsap) {
		System.err.println("Error during parsing of program arguments. Closing program.");
		System.err.println("Usage: Caleydo");
		System.err.println(jsap.getUsage());
		System.err.println();
		System.exit(1);
	}

	private void replaceStringInFile(String oldstring, String newstring, File in, File out)
			throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(in));
		PrintWriter writer = new PrintWriter(new FileWriter(out));
		String line = null;
		while ((line = reader.readLine()) != null)
			writer.println(line.replaceAll(oldstring, newstring));
		reader.close();
		writer.close();
	}

	private void cleanUp(String xmlFilePath, String jnlpOutputPath, String jnlpFileName,
			String jnlpRemoteOutputURL) {

		DataDomainManager.get().unregisterAllDataDomains();

		// Clean up
		new File(xmlFilePath).delete();

		try {
			// Generate jnlp file from jnlp template
			replaceStringInFile("CALEYDO_PROJECT_URL", jnlpRemoteOutputURL, new File(
					"resources/caleydo.jnlp"), new File(jnlpOutputPath + "_"));

			replaceStringInFile("JNLP_NAME", jnlpFileName, new File(jnlpOutputPath + "_"),
					new File(jnlpOutputPath));

			new File(jnlpOutputPath + "_").delete();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		for (IDMappingManager idMappingManager : IDMappingManagerRegistry.get()
				.getAllIDMappingManager()) {
			idMappingManager.clearInternalMappingsAndIDTypes();
		}
	}

	private void generateJSONReport(String analysisRun, String dataRun,
			String runSpecificOutputPath) {

		String reportJSONOutputPath = runSpecificOutputPath + analysisRun + ".json";

		reportJSONGenomicData = reportJSONGenomicData.replace("\"null\"", "null");

		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("{\"analysisRun\":\"" + analysisRun + "\",\"dataRun\":\"" + dataRun
				+ "\",");
		htmlBuilder.append("\"details\":[" + reportJSONGenomicData + "],\"caleydoVersion\":\""
				+ GeneralManager.VERSION + "\"");
		htmlBuilder.append("}\n");

		FileWriter writer;
		try {
			writer = new FileWriter(reportJSONOutputPath);
			writer.write(htmlBuilder.toString());
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateTumorReportLine(String tumorAbbreviation, String analysisRun,
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

		String jnlpURL = CALEYDO_WEBSTART_URL + jnlpFileName;

		String firehoseReportURL = TCGAXMLGenerator.FIREHOSE_URL_PREFIX + "analyses__"
				+ analysisRun + "/reports/cancer/" + tumorAbbreviation + "/";

		String tumorName = ETumorType.valueOf(tumorAbbreviation).getTumorName();

		for (ATableBasedDataDomain dataDomain : DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class)) {

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

		reportJSONGenomicData += "{\"tumorAbbreviation\":\"" + tumorAbbreviation
				+ "\",\"tumorName\":\"" + tumorName + "\",\"genomic\":{\"mRNA\":"
				+ addInfoMRNA + ",\"mRNA-seq\":" + addInfoMRNASeq + ",\"microRNA\":"
				+ addInfoMicroRNA + ",\"microRNA-seq\":" + addInfoMicroRNASeq
				+ ",\"Mutations\":" + addInfoMutations + ",\"Copy Number\":"
				+ addInfoCopyNumber + ",\"Methylation\":" + addInfoMethylation + ",\"RPPA\":"
				+ addInfoRPPA + "},\"nonGenomic\":{\"Clinical\":" + addInfoClinical
				+ "},\"Caleydo JNLP\":\"" + jnlpURL + "\",\"Caleydo Project\":\""
				+ projectOutputPath + "\",\"Firehose Report\":\"" + firehoseReportURL
				+ "\"}\n";
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

	private String getAdditionalInfo(ATableBasedDataDomain dataDomain) {
		return "{\"gene\":{\"count\":\"" + dataDomain.getTable().getMetaData().size()
				+ "\",\"groupings\":["
				+ GroupingListCreator.getDimensionGroupingList(dataDomain)
				+ "]},\"sample\":{\"count\":\"" + dataDomain.getTable().getMetaData().depth()
				+ "\",\"groupings\":[" + GroupingListCreator.getRecordGroupingList(dataDomain)
				+ "]}}";
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
