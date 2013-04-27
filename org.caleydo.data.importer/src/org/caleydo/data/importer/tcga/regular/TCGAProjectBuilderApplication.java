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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.caleydo.data.importer.tcga.AProjectBuilderApplication;
import org.eclipse.equinox.app.IApplication;

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

	@Override
	protected TCGASettings createSettings() {
		return new TCGASettings();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.data.importer.tcga.AProjectBuilderApplication#createTasks(org.caleydo.data.importer.tcga.Settings)
	 */
	@Override
	protected List<ForkJoinTask<Void>> createTasks(TCGASettings settings) {
		List<ForkJoinTask<Void>> tasks = new ArrayList<>();
		List<Date> analysisRuns = settings.getAnalysisRuns();
		List<Date> dataRuns = settings.getDataRuns();
		for (int i = 0; i < analysisRuns.size(); i++) {
			Date analysisRun = analysisRuns.get(i);
			Date dataRun = dataRuns.get(i);

			tasks.add(new TCGARunTask(analysisRun, dataRun, settings));
		}
		return tasks;
	}
}
