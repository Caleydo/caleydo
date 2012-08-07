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
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.system.FileOperations;
import org.caleydo.data.importer.XMLToProjectBuilder;
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

	private String[] tumorTypes = null;
	private String analysisRunIdentifier = "";
	private String dataRunIdentifier = "";
	private String outputPath = "";
	private String tcgaServerURL = "";

	private String reportHTMLOutputPath;
	private String reportJSONOutputPath;

	private String reportStringHTML = "";
	private String reportStringJSON = "";

	@Override
	public Object start(IApplicationContext context) throws Exception {

		handleProgramArguments(context);

		generateTCGAProjectFiles();

		generateHTMLReport();
		generateJSONReport();

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

			FlaggedOption analysisRunIdentifierOpt = new FlaggedOption("analysis_run")
					.setStringParser(JSAP.STRING_PARSER).setDefault(JSAP.NO_DEFAULT)
					.setRequired(true).setShortFlag('a').setLongFlag(JSAP.NO_LONGFLAG);
			analysisRunIdentifierOpt.setHelp("Analysis run identifier");
			jsap.registerParameter(analysisRunIdentifierOpt);

			FlaggedOption dataRunIdentifierOpt = new FlaggedOption("data_run")
					.setStringParser(JSAP.STRING_PARSER).setDefault(JSAP.NO_DEFAULT)
					.setRequired(true).setShortFlag('d').setLongFlag(JSAP.NO_LONGFLAG);
			dataRunIdentifierOpt.setHelp("Data run identifier");
			jsap.registerParameter(dataRunIdentifierOpt);

			String defaultDestinationPath = GeneralManager.CALEYDO_HOME_PATH + "TCGA/";

			FlaggedOption outputFolderOpt = new FlaggedOption("output-folder")
					.setStringParser(JSAP.STRING_PARSER).setDefault(defaultDestinationPath)
					.setRequired(false).setShortFlag('o').setLongFlag(JSAP.NO_LONGFLAG);
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
			analysisRunIdentifier = config.getString("analysis_run");
			dataRunIdentifier = config.getString("data_run");
			outputPath = config.getString("output-folder");
			tcgaServerURL = config.getString("server");

			reportHTMLOutputPath = outputPath + "index.html";
			reportJSONOutputPath = outputPath + analysisRunIdentifier + ".json";
		}
		catch (JSAPException e) {
			handleJSAPError(jsap);
		}
	}

	private void generateTCGAProjectFiles() {
		String tmpDataOutputPath = outputPath + "tmp";

		for (int tumorIndex = 0; tumorIndex < tumorTypes.length; tumorIndex++) {

			String tumorType = tumorTypes[tumorIndex];

			String xmlFilePath = outputPath + analysisRunIdentifier + "_" + tumorType + ".xml";

			String projectOutputPath = outputPath + analysisRunIdentifier + "_" + tumorType
					+ ".cal";

			String jnlpFileName = analysisRunIdentifier + "_" + tumorType + ".jnlp";
			String jnlpOutputPath = outputPath + jnlpFileName;

			String projectRemoteOutputURL = tcgaServerURL + analysisRunIdentifier + "_"
					+ tumorType + ".cal";

			FileOperations.createDirectory(tmpDataOutputPath);

			System.out.println("Downloading data for tumor type " + tumorType
					+ " for analysis run " + analysisRunIdentifier);

			TCGADataXMLGenerator generator = new TCGADataXMLGenerator(tumorType,
					analysisRunIdentifier, dataRunIdentifier, xmlFilePath, outputPath,
					tmpDataOutputPath);

			generator.run();

			System.out.println("Building project file for tumor type " + tumorType
					+ " for analysis run " + analysisRunIdentifier);

			XMLToProjectBuilder xmlToProjectBuilder = new XMLToProjectBuilder();
			xmlToProjectBuilder.buildProject(xmlFilePath, projectOutputPath);

			generateTumorReportLine(tumorType, jnlpFileName, projectRemoteOutputURL);

			if (tumorIndex < tumorTypes.length - 1)
				reportStringJSON += ",";

			cleanUp(xmlFilePath, jnlpOutputPath, jnlpFileName, projectRemoteOutputURL);
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

	private void generateHTMLReport() {

		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html><body>");
		htmlBuilder.append("<h1>Caleydo TCGA Projects for Analysis Run "
				+ analysisRunIdentifier + "</h1>");
		htmlBuilder.append("<table>");
		htmlBuilder.append("<tr><th>Tumor Type</th>" + "<th>mRNA</th>" + "<th>mRNA-seq</th>"
				+ "<th>microRNA</th>" + "<th>microRNA-seq</th>" + "<th>Clinical</th>"
				+ "<th>Mutations</th>" + "<th>Copy Number</th>" + "<th>Methylation</th>"
				+ "<th>RPPA</th>" + "<th>Caleydo Webstart</th>" + "<th>Caleydo Project</th>"
				+ "<th>Firehose Report</th>" + "</tr>");
		htmlBuilder.append(reportStringHTML);
		htmlBuilder.append("</table>");
		htmlBuilder.append("</body></html>\n");

		FileWriter writer;
		try {
			writer = new FileWriter(reportHTMLOutputPath);
			writer.write(htmlBuilder.toString());
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateJSONReport() {

		reportStringJSON = reportStringJSON.replace("\"null\"", "null");

		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("{\"analysisRun\":\"" + analysisRunIdentifier + "\",\"dataRun\":\""
				+ dataRunIdentifier + "\",");
		htmlBuilder.append("\"details\":[" + reportStringJSON + "]");
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

		boolean isMRNALoaded = false;
		boolean isMRNASeqLoaded = false;
		boolean isMicroRNALoaded = false;
		boolean isMicroRNASeqLoaded = false;
		boolean isClinicalLoaded = false;
		boolean isMutationsLoaded = false;
		boolean isCopyNumberLoaded = false;
		boolean isMethylationLoaded = false;
		boolean isRPPALoaded = false;

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
		String jnlpLinkTag = "<a href=" + jnlpURL + ">Open in Caleydo</a>";
		String projectLinkTag = "<a href=" + projectOutputPath + ">Download</a>";

		String firehoseReportURL = TCGADataXMLGenerator.FIREHOSE_URL_PREFIX + "analyses__"
				+ analysisRunIdentifier + "/reports/cancer/" + tumorAbbreviation + "/";
		String firehoseReportLinkTag = "<a href=" + firehoseReportURL + ">Report Link</a>";

		String tumorName = ETumorType.valueOf(tumorAbbreviation).getTumorName();

		for (ATableBasedDataDomain dataDomain : DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class)) {

			String dataSetName = dataDomain.getDataSetDescription().getDataSetName();

			if (dataSetName.equals("mRNA")) {
				isMRNALoaded = true;
				addInfoMRNA = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("mRNA-seq")) {
				isMRNASeqLoaded = true;
				addInfoMRNASeq = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("microRNA")) {
				isMicroRNALoaded = true;
				addInfoMicroRNA = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("microRNA-seq")) {
				isMicroRNASeqLoaded = true;
				addInfoMicroRNASeq = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("Clinical")) {
				isClinicalLoaded = true;
			}
			else if (dataSetName.equals("Mutations")) {
				isMutationsLoaded = true;
				addInfoMutations = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("Copy Number")) {
				isCopyNumberLoaded = true;
				addInfoCopyNumber = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("Methylation")) {
				isMethylationLoaded = true;
				addInfoMethylation = getAdditionalInfo(dataDomain);
			}
			else if (dataSetName.equals("RPPA")) {
				isRPPALoaded = true;
				addInfoRPPA = getAdditionalInfo(dataDomain);
			}
		}

		reportStringHTML += "<tr><td>" + tumorAbbreviation + "</td>" + "<td>" + isMRNALoaded
				+ "</td>" + "<td>" + isMRNASeqLoaded + "</td>" + "<td>" + isMicroRNALoaded
				+ "</td>" + "<td>" + isMicroRNASeqLoaded + "</td>" + "<td>" + isClinicalLoaded
				+ "</td>" + "<td>" + isMutationsLoaded + "</td>" + "<td>" + isCopyNumberLoaded
				+ "</td>" + "<td>" + isMethylationLoaded + "</td>" + "<td>" + isRPPALoaded
				+ "</td>" + "<td>" + jnlpLinkTag + "</td>" + "<td>" + projectLinkTag + "</td>"
				+ "<td>" + firehoseReportLinkTag + "</td>" + "</td></tr>";

		reportStringJSON += "{\"tumorAbbreviation\":\"" + tumorAbbreviation
				+ "\",\"tumorName\":\"" + tumorName + "\",\"statistics\":{\"mRNA\":"
				+ addInfoMRNA + ",\"mRNA-seq\":" + addInfoMRNASeq + ",\"microRNA\":"
				+ addInfoMicroRNA + ",\"microRNA-seq\":" + addInfoMicroRNASeq
				+ ",\"Clinical\":" + addInfoClinical + ",\"Mutations\":" + addInfoMutations
				+ ",\"Copy Number\":" + addInfoCopyNumber + ",\"Methylation\":"
				+ addInfoMethylation + ",\"RPPA\":" + addInfoRPPA + "},\"Caleydo JNLP\":\""
				+ jnlpURL + "\",\"Caleydo Project\":\"" + projectOutputPath
				+ "\",\"Firehose Report\":\"" + firehoseReportURL + "\"}\n";
	}

	private String getAdditionalInfo(ATableBasedDataDomain dataDomain) {
		return "{\"gene\":{\"count\":\"" + dataDomain.getTable().getMetaData().size()
				+ "\",\"groupings\":\"" + getDimensionGroupingList(dataDomain)
				+ "\"},\"sample\":{\"count\":\"" + dataDomain.getTable().getMetaData().depth()
				+ "\",\"groupings\":\"" + getRecordGroupingList(dataDomain) + "\"}}";
	}

	private String getRecordGroupingList(ATableBasedDataDomain dataDomain) {

		String recordGroupings = "";

		for (String recordPerspectiveID : dataDomain.getRecordPerspectiveIDs()) {
			RecordPerspective recordPerspective = dataDomain.getTable().getRecordPerspective(
					recordPerspectiveID);

			if (recordPerspective.isPrivate())
				continue;
			if (recordPerspective.getLabel().equals("Default"))
				continue;

			recordGroupings += recordPerspective.getLabel() + ", ";
		}

		// remove last comma
		if (recordGroupings.length() > 2)
			recordGroupings = recordGroupings.substring(0, recordGroupings.length() - 2);

		return recordGroupings;
	}

	private String getDimensionGroupingList(ATableBasedDataDomain dataDomain) {
		String dimensionGroupings = "";
		for (String dimensionPerspectiveID : dataDomain.getDimensionPerspectiveIDs()) {
			DimensionPerspective dimensionPerspective = dataDomain.getTable()
					.getDimensionPerspective(dimensionPerspectiveID);
			if (dimensionPerspective.isPrivate()) {
				continue;
			}
			if (dimensionPerspective.getLabel().equals("Default"))
				continue;

			dimensionGroupings += dimensionPerspective.getLabel() + ", ";
		}

		// remove last comma
		if (dimensionGroupings.length() > 2)
			dimensionGroupings = dimensionGroupings.substring(0,
					dimensionGroupings.length() - 2);

		return dimensionGroupings;
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
