/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
