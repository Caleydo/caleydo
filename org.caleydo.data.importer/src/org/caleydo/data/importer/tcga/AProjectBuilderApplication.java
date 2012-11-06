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

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
 * This abstract class coordinates the whole workflow of creating a Caleydo
 * projects including the intermediate XML file generation and cleanup.
 *
 * @author Marc Streit
 *
 */
public abstract class AProjectBuilderApplication<S extends Settings>
	implements IApplication {

	protected S settings;

	@Override
	public final Object start(IApplicationContext context) throws Exception {
		parseArgs(context);

		generateTCGAProjectFiles();

		// FileOperations.deleteDirectory(tmpDataOutputPath);

		return context;
	}

	private void parseArgs(IApplicationContext context) {
		String[] args = (String[]) context.getArguments().get("application.args");
		JSAP jsap = new JSAP();
		try {
			registerArguments(jsap);

			JSAPResult config = jsap.parse(args);

			// check whether the command line was valid, and if it wasn't,
			// display usage information and exit.
			if (!config.success()) {
				handleJSAPError(jsap);
			}
			this.settings = createSettings();
			extractArguments(config, settings, jsap);
		} catch (JSAPException e) {
			handleJSAPError(jsap);
		}
	}

	protected abstract S createSettings();

	protected void registerArguments(JSAP jsap) throws JSAPException {
		FlaggedOption tumorOpt = new FlaggedOption("tumor").setStringParser(JSAP.STRING_PARSER).setDefault(JSAP.NO_DEFAULT).setRequired(true).setShortFlag('t').setLongFlag(JSAP.NO_LONGFLAG);
		tumorOpt.setList(true);
		tumorOpt.setListSeparator(',');
		tumorOpt.setHelp("Tumor abbreviation");
		jsap.registerParameter(tumorOpt);

		FlaggedOption outputFolderOpt = new FlaggedOption("output-folder").setStringParser(JSAP.STRING_PARSER).setDefault(".").setRequired(false).setShortFlag('o').setLongFlag(JSAP.NO_LONGFLAG);
		outputFolderOpt.setHelp("Output folder (full path)");
		jsap.registerParameter(outputFolderOpt);

		jsap.registerParameter(new FlaggedOption("flatOutput").setDefault("false").setRequired(false).setShortFlag('f').setLongFlag("flat").setStringParser(JSAP.BOOLEAN_PARSER)
				.setHelp("whether the project files should be generated flat, i.e. without the differnt types as directories"));
		jsap.registerParameter(new FlaggedOption("cleanOutput").setDefault("false").setRequired(false).setShortFlag('c').setLongFlag("clean").setStringParser(JSAP.BOOLEAN_PARSER)
				.setHelp("whether the cached temporary files should not be used"));

		jsap.registerParameter(new FlaggedOption("numThreads").setDefault("1").setRequired(false).setShortFlag('p')
				.setLongFlag("processes").setStringParser(JSAP.INTEGER_PARSER)
				.setHelp("number of processes to be used for multi processing"));
	}

	protected void extractArguments(JSAPResult config, S settings, JSAP jsap) {
		settings.setNumThreads(config.getInt("numThreads"));
		settings.setOutput(config.getString("output-folder"), config.getBoolean("flatOutput"),
				config.getBoolean("cleanOutput"));
		settings.setTumorTypes(config.getStringArray("tumor"));
	}

	protected abstract void generateTCGAProjectFiles();

	protected void handleJSAPError(JSAP jsap) {
		System.err.println("Error during parsing of program arguments. Closing program.");
		System.err.println("Usage: Caleydo");
		System.err.println(jsap.getUsage());
		System.err.println();
		System.exit(1);
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
