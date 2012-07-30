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

import java.io.File;
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

	@Override
	public Object start(IApplicationContext context) throws Exception {

		String[] runConfigParameters = (String[]) context.getArguments().get(
				"application.args");

		String[] tumorTypes = null;
		String analysisRunIdentifier = "";
		String dataRunIdentifier = "";
		String outputFolder = "";

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
		}
		catch (JSAPException e) {
			handleJSAPError(jsap);
		}
		
		String tmpDataOutputPath = outputFolder + "tmp";

		for (int tumorIndex = 0; tumorIndex < tumorTypes.length; tumorIndex++) {
			String tumorType = tumorTypes[tumorIndex];

			String xmlFilePath = outputFolder + analysisRunIdentifier + "_" + tumorType
					+ ".xml";

			String projectOutputPath = outputFolder + analysisRunIdentifier + "_"
					+ tumorType + ".cal";
			
			FileOperations.createDirectory(tmpDataOutputPath);
			
			System.out.println("Downloading data for tumor type " + tumorType
					+ " for analysis run " + analysisRunIdentifier);
			
			TCGADataXMLGenerator generator = new TCGADataXMLGenerator(tumorType,
					analysisRunIdentifier, dataRunIdentifier, xmlFilePath,
					tmpDataOutputPath);

			generator.run();

			System.out.println("Building project file for tumor type " + tumorType
					+ " for analysis run " + analysisRunIdentifier);

			XMLToProjectBuilder xmlToProjectBuilder = new XMLToProjectBuilder();
			xmlToProjectBuilder.buildProject(xmlFilePath, projectOutputPath);
					
			// Clean up
			new File(xmlFilePath).delete();
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

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
}
