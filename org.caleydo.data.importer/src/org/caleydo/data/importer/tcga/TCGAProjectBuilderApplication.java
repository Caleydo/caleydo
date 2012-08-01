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
import org.caleydo.core.data.datadomain.DataDomainManager;
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

	@Override
	public Object start(IApplicationContext context) throws Exception {

		String[] runConfigParameters = (String[]) context.getArguments().get(
				"application.args");

		String[] tumorTypes = null;
		String analysisRunIdentifier = "";
		String dataRunIdentifier = "";
		String outputFolder = "";
		String tcgaServerURL = "";

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
			outputFolder = config.getString("output-folder");
			tcgaServerURL = config.getString("server");
		}
		catch (JSAPException e) {
			handleJSAPError(jsap);
		}

		String tmpDataOutputPath = outputFolder + "tmp";

		for (int tumorIndex = 0; tumorIndex < tumorTypes.length; tumorIndex++) {
			String tumorType = tumorTypes[tumorIndex];

			String xmlFilePath = outputFolder + analysisRunIdentifier + "_" + tumorType
					+ ".xml";

			String projectOutputPath = outputFolder + analysisRunIdentifier + "_" + tumorType
					+ ".cal";

			String jnlpFileName = analysisRunIdentifier + "_" + tumorType + ".jnlp";
			String jnlpOutputPath = outputFolder + jnlpFileName;

			String jnlpRemoteOutputURL = tcgaServerURL + analysisRunIdentifier + "_"
					+ tumorType + ".cal";

			FileOperations.createDirectory(tmpDataOutputPath);

			System.out.println("Downloading data for tumor type " + tumorType
					+ " for analysis run " + analysisRunIdentifier);

			TCGADataXMLGenerator generator = new TCGADataXMLGenerator(tumorType,
					analysisRunIdentifier, dataRunIdentifier, xmlFilePath, tmpDataOutputPath);

			generator.run();

			System.out.println("Building project file for tumor type " + tumorType
					+ " for analysis run " + analysisRunIdentifier);

			XMLToProjectBuilder xmlToProjectBuilder = new XMLToProjectBuilder();
			xmlToProjectBuilder.buildProject(xmlFilePath, projectOutputPath);

			DataDomainManager.get().unregisterAllDataDomains();

			// Clean up
			new File(xmlFilePath).delete();

			try {
				// Generate jnlp file from jnlp template
				replaceStringInFile("CALEYDO_PROJECT_URL", jnlpRemoteOutputURL, new File(
						"resources/caleydo.jnlp"), new File(jnlpOutputPath));

				replaceStringInFile("JNLP_NAME", jnlpFileName, new File(jnlpOutputPath),
						new File(jnlpOutputPath));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		FileOperations.deleteDirectory(tmpDataOutputPath);

		return context;
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

		// I'm aware of the potential for resource leaks here. Proper resource
		// handling has been omitted in the interest of brevity
		reader.close();
		writer.close();
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
