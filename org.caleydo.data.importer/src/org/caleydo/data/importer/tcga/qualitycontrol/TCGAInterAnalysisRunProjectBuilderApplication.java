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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.tcga.AProjectBuilderApplication;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.eclipse.equinox.app.IApplication;

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
 extends AProjectBuilderApplication<TCGAQCSettings>
	implements IApplication {

	public static String DEFAULT_TCGA_SERVER_URL = "http://compbio.med.harvard.edu/tcga/stratomex/data_qc/";
	public static String CALEYDO_WEBSTART_URL = "http://data.icg.tugraz.at/caleydo/download/webstart_"
			+ GeneralManager.VERSION + "/";
	public static String DEFAULT_OUTPUT_FOLDER_PATH = GeneralManager.CALEYDO_HOME_PATH + "TCGA/";

	@Override
	protected TCGAQCSettings createSettings() {
		return new TCGAQCSettings();
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

		FlaggedOption tcgaServerURLOpt = new FlaggedOption("server").setStringParser(JSAP.STRING_PARSER).setDefault(DEFAULT_TCGA_SERVER_URL).setRequired(false).setShortFlag('s')
				.setLongFlag(JSAP.NO_LONGFLAG);
		tcgaServerURLOpt.setHelp("TCGA Server URL that hosts TCGA Caleydo project files");
		jsap.registerParameter(tcgaServerURLOpt);

	}

	@Override
	protected void extractArguments(JSAPResult config, TCGAQCSettings settings, JSAP jsap) {
		super.extractArguments(config, settings, jsap);
		settings.setRuns(config.getStringArray("analysis_runs"));
		settings.setTcgaServerURL(config.getString("server"));
	}

	@Override
	protected void generateTCGAProjectFiles() {
		ForkJoinPool pool = new ForkJoinPool(settings.getNumThreads());

		RecursiveAction action = new RecursiveAction() {
			private static final long serialVersionUID = -8919058430905145146L;

			@Override
			protected void compute() {
				Collection<TCGAQCDataSetTypeTask> tasks = new ArrayList<>();
				for (EDataSetType dataSetType : EDataSetType.values()) {
					tasks.add(new TCGAQCDataSetTypeTask(dataSetType, settings));
				}
				invokeAll(tasks);
			}
		};
		pool.invoke(action);
		pool.shutdown();
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
