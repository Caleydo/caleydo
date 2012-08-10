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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.system.FileOperations;
import org.caleydo.data.importer.XMLToProjectBuilder;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.caleydo.data.importer.tcga.ETumorType;
import org.caleydo.data.importer.tcga.utils.GroupingListCreator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
 * This class handles the whole workflow of creating a Caleydo project from TCGA
 * data for inter analysis run comparisons.
 * 
 * @author Marc Streit
 * 
 */
public class TCGAInterAnalysisRunProjectBuilderApplication
	implements IApplication {

	public static String DEFAULT_TCGA_SERVER_URL = "http://compbio.med.harvard.edu/tcga/stratomex/data_qc/";
	public static String CALEYDO_WEBSTART_URL = "http://data.icg.tugraz.at/caleydo/download/webstart_"
			+ GeneralManager.VERSION + "/";
	public static String DEFAULT_OUTPUT_FOLDER_PATH = GeneralManager.CALEYDO_HOME_PATH
			+ "TCGA/";

	private String[] tumorTypes = null;
	private String[] analysisRuns = null;
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

		for (EDataSetType dataSetType : EDataSetType.values()) {

			String dataTypeSpecificOutputPath = outputPath + "data_qc" + dataSetType + "/";
			FileOperations.createDirectory(dataTypeSpecificOutputPath);

			for (int tumorIndex = 0; tumorIndex < tumorTypes.length; tumorIndex++) {

				String tumorType = tumorTypes[tumorIndex];

				String xmlFilePath = tmpDataOutputPath + dataSetType + "_" + tumorType
						+ ".xml";

				String projectOutputPath = dataTypeSpecificOutputPath + dataSetType + "_"
						+ tumorType + ".cal";

				String jnlpFileName = dataSetType + "_" + tumorType + ".jnlp";
				String jnlpOutputPath = jnlpOutputFolder + jnlpFileName;

				String projectRemoteOutputURL = tcgaServerURL + dataSetType + "/"
						+ dataSetType + "_" + tumorType + ".cal";

				System.out.println("Downloading " + dataSetType + " data for tumor type "
						+ tumorType);

				TCGAInterAnalysisRunXMLGenerator generator = new TCGAInterAnalysisRunXMLGenerator(
						tumorType, analysisRuns, dataSetType, xmlFilePath,
						dataTypeSpecificOutputPath, tmpDataOutputPath);

				generator.run();

				System.out.println("Building project file for " + dataSetType
						+ " data for tumor type " + tumorType);

				XMLToProjectBuilder xmlToProjectBuilder = new XMLToProjectBuilder();
				xmlToProjectBuilder.buildProject(xmlFilePath, projectOutputPath);

				generateTumorReportLine(tumorType, jnlpFileName, projectRemoteOutputURL);

				if (tumorIndex < tumorTypes.length - 1)
					reportJSONGenomicData += ",";

				cleanUp(xmlFilePath, jnlpOutputPath, jnlpFileName, projectRemoteOutputURL);
			}

			generateJSONReport(dataSetType, dataTypeSpecificOutputPath);
			reportJSONGenomicData = "";
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

	private void generateJSONReport(EDataSetType dataSetType,
			String dataSetTypeSpecificOutputPath) {

		String reportJSONOutputPath = dataSetTypeSpecificOutputPath + dataSetType + ".json";

		reportJSONGenomicData = reportJSONGenomicData.replace("\"null\"", "null");

		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("{\"analysisRun\":\"" + dataSetType + "\",");
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

	private void generateTumorReportLine(String tumorAbbreviation, String jnlpFileName,
			String projectOutputPath) {

		String jnlpURL = CALEYDO_WEBSTART_URL + jnlpFileName;

		String tumorName = ETumorType.valueOf(tumorAbbreviation).getTumorName();

		reportJSONGenomicData += "{\"tumorAbbreviation\":\"" + tumorAbbreviation
				+ "\",\"tumorName\":\"" + tumorName + "\",\"genomic\":{";

		for (ATableBasedDataDomain dataDomain : DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class)) {

			String analysisRunName = dataDomain.getDataSetDescription().getDataSetName();

			reportJSONGenomicData += "\"" + analysisRunName + "\":"
					+ getAdditionalInfo(dataDomain) + ",";
		}

		// remove last comma
		if (DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class).size() > 0)
			reportJSONGenomicData = reportJSONGenomicData.substring(0,
					reportJSONGenomicData.length() - 1);

		reportJSONGenomicData += "},\"Caleydo JNLP\":\"" + jnlpURL
				+ "\",\"Caleydo Project\":\"" + projectOutputPath + "\"}\n";
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
