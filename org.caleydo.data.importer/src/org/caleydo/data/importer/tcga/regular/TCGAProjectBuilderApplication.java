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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.tcga.AProjectBuilderApplication;
import org.eclipse.equinox.app.IApplication;

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
 extends AProjectBuilderApplication<TCGASettings>
	implements IApplication {

	public static String DEFAULT_TCGA_SERVER_URL = "http://compbio.med.harvard.edu/tcga/stratomex/data/";
	public static String CALEYDO_WEBSTART_URL = "http://data.icg.tugraz.at/caleydo/download/webstart_"
			+ GeneralManager.VERSION + "/";

	@Override
	protected TCGASettings createSettings() {
		return new TCGASettings();
	}

	@Override
	protected void registerArguments(JSAP jsap) throws JSAPException {
		super.registerArguments(jsap);
		FlaggedOption analysisRunIdentifierOpt = new FlaggedOption("analysis_runs").setStringParser(JSAP.STRING_PARSER).setDefault(JSAP.NO_DEFAULT).setRequired(true).setShortFlag('a')
				.setLongFlag(JSAP.NO_LONGFLAG);
		analysisRunIdentifierOpt.setList(true);
		analysisRunIdentifierOpt.setListSeparator(',');
		analysisRunIdentifierOpt.setHelp("Analysis run identifiers");
		jsap.registerParameter(analysisRunIdentifierOpt);

		FlaggedOption dataRunIdentifierOpt = new FlaggedOption("data_runs").setStringParser(JSAP.STRING_PARSER).setDefault(JSAP.NO_DEFAULT).setRequired(true).setShortFlag('d')
				.setLongFlag(JSAP.NO_LONGFLAG);
		dataRunIdentifierOpt.setList(true);
		dataRunIdentifierOpt.setListSeparator(',');
		dataRunIdentifierOpt.setHelp("Data run identifiers");
		jsap.registerParameter(dataRunIdentifierOpt);

		FlaggedOption tcgaServerURLOpt = new FlaggedOption("server").setStringParser(JSAP.STRING_PARSER).setDefault(DEFAULT_TCGA_SERVER_URL).setRequired(false).setShortFlag('s')
				.setLongFlag(JSAP.NO_LONGFLAG);
		tcgaServerURLOpt.setHelp("TCGA Server URL that hosts TCGA Caleydo project files");
		jsap.registerParameter(tcgaServerURLOpt);

		FlaggedOption sampleGenesOpt = new FlaggedOption("sample_genes").setStringParser(JSAP.BOOLEAN_PARSER).setDefault("true").setRequired(false).setShortFlag('g').setLongFlag(JSAP.NO_LONGFLAG);
		sampleGenesOpt.setHelp("TCGA Server URL that hosts TCGA Caleydo project files");
		jsap.registerParameter(sampleGenesOpt);
	}

	@Override
	protected void extractArguments(JSAPResult config, TCGASettings settings, JSAP jsap) {
		super.extractArguments(config, settings, jsap);

		settings.setRuns(config.getStringArray("analysis_runs"), config.getStringArray("data_runs"));
		settings.setSampleGenes(config.getBoolean("sample_genes"));

		if (settings.getAnalysisRuns().length != settings.getDataRuns().length) {
			System.err.println("Error during parsing of program arguments. You need to provide a corresponding data run for each analysis run. Closing program.");
			System.err.println("Usage: Caleydo");
			System.err.println(jsap.getUsage());
			System.err.println();
			System.exit(1);
		}
		settings.setTcgaServerURL(config.getString("server"));
	}

	@Override
	protected void generateTCGAProjectFiles() {
		ForkJoinPool pool = new ForkJoinPool(settings.getNumThreads());

		RecursiveAction action = new RecursiveAction() {
			private static final long serialVersionUID = -8919058430905145146L;

			@Override
			protected void compute() {
				Collection<TCGARunTask> tasks = new ArrayList<>();
				for (int i = 0; i < settings.getNumRuns(); i++) {
					String analysisRun = settings.getAnalysisRun(i);
					String dataRun = settings.getDataRun(i);

					tasks.add(new TCGARunTask(analysisRun, dataRun, settings));
				}
				invokeAll(tasks);
			}
		};
		pool.invoke(action);
		pool.shutdown();
	}
}
